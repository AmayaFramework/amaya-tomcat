# amaya-tomcat [![maven-central](https://img.shields.io/maven-central/v/io.github.amayaframework/amaya-tomcat?color=blue)](https://repo1.maven.org/maven2/io/github/amayaframework/amaya-tomcat/)

Amaya is a fairly lightweight web framework for Java, which guarantees speed, ease of creating plugins/addons, 
flexibility and ease of use.

The main goals pursued during the creation were the avoidance of redundant abstractions, 
modularity, simplification of the creation of REST services and sufficient freedom for user configuration 
and modification. The server part is built on the basis of a tomcat embedded server.

## Getting Started

The core of the Amaya framework, which is an independent dependency. I.e. it is enough to install only it to
create a simple server. The kernel is built in such a way as not to force users to install unnecessary dependencies.
In case you need any missing functionality, it will be enough to deliver the necessary dependency. The only possible
inconvenience is the need to specify classindex in dependencies, but this is the fee for the launch speed:
other similar libraries are incomparably slow.

To install it, you will need:
* java 8+
* [classindex](https://github.com/atteo/classindex)
* some implementation of slf4j
* [tomcat](https://tomcat.apache.org)
* Maven/Gradle

## Installing

### Gradle dependency

```Groovy
dependencies {
    annotationProcessor group: 'org.atteo.classindex', name: 'classindex', version: '3.11'
    implementation group: 'org.apache.tomcat.embed', name: 'tomcat-embed-core', version: '8.5.78'
    implementation group: 'io.github.amayaframework', name: 'amaya-core', version: '1+'
    implementation group: 'io.github.amayaframework', name: 'amaya-tomcat', version: 'LATEST'
}
```

### Maven dependency

```
<dependency>
    <groupId>org.apache.tomcat.embed</groupId>
    <artifactId>tomcat-embed-core</artifactId>
    <version>8.5.78</version>
</dependency>

<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-core</artifactId>
    <version>1+</version>
</dependency>

<dependency>
    <groupId>io.github.amayaframework</groupId>
    <artifactId>amaya-tomcat</artifactId>
    <version>LATEST</version>
</dependency>
```

## Usage example
### Build manifest
<p>To support getting method parameter names, add to your build.gradle the following</p>

```Groovy
compileJava {
    options.compilerArgs << '-parameters'
}
```

### Server class

```Java
public class Server {
    public static void main(String[] args) throws Throwable {
        Amaya<Tomcat> amaya = new TomcatBuilder().
                bind(8080).
                build();
        amaya.start();
    }
}
```

### MyController class
```Java
@Endpoint
public class ExampleController {
    @Get("/hello/{count:int}")
    public HttpResponse get(HttpRequest request, @Path Integer count) {
        String helloWorld = "Hello, world!";
        StringBuilder response = new StringBuilder();
        for (int i = 0; i < count; ++i) {
            response.append(helloWorld).append('\n');
        }
        return Responses.ok(response);
    }
}
```

Let's look at the controller code in more detail.
* The Endpoint annotation instructs the framework to add a controller to the server at the specified path.
* Inheritance from AbstractController ensures that the contents of your controller will be properly processed.
* The Get annotation indicates to the framework that you want to register the corresponding method located along 
the passed sub-path relative to the controller path.
* The Path annotation shows the framework that you want to put the value of the path variable "count" 
in the specified argument.

There is nothing complicated or requiring a long study in this construction. However, the user is 
provided with a convenient tool that allows you to quickly and easily create an api without thinking 
about unnecessary things.

To learn more about the core capabilities, check [this](https://github.com/AmayaFramework/amaya-core)

## Built With

* [Gradle](https://gradle.org) - Dependency management
* [classindex](https://github.com/atteo/classindex) - Annotation scanning
* [slf4j](https://www.slf4j.org) - Logging facade
* [javax.servlet](https://docs.oracle.com/javaee/7/api/javax/servlet/Servlet.html) - Servlets
* [amaya-core](https://github.com/AmayaFramework/amaya-core) - Basic framework utilities
* [tomcat](https://tomcat.apache.org) - Server part

## Authors
* **RomanQed** - *Main work* - [RomanQed](https://github.com/RomanQed)
* **max0000402** - *Technical advices and ideas for features* - [max0000402](https://github.com/max0000402)

See also the list of [contributors](https://github.com/AmayaFramework/amaya-tomcat/contributors) 
who participated in this project.

## License

This project is licensed under the Apache License Version 2.0 - see the [LICENSE](LICENSE) file for details

## Acknowledgments

<p>Thanks to all the tomcat developers who participated in the creation of this server.</p>
<p>Thanks to max0000402 for good advices and great suggestions.</p>
<p>Thanks to IntelliJ IDEA creators - this is a really great IDE that saved me a couple of hundred hours.</p>
