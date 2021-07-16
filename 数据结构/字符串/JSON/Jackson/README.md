# Jackson

Jackson 是 Java JSON API，它提供了几种使用 JSON 的方式。Jackson 是目前最流行的 Java JSON API 之一。您可以在这里找到 Jackson：https://github.com/FasterXML/jackson

Jackson 包含 2 个不同的 JSON 解析器：

- Jackson ObjectMapper，它将 JSON 解析为自定义 Java 对象或 Jackson 特定的树结构（树模型）。
- Jackson JsonParser 是 Jackson 的 JSON 提取解析器，一次解析 JSON 一个令牌。

Jackson 还包含两个 JSON 生成器：

- Jackson ObjectMapper 可以从自定义 Java 对象或 Jackson 特定的树结构（树模型）生成 JSON。
- Jackson JsonGenerator 一次可以生成一个 JSON 令牌。
