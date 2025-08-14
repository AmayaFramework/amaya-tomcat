package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.coyote.http2.Http2Protocol;

import java.net.InetSocketAddress;

final class Http2ConnectorFactory implements ConnectorFactory {

    @Override
    public Connector create(Executor executor, InetSocketAddress address, OptionSet options) {
        var protocol = new Http11NioProtocol();
        var ret = new Connector(protocol);
        if (executor != null) {
            ret.getProtocolHandler().setExecutor(executor);
        }
        ret.setXpoweredBy(true);
        var http2 = new Http2Protocol();
        ret.addUpgradeProtocol(http2);
        Util.configureHttp2(http2, options);
        Util.setAddress(ret, address);
        Util.configureSSL(ret, protocol, address, options);
        Util.configureHttp1(protocol, options);
        Util.configureConnector(ret, HttpVersion.HTTP_2_0, options);
        return ret;
    }
}
