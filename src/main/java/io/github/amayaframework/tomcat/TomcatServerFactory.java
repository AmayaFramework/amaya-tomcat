package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Exceptions;
import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.http.HttpCode;
import io.github.amayaframework.http.HttpMethod;
import io.github.amayaframework.options.OptionSet;
import io.github.amayaframework.options.Options;
import io.github.amayaframework.server.HttpServer;
import io.github.amayaframework.server.HttpServerFactory;
import io.github.amayaframework.server.ServerOptions;
import org.apache.catalina.Context;
import org.apache.catalina.Executor;
import org.apache.catalina.core.StandardContext;
import org.apache.catalina.session.StandardManager;
import org.apache.catalina.startup.Tomcat;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.function.Supplier;

public class TomcatServerFactory implements HttpServerFactory {
    private static final boolean JVM_21 = Runtime.version().feature() >= 21;
    private static final boolean PREFER_ASYNC = !JVM_21;

    private final TomcatFactory tomcatFactory;
    private final TomcatContextFactory contextFactory;
    private final Supplier<Executor> executorSupplier;
    private final Path root;

    public TomcatServerFactory(TomcatFactory tomcatFactory,
                               TomcatContextFactory contextFactory,
                               Supplier<Executor> executorSupplier,
                               Path root) {
        this.tomcatFactory = tomcatFactory;
        this.contextFactory = contextFactory;
        this.executorSupplier = executorSupplier;
        this.root = root;
    }

    public TomcatServerFactory(TomcatFactory tomcatFactory,
                               TomcatContextFactory contextFactory,
                               Supplier<Executor> executorSupplier) {
        this(tomcatFactory, contextFactory, executorSupplier, null);
    }

    public TomcatServerFactory(TomcatFactory tomcatFactory, TomcatContextFactory contextFactory) {
        this(tomcatFactory, contextFactory, null, null);
    }

    public TomcatServerFactory(TomcatFactory tomcatFactory) {
        this(tomcatFactory, null, null, null);
    }

    public TomcatServerFactory() {
        this(null, null, null, null);
    }

    private Path getRoot() {
        if (root == null) {
            return Path.of(".").toAbsolutePath().normalize();
        }
        return root;
    }

    private String getBaseDir(Path root) {
        var ret = root.resolve("tomcat").toAbsolutePath();
        try {
            Files.createDirectories(ret);
        } catch (IOException e) {
            Exceptions.throwAny(e);
        }
        return ret.toString();
    }

    private Tomcat createTomcat(OptionSet options, Environment env, Path root) {
        if (tomcatFactory == null) {
            var ret = new Tomcat();
            ret.setSilent(true);
            ret.setBaseDir(getBaseDir(root));
            return ret;
        }
        if (env == null) {
            return tomcatFactory.create(options);
        }
        return tomcatFactory.create(options, env);
    }

    private static void configureContext(Context context, OptionSet set) {
        context.setUseHttpOnly(true);
        context.setAlwaysAccessSession(false);
        context.setXmlValidation(false);
        context.setXmlNamespaceAware(false);
        context.setCrossContext(false);
        context.setDenyUncoveredHttpMethods(false);
        context.setAllowCasualMultipartParsing(true);
        context.setUseRelativeRedirects(false);
        context.setLogEffectiveWebXml(false);
        context.setValidateClientProvidedNewSessionId(false);
        context.setJspConfigDescriptor(null);
        context.setIgnoreAnnotations(true);
        context.setJarScanner(NoopJarScanner.NOOP_JAR_SCANNER);
        var manager = context.getManager();
        if (manager instanceof StandardManager) {
            ((StandardManager) manager).setPathname(null);
        }
        var cookies = set != null && set.asKey(TomcatOptions.USE_SESSION_COOKIES);
        if (!cookies) {
            context.setCookies(false);
            context.setSessionCookieName(null);
            context.setSessionCookiePath(null);
            context.setSessionCookieDomain(null);
        } else {
            context.setSessionCookiePathUsesTrailingSlash(true);
        }
        if (context instanceof StandardContext) {
            var standardContext = (StandardContext) context;
            standardContext.setClearReferencesObjectStreamClassCaches(false);
            standardContext.setClearReferencesRmiTargets(false);
            standardContext.setClearReferencesThreadLocals(false);
        }
        context.setConfigured(true);
    }

    private Context createContext(Tomcat tomcat, OptionSet set, Environment env) {
        if (contextFactory != null) {
            if (env == null) {
                return contextFactory.create(tomcat, set);
            }
            return contextFactory.create(tomcat, set, env);
        }
        var context = tomcat.addContext("", null);
        configureContext(context, set);
        return context;
    }

    private static void processBindOptions(Set<InetSocketAddress> set, OptionSet options) {
        // Add ports
        var port = options.get(ServerOptions.PORT);
        var ports = options.get(TomcatOptions.PORTS);
        if (port != null) {
            set.add(new InetSocketAddress(port));
        }
        if (ports != null) {
            ports.forEach(p -> set.add(new InetSocketAddress(p)));
        }
        // Add ips
        var ip = options.get(ServerOptions.IP);
        var ips = options.get(TomcatOptions.IPS);
        if (ip != null) {
            set.add(ip);
        }
        if (ips != null) {
            ips.forEach(set::add);
        }
    }

    private static HttpMethodBuffer getMethodBuffer(OptionSet options) {
        if (options == null) {
            return HttpMethod::of;
        }
        var buffer = options.get(TomcatOptions.HTTP_METHOD_BUFFER);
        if (buffer == null) {
            return HttpMethod::of;
        }
        return buffer;
    }

    private static HttpCodeBuffer getCodeBuffer(OptionSet options) {
        if (options == null) {
            return HttpCode::of;
        }
        var buffer = options.get(TomcatOptions.HTTP_CODE_BUFFER);
        if (buffer == null) {
            return HttpCode::of;
        }
        return buffer;
    }

    private static boolean decideAsync(OptionSet set) {
        if (set == null) {
            return PREFER_ASYNC;
        }
        var flag = set.get(TomcatOptions.PREFER_ASYNC);
        if (flag == null) {
            return PREFER_ASYNC;
        }
        return flag;
    }

    private Supplier<Executor> getExecutorSupplier() {
        if (executorSupplier == null && JVM_21) {
            return VirtualThreadExecutor::new;
        }
        return executorSupplier;
    }

    private Supplier<Executor> getExecutorSupplier(OptionSet set) {
        if (set == null) {
            return getExecutorSupplier();
        }
        var ret = set.get(TomcatOptions.EXECUTOR);
        if (ret == null) {
            return getExecutorSupplier();
        }
        return ret;
    }

    private HttpServer createHttpServer(OptionSet set, Environment env, Path root) {
        // Create tomcat
        var tomcat = createTomcat(set, env, root);
        // Create context
        var context = createContext(tomcat, set, env);
        // Prepare bind addresses and http config
        var executorSupplier = getExecutorSupplier(set);
        var addresses = new AddressSet(tomcat.getService(), set == null ? Options.empty() : set, executorSupplier);
        var config = new TomcatHttpConfig(addresses, context);
        if (set != null) {
            processBindOptions(addresses, set);
        }
        return new TomcatHttpServer(
                tomcat,
                addresses,
                config,
                context,
                getMethodBuffer(set),
                getCodeBuffer(set),
                decideAsync(set)
        );
    }

    private HttpServer createHttpServer(Environment env, Path root) {
        // Create tomcat
        var tomcat = createTomcat(null, env, root);
        // Create context
        var context = createContext(tomcat, null, env);
        // Prepare bind addresses and http config
        var addresses = new AddressSet(tomcat.getService(), Options.empty(), getExecutorSupplier());
        var config = new TomcatHttpConfig(addresses, context);
        return new TomcatHttpServer(
                tomcat,
                addresses,
                config,
                context,
                HttpMethod::of,
                HttpCode::of,
                PREFER_ASYNC
        );
    }

    @Override
    public HttpServer create(OptionSet set, Environment env) {
        var root = env == null ? getRoot() : env.root();
        return createHttpServer(set, env, root);
    }

    @Override
    public HttpServer create(OptionSet set) {
        return createHttpServer(set, null, getRoot());
    }

    @Override
    public HttpServer create(Environment env) {
        var root = env == null ? getRoot() : env.root();
        return createHttpServer(env, root);
    }

    @Override
    public HttpServer create() {
        return createHttpServer(null, getRoot());
    }
}
