package io.github.amayaframework.core.tomcat;

import io.github.amayaframework.core.Amaya;
import io.github.amayaframework.core.util.IOUtil;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class TomcatAmaya implements Amaya<Tomcat> {
    private static final Logger LOGGER = LoggerFactory.getLogger(TomcatAmaya.class);
    private final Tomcat server;

    protected TomcatAmaya(Tomcat server) {
        this.server = server;
    }

    private static void printHelloMessage() throws IOException {
        LOGGER.info("Amaya started successfully");
        LOGGER.info("\n" + IOUtil.readLogo());
        LOGGER.info("We are glad to welcome you, senpai!");
        LOGGER.info("\n" + IOUtil.readArt());
    }

    @Override
    public String getAddress() {
        return server.getServer().getAddress();
    }

    @Override
    public int getPort() {
        return server.getServer().getPort();
    }

    @Override
    public Tomcat getServer() {
        return server;
    }

    @Override
    public void start() throws IOException, LifecycleException {
        printHelloMessage();
        server.start();
        server.getServer().await();
    }

    @Override
    public void close() throws Exception {
        server.stop();
        LOGGER.info("Amaya server stopped");
    }
}
