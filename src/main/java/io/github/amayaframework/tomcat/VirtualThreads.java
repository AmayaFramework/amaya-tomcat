package io.github.amayaframework.tomcat;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

final class VirtualThreads {
    private VirtualThreads() {
    }

    public static ExecutorService getNamedVirtualThreadsExecutor(String namePrefix) {
        try {
            var builderClass = Class.forName("java.lang.Thread$Builder");
            var threadBuilder = Thread.class.getMethod("ofVirtual").invoke(null);
            if (namePrefix != null && !namePrefix.isBlank()) {
                threadBuilder = builderClass
                        .getMethod("name", String.class, long.class)
                        .invoke(threadBuilder, namePrefix, 0L);
            }
            var factory = (ThreadFactory) builderClass.getMethod("factory").invoke(threadBuilder);
            return (ExecutorService) Executors.class
                    .getMethod("newThreadPerTaskExecutor", ThreadFactory.class)
                    .invoke(null, factory);
        } catch (Throwable x) {
            return null;
        }
    }
}
