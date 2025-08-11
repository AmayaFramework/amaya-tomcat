package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.HttpVersion;

final class TomcatProtocols {
    final static ConnectorFactory HTTP1_CONNECTOR_FACTORY = new Http1ConnectorFactory();
    final static ConnectorFactory HTTP2_CONNECTOR_FACTORY = new Http2ConnectorFactory();

    static ConnectorFactory getConnectorFactory(HttpVersion version) {
        if (version.before(HttpVersion.HTTP_2_0)) {
            return HTTP1_CONNECTOR_FACTORY;
        }
        return HTTP2_CONNECTOR_FACTORY;
    }
}
