package io.github.amayaframework.core.tomcat;

import com.github.romanqed.jutils.util.Checks;
import io.github.amayaframework.core.Amaya;
import io.github.amayaframework.core.AmayaBuilder;
import io.github.amayaframework.core.configurators.Configurator;
import io.github.amayaframework.core.controllers.Controller;
import io.github.amayaframework.core.tomcat.handlers.ServletHandler;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.lang.annotation.Annotation;
import java.util.Objects;

/**
 * A builder that helps to instantiate a properly configured collection of Servlets
 */
public class TomcatBuilder extends AmayaBuilder<Tomcat> {
    private static final String ACTIONS_PREFIX = "io.github.amayaframework.core.tomcat.actions";
    private final String DEFAULT_DOC_BASE = ".";
    private final String DEFAULT_CONTEXT = "";
    private final String URL_PATTERN = "/*";
    private int port;
    private String hostname;
    private String contextPath;
    private String docBase;

    public TomcatBuilder() {
        super(ACTIONS_PREFIX);
        resetValues();
    }

    @Override
    protected void resetValues() {
        port = 8000;
        hostname = null;
        contextPath = DEFAULT_CONTEXT;
        docBase = DEFAULT_DOC_BASE;
        super.resetValues();
    }

    /**
     * Adds the configurator to the end of the current list of configurators.
     *
     * @param configurator {@link Configurator} configurator to be added. Must be not null.
     * @return {@link TomcatBuilder} instance
     */
    @Override
    public TomcatBuilder addConfigurator(Configurator configurator) {
        return (TomcatBuilder) super.addConfigurator(configurator);
    }

    /**
     * Deletes all configurators whose class matches the specified class.
     *
     * @param clazz with which to delete
     * @return {@link TomcatBuilder} instance
     */
    @Override
    public TomcatBuilder removeConfigurator(Class<? extends Configurator> clazz) {
        return (TomcatBuilder) super.removeConfigurator(clazz);
    }

    /**
     * Adds the controller to the list of processed.
     *
     * @param controller {@link Controller} controller to be added. Must be not null.
     * @return {@link TomcatBuilder} builder instance
     */
    @Override
    public TomcatBuilder addController(Controller controller) {
        return (TomcatBuilder) super.addController(controller);
    }

    /**
     * Removes the controller from the list of processed.
     *
     * @param path controller path
     * @return {@link TomcatBuilder} instance
     */
    @Override
    public TomcatBuilder removeController(String path) {
        return (TomcatBuilder) super.removeController(path);
    }

    /**
     * Sets the annotation by which the controllers will be scanned.
     * If value will be null, the scan will not be performed.
     *
     * @param annotation {@link Class} of annotation
     * @return {@link TomcatBuilder} instance
     */
    @Override
    public TomcatBuilder controllerAnnotation(Class<? extends Annotation> annotation) {
        return (TomcatBuilder) super.controllerAnnotation(annotation);
    }

    /**
     * Binds tomcat to received host and port.
     *
     * @param hostname hostname
     * @param port     port value
     * @return {@link TomcatBuilder} instance
     */
    public TomcatBuilder bind(String hostname, int port) {
        Objects.requireNonNull(hostname);
        this.hostname = hostname;
        this.port = port;
        if (config.isDebug()) {
            logger.debug("Bind server to " + hostname + ":" + port);
        }
        return this;
    }

    /**
     * Binds tomcat to received host and port.
     *
     * @param port port value
     * @return {@link TomcatBuilder} instance
     */
    public TomcatBuilder bind(int port) {
        this.port = Checks.requireCorrectValue(port, e -> e >= 0);
        if (config.isDebug()) {
            logger.debug("Bind server to " + (hostname == null ? "localhost" : hostname) + ":" + port);
        }
        return this;
    }

    /**
     * Sets tomcat context path (relative to which all servlets will be added)
     * and tomcat base directory for the context.
     *
     * @param context context path, by default is ""
     * @param docBase directory for static files. Must exist, relative to the server home.
     * @return {@link TomcatBuilder} instance
     */
    public TomcatBuilder context(String context, String docBase) {
        this.contextPath = Checks.requireNonNullElse(context, DEFAULT_CONTEXT);
        this.docBase = Checks.requireNonNullElse(docBase, DEFAULT_DOC_BASE);
        if (config.isDebug()) {
            logger.debug("Set context to \"" + context + "\"; directory = \"" + docBase + "\"");
        }
        return this;
    }

    /**
     * Sets tomcat context path relative to which all servlets will be added.
     *
     * @param context context path, by default is ""
     * @return {@link TomcatBuilder} instance
     */
    public TomcatBuilder context(String context) {
        return context(context, null);
    }

    /**
     * Sets tomcat base directory for the context.
     *
     * @param docBase directory for static files. Must exist, relative to the server home.
     * @return {@link TomcatBuilder} instance
     */
    public TomcatBuilder docBase(String docBase) {
        return context(null, docBase);
    }

    @Override
    public Amaya<Tomcat> build() {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(port);
        if (hostname != null) {
            tomcat.setHostname(hostname);
        }
        Context context = tomcat.addContext(contextPath, new File(docBase).getAbsolutePath());
        findControllers();
        controllers.forEach((path, controller) -> {
            ServletHandler handler = new ServletHandler(controller);
            configure(handler.getHandler(), controller);
            tomcat.addServlet(contextPath, path, handler);
            context.addServletMappingDecoded(path + URL_PATTERN, path);
        });
        resetValues();
        return new TomcatAmaya(tomcat);
    }
}
