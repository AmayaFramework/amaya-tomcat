package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Executor;
import org.apache.catalina.Service;
import org.apache.catalina.connector.Connector;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

@SuppressWarnings("NullableProblems")
final class AddressSet implements Set<InetSocketAddress> {
    // Tomcat instance
    private final Service service;
    // Provided option set
    private final OptionSet options;
    // Catalina executor supplier
    private final Supplier<Executor> supplier;
    private Executor executor;
    // Content map and it sets
    private final Map<InetSocketAddress, Connector> connectors;
    private final ExecutorRegistry executors;
    // Current http version
    HttpVersion version;

    AddressSet(Service service, OptionSet options, Supplier<Executor> supplier) {
        this.service = service;
        this.options = options;
        this.supplier = supplier;
        this.connectors = new HashMap<>();
        this.executors = new ExecutorRegistry(service);
    }

    private Executor ensureDefaultExecutor() {
        if (supplier == null) {
            return null;
        }
        if (executor == null) {
            executor = supplier.get();
            service.addExecutor(executor);
        }
        return executor;
    }

    private Executor ensureExecutor(InetSocketAddress address) {
        var found = options.<Supplier<Executor>>get(TomcatOptions.executorStringKey(address));
        if (found == null) {
            return ensureDefaultExecutor();
        }
        var ret = found.get();
        executors.add(address, ret);
        return ret;
    }

    private Connector createConnector(InetSocketAddress address, HttpVersion version) {
        var factory = TomcatProtocols.getConnectorFactory(version);
        return factory.create(ensureExecutor(address), address, options);
    }

    void add(InetSocketAddress address, HttpVersion version) {
        Objects.requireNonNull(address);
        if (version.before(HttpVersion.HTTP_1_0)) {
            throw new IllegalArgumentException("Only versions starting with HTTP/1.0 are supported");
        }
        if (version.after(this.version)) {
            throw new IllegalArgumentException("Maximum allowed http version is " + this.version);
        }
        if (connectors.containsKey(address)) {
            return;
        }
        var connector = createConnector(address, version);
        service.addConnector(connector);
        connectors.put(address, connector);
    }

    @Override
    public boolean add(InetSocketAddress address) {
        Objects.requireNonNull(address);
        if (connectors.containsKey(address)) {
            return false;
        }
        var connector = createConnector(address, version);
        service.addConnector(connector);
        connectors.put(address, connector);
        return true;
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean contains(Object o) {
        return connectors.containsKey(o);
    }

    @Override
    @SuppressWarnings("SuspiciousMethodCalls")
    public boolean remove(Object o) {
        if (!connectors.containsKey(o)) {
            return false;
        }
        var connector = connectors.remove(o);
        service.removeConnector(connector);
        executors.remove((InetSocketAddress) o);
        try {
            connector.destroy();
        } catch (Throwable e) {
            Exceptions.throwAny(e);
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends InetSocketAddress> c) {
        Objects.requireNonNull(c);
        var ret = false;
        for (var address : c) {
            ret |= add(address);
        }
        return ret;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
        return connectors.keySet().containsAll(c);
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        Objects.requireNonNull(c);
        var ret = false;
        var keys = connectors.keySet();
        for (var address : keys) {
            if (!c.contains(address)) {
                remove(address);
                ret = true;
            }
        }
        return ret;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        Objects.requireNonNull(c);
        var ret = false;
        for (var address : c) {
            ret |= remove(address);
        }
        return ret;
    }

    @Override
    public int size() {
        return connectors.size();
    }

    @Override
    public boolean isEmpty() {
        return connectors.isEmpty();
    }

    @Override
    public Iterator<InetSocketAddress> iterator() {
        return new AddressIterator(connectors.entrySet().iterator());
    }

    @Override
    public void forEach(Consumer<? super InetSocketAddress> action) {
        connectors.keySet().forEach(action);
    }

    @Override
    public Object[] toArray() {
        return connectors.keySet().toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
        return connectors.keySet().toArray(a);
    }

    @Override
    public void clear() {
        for (var connector : connectors.values()) {
            service.removeConnector(connector);
        }
        connectors.clear();
        executors.clear();
    }

    @Override
    public Spliterator<InetSocketAddress> spliterator() {
        return connectors.keySet().spliterator();
    }

    private final class AddressIterator implements Iterator<InetSocketAddress> {
        private final Iterator<Map.Entry<InetSocketAddress, Connector>> iterator;
        private Map.Entry<InetSocketAddress, Connector> current;

        private AddressIterator(Iterator<Map.Entry<InetSocketAddress, Connector>> iterator) {
            this.iterator = iterator;
        }

        @Override
        public boolean hasNext() {
            return iterator.hasNext();
        }

        @Override
        public InetSocketAddress next() {
            var ret = iterator.next();
            this.current = ret;
            return ret.getKey();
        }

        @Override
        public void remove() {
            if (current == null) {
                throw new IllegalStateException();
            }
            var connector = current.getValue();
            service.removeConnector(connector);
            executors.remove(current.getKey());
            iterator.remove();
            current = null;
            try {
                connector.destroy();
            } catch (Throwable e) {
                Exceptions.throwAny(e);
            }
        }
    }
}
