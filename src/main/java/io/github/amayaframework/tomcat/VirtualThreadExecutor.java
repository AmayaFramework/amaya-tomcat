package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Exceptions;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.core.StandardVirtualThreadExecutor;

import java.lang.reflect.Field;

final class VirtualThreadExecutor extends StandardVirtualThreadExecutor {
    private static final Field EXECUTOR = Exceptions.silent(() -> {
        var ret = StandardVirtualThreadExecutor.class.getDeclaredField("executor");
        ret.setAccessible(true);
        return ret;
    });

    @Override
    protected void startInternal() throws LifecycleException {
        var executor = VirtualThreads.getNamedVirtualThreadsExecutor(getNamePrefix());
        try {
            EXECUTOR.set(this, executor);
        } catch (IllegalAccessException e) {
            Exceptions.throwAny(e);
        }
        setState(LifecycleState.STARTING);
    }
}
