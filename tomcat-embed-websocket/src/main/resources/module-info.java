open module org.apache.tomcat.embed.websocket {
    requires transitive java.base;
    requires transitive jakarta.websocket;
    requires java.naming;
    requires org.apache.tomcat.embed.core;

    exports org.apache.tomcat.websocket;
    exports org.apache.tomcat.websocket.server;

    uses jakarta.websocket.ContainerProvider;
    uses jakarta.websocket.server.ServerEndpointConfig.Configurator;

    provides jakarta.websocket.ContainerProvider with org.apache.tomcat.websocket.WsContainerProvider;
    provides jakarta.websocket.server.ServerEndpointConfig.Configurator with org.apache.tomcat.websocket.server.DefaultServerEndpointConfigurator;
}
