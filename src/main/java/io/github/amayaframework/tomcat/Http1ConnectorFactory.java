package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11NioProtocol;

import java.net.InetSocketAddress;

final class Http1ConnectorFactory implements ConnectorFactory {

    @Override
    public Connector create(Executor executor, InetSocketAddress address, OptionSet options) {
        var ret = new Connector(new Http11NioProtocol());
        if (executor != null) {
            ret.getProtocolHandler().setExecutor(executor);
        }
        ret.setXpoweredBy(true);
        Util.setAddress(ret, address);
        Util.configureSSL(ret, address, options);
        Util.configureConnector(ret, HttpVersion.HTTP_1_1, options);
        return ret;
    }
}
