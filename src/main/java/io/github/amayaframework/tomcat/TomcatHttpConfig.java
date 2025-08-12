package io.github.amayaframework.tomcat;

import io.github.amayaframework.http.HttpVersion;
import io.github.amayaframework.server.HttpServerConfig;
import io.github.amayaframework.server.MimeFormatter;
import io.github.amayaframework.server.MimeParser;
import io.github.amayaframework.server.PathTokenizer;
import jakarta.servlet.ServletContext;
import org.apache.catalina.Context;

import java.net.InetSocketAddress;
import java.util.Objects;
import java.util.Set;

final class TomcatHttpConfig implements HttpServerConfig {
    private static final MimeFormatter DEFAULT_FORMATTER = new TomcatMimeFormatter();
    private static final MimeParser DEFAULT_PARSER = new TomcatMimeParser();
    private static final PathTokenizer DEFAULT_TOKENIZER = new TomcatPathTokenizer();

    final AddressSet addresses;
    final Context context;
    HttpVersion version;
    PathTokenizer tokenizer;
    MimeParser parser;
    MimeFormatter formatter;

    TomcatHttpConfig(AddressSet addresses, Context context) {
        this.addresses = addresses;
        this.context = context;
        this.version = HttpVersion.HTTP_1_1;
        this.addresses.version = HttpVersion.HTTP_1_1;
        this.tokenizer = DEFAULT_TOKENIZER;
        this.parser = DEFAULT_PARSER;
        this.formatter = DEFAULT_FORMATTER;
    }

    @Override
    public ServletContext servletContext() {
        return context.getServletContext();
    }

    @Override
    public HttpVersion httpVersion() {
        return version;
    }

    @Override
    public void httpVersion(HttpVersion version) {
        Objects.requireNonNull(version);
        if (version.before(HttpVersion.HTTP_1_1)) {
            throw new IllegalArgumentException("Minimal allowed version is HTTP/1.1");
        }
        if (version.after(HttpVersion.HTTP_2_0)) {
            throw new IllegalArgumentException("Maximum supported http version is HTTP/2.0");
        }
        this.version = version;
        this.addresses.version = version;
    }

    @Override
    public void addAddress(InetSocketAddress address, HttpVersion version) {
        this.addresses.add(address, version);
    }

    @Override
    public MimeFormatter mimeFormatter() {
        return formatter;
    }

    @Override
    public void mimeFormatter(MimeFormatter formatter) {
        this.formatter = Objects.requireNonNull(formatter);
    }

    @Override
    public MimeParser mimeParser() {
        return parser;
    }

    @Override
    public void mimeParser(MimeParser parser) {
        this.parser = Objects.requireNonNull(parser);
    }

    @Override
    public PathTokenizer pathTokenizer() {
        return tokenizer;
    }

    @Override
    public void pathTokenizer(PathTokenizer tokenizer) {
        this.tokenizer = Objects.requireNonNull(tokenizer);
    }

    @Override
    public Set<InetSocketAddress> addresses() {
        return addresses;
    }

    @Override
    public void addAddress(InetSocketAddress address) {
        addresses.add(address);
    }

    @Override
    public void removeAddress(InetSocketAddress address) {
        addresses.remove(address);
    }
}
