package io.github.amayaframework.tomcat;

final class LookupUtil {
    private LookupUtil() {
    }

    // Websocket
    private static final String WEBSOCKET_MODULE = "org.apache.tomcat.embed.websocket";
    private static final String WEBSOCKET_INITIALIZER = "org.apache.tomcat.websocket.server.WsSci";

    static boolean isModuleLoaded(String name) {
        var layer = ModuleLayer.boot();
        return layer.findModule(name).isPresent();
    }

    static boolean isClassExists(String name) {
        var loader = Thread.currentThread().getContextClassLoader();
        try {
            var loaded = loader.loadClass(name);
            return loaded.getName().equals(name);
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    static boolean isLibraryLoaded(String module, String type) {
        return isModuleLoaded(module) || isClassExists(type);
    }

    static boolean isWebsocketLoaded() {
        return isLibraryLoaded(WEBSOCKET_MODULE, WEBSOCKET_INITIALIZER);
    }
}
