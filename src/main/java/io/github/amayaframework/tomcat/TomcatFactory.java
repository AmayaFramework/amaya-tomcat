package io.github.amayaframework.tomcat;

import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.startup.Tomcat;

/**
 * Factory interface for creating configured {@link Tomcat} instances.
 */
public interface TomcatFactory {

    /**
     * Creates a new {@link Tomcat} instance using the given options and environment.
     *
     * @param options     the {@link OptionSet} with Tomcat configuration
     * @param environment the {@link Environment} providing runtime context
     * @return the created Tomcat instance
     */
    default Tomcat create(OptionSet options, Environment environment) {
        return create(options);
    }

    /**
     * Creates a new {@link Tomcat} instance using the given options.
     *
     * @param options the {@link OptionSet} with Tomcat configuration
     * @return the created Tomcat instance
     */
    Tomcat create(OptionSet options);
}
