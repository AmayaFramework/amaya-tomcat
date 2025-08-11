module amayaframework.tomcat {
    // Imports
    // Basic dependencies
    requires com.github.romanqed.jfunc;
    requires com.github.romanqed.jsync;
    requires com.github.romanqed.juni;
    requires com.github.romanqed.jct;
    requires com.github.romanqed.jtype;
    // Jakarta servlets
    requires jakarta.servlet;
    // Tomcat dependencies
    requires org.apache.tomcat.embed.core;
    // Amaya dependencies
    requires amayaframework.options;
    requires amayaframework.server;
    requires amayaframework.service;
    requires static amayaframework.environment;
    // Exports
    exports io.github.amayaframework.tomcat;
}
