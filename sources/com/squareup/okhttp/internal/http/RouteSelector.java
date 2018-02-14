package com.squareup.okhttp.internal.http;

import com.squareup.okhttp.Address;
import com.squareup.okhttp.HttpUrl;
import com.squareup.okhttp.Route;
import com.squareup.okhttp.internal.RouteDatabase;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.SocketAddress;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

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
        this.address = address;
        this.routeDatabase = routeDatabase;
        HttpUrl url = address.url();
        Proxy proxy = address.getProxy();
        if (proxy != null) {
            this.proxies = Collections.singletonList(proxy);
        } else {
            this.proxies = new ArrayList();
            Collection select = this.address.getProxySelector().select(url.uri());
            if (select != null) {
                this.proxies.addAll(select);
            }
            this.proxies.removeAll(Collections.singleton(Proxy.NO_PROXY));
            this.proxies.add(Proxy.NO_PROXY);
        }
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
                    throw new SocketException("No route to " + this.address.getUriHost() + "; exhausted proxy configurations: " + this.proxies);
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
                throw new SocketException("No route to " + this.address.getUriHost() + "; exhausted inet socket addresses: " + this.inetSocketAddresses);
            }
        }
        if (hasNextPostponed()) {
            return (Route) this.postponedRoutes.remove(0);
        }
        throw new NoSuchElementException();
    }

    public final void connectFailed(Route failedRoute, IOException failure) {
        if (!(failedRoute.getProxy().type() == Type.DIRECT || this.address.getProxySelector() == null)) {
            this.address.getProxySelector().connectFailed(this.address.url().uri(), failedRoute.getProxy().address(), failure);
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
            socketHost = this.address.getUriHost();
            socketPort = this.address.getUriPort();
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
            List<InetAddress> addresses = this.address.getDns().lookup(socketHost);
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
