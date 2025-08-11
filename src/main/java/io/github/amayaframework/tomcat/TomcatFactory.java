package io.github.amayaframework.tomcat;

import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.startup.Tomcat;

public interface TomcatFactory {

    default Tomcat create(OptionSet options, Environment environment) {
        return create(options);
    }

    Tomcat create(OptionSet options);
}
