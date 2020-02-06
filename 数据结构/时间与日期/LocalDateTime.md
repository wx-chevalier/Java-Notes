# LocalDateTime

## LocalDate

```java
// 取当前日期：
LocalDate today = LocalDate.now();

// 根据年月日取日期，12月就是12：
LocalDate crischristmas = LocalDate.of(2017, 5, 15);

// 根据指定格式字符串取
LocalDate endOfFeb = LocalDate.parse("2017-05-15"); // 严格按照ISO yyyy-MM-dd验证，02写成2都不行，当然也有一个重载方法允许自己定义格式

LocalDate.parse("2014-02-29"); // 无效日期无法通过：DateTimeParseException: Invalid date

// 通过自定义时间字符串格式获取
DateTimeFormatter germanFormatter =
    DateTimeFormatter
        .ofLocalizedDate(FormatStyle.MEDIUM)
        .withLocale(Locale.GERMAN);

LocalDate xmas = LocalDate.parse("24.12.2014", germanFormatter);
System.out.println(xmas);   // 2014-12-24

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
// 取本月第1天
LocalDate firstDayOfThisMonth = today.with(TemporalAdjusters.firstDayOfMonth()); // 2014-12-01

// 取本月第2天
LocalDate secondDayOfThisMonth = today.withDayOfMonth(2); // 2014-12-02

// 取本月最后一天，再也不用计算是28，29，30还是31
LocalDate lastDayOfThisMonth = today.with(TemporalAdjusters.lastDayOfMonth()); // 2014-12-31

// 取下一天
LocalDate firstDayOf2015 = lastDayOfThisMonth.plusDays(1); // 变成了2015-01-01

// 取2015年1月第一个周一
LocalDate firstMondayOf2015 = LocalDate.parse("2015-01-01").with(TemporalAdjusters.firstInMonth(DayOfWeek.MONDAY)); // 2015-01-05
```

## LocalTime

```java
// 获取其他时区下时间
LocalTime localTime = LocalTime.now(ZoneId.of("GMT+02:30"));

// 从 LocalDateTime 中获取实例
LocalDateTime localDateTime = LocalDateTime.now();
LocalTime localTime = localDateTime.toLocalTime();

- 12:00
- 12:01:02
- 12:01:02.345
```

## LocalDateTime

```java
// 通过时间戳创建
LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochSecond(1450073569l), TimeZone.getDefault().toZoneId());

// 通过 Date 对象创建
Date in = new Date();

LocalDateTime ldt = LocalDateTime.ofInstant(in.toInstant(), ZoneId.systemDefault());

// 通过解析时间字符串创建
DateTimeFormatter formatter =
    DateTimeFormatter
        .ofPattern("MMM dd, yyyy - HH:mm");

LocalDateTime parsed = LocalDateTime.parse("Nov 03, 2014 - 07:13", formatter);
String string = formatter.format(parsed);
System.out.println(string);     // Nov 03, 2014 - 07:13
```

- 获取年、月、日等信息

```java
LocalDateTime sylvester = LocalDateTime.of(2014, Month.DECEMBER, 31, 23, 59, 59);

DayOfWeek dayOfWeek = sylvester.getDayOfWeek();
System.out.println(dayOfWeek);      // WEDNESDAY

Month month = sylvester.getMonth();
System.out.println(month);          // DECEMBER

long minuteOfDay = sylvester.getLong(ChronoField.MINUTE_OF_DAY);
System.out.println(minuteOfDay);    // 1439
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

# 时区转换

Timezones 以 `ZoneId` 来区分。可以通过静态构造方法很容易的创建，Timezones 定义了 Instants 与 Local Dates 之间的转化关系：

```java
System.out.println(ZoneId.getAvailableZoneIds());
// prints all available timezone ids

ZoneId zone1 = ZoneId.of("Europe/Berlin");
ZoneId zone2 = ZoneId.of("Brazil/East");
System.out.println(zone1.getRules());
System.out.println(zone2.getRules());

// ZoneRules[currentStandardOffset=+01:00]
// ZoneRules[currentStandardOffset=-03:00]
```

```java
LocalDateTime ldt = ...
ZonedDateTime zdt = ldt.atZone(ZoneId.systemDefault());
Date output = Date.from(zdt.toInstant());
```

```java
ZoneId losAngeles = ZoneId.of("America/Los_Angeles");
ZoneId berlin = ZoneId.of("Europe/Berlin");

// 2014-02-20 12:00
LocalDateTime dateTime = LocalDateTime.of(2014, 02, 20, 12, 0);

// 2014-02-20 12:00, Europe/Berlin (+01:00)
ZonedDateTime berlinDateTime = ZonedDateTime.of(dateTime, berlin);

// 2014-02-20 03:00, America/Los_Angeles (-08:00)
ZonedDateTime losAngelesDateTime = berlinDateTime.withZoneSameInstant(losAngeles);
int offsetInSeconds = losAngelesDateTime.getOffset().getTotalSeconds(); // -28800

// a collection of all available zones
Set<String> allZoneIds = ZoneId.getAvailableZoneIds();

// using offsets
LocalDateTime date = LocalDateTime.of(2013, Month.JULY, 20, 3, 30);
ZoneOffset offset = ZoneOffset.of("+05:00");

// 2013-07-20 03:30 +05:00
OffsetDateTime plusFive = OffsetDateTime.of(date, offset);

// 2013-07-19 20:30 -02:00
OffsetDateTime minusTwo = plusFive.withOffsetSameInstant(ZoneOffset.ofHours(-2));
```

# 时差

Period 类以年月日来表示日期差，而 Duration 以秒与毫秒来表示时间差；Duration 适用于处理 Instant 与机器时间。

```java
// periods
LocalDate firstDate = LocalDate.of(2010, 5, 17); // 2010-05-17
LocalDate secondDate = LocalDate.of(2015, 3, 7); // 2015-03-07
Period period = Period.between(firstDate, secondDate);

int days = period.getDays(); // 18
int months = period.getMonths(); // 9
int years = period.getYears(); // 4
boolean isNegative = period.isNegative(); // false

Period twoMonthsAndFiveDays = Period.ofMonths(2).plusDays(5);
LocalDate sixthOfJanuary = LocalDate.of(2014, 1, 6);

// add two months and five days to 2014-01-06, result is 2014-03-11
LocalDate eleventhOfMarch = sixthOfJanuary.plus(twoMonthsAndFiveDays);

// durations

Instant firstInstant= Instant.ofEpochSecond( 1294881180 ); // 2011-01-13 01:13
Instant secondInstant = Instant.ofEpochSecond(1294708260); // 2011-01-11 01:11

Duration between = Duration.between(firstInstant, secondInstant);

// negative because firstInstant is after secondInstant (-172920)
long seconds = between.getSeconds();

// get absolute result in minutes (2882)
long absoluteResult = between.abs().toMinutes();

// two hours in seconds (7200)
long twoHoursInSeconds = Duration.ofHours(2).getSeconds();
```
