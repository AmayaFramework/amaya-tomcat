package io.github.amayaframework.core.tomcat.actions;

import io.github.amayaframework.core.pipeline.PipelineAction;

import javax.servlet.http.Cookie;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>The input action during which the request cookies are parsed.</p>
 * <p>Receives: {@link ServletRequestData}</p>
 * <p>Returns: {@link ServletRequestData}</p>
 */
public class ParseRequestCookiesAction extends PipelineAction<ServletRequestData, ServletRequestData> {
    @Override
    public ServletRequestData execute(ServletRequestData data) {
        Cookie[] cookies = data.servletRequest.getCookies();
        Map<String, Cookie> toAdd = new HashMap<>();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                toAdd.put(cookie.getName(), cookie);
            }
        }
        data.getRequest().setCookies(Collections.unmodifiableMap(toAdd));
        return data;
    }
}
