package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.AbstractHttp11Protocol;
import org.apache.tomcat.util.net.SSLHostConfig;

import java.net.InetSocketAddress;

final class Util {
    private Util() {
    }

    static final String HOST_PROPERTY = "address";

    static void setAddress(Connector connector, InetSocketAddress address) {
        connector.setPort(address.getPort());
        connector.setProperty(HOST_PROPERTY, address.getHostString());
    }

    static void configureSSL(Connector connector,
                             AbstractHttp11Protocol<?> protocol,
                             InetSocketAddress address,
                             OptionSet options) {
        var config = options.<SSLHostConfig>get(TomcatOptions.sslStringKey(address));
        if (config == null) {
            return;
        }
        protocol.setSSLEnabled(true);
        protocol.setSecure(true);
        connector.setSecure(true);
        connector.setScheme("https");
        connector.addSslHostConfig(config);
    }

    static void configureConnector(Connector connector, HttpVersion version, OptionSet options) {
        var configurer = options.get(TomcatOptions.CONNECTOR_CONFIGURER);
        if (configurer != null) {
            configurer.accept(version, connector);
        }
    }
}
