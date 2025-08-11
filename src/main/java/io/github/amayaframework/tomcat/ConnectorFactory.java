package io.github.amayaframework.tomcat;

import io.github.amayaframework.options.OptionSet;
import org.apache.catalina.Executor;
import org.apache.catalina.connector.Connector;

import java.net.InetSocketAddress;

interface ConnectorFactory {

    Connector create(Executor executor, InetSocketAddress address, OptionSet options);
}
