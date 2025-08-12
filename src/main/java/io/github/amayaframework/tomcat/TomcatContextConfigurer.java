package io.github.amayaframework.tomcat;

import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Context;

public interface TomcatContextConfigurer {

    default void configure(Context context, OptionSet options, Environment env) {
        configure(context, options);
    }

    void configure(Context context, OptionSet options);
}
