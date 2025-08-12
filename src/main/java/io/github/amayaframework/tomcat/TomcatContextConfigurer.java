package io.github.amayaframework.tomcat;

import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Context;

/**
 * Configurer interface for customizing a {@link org.apache.catalina.Context}.
 */
public interface TomcatContextConfigurer {

    /**
     * Configures the given {@link Context} using the provided options and environment.
     *
     * @param context the {@link Context} to configure
     * @param options the {@link OptionSet} with configuration parameters
     * @param env     the {@link Environment} providing runtime context
     */
    default void configure(Context context, OptionSet options, Environment env) {
        configure(context, options);
    }

    /**
     * Configures the given {@link Context} using the provided options.
     *
     * @param context the {@link Context} to configure
     * @param options the {@link OptionSet} with configuration parameters
     */
    void configure(Context context, OptionSet options);
}
