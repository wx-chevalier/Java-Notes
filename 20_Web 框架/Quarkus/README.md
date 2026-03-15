# Quarkus 开发指南

## 一、Quarkus 简介

Quarkus 是一个为 Java 虚拟机（JVM）和原生编译设计的全堆栈 Kubernetes 原生 Java 框架。

### 主要特点

- 超快的启动时间
- 低内存占用
- 实时编码
- 统一的命令式和响应式编程
- 原生支持 GraalVM

## 二、环境搭建

### 1. 前置要求

```bash
# JDK 11+
java -version

# Maven 3.8.1+
mvn -version

# GraalVM (可选，用于原生编译)
gu install native-image
```

### 2. 创建项目

```bash
# 使用 Maven 创建
mvn io.quarkus:quarkus-maven-plugin:create \
    -DprojectGroupId=org.example \
    -DprojectArtifactId=quarkus-demo \
    -DclassName="org.example.GreetingResource" \
    -Dpath="/hello"

# 或使用 Quarkus CLI
quarkus create app org.example:quarkus-demo
```

### 3. 项目结构

```
quarkus-demo/
├── src/
│   ├── main/
│   │   ├── java/
│   │   ├── resources/
│   │   └── docker/
│   └── test/
├── pom.xml
└── README.md
```

## 三、基础开发

### 1. REST 接口开发

```java
@Path("/hello")
public class GreetingResource {

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello RESTEasy";
    }

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response createGreeting(Greeting greeting) {
        // 处理逻辑
        return Response.ok(greeting).build();
    }
}
```

### 2. 配置管理

```properties
# application.properties
greeting.message=Hello
quarkus.http.port=8080
```

```java
@ConfigProperty(name = "greeting.message")
String message;
```

### 3. 依赖注入

```java
@ApplicationScoped
public class GreetingService {
    public String greet(String name) {
        return "Hello " + name;
    }
}

@Path("/hello")
public class GreetingResource {
    @Inject
    GreetingService greetingService;

    @GET
    @Path("/{name}")
    public String hello(@PathParam String name) {
        return greetingService.greet(name);
    }
}
```

## 四、数据库访问

### 1. Panache (简化版 JPA)

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-hibernate-orm-panache</artifactId>
</dependency>
```

```java
@Entity
public class Person extends PanacheEntity {
    public String name;
    public LocalDate birth;
    public Status status;

    public static Person findByName(String name){
        return find("name", name).firstResult();
    }
}
```

### 2. 事务管理

```java
@ApplicationScoped
public class PersonService {

    @Transactional
    public void createPerson(Person person) {
        person.persist();
    }

    @Transactional
    public List<Person> getActivePersons() {
        return Person.list("status", Status.ACTIVE);
    }
}
```

## 五、测试

### 1. 单元测试

```java
@QuarkusTest
public class GreetingResourceTest {

    @Test
    public void testHelloEndpoint() {
        given()
          .when().get("/hello")
          .then()
             .statusCode(200)
             .body(is("Hello RESTEasy"));
    }
}
```

### 2. 集成测试

```java
@QuarkusTest
public class PersonResourceIT {

    @Test
    @TestTransaction
    public void testCreatePerson() {
        Person person = new Person();
        person.name = "Test";

        given()
            .contentType(ContentType.JSON)
            .body(person)
            .when().post("/persons")
            .then()
                .statusCode(201);
    }
}
```

## 六、部署

### 1. JVM 模式

```bash
# 打包
./mvnw package

# 运行
java -jar target/quarkus-app/quarkus-run.jar
```

### 2. 原生模式

```bash
# 原生编译
./mvnw package -Pnative

# 运行
./target/quarkus-demo-1.0.0-SNAPSHOT-runner
```

### 3. Docker 部署

```dockerfile
# Dockerfile.jvm
FROM registry.access.redhat.com/ubi8/openjdk-11:latest

ENV LANGUAGE='en_US:en'

COPY target/quarkus-app/lib/ /deployments/lib/
COPY target/quarkus-app/*.jar /deployments/
COPY target/quarkus-app/app/ /deployments/app/
COPY target/quarkus-app/quarkus/ /deployments/quarkus/

EXPOSE 8080
USER 185

ENTRYPOINT [ "java", "-jar", "/deployments/quarkus-run.jar" ]
```

## 七、高级特性

### 1. 响应式编程

```xml
<dependency>
    <groupId>io.quarkus</groupId>
    <artifactId>quarkus-resteasy-reactive</artifactId>
</dependency>
```

```java
@Path("/reactive")
public class ReactiveResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Multi<String> stream() {
        return Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .map(tick -> "Tick " + tick);
    }
}
```

### 2. 健康检查

```java
@Health
public class CustomHealthCheck implements HealthCheck {

    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up("Custom health check");
    }
}
```

### 3. 指标监控

```java
@Path("/counter")
public class CounterResource {

    @Inject
    Counter counter;

    @GET
    public Long get() {
        return counter.increment();
    }
}
```

## 八、最佳实践

1. **项目结构**

   - 按功能模块划分包
   - 使用领域驱动设计原则

2. **配置管理**

   - 使用配置文件分离环境
   - 敏感信息使用密钥管理

3. **性能优化**

   - 合理使用连接池
   - 启用响应式编程
   - 使用缓存

4. **安全性**

   - 启用 CORS
   - 实现认证授权
   - 输入验证

5. **监控**
   - 集成日志系统
   - 配置健康检查
   - 添加性能指标

## 九、常见问题

1. **启动问题**

   - 检查端口占用
   - 验证配置文件
   - 查看依赖冲突

2. **性能问题**

   - 优化数据库查询
   - 使用缓存
   - 开启响应式

3. **内存问题**
   - 调整 JVM 参数
   - 检查内存泄漏
   - 优化对象创建

这个教程涵盖了 Quarkus 的主要特性和使用方法。建议：

- 循序渐进地学习
- 多动手实践
- 参考官方文档
- 关注社区动态
