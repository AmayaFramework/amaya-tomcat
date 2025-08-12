package io.github.amayaframework.tomcat;

import org.apache.catalina.Executor;
import org.apache.catalina.Service;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Map;

final class ExecutorRegistry {
    private final Service service;
    private final Map<InetSocketAddress, Executor> executors;
    private final Map<Executor, Integer> count;

    ExecutorRegistry(Service service) {
        this.service = service;
        this.executors = new HashMap<>();
        this.count = new HashMap<>();
    }

    void add(InetSocketAddress address, Executor executor) {
        executors.put(address, executor);
        var num = count.getOrDefault(executor, 0);
        if (num == 0) {
            service.addExecutor(executor);
        }
        count.put(executor, num + 1);
    }

    void remove(InetSocketAddress address) {
        var executor = executors.remove(address);
        if (executor == null) {
            return;
        }
        var num = count.get(executor);
        if (num == 1) {
            count.remove(executor);
            service.removeExecutor(executor);
        } else {
            count.put(executor, num - 1);
        }
    }

    void clear() {
        for (var executor : count.keySet()) {
            service.removeExecutor(executor);
        }
        executors.clear();
        count.clear();
    }
}
