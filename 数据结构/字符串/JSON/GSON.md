# Gson

GSON 是 Google 的 JSON 解析器和 Java 生成器。 Google 开发了 GSON 供内部使用，但后来将其开源。 GSON 相当容易使用，但我认为它不如 Jackson 优雅。 在本 GSON 教程中，我将指导您如何使用 GSON 将 JSON 解析为 Java 对象，以及将 Java 对象序列化为 JSON。GSON 包含多个可用于 JSON 的 API。 本教程介绍了 Gson 组件，该组件将 JSON 解析为 Java 对象，或从 Java 对象生成 JSON。 除 Gson 组件外，GSON 在 GSON JsonReader 组件中还具有拉式解析器。

```java
Gson gson = new Gson();

GsonBuilder builder = new GsonBuilder();
Gson gson = builder.create();

String json = "{\"brand\":\"Jeep\", \"doors\": 3}";
Gson gson = new Gson();
Car car = gson.fromJson(json, Car.class);

Car car = new Car();
car.brand = "Rover";
car.doors = 5;
Gson gson = new Gson();
String json = gson.toJson(car);

// 美化输出
Gson gson = new GsonBuilder().setPrettyPrinting().create();
```

# 类注解

您可以告诉 GSON 从序列中排除 Java 类中的字段。 有几种告诉 GSON 排除字段的方法。 GSON 教程的以下部分将介绍最有用和最容易使用的排除字段的方法。

## Transient Fields

如果您将 Java 类中的字段设为瞬态，则 GSON 将在序列化和反序列化中均将其忽略：

```java
public class Car {
    public transient String brand = null;
    public int    doors = 0;
}
```

# JsonReader

GSON JsonReader 是 GSON 流 JSON 解析器。 GSON JsonReader 使您可以读取 JSON 字符串或文件作为 JSON 令牌流。为令牌迭代 JSON 令牌也称为通过 JSON 令牌进行流传输。 这就是为什么 GSON JsonReader 有时也称为流 JSON 解析器的原因。

流解析器通常有两种版本：拉式（Pull）解析器和推式（Push）解析器。拉式解析器是一种解析器，当代码准备处理下一个标记时，使用它的代码会将标记从解析器中拉出。推送解析器解析 JSON 令牌并将其推送到事件处理程序中。GSON JsonReader 是拉式解析器。

```java
String json = "{\"brand\" : \"Toyota\", \"doors\" : 5}";

JsonReader jsonReader = new JsonReader(new StringReader(json));

String json = "{\"brand\" : \"Toyota\", \"doors\" : 5}";

JsonReader jsonReader = new JsonReader(new StringReader(json));

try {
    while(jsonReader.hasNext()){
        JsonToken nextToken = jsonReader.peek();
        System.out.println(nextToken);

        if(JsonToken.BEGIN_OBJECT.equals(nextToken)){

            jsonReader.beginObject();

        } else if(JsonToken.NAME.equals(nextToken)){

            String name  =  jsonReader.nextName();
            System.out.println(name);

        } else if(JsonToken.STRING.equals(nextToken)){

            String value =  jsonReader.nextString();
            System.out.println(value);

        } else if(JsonToken.NUMBER.equals(nextToken)){

            long value =  jsonReader.nextLong();
            System.out.println(value);

        }
    }
} catch (IOException e) {
    e.printStackTrace();
}
```
