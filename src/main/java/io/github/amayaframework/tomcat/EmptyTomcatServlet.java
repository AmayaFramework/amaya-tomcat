package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Runnable0;
import com.github.romanqed.jfunc.Runnable1;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

final class EmptyTomcatServlet extends AbstractTomcatServlet {
    EmptyTomcatServlet(Runnable1<ServletConfig> onInit, Runnable0 onDestroy) {
        super(onInit, onDestroy);
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse res) {
        // Do nothing
    }
}
