# JSON Tree Model

Jackson 具有内置的树模型，可用于表示 JSON 对象。如果您不知道接收到的 JSON 的外观，或者由于某种原因而不能（或者只是不想）创建一个类来表示它，那么 Jackson 的树模型将非常有用。如果您需要在使用或转发 JSON 之前对其进行操作，则 Jackson 树模型也很有用。所有这些情况都可以在数据流场景中轻易发生。Jackson 树模型由 JsonNode 类表示。您可以使用 Jackson ObjectMapper 将 JSON 解析为 JsonNode 树模型，就像使用您自己的类一样。

```java
String carJson =
        "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
ObjectMapper objectMapper = new ObjectMapper();
try {
    JsonNode jsonNode = objectMapper.readValue(carJson, JsonNode.class);
} catch (IOException e) {
    e.printStackTrace();
}
```

如您所见，只需将 JsonNode.class 作为第二个参数传递给 readValue() 方法，而不是本教程前面的示例中使用的 Car.class，就可以将 JSON 字符串解析为 JsonNode 对象而不是 Car 对象。。

ObjectMapper 类还具有一个特殊的 readTree() 方法，该方法始终返回 JsonNode。这是使用 ObjectMapper readTree() 方法将 JSON 解析为 JsonNode 的示例：

```java
String carJson =
        "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
ObjectMapper objectMapper = new ObjectMapper();
try {
    JsonNode jsonNode = objectMapper.readTree(carJson);
} catch (IOException e) {
    e.printStackTrace();
}
```

通过 JsonNode 类，您可以以非常灵活和动态的方式将 JSON 作为 Java 对象进行导航。如前所述，JsonNode 类在其自己的教程中进行了更详细的介绍，但是我仅在此处向您展示如何使用它的基础知识。

将 JSON 解析为 JsonNode（或 JsonNode 实例树）后，就可以浏览 JsonNode 树模型。这是一个 JsonNode 示例，显示了如何访问 JSON 字段，数组和嵌套对象：

```java
String carJson =
        "{ \"brand\" : \"Mercedes\", \"doors\" : 5," +
        "  \"owners\" : [\"John\", \"Jack\", \"Jill\"]," +
        "  \"nestedObject\" : { \"field\" : \"value\" } }";

ObjectMapper objectMapper = new ObjectMapper();


try {

    JsonNode jsonNode = objectMapper.readValue(carJson, JsonNode.class);

    JsonNode brandNode = jsonNode.get("brand");
    String brand = brandNode.asText();
    System.out.println("brand = " + brand);

    JsonNode doorsNode = jsonNode.get("doors");
    int doors = doorsNode.asInt();
    System.out.println("doors = " + doors);

    JsonNode array = jsonNode.get("owners");
    JsonNode jsonNode = array.get(0);
    String john = jsonNode.asText();
    System.out.println("john  = " + john);

    JsonNode child = jsonNode.get("nestedObject");
    JsonNode childField = child.get("field");
    String field = childField.asText();
    System.out.println("field = " + field);

} catch (IOException e) {
    e.printStackTrace();
}
```

请注意，JSON 字符串现在包含一个称为所有者的数组字段和一个称为 nestedObject 的嵌套对象字段。无论您访问的是字段，数组还是嵌套对象，都可以使用 JsonNode 类的 get() 方法。通过将字符串作为参数提供给 get() 方法，您可以访问 JsonNode 的字段。如果 JsonNode 表示数组，则需要将索引传递给 get() 方法。索引指定要获取的数组元素。

可以使用 Jackson ObjectMapper 将 Java 对象转换为 JsonNode，而 JsonNode 是转换后的 Java 对象的 JSON 表示形式。您可以通过 Jackson ObjectMapper valueToTree() 方法将 Java 对象转换为 JsonNode。这是一个使用 ObjectMapper valueToTree() 方法将 Java 对象转换为 JsonNode 的示例：

```java
ObjectMapper objectMapper = new ObjectMapper();
Car car = new Car();
car.brand = "Cadillac";
car.doors = 4;
JsonNode carJsonNode = objectMapper.valueToTree(car);

ObjectMapper objectMapper = new ObjectMapper();
String carJson = "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";
JsonNode carJsonNode = objectMapper.readTree(carJson);
Car car = objectMapper.treeToValue(carJsonNode);
```
