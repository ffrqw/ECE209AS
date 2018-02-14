package com.squareup.okhttp.internal;

import com.squareup.okhttp.Protocol;
import com.squareup.okhttp.internal.tls.AndroidTrustRootIndex;
import com.squareup.okhttp.internal.tls.RealTrustRootIndex;
import com.squareup.okhttp.internal.tls.TrustRootIndex;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;
import okio.Buffer;

public class Platform {
    private static final Platform PLATFORM = findPlatform();

    private static class Android extends Platform {
        private final OptionalMethod<Socket> getAlpnSelectedProtocol;
        private final OptionalMethod<Socket> setAlpnProtocols;
        private final OptionalMethod<Socket> setHostname;
        private final OptionalMethod<Socket> setUseSessionTickets;
        private final Class<?> sslParametersClass;
        private final Method trafficStatsTagSocket;
        private final Method trafficStatsUntagSocket;

        public Android(Class<?> sslParametersClass, OptionalMethod<Socket> setUseSessionTickets, OptionalMethod<Socket> setHostname, Method trafficStatsTagSocket, Method trafficStatsUntagSocket, OptionalMethod<Socket> getAlpnSelectedProtocol, OptionalMethod<Socket> setAlpnProtocols) {
            this.sslParametersClass = sslParametersClass;
            this.setUseSessionTickets = setUseSessionTickets;
            this.setHostname = setHostname;
            this.trafficStatsTagSocket = trafficStatsTagSocket;
            this.trafficStatsUntagSocket = trafficStatsUntagSocket;
            this.getAlpnSelectedProtocol = getAlpnSelectedProtocol;
            this.setAlpnProtocols = setAlpnProtocols;
        }

        public final void connectSocket(Socket socket, InetSocketAddress address, int connectTimeout) throws IOException {
            try {
                socket.connect(address, connectTimeout);
            } catch (AssertionError e) {
                if (Util.isAndroidGetsocknameError(e)) {
                    throw new IOException(e);
                }
                throw e;
            } catch (SecurityException e2) {
                IOException ioException = new IOException("Exception in connect");
                ioException.initCause(e2);
                throw ioException;
            }
        }

        public final X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
            Object context = Platform.readFieldOrNull(sslSocketFactory, this.sslParametersClass, "sslParameters");
            if (context == null) {
                try {
                    context = Platform.readFieldOrNull(sslSocketFactory, Class.forName("com.google.android.gms.org.conscrypt.SSLParametersImpl", false, sslSocketFactory.getClass().getClassLoader()), "sslParameters");
                } catch (ClassNotFoundException e) {
                    return null;
                }
            }
            X509TrustManager x509TrustManager = (X509TrustManager) Platform.readFieldOrNull(context, X509TrustManager.class, "x509TrustManager");
            return x509TrustManager != null ? x509TrustManager : (X509TrustManager) Platform.readFieldOrNull(context, X509TrustManager.class, "trustManager");
        }

        public final TrustRootIndex trustRootIndex(X509TrustManager trustManager) {
            TrustRootIndex result = AndroidTrustRootIndex.get(trustManager);
            return result != null ? result : super.trustRootIndex(trustManager);
        }

        public final void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
            if (hostname != null) {
                this.setUseSessionTickets.invokeOptionalWithoutCheckedException(sslSocket, Boolean.valueOf(true));
                this.setHostname.invokeOptionalWithoutCheckedException(sslSocket, hostname);
            }
            if (this.setAlpnProtocols != null && this.setAlpnProtocols.isSupported(sslSocket)) {
                Object[] parameters = new Object[1];
                Buffer buffer = new Buffer();
                int size = protocols.size();
                for (int i = 0; i < size; i++) {
                    Protocol protocol = (Protocol) protocols.get(i);
                    if (protocol != Protocol.HTTP_1_0) {
                        buffer.writeByte(protocol.toString().length());
                        buffer.writeUtf8(protocol.toString());
                    }
                }
                parameters[0] = buffer.readByteArray();
                this.setAlpnProtocols.invokeWithoutCheckedException(sslSocket, parameters);
            }
        }

        public final String getSelectedProtocol(SSLSocket socket) {
            if (this.getAlpnSelectedProtocol == null || !this.getAlpnSelectedProtocol.isSupported(socket)) {
                return null;
            }
            byte[] alpnResult = (byte[]) this.getAlpnSelectedProtocol.invokeWithoutCheckedException(socket, new Object[0]);
            if (alpnResult != null) {
                return new String(alpnResult, Util.UTF_8);
            }
            return null;
        }
    }

    private static class JdkPlatform extends Platform {
        private final Class<?> sslContextClass;

        public JdkPlatform(Class<?> sslContextClass) {
            this.sslContextClass = sslContextClass;
        }

        public final X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
            Object context = Platform.readFieldOrNull(sslSocketFactory, this.sslContextClass, "context");
            if (context == null) {
                return null;
            }
            return (X509TrustManager) Platform.readFieldOrNull(context, X509TrustManager.class, "trustManager");
        }
    }

    private static class JdkWithJettyBootPlatform extends JdkPlatform {
        private final Class<?> clientProviderClass;
        private final Method getMethod;
        private final Method putMethod;
        private final Method removeMethod;
        private final Class<?> serverProviderClass;

        public JdkWithJettyBootPlatform(Class<?> sslContextClass, Method putMethod, Method getMethod, Method removeMethod, Class<?> clientProviderClass, Class<?> serverProviderClass) {
            super(sslContextClass);
            this.putMethod = putMethod;
            this.getMethod = getMethod;
            this.removeMethod = removeMethod;
            this.clientProviderClass = clientProviderClass;
            this.serverProviderClass = serverProviderClass;
        }

        public final void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
            ReflectiveOperationException e;
            List<String> names = new ArrayList(protocols.size());
            int size = protocols.size();
            for (int i = 0; i < size; i++) {
                Protocol protocol = (Protocol) protocols.get(i);
                if (protocol != Protocol.HTTP_1_0) {
                    names.add(protocol.toString());
                }
            }
            try {
                Object provider = Proxy.newProxyInstance(Platform.class.getClassLoader(), new Class[]{this.clientProviderClass, this.serverProviderClass}, new JettyNegoProvider(names));
                this.putMethod.invoke(null, new Object[]{sslSocket, provider});
            } catch (InvocationTargetException e2) {
                e = e2;
                throw new AssertionError(e);
            } catch (IllegalAccessException e3) {
                e = e3;
                throw new AssertionError(e);
            }
        }

        public final void afterHandshake(SSLSocket sslSocket) {
            try {
                this.removeMethod.invoke(null, new Object[]{sslSocket});
            } catch (IllegalAccessException e) {
                throw new AssertionError();
            } catch (InvocationTargetException e2) {
                throw new AssertionError();
            }
        }

        public final String getSelectedProtocol(SSLSocket socket) {
            String str = null;
            try {
                JettyNegoProvider provider = (JettyNegoProvider) Proxy.getInvocationHandler(this.getMethod.invoke(null, new Object[]{socket}));
                if (!provider.unsupported && provider.selected == null) {
                    Internal.logger.log(Level.INFO, "ALPN callback dropped: SPDY and HTTP/2 are disabled. Is alpn-boot on the boot class path?");
                } else if (!provider.unsupported) {
                    str = provider.selected;
                }
                return str;
            } catch (InvocationTargetException e) {
                throw new AssertionError();
            } catch (IllegalAccessException e2) {
                throw new AssertionError();
            }
        }
    }

    private static class JettyNegoProvider implements InvocationHandler {
        private final List<String> protocols;
        private String selected;
        private boolean unsupported;

        public JettyNegoProvider(List<String> protocols) {
            this.protocols = protocols;
        }

        public final Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            String methodName = method.getName();
            Class<?> returnType = method.getReturnType();
            if (args == null) {
                args = Util.EMPTY_STRING_ARRAY;
            }
            if (methodName.equals("supports") && Boolean.TYPE == returnType) {
                return Boolean.valueOf(true);
            }
            if (methodName.equals("unsupported") && Void.TYPE == returnType) {
                this.unsupported = true;
                return null;
            } else if (methodName.equals("protocols") && args.length == 0) {
                return this.protocols;
            } else {
                if ((methodName.equals("selectProtocol") || methodName.equals("select")) && String.class == returnType && args.length == 1 && (args[0] instanceof List)) {
                    String str;
                    List<String> peerProtocols = args[0];
                    int size = peerProtocols.size();
                    for (int i = 0; i < size; i++) {
                        if (this.protocols.contains(peerProtocols.get(i))) {
                            str = (String) peerProtocols.get(i);
                            this.selected = str;
                            return str;
                        }
                    }
                    str = (String) this.protocols.get(0);
                    this.selected = str;
                    return str;
                } else if ((!methodName.equals("protocolSelected") && !methodName.equals("selected")) || args.length != 1) {
                    return method.invoke(this, args);
                } else {
                    this.selected = (String) args[0];
                    return null;
                }
            }
        }
    }

    public static Platform get() {
        return PLATFORM;
    }

    public static String getPrefix() {
        return "OkHttp";
    }

    public X509TrustManager trustManager(SSLSocketFactory sslSocketFactory) {
        return null;
    }

    public TrustRootIndex trustRootIndex(X509TrustManager trustManager) {
        return new RealTrustRootIndex(trustManager.getAcceptedIssuers());
    }

    public void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> list) {
    }

    public void afterHandshake(SSLSocket sslSocket) {
    }

    public String getSelectedProtocol(SSLSocket socket) {
        return null;
    }

    public void connectSocket(Socket socket, InetSocketAddress address, int connectTimeout) throws IOException {
        socket.connect(address, connectTimeout);
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    private static com.squareup.okhttp.internal.Platform findPlatform() {
        /*
        r2 = "com.android.org.conscrypt.SSLParametersImpl";
        r3 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x00a1 }
    L_0x0006:
        r4 = new com.squareup.okhttp.internal.OptionalMethod;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r2 = 0;
        r10 = "setUseSessionTickets";
        r23 = 1;
        r0 = r23;
        r0 = new java.lang.Class[r0];	 Catch:{ ClassNotFoundException -> 0x00aa }
        r23 = r0;
        r24 = 0;
        r25 = java.lang.Boolean.TYPE;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r23[r24] = r25;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r0 = r23;
        r4.<init>(r2, r10, r0);	 Catch:{ ClassNotFoundException -> 0x00aa }
        r5 = new com.squareup.okhttp.internal.OptionalMethod;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r2 = 0;
        r10 = "setHostname";
        r23 = 1;
        r0 = r23;
        r0 = new java.lang.Class[r0];	 Catch:{ ClassNotFoundException -> 0x00aa }
        r23 = r0;
        r24 = 0;
        r25 = java.lang.String.class;
        r23[r24] = r25;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r0 = r23;
        r5.<init>(r2, r10, r0);	 Catch:{ ClassNotFoundException -> 0x00aa }
        r6 = 0;
        r7 = 0;
        r8 = 0;
        r9 = 0;
        r2 = "android.net.TrafficStats";
        r22 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r2 = "tagSocket";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r23 = 0;
        r24 = java.net.Socket.class;
        r10[r23] = r24;	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r0 = r22;
        r6 = r0.getMethod(r2, r10);	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r2 = "untagSocket";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r23 = 0;
        r24 = java.net.Socket.class;
        r10[r23] = r24;	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r0 = r22;
        r7 = r0.getMethod(r2, r10);	 Catch:{ ClassNotFoundException -> 0x0152, NoSuchMethodException -> 0x0155 }
        r2 = "android.net.Network";
        java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x015d, NoSuchMethodException -> 0x0155 }
        r17 = new com.squareup.okhttp.internal.OptionalMethod;	 Catch:{ ClassNotFoundException -> 0x015d, NoSuchMethodException -> 0x0155 }
        r2 = byte[].class;
        r10 = "getAlpnSelectedProtocol";
        r23 = 0;
        r0 = r23;
        r0 = new java.lang.Class[r0];	 Catch:{ ClassNotFoundException -> 0x015d, NoSuchMethodException -> 0x0155 }
        r23 = r0;
        r0 = r17;
        r1 = r23;
        r0.<init>(r2, r10, r1);	 Catch:{ ClassNotFoundException -> 0x015d, NoSuchMethodException -> 0x0155 }
        r21 = new com.squareup.okhttp.internal.OptionalMethod;	 Catch:{ ClassNotFoundException -> 0x0160, NoSuchMethodException -> 0x0158 }
        r2 = 0;
        r10 = "setAlpnProtocols";
        r23 = 1;
        r0 = r23;
        r0 = new java.lang.Class[r0];	 Catch:{ ClassNotFoundException -> 0x0160, NoSuchMethodException -> 0x0158 }
        r23 = r0;
        r24 = 0;
        r25 = byte[].class;
        r23[r24] = r25;	 Catch:{ ClassNotFoundException -> 0x0160, NoSuchMethodException -> 0x0158 }
        r0 = r21;
        r1 = r23;
        r0.<init>(r2, r10, r1);	 Catch:{ ClassNotFoundException -> 0x0160, NoSuchMethodException -> 0x0158 }
        r9 = r21;
        r8 = r17;
    L_0x009a:
        r2 = new com.squareup.okhttp.internal.Platform$Android;	 Catch:{ ClassNotFoundException -> 0x00aa }
        r2.<init>(r3, r4, r5, r6, r7, r8, r9);	 Catch:{ ClassNotFoundException -> 0x00aa }
        r10 = r2;
    L_0x00a0:
        return r10;
    L_0x00a1:
        r2 = move-exception;
        r2 = "org.apache.harmony.xnet.provider.jsse.SSLParametersImpl";
        r3 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x00aa }
        goto L_0x0006;
    L_0x00aa:
        r2 = move-exception;
        r2 = "sun.security.ssl.SSLContextImpl";
        r11 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0148 }
        r19 = "org.eclipse.jetty.alpn.ALPN";
        r18 = java.lang.Class.forName(r19);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2.<init>();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r19;
        r2 = r2.append(r0);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r10 = "$Provider";
        r2 = r2.append(r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = r2.toString();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r20 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2.<init>();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r19;
        r2 = r2.append(r0);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r10 = "$ClientProvider";
        r2 = r2.append(r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = r2.toString();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r15 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = new java.lang.StringBuilder;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2.<init>();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r19;
        r2 = r2.append(r0);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r10 = "$ServerProvider";
        r2 = r2.append(r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = r2.toString();	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r16 = java.lang.Class.forName(r2);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = "put";
        r10 = 2;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r23 = 0;
        r24 = javax.net.ssl.SSLSocket.class;
        r10[r23] = r24;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r23 = 1;
        r10[r23] = r20;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r18;
        r12 = r0.getMethod(r2, r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = "get";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r23 = 0;
        r24 = javax.net.ssl.SSLSocket.class;
        r10[r23] = r24;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r18;
        r13 = r0.getMethod(r2, r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r2 = "remove";
        r10 = 1;
        r10 = new java.lang.Class[r10];	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r23 = 0;
        r24 = javax.net.ssl.SSLSocket.class;
        r10[r23] = r24;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r0 = r18;
        r14 = r0.getMethod(r2, r10);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r10 = new com.squareup.okhttp.internal.Platform$JdkWithJettyBootPlatform;	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        r10.<init>(r11, r12, r13, r14, r15, r16);	 Catch:{ ClassNotFoundException -> 0x0140, NoSuchMethodException -> 0x0150 }
        goto L_0x00a0;
    L_0x0140:
        r2 = move-exception;
    L_0x0141:
        r10 = new com.squareup.okhttp.internal.Platform$JdkPlatform;	 Catch:{ ClassNotFoundException -> 0x0148 }
        r10.<init>(r11);	 Catch:{ ClassNotFoundException -> 0x0148 }
        goto L_0x00a0;
    L_0x0148:
        r2 = move-exception;
        r10 = new com.squareup.okhttp.internal.Platform;
        r10.<init>();
        goto L_0x00a0;
    L_0x0150:
        r2 = move-exception;
        goto L_0x0141;
    L_0x0152:
        r2 = move-exception;
        goto L_0x009a;
    L_0x0155:
        r2 = move-exception;
        goto L_0x009a;
    L_0x0158:
        r2 = move-exception;
        r8 = r17;
        goto L_0x009a;
    L_0x015d:
        r2 = move-exception;
        goto L_0x009a;
    L_0x0160:
        r2 = move-exception;
        r8 = r17;
        goto L_0x009a;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.squareup.okhttp.internal.Platform.findPlatform():com.squareup.okhttp.internal.Platform");
    }

    static <T> T readFieldOrNull(Object instance, Class<T> fieldType, String fieldName) {
        Class<?> c;
        T t = null;
        loop0:
        while (true) {
            c = instance.getClass();
            while (c != Object.class) {
                try {
                    break loop0;
                } catch (NoSuchFieldException e) {
                    c = c.getSuperclass();
                } catch (IllegalAccessException e2) {
                    throw new AssertionError();
                }
            }
            if (!fieldName.equals("delegate")) {
                Object delegate = readFieldOrNull(instance, Object.class, "delegate");
                if (delegate == null) {
                    break;
                }
                instance = delegate;
            } else {
                break;
            }
        }
        Field field = c.getDeclaredField(fieldName);
        field.setAccessible(true);
        Object value = field.get(instance);
        if (value != null && fieldType.isInstance(value)) {
            t = fieldType.cast(value);
        }
        return t;
    }
}
