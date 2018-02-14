package okhttp3.internal.connection;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import okhttp3.Address;
import okhttp3.HttpUrl;
import okhttp3.Route;
import okhttp3.internal.Util;

public final class RouteSelector {
    private final Address address;
    private List<InetSocketAddress> inetSocketAddresses = Collections.emptyList();
    private InetSocketAddress lastInetSocketAddress;
    private Proxy lastProxy;
    private int nextInetSocketAddressIndex;
    private int nextProxyIndex;
    private final List<Route> postponedRoutes = new ArrayList();
    private List<Proxy> proxies = Collections.emptyList();
    private final RouteDatabase routeDatabase;

    public RouteSelector(Address address, RouteDatabase routeDatabase) {
        List singletonList;
        RouteSelector this;
        this.address = address;
        this.routeDatabase = routeDatabase;
        HttpUrl url = address.url();
        Proxy proxy = address.proxy();
        if (proxy != null) {
            singletonList = Collections.singletonList(proxy);
            this = this;
        } else {
            singletonList = this.address.proxySelector().select(url.uri());
            if (singletonList == null || singletonList.isEmpty()) {
                singletonList = Util.immutableList(Proxy.NO_PROXY);
                this = this;
            } else {
                singletonList = Util.immutableList(singletonList);
                this = this;
            }
        }
        this.proxies = singletonList;
        this.nextProxyIndex = 0;
    }

    public final boolean hasNext() {
        return hasNextInetSocketAddress() || hasNextProxy() || hasNextPostponed();
    }

    public final Route next() throws IOException {
        while (true) {
            if (!hasNextInetSocketAddress()) {
                if (!hasNextProxy()) {
                    break;
                } else if (hasNextProxy()) {
                    List list = this.proxies;
                    int i = this.nextProxyIndex;
                    this.nextProxyIndex = i + 1;
                    Proxy proxy = (Proxy) list.get(i);
                    resetNextInetSocketAddress(proxy);
                    this.lastProxy = proxy;
                } else {
                    throw new SocketException("No route to " + this.address.url().host() + "; exhausted proxy configurations: " + this.proxies);
                }
            }
            if (hasNextInetSocketAddress()) {
                list = this.inetSocketAddresses;
                i = this.nextInetSocketAddressIndex;
                this.nextInetSocketAddressIndex = i + 1;
                this.lastInetSocketAddress = (InetSocketAddress) list.get(i);
                Route route = new Route(this.address, this.lastProxy, this.lastInetSocketAddress);
                if (!this.routeDatabase.shouldPostpone(route)) {
                    return route;
                }
                this.postponedRoutes.add(route);
            } else {
                throw new SocketException("No route to " + this.address.url().host() + "; exhausted inet socket addresses: " + this.inetSocketAddresses);
            }
        }
        if (hasNextPostponed()) {
            return (Route) this.postponedRoutes.remove(0);
        }
        throw new NoSuchElementException();
    }

    public final void connectFailed(Route failedRoute, IOException failure) {
        if (!(failedRoute.proxy().type() == Type.DIRECT || this.address.proxySelector() == null)) {
            this.address.proxySelector().connectFailed(this.address.url().uri(), failedRoute.proxy().address(), failure);
        }
        this.routeDatabase.failed(failedRoute);
    }

    private boolean hasNextProxy() {
        return this.nextProxyIndex < this.proxies.size();
    }

    private void resetNextInetSocketAddress(Proxy proxy) throws IOException {
        String socketHost;
        int socketPort;
        this.inetSocketAddresses = new ArrayList();
        if (proxy.type() == Type.DIRECT || proxy.type() == Type.SOCKS) {
            socketHost = this.address.url().host();
            socketPort = this.address.url().port();
        } else {
            SocketAddress proxyAddress = proxy.address();
            if (proxyAddress instanceof InetSocketAddress) {
                InetSocketAddress proxySocketAddress = (InetSocketAddress) proxyAddress;
                InetAddress address = proxySocketAddress.getAddress();
                if (address == null) {
                    socketHost = proxySocketAddress.getHostName();
                } else {
                    socketHost = address.getHostAddress();
                }
                socketPort = proxySocketAddress.getPort();
            } else {
                throw new IllegalArgumentException("Proxy.address() is not an InetSocketAddress: " + proxyAddress.getClass());
            }
        }
        if (socketPort <= 0 || socketPort > 65535) {
            throw new SocketException("No route to " + socketHost + ":" + socketPort + "; port is out of range");
        }
        if (proxy.type() == Type.SOCKS) {
            this.inetSocketAddresses.add(InetSocketAddress.createUnresolved(socketHost, socketPort));
        } else {
            List<InetAddress> addresses = this.address.dns().lookup(socketHost);
            int size = addresses.size();
            for (int i = 0; i < size; i++) {
                this.inetSocketAddresses.add(new InetSocketAddress((InetAddress) addresses.get(i), socketPort));
            }
        }
        this.nextInetSocketAddressIndex = 0;
    }

    private boolean hasNextInetSocketAddress() {
        return this.nextInetSocketAddressIndex < this.inetSocketAddresses.size();
    }

    private boolean hasNextPostponed() {
        return !this.postponedRoutes.isEmpty();
    }
}
