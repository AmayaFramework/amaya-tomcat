/**
 * Provides integration between the Amaya Framework and the embedded
 * Apache Tomcat server.
 *
 * <p>This module contains:
 * <ul>
 *     <li>Option keys and configuration helpers for Tomcat-based servers</li>
 *     <li>Utilities for SSL, connector, and executor configuration</li>
 *     <li>Support for optional WebSocket integration via the Tomcat WebSocket module</li>
 *     <li>Session ID generators with pluggable random number sources</li>
 * </ul>
 *
 * <p>Required dependencies include:
 * <ul>
 *     <li>Apache Tomcat embed core API</li>
 *     <li>Jakarta Servlet API</li>
 *     <li>Amaya Framework core modules (options, server, service)</li>
 * </ul>
 *
 * <p>Optional dependencies include:
 * <ul>
 *     <li>Amaya environment module</li>
 *     <li>Tomcat embedded WebSocket support</li>
 * </ul>
 */
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
    // Optional websockets
    requires static org.apache.tomcat.embed.websocket;
    // Amaya dependencies
    requires amayaframework.options;
    requires amayaframework.server;
    requires amayaframework.service;
    requires static amayaframework.environment;
    // Exports
    exports io.github.amayaframework.tomcat;
}
