package okhttp3.internal.platform;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.X509TrustManager;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.internal.tls.BasicCertificateChainCleaner;
import okhttp3.internal.tls.CertificateChainCleaner;
import okhttp3.internal.tls.TrustRootIndex;

public class Platform {
    private static final Platform PLATFORM;
    private static final Logger logger = Logger.getLogger(OkHttpClient.class.getName());

    public static Platform get() {
        return PLATFORM;
    }

    public static String getPrefix() {
        return "OkHttp";
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

    public void log(int level, String message, Throwable t) {
        logger.log(level == 5 ? Level.WARNING : Level.INFO, message, t);
    }

    public boolean isCleartextTrafficPermitted(String hostname) {
        return true;
    }

    public Object getStackTraceForCloseable(String closer) {
        if (logger.isLoggable(Level.FINE)) {
            return new Throwable(closer);
        }
        return null;
    }

    public void logCloseableLeak(String message, Object stackTrace) {
        if (stackTrace == null) {
            message = message + " To see where this was allocated, set the OkHttpClient logger level to FINE: Logger.getLogger(OkHttpClient.class.getName()).setLevel(Level.FINE);";
        }
        log(5, message, (Throwable) stackTrace);
    }

    public static List<String> alpnProtocolNames(List<Protocol> protocols) {
        List<String> names = new ArrayList(protocols.size());
        int size = protocols.size();
        for (int i = 0; i < size; i++) {
            Protocol protocol = (Protocol) protocols.get(i);
            if (protocol != Protocol.HTTP_1_0) {
                names.add(protocol.toString());
            }
        }
        return names;
    }

    public CertificateChainCleaner buildCertificateChainCleaner(X509TrustManager trustManager) {
        return new BasicCertificateChainCleaner(TrustRootIndex.get(trustManager));
    }

    static {
        Platform buildIfSupported = AndroidPlatform.buildIfSupported();
        if (buildIfSupported == null) {
            buildIfSupported = Jdk9Platform.buildIfSupported();
            if (buildIfSupported == null) {
                buildIfSupported = JdkWithJettyBootPlatform.buildIfSupported();
                if (buildIfSupported == null) {
                    buildIfSupported = new Platform();
                }
            }
        }
        PLATFORM = buildIfSupported;
    }
}
