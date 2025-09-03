package io.github.amayaframework.tomcat;

import com.github.romanqed.jfunc.Exceptions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

final class VirtualThreads {
    private static final Class<?> THREAD_BUILDER = Exceptions.suppress(
            () -> Class.forName("java.lang.Thread$Builder"),
            e -> null
    );

    private VirtualThreads() {
    }

    static boolean areSupported() {
        return THREAD_BUILDER != null;
    }

    static ExecutorService getNamedVirtualThreadsExecutor(String namePrefix) {
        if (THREAD_BUILDER == null) {
            return null;
        }
        try {
            var threadBuilder = Thread.class.getMethod("ofVirtual").invoke(null);
            if (namePrefix != null && !namePrefix.isBlank()) {
                threadBuilder = THREAD_BUILDER
                        .getMethod("name", String.class, long.class)
                        .invoke(threadBuilder, namePrefix, 0L);
            }
            var factory = (ThreadFactory) THREAD_BUILDER.getMethod("factory").invoke(threadBuilder);
            return (ExecutorService) Executors.class
                    .getMethod("newThreadPerTaskExecutor", ThreadFactory.class)
                    .invoke(null, factory);
        } catch (Throwable x) {
            return null;
        }
    }
}
