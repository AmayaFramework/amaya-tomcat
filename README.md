# amaya-tomcat [![amaya-tomcat](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-tomcat?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-tomcat)
The amaya-server implementation is based on Apache Tomcat.

## Getting Started

To install it, you will need:

* Java 11+
* Maven/Gradle

### Features

* Full implementation of amaya-server
* Convenient interfaces for http request handling
* Full access to tomcat configuration
* Ready-to-use default configuration

## Installing

### Gradle dependency

```Groovy
dependencies {
    implementation group: 'io.github.amayaframework', name: 'amaya-tomcat', version: '3.3.1-10.1.45'
    // For websocket support (optionally)
    implementation group: 'io.github.amayaframework', name: 'tomcat-embed-websocket', version: '10.1.45'
}
```

### Maven dependency

```
<dependencies>
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>amaya-tomcat</artifactId>
        <version>3.3.1-10.1.45</version>
    </dependency>
    
    <!-- For Websocket support (optionally) -->
    <dependency>
        <groupId>io.github.amayaframework</groupId>
        <artifactId>tomcat-embed-websocket</artifactId>
        <version>10.1.45</version>
    </dependency>
```

## Examples

### Hello world

```Java
import com.github.romanqed.juni.UniRunnable1;
import io.github.amayaframework.tomcat.TomcatServerFactory;

public final class Main {
    public static void main(String[] args) throws Throwable {
        var factory = new TomcatServerFactory();
        var server = factory.create();
        server.bind(8080);
        server.handler(UniRunnable1.of(ctx -> {
            ctx.response().writer().println("Hello from Amaya (Tomcat 10)");
        }));
        server.start();
    }
}
```

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [Tomcat 10](https://tomcat.apache.org/tomcat-10.1-doc/index.html) - Http server implementation
* [jfunc](https://github.com/RomanQed/jfunc) - Basic functional interfaces
* [amaya-context](https://github.com/AmayaFramework/amaya-core) - Universal http context api
* [amaya-server](https://github.com/AmayaFramework/amaya-core) - Universal server api

## Authors

* **[RomanQed](https://github.com/RomanQed)** - *Main work*

See also the list of [contributors](https://github.com/AmayaFramework/amaya-jetty/contributors)
who participated in this project.

## License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details
