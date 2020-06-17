# Jackson

Jackson 是 Java JSON API，它提供了几种使用 JSON 的方式。Jackson 是目前最流行的 Java JSON API 之一。您可以在这里找到 Jackson：https://github.com/FasterXML/jackson

Jackson 包含 2 个不同的 JSON 解析器：

- Jackson ObjectMapper，它将 JSON 解析为自定义 Java 对象或 Jackson 特定的树结构（树模型）。
- Jackson JsonParser 是 Jackson 的 JSON 提取解析器，一次解析 JSON 一个令牌。

Jackson 还包含两个 JSON 生成器：

- Jackson ObjectMapper 可以从自定义 Java 对象或 Jackson 特定的树结构（树模型）生成 JSON。
- Jackson JsonGenerator 一次可以生成一个 JSON 令牌。

# ObjectMapper

Jackson ObjectMapper 类（com.fasterxml.jackson.databind.ObjectMapper）是使用 Jackson 解析 JSON 的最简单方法。Jackson ObjectMapper 可以从字符串，流或文件中解析 JSON，并创建表示已解析的 JSON 的 Java 对象或对象图。将 JSON 解析为 Java 对象也称为从 JSON 反序列化 Java 对象。Jackson ObjectMapper 也可以从 Java 对象创建 JSON。从 Java 对象生成 JSON 也称为将 Java 对象序列化为 JSON。Jackson Object 映射器可以将 JSON 解析为您开发的类的对象，也可以解析为本教程稍后说明的内置 JSON 树模型的对象。顺便说一下，之所以称为 ObjectMapper 是因为它将 JSON 映射到 Java 对象（反序列化），或者将 Java 对象映射到 JSON（序列化）。

```java
public class Car {
    private String brand = null;
    private int doors = 0;
}

ObjectMapper objectMapper = new ObjectMapper();

String carJson =
    "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";

try {
    Car car = objectMapper.readValue(carJson, Car.class);

    System.out.println("car brand = " + car.getBrand());
    System.out.println("car doors = " + car.getDoors());
} catch (IOException e) {
    e.printStackTrace();
}
```

Car.class 是我们自定义的类，其作为第二个参数传入到 readValue 函数中。为了使用 Jackson 正确地从 JSON 读取 Java 对象，重要的是要知道 Jackson 如何将 JSON 对象的字段映射到 Java 对象的字段，因此我将解释 Jackson 是如何做到的。默认情况下，Jackson 通过将 JSON 字段的名称与 Java 对象中的 getter 和 setter 方法进行匹配，将 JSON 对象的字段映射到 Java 对象中的字段。Jackson 删除了 getter 和 setter 方法名称的“ get”和“ set”部分，并将其余名称的第一个字符转换为小写。

例如，名为 brand 的 JSON 字段与名为 getBrand() 和 setBrand() 的 Java getter 和 setter 方法匹配。名为 engineNumber 的 JSON 字段将与名为 getEngineNumber() 和 setEngineNumber() 的 getter 和 setter 匹配。如果需要以其他方式将 JSON 对象字段与 Java 对象字段匹配，则需要使用自定义序列化器和反序列化器，或者使用许多 Jackson 注释中的一些。

## APIs

### 读取为对象

```java
ObjectMapper objectMapper = new ObjectMapper();
String carJson =
    "{ \"brand\" : \"Mercedes\", \"doors\" : 5 }";

// 来自于 JSON 字符串
Car car = objectMapper.readValue(carJson, Car.class);

// 来自于 JSON 数组
String jsonArray = "[{\"brand\":\"ford\"}, {\"brand\":\"Fiat\"}]";

Car[] cars2 = objectMapper.readValue(jsonArray, Car[].class);
List<Car> cars1 = objectMapper.readValue(jsonArray, new TypeReference<List<Car>>(){});

// 从 JSON Byte Array 读取
byte[] bytes = carJson.getBytes("UTF-8");
Car car = objectMapper.readValue(bytes, Car.class);

// 读取为 Map
String jsonObject = "{\"brand\":\"ford\", \"doors\":5}";
Map<String, Object> jsonMap = objectMapper.readValue(jsonObject,
    new TypeReference<Map<String,Object>>(){});

// 来自于 StringReader
Reader reader = new StringReader(carJson);
Car car = objectMapper.readValue(reader, Car.class);

// 来自于 JSON 文件
File file = new File("data/car.json");
Car car = objectMapper.readValue(file, Car.class);

// 从 URL 读取
InputStream input = new FileInputStream("data/car.json");
Car car = objectMapper.readValue(input, Car.class);
```

我们可以设置在读取到 Null 的原始类型时抛出异常：

```java
objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, true);
String carJson = "{ \"brand\":\"Toyota\", \"doors\":null }";
Car car = objectMapper.readValue(carJson, Car.class);

Exception in thread "main" com.fasterxml.jackson.databind.exc.MismatchedInputException:
    Cannot map `null` into type int
    (set DeserializationConfig.DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES to 'false' to allow)
 at [Source: (String)
    "{ "brand":"Toyota", "doors":null }"; line: 1, column: 29] (through reference chain: jackson.Car["doors"])
```

最后，我们也可以注册自定义的解释器：

```java
public class CarDeserializer extends StdDeserializer<Car> {

    public CarDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public Car deserialize(JsonParser parser, DeserializationContext deserializer) throws IOException {
        Car car = new Car();
        while(!parser.isClosed()){
            JsonToken jsonToken = parser.nextToken();

            if(JsonToken.FIELD_NAME.equals(jsonToken)){
                String fieldName = parser.getCurrentName();
                System.out.println(fieldName);

                jsonToken = parser.nextToken();

                if("brand".equals(fieldName)){
                    car.setBrand(parser.getValueAsString());
                } else if ("doors".equals(fieldName)){
                    car.setDoors(parser.getValueAsInt());
                }
            }
        }
        return car;
    }
}

String json = "{ \"brand\" : \"Ford\", \"doors\" : 6 }";

SimpleModule module =
        new SimpleModule("CarDeserializer", new Version(3, 1, 8, null, null, null));
module.addDeserializer(Car.class, new CarDeserializer(Car.class));

ObjectMapper mapper = new ObjectMapper();
mapper.registerModule(module);

Car car = mapper.readValue(json, Car.class);
```

### 序列化为字符串

```java
ObjectMapper objectMapper = new ObjectMapper();

Car car = new Car();
car.brand = "BMW";
car.doors = 4;

objectMapper.writeValue(
    new FileOutputStream("data/output-2.json"), car);

ObjectMapper objectMapper = new ObjectMapper();

Car car = new Car();
car.brand = "BMW";
car.doors = 4;

String json = objectMapper.writeValueAsString(car);
System.out.println(json);
```

## Annotations

Jackson 包含一组 Java 批注，您可以使用这些批注来修改 Jackson 到 Java 对象之间读写 JSON 的方式。

Jackson 批注 @JsonIgnore 用于告诉 Jackson 忽略 Java 对象的某个属性（字段）。在将 JSON 读取到 Java 对象中以及将 Java 对象写入 JSON 时，都将忽略该属性。这是使用 @JsonIgnore 批注的示例类：

```java
import com.fasterxml.jackson.annotation.JsonIgnore;

public class PersonIgnore {

    @JsonIgnore
    public long    personId = 0;

    public String  name = null;
}
```

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
