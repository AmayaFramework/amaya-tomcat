package io.github.amayaframework.tomcat;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@FunctionalInterface
interface ServletHandler {

    void handle(HttpServletRequest req, HttpServletResponse res) throws Throwable;
}
