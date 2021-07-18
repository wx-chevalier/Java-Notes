# Jackson

- [jackson-databind 文档](https://github.com/FasterXML/jackson-databind)

Jackson 可以轻松的将 Java 对象转换成 json 对象和 xml 文档，同样也可以将 json、xml 转换成 Java 对象。在项目中如果要引入 Jackson，可以直接利用 Maven 或者 Gradle 引入：

```xml
<properties>
  ...
  <!-- Use the latest version whenever possible. -->
  <jackson.version>2.7.0</jackson.version>
  ...
</properties>

<dependencies>
  ...
  <dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>${jackson.version}</version>
  </dependency>
  ...
</dependencies>
```

注意，databind 项目已经自动依赖了 jackson-core 与 jackson-annotation，不需要额外重复引入。
