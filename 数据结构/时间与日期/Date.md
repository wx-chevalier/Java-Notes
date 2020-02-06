# Java 时间与日期处理

在 Java 8 之前，我们最常见的时间与日期处理相关的类就是 Date、Calendar 以及  SimpleDateFormatter  等等。不过 `java.util.Date` 也是被诟病已久，它包含了日期、时间、毫秒数等众多繁杂的信息，其内部利用午夜 12 点来区分日期，利用 1970-01-01 来计算时间；并且其月份从 0 开始计数，而且用于获得年、月、日等信息的接口也是太不直观。除此之外，`java.util.Date` 与 `SimpleDateFormatter` 都不是类型安全的，而 JSR-310 中的 `LocalDate` 与 `LocalTime` 等则是不变类型，更加适合于并发编程。JSR 310 实际上有两个日期概念。第一个是 Instant，它大致对应于 `java.util.Date` 类，因为它代表了一个确定的时间点，即相对于标准 Java 纪元(1970 年 1 月 1 日)的偏移量；但与 `java.util.Date` 类不同的是其精确到了纳秒级别。另一个则是 LocalDate、LocalTime 以及 LocalDateTime 这样代表了一般时区概念、易于理解的对象。

| Class / Type  | Description                                                            |
| ------------- | ---------------------------------------------------------------------- |
| Year          | Represents a year.                                                     |
| YearMonth     | A month within a specific year.                                        |
| LocalDate     | A date without an explicitly specified time zone.                      |
| LocalTime     | A time without an explicitly specified time zone.                      |
| LocalDateTime | A combination date and time without an explicitly specified time zone. |

最新 JDBC 映射将把数据库的日期类型和 Java 8 的新类型关联起来：

| SQL       | Java          |
| --------- | ------------- |
| date      | LocalDate     |
| time      | LocalTime     |
| timestamp | LocalDateTime |
| datetime  | LocalDateTime |

# Date

java.util.Date 对象本身不包含任何时区信息，我们无法在 Date 对象上设置时区。一个 Date 对象包含的唯一东西是从 1970 年 1 月 1 日 00:00:00 UTC 开始的“纪元”以来的毫秒数。你可以在 DateFormat 对象上设置时区，告诉它你想在哪个时区显示日期和时间。Date 表示特定的瞬间，精确到毫秒，`yyyy-mm-dd hh:mm:ss` ；Timestamp 此类型由.Date 和单独的毫微秒值组成。yyyy-mm-dd hh:mm:ss.fffffffff

```java
// 默认创建
Date date0 = new Date();

// 从 TimeStamp 中创建
Date date1 = new Date(time);

// 基于 Instant 创建
Date date = Date.from(instant);

// 从格式化字符串中获取
SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");

java.util.Date dt=sdf.parse("2005-2-19");

// 从 LocalDateTime 中转化而来
Date out = Date.from(ldt.atZone(ZoneId.systemDefault()).toInstant());
```

## 获取与比较

基于 Date 的日期比较常常使用以下方式：

- 使用 getTime() 方法获取两个日期(自 1970 年 1 月 1 日经历的毫秒数值)，然后比较这两个值。

- 使用方法 before()，after() 和 equals()。例如，一个月的 12 号比 18 号早，则 `new Date(99, 2, 12).before(new Date (99, 2, 18))` 返回 true。

- 使用 `compareTo()` 方法，它是由 Comparable 接口定义的，Date 类实现了这个接口。

```java
public static void main(String[] args) {
  Date date = new Date(1359641834000L);// 本地时间 2013-1-31 22:17:14 对应的时间戳
  String dateStr = "2013-1-31 22:17:14";
  SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
  try {
    Date dateTmp = dateFormat.parse(dateStr);

    // System.out.printIn 默认按照本地时间打印
    System.out.println(dateTmp);
  } catch (ParseException e) {
    e.printStackTrace();
  }
  String dateStrTmp = dateFormat.format(date);
  System.out.println(dateStrTmp);
}

// Fri Feb 01 06:17:14 CST 2013
// 2013-01-31 14:17:14
```

操作系统是"Asia/Shanghai"，即 GMT+8 的北京时间，那么执行日期转字符串的 format 方法时，由于日期生成时默认是操作系统时区，因此 2013-1-31 22:17:14 是北京时间，那么推算到 GMT 时区，自然是要减 8 个小时的；而执行字符串转日期的 parse 方法时，由于字符串本身没有时区的概念，因此 2013-1-31 22:17:14 就是指 GMT（UTC）时间，那么当转化为日期时要加上默认时区，即"Asia/Shanghai"，因此要加上 8 个小时。

# Calendar

Date 用于记录某一个含日期的、精确到毫秒的时间。重点在代表一刹那的时间本身。Calendar 用于将某一日期放到历法中的互动——时间和年、月、日、星期、上午、下午、夏令时等这些历法规定互相作用关系和互动。我们可以通过 Calendar 内置的构造器来创建实例：

```java
Calendar.Builder builder =new Calendar.Builder();
Calendar calendar1 = builder.build();

Date date = calendar.getTime();
```

在 Calendar 中我们则能够获得较为直观的年月日信息：

```java
// 2017，不再是 2017 - 1900 = 117
int year =calendar.get(Calendar.YEAR);

int month=calendar.get(Calendar.MONTH)+1;

int day =calendar.get(Calendar.DAY_OF_MONTH);

int hour =calendar.get(Calendar.HOUR_OF_DAY);

int minute =calendar.get(Calendar.MINUTE);

int seconds =calendar.get(Calendar.SECOND);
```

除此之外，Calendar 还提供了一系列 set 方法来允许我们动态设置时间，还可以使用 add 等方法进行日期的加减。

# SimpleDateFormat

SimpleDateFormat 用来进行简单的数据格式化转化操作：

```java
Date dNow = new Date( );
SimpleDateFormat ft = new SimpleDateFormat ("E yyyy.MM.dd 'at' hh:mm:ss a zzz");
```
