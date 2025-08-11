package io.github.amayaframework.tomcat;

import io.github.amayaframework.environment.Environment;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Context;
import org.apache.catalina.startup.Tomcat;

public interface TomcatContextFactory {

    default Context create(Tomcat tomcat, OptionSet options, Environment env) {
        return create(tomcat, options);
    }

    Context create(Tomcat tomcat, OptionSet options);
}
