# Apache Commons IO

```java
@Test
public void whenAppendToFileUsingFiles_thenCorrect()
 throws IOException {
    File file = new File(fileName);
    FileUtils.writeStringToFile(
      file, "Spain\r\n", StandardCharsets.UTF_8, true);

    assertThat(StreamUtils.getStringFromInputStream(
      new FileInputStream(fileName)))
      .isEqualTo("UK\r\n" + "US\r\n" + "Germany\r\n" + "Spain\r\n");
}
```
