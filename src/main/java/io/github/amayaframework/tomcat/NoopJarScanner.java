package io.github.amayaframework.tomcat;

import jakarta.servlet.ServletContext;
import org.apache.tomcat.JarScanFilter;
import org.apache.tomcat.JarScanType;
import org.apache.tomcat.JarScanner;
import org.apache.tomcat.JarScannerCallback;

public final class NoopJarScanner implements JarScanner {
    private JarScanFilter filter;

    @Override
    public void scan(JarScanType scanType, ServletContext context, JarScannerCallback callback) {
        // Do nothing
    }

    @Override
    public JarScanFilter getJarScanFilter() {
        return filter;
    }

    @Override
    public void setJarScanFilter(JarScanFilter filter) {
        this.filter = filter;
    }
}
