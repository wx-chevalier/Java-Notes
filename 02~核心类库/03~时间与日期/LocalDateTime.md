# LocalDateTime

## LocalDate

```java
// 取当前日期：
LocalDate today = LocalDate.now();

// 根据年月日取日期，12月就是12：
LocalDate crischristmas = LocalDate.of(2017, 5, 15);

// 根据指定格式字符串取
LocalDate endOfFeb = LocalDate.parse("2017-05-15"); // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式

LocalDate.parse("2014-02-29"); // 无效日期无法通过：DateTimeParseException: Invalid date

// 通过自定义时间字符串格式获取
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);

LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24

// 获取其他时区下日期
LocalDate localDate = LocalDate.now(ZoneId.of("GMT+02:30"));

// 从 LocalDateTime 中获取实例
LocalDateTime localDateTime = LocalDateTime.now();

LocalDate localDate = localDateTime.toLocalDate();
```

LocalDate 同样需要依赖于 DateTimeFormatter 来进行格式化：

```java
LocalDate localDate = LocalDate.now();//For reference
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd LLLL yyyy");
String formattedString = localDate.format(formatter);
```

LocalDate 提供了内置方法以提取日历相关的信息，以及对于日期进行加减操作：

```java
// 取本月第1天
LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2014-12-01

// 取本月第2天
LocalDate secondDayOfThisMonth = today.withDayOfMonth(2); // 2014-12-02

// 取本月最后一天，再也不用计算是28，29，30还是31
LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth()); // 2014-12-31

// 取下一天
LocalDate firstDayOf2015 = lastDayOfThisMonth.plusDays(1); // 变成了2015-01-01

// 取2015年1月第一个周一
LocalDate firstMondayOf2015 = LocalDate.parse("2015-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015-01-05
```

## LocalTime

```java
// 获取其他时区下时间
LocalTime localTime = LocalTime.now(ZoneId.of("GMT+02:30"));

// 从 LocalDateTime 中获取实例
LocalDateTime localDateTime = LocalDateTime.now();
LocalTime localTime = localDateTime.toLocalTime();

- 12:00
- 12:01:02
- 12:01:02.345
```

## LocalDateTime

```java
// 通过时间戳创建
LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(1450073569l), TimeZone.getDefault().toZoneId());

// 通过 Date 对象创建
Date in = new Date();

LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());

// 通过解析时间字符串创建
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");

LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

- 获取年、月、日等信息

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY

Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER

long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
```

- 时间格式化展示

```java
DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
LocalDateTime dateTime = LocalDateTime.of(1986, Month.APRIL, 8, 12, 30);
String formattedDateTime = dateTime.format(formatter); // "1986-04-08 12:30"
```

```java
localDateTime.plusDays(1);

localDateTime.minusHours(2);
```
