package okhttp3.internal.platform;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.SSLSocket;
import okhttp3.Protocol;

final class Jdk9Platform extends Platform {
    final Method getProtocolMethod;
    final Method setProtocolMethod;

    private Jdk9Platform(Method setProtocolMethod, Method getProtocolMethod) {
        this.setProtocolMethod = setProtocolMethod;
        this.getProtocolMethod = getProtocolMethod;
    }

    public final void configureTlsExtensions(SSLSocket sslSocket, String hostname, List<Protocol> protocols) {
        try {
            SSLParameters sslParameters = sslSocket.getSSLParameters();
            List<String> names = Platform.alpnProtocolNames(protocols);
            this.setProtocolMethod.invoke(sslParameters, new Object[]{names.toArray(new String[names.size()])});
            sslSocket.setSSLParameters(sslParameters);
        } catch (IllegalAccessException e) {
            throw new AssertionError();
        } catch (InvocationTargetException e2) {
            throw new AssertionError();
        }
    }

    public final String getSelectedProtocol(SSLSocket socket) {
        try {
            String protocol = (String) this.getProtocolMethod.invoke(socket, new Object[0]);
            if (protocol == null || protocol.equals("")) {
                return null;
            }
            return protocol;
        } catch (IllegalAccessException e) {
            throw new AssertionError();
        } catch (InvocationTargetException e2) {
            throw new AssertionError();
        }
    }

    public static Jdk9Platform buildIfSupported() {
        try {
            return new Jdk9Platform(SSLParameters.class.getMethod("setApplicationProtocols", new Class[]{String[].class}), SSLSocket.class.getMethod("getApplicationProtocol", new Class[0]));
        } catch (NoSuchMethodException e) {
            return null;
        }
    }
}
