package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable1;
import com.github.romanqed.jtype.JType;
import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.options.Key;
import org.apache.catalina.Context;
import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;

import java.net.InetSocketAddress;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

/**
 * Contains constants for Tomcat-specific {@link io.github.amayaframework.options.OptionSet} keys
 * and utility methods for constructing option keys and names.
 */
public final class TomcatOptions {
    private TomcatOptions() {
    }

    /**
     * The key for the listened ports option.
     * <br>
     * Required type: {@link Iterable} of {@link Integer}.
     */
    public static final Key<Iterable<Integer>> PORTS = Key.of("ports", new JType<>(){});

    /**
     * The key for the listened ip addresses option.
     * <br>
     * Required type: {@link Iterable} of {@link InetSocketAddress}.
     */
    public static final Key<Iterable<InetSocketAddress>> IPS = Key.of("ips", new JType<>(){});

    /**
     * The key for the flag determines whether the server will use cookies for http sessions.
     * <br>
     * Required type: {@link Boolean}.
     */
    public static final String USE_SESSION_COOKIES = "use_cookies";

    /**
     * The prefix for the key for the address bound ssl config.
     */
    public static final String SSL_CONFIG_PREFIX = "ssl.";

    /**
     * Option key for supplying an {@link Executor} factory used by connectors.
     *
     * <p>Expected value type: {@link Supplier} of {@link Executor}. When set, this supplier
     * is used to create the executor for server connectors. For per-address executors use
     * {@link #executorKey(InetSocketAddress)} / {@link #executorStringKey(InetSocketAddress)}.
     */
    public static final Key<Supplier<Executor>> EXECUTOR = Key.of("executor", new JType<>(){});

    /**
     * Prefix for option keys that refer to per-address executor suppliers.
     *
     * <p>Used by {@link #executorStringKey(InetSocketAddress)} and {@link #executorKey(InetSocketAddress)}
     * to build full option keys for binding executors to specific addresses.
     */
    public static final String EXECUTOR_PREFIX = "executor.";

    /**
     * Option key for a context initialization callback.
     *
     * <p>Expected value type: {@link Runnable1} of {@link Context}. If provided, the callback
     * is executed after a {@link Context} is created and configured, allowing additional
     * programmatic customization (for example registering servlets, filters or attributes).
     */
    public static final Key<Runnable1<Context>> CONTEXT_INITIALIZER = Key.of("ctx_initializer", new JType<>(){});

    /**
     * The key for the flag determines whether the server will prefer async mode.
     * <br>
     * Required type: {@link Boolean}.
     */
    public static final Key<Boolean> PREFER_ASYNC = Key.of("prefer_async", Boolean.class);

    /**
     * The key for the http method buffer option.
     * <br>
     * Required type: {@link HttpMethodBuffer}
     */
    public static final Key<HttpMethodBuffer> HTTP_METHOD_BUFFER = Key.of(
            "http_method_buffer",
            HttpMethodBuffer.class
    );

    /**
     * The key for the http code buffer option.
     * <br>
     * Required type: {@link HttpCodeBuffer}.
     */
    public static final Key<HttpCodeBuffer> HTTP_CODE_BUFFER = Key.of("http_code_buffer", HttpCodeBuffer.class);

    /**
     * The key for the connector configurer option.
     * <br>
     * Required type: {@link BiConsumer} of {@link Connector}.
     */
    public static final Key<BiConsumer<HttpVersion, Connector>> CONNECTOR_CONFIGURER = Key.of(
            "connector_configurer",
            new JType<>(){}
    );

    /**
     * Creates an SSL configuration option key string for the given address.
     *
     * @param address the bind address
     * @return the SSL config key string
     */
    public static String sslStringKey(InetSocketAddress address) {
        return SSL_CONFIG_PREFIX + address.getHostString() + ":" + address.getPort();
    }

    /**
     * Creates an SSL configuration option key string for the given host and port.
     *
     * @param host the hostname
     * @param port the port number
     * @return the SSL config key string
     */
    public static String sslStringKey(String host, int port) {
        return SSL_CONFIG_PREFIX + host + ":" + port;
    }

    /**
     * Creates an SSL configuration option key string for the given port on all interfaces.
     *
     * @param port the port number
     * @return the SSL config key string
     */
    public static String sslStringKey(int port) {
        return SSL_CONFIG_PREFIX + "0.0.0.0:" + port;
    }

    /**
     * Creates an {@link Key} for the SSL configuration bound to the given address.
     *
     * @param address the bind address
     * @return the SSL config key
     */
    public static Key<SSLHostConfig> sslKey(InetSocketAddress address) {
        return Key.of(sslStringKey(address), SSLHostConfig.class);
    }

    /**
     * Creates an {@link Key} for the SSL configuration bound to the given host and port.
     *
     * @param host the hostname
     * @param port the port number
     * @return the SSL config key
     */
    public static Key<SSLHostConfig> sslKey(String host, int port) {
        return Key.of(sslStringKey(host, port), SSLHostConfig.class);
    }

    /**
     * Creates an {@link Key} for the SSL configuration bound to the given port on all interfaces.
     *
     * @param port the port number
     * @return the SSL config key
     */
    public static Key<SSLHostConfig> sslKey(int port) {
        return Key.of(sslStringKey(port), SSLHostConfig.class);
    }

    /**
     * Creates an executor option key string for the given address.
     *
     * @param address the bind address
     * @return the executor key string
     */
    public static String executorStringKey(InetSocketAddress address) {
        return EXECUTOR_PREFIX + address.getHostString() + ":" + address.getPort();
    }

    /**
     * Creates an executor option key string for the given host and port.
     *
     * @param host the hostname
     * @param port the port number
     * @return the executor key string
     */
    public static String executorStringKey(String host, int port) {
        return EXECUTOR_PREFIX + host + ":" + port;
    }

    /**
     * Creates an executor option key string for the given port on all interfaces.
     *
     * @param port the port number
     * @return the executor key string
     */
    public static String executorStringKey(int port) {
        return EXECUTOR_PREFIX + "0.0.0.0:" + port;
    }

    /**
     * Creates an {@link Key} for an executor bound to the given address.
     *
     * @param address the bind address
     * @return the executor key
     */
    public static Key<Supplier<Executor>> executorKey(InetSocketAddress address) {
        return Key.of(executorStringKey(address), new JType<>(){});
    }

    /**
     * Creates an {@link Key} for an executor bound to the given host and port.
     *
     * @param host the hostname
     * @param port the port number
     * @return the executor key
     */
    public static Key<Supplier<Executor>> executorKey(String host, int port) {
        return Key.of(executorStringKey(host, port), new JType<>(){});
    }

    /**
     * Creates an {@link Key} for an executor bound to the given port on all interfaces.
     *
     * @param port the port number
     * @return the executor key
     */
    public static Key<Supplier<Executor>> executorKey(int port) {
        return Key.of(executorStringKey(port), new JType<>(){});
    }
}
