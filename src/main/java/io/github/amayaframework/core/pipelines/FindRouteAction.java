package io.github.amayaframework.core.pipelines;

import com.github.romanqed.jutils.http.HttpCode;
import io.github.amayaframework.core.routers.MethodRouter;
import io.github.amayaframework.core.routes.MethodRoute;
import io.github.amayaframework.core.util.ParseUtil;

import java.util.Objects;

/**
 * <p>An input action during which the requested method will be checked and the requested route will be found.</p>
 * <p>Receives: {@link ServletRequestData}</p>
 * <p>Returns: {@link ServletRequestData}</p>
 */
public class FindRouteAction extends PipelineAction<ServletRequestData, ServletRequestData> {
    private final MethodRouter router;
    private final int length;

    public FindRouteAction(MethodRouter router, String path) {
        this.router = Objects.requireNonNull(router);
        this.length = Objects.requireNonNull(path).length();
    }

    @Override
    public ServletRequestData execute(ServletRequestData requestData) {
        String path = requestData.servletRequest.getRequestURI().substring(length);
        path = ParseUtil.normalizePath(path);
        MethodRoute route = router.follow(requestData.getMethod(), path);
        if (route == null) {
            reject(HttpCode.NOT_FOUND);
        }
        requestData.setRoute(route);
        requestData.setPath(path);
        return requestData;
    }
}
