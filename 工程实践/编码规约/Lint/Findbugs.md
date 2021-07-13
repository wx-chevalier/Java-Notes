### 一、编程规约

##### （一）命名风格

1. 【强制】所有编程相关的命名严禁使用拼音与英文混合的方式，更不允许直接使用中文的方式。

2. 【强制】类名使用 UpperCamelCase 风格，但以下情形例外：DO / BO / DTO / VO / AO / PO / UID 等。

3. 【强制】抽象类命名使用 Abstract 或 Base 开头；异常类命名使用 Exception 结尾；测试类 命名以它要测试的类的名称开始，以 Test 结尾。

4. 【强制】类型与中括号紧挨相连来表示数组。（正例：定义整形数组 int[] arrayDemo；反例：在 main 参数中，使用 String args[]来定义）

5. 【强制】POJO 类中的任何布尔类型的变量，都不要加 is 前缀，否则部分框架解析会引起序列化错误。否则可能会使某些自动化构建工具识别错误。

6. 【强制】包名统一使用小写，点分隔符之间有且仅有一个自然语义的英语单词。包名统一使用单数形式，但是类名如果有复数含义，类名可以使用复数形式。如工具包为`com.xzm.test.util`，而对于其中的类则是`MessageUtils`为复数形式。

7. 【推荐】在常量与变量的命名时，表示类型的名词放在词尾，以提升辨识度。（正例：startTime / workQueue / nameList /TERMINATED_THREAD_COUNT；反例：startedAt / QueueOfWork / listName / COUNT_TERMINATED_THREAD）

8. 【推荐】如果模块、接口、类、方法使用了设计模式，在命名时需体现出具体模式。如：`OrderFactory,LoginProxy,ResourceObserver`

9. 接口中的方法和属性都不要添加任何修饰符号，包括 public。

10. 【参考】枚举类名带上 Enum 后缀，枚举成员名称需要全大写，单词间用下划线隔开。如枚举名字为 ProcessStatusEnum 的成员名称：SUCCESS / UNKNOWN_REASON。

11. 【参考】各层命名规约：

    **A) Service/DAO 层方法命名规约**

    1） 获取单个对象的方法用 get 做前缀。

    2） 获取多个对象的方法用 list 做前缀，复数结尾，如：listObjects。

    3） 获取统计值的方法用 count 做前缀。

    4） 插入的方法用 save/insert 做前缀。

    5） 删除的方法用 remove/delete 做前缀。

    6） 修改的方法用 update 做前缀。 **B) 领域模型命名规约**

    1） 数据对象：xxxDO，xxx 即为数据表名。

    2） 数据传输对象：xxxDTO，xxx 为业务领域相关的名称。

    3） 展示对象：xxxVO，xxx 一般为网页名称。 4） POJO 是 DO/DTO/BO/VO 的统称，禁止命名成 xxxPOJO。

##### （二）常量的定义

1. 【推荐】如果变量值仅在一个固定范围内变化用 enum 类型来定义。
2. 【推荐】不要使用一个常量类维护所有常量，要按常量功能进行归类，分开维护。
3. 【强制】在 long 或者 Long 赋值时，数值后使用大写字母 L，不能是小写字母 l，小写容易跟数字混淆，造成误解。

##### （三）代码格式

1. 【强制】左小括号和右边相邻字符之间不出现空格；右小括号和左边相邻字符之间也不出现空 格；而左大括号前需要加空格。正确的方式是`if[空格](a[空格]==[空格]b)`

2. 【强制】if/for/while/switch/do 等保留字与括号之间都必须加空格。

3. 【强制】任何二目、三目运算符的左右两边都需要加一个空格。包括赋值运算符=、逻辑运算符&&、加减乘除符号等。

4. 【强制】采用 4 个空格缩进，禁止使用 Tab 字符。

5. 【强制】注释的双斜线与注释内容之间有且仅有一个空格。

6. 【强制】在进行类型强制转换时，右括号与强制转换值之间不需要任何空格隔开。如`int second = (int)first + 2`

7. 【强制】单行字符数限制不超过 120 个，超出需要换行，换行时遵循如下原则：

   1）第二行相对第一行缩进 4 个空格，从第三行开始，不再继续缩进。

   2）运算符与下文一起换行。

   3）方法调用的点符号与下文一起换行。

   4）方法调用中的多个参数需要换行时，在逗号后进行。 5）在括号前不要换行。

8. 【强制】方法参数在定义和传入时，多个参数逗号后面必须加空格。如`method(args1, args2, args3);`

9. 【推荐】不同逻辑、不同语义、不同业务的代码之间插入一个空行分隔开来以提升可读性。 说明:任何情形，没有必要插入多个空行进行隔开。

##### （四）OOP 规范

1. 【强制】避免通过一个类的对象引用访问此类的静态变量或静态方法，无谓增加编译器解析成 本，直接用类名来访问即可。
2. 【强制】相同参数类型，相同业务含义，才可以使用 Java 的可变参数，避免使用 Object。
3. 【强制】Object 的 equals 方法容易抛空指针异常，应使用常量或确定有值的对象来调用 equals。
4. 【强制】所有整型包装类对象之间值的比较，全部使用 equals 方法比较，因为对于-128 到 127 之外的数字，==使用则会出现意想不到的错误。
5. 【强制】任何货币金额，均以最小货币单位且整型类型来进行存储。
6. 【强制】浮点数之间的等值判断，基本数据类型不能用==来比较，包装数据类型不能用 equals 来判断。

使用误差范围或者使用 BigDecimal 来定义值，再进行浮点数的运算操作。

```
BigDecimal a = new BigDecimal("1.0");
BigDecimal b = new BigDecimal("0.9");
BigDecimal c = new BigDecimal("0.8");
BigDecimal x = a.subtract(b);
BigDecimal y = b.subtract(c);
if (x.compareTo(y) == 0) {
    System.out.println("true");
}
```

1. 【强制】如上所示 BigDecimal 的等值比较应使用 compareTo()方法，而不是 equals()方法。其中 equals 会比较值和精度，而 compareTo 则会忽略精度，也就是 2.00 和 2.0 的差别。
2. 【强制】定义数据对象 DO 类时，属性类型要与数据库字段类型相匹配。如数据库字段的 bigint 必须与类属性的 Long 类型相对应。
3. 【强制】禁止使用构造方法 BigDecimal(double)的方式把 double 值转化为 BigDecimal 对象。应该使用 String 形式的参数作为首选方案。
4. 数据库的查询结果可能是 null，因为自动拆箱，用基本数据类型接收有 NPE 风险。
5. 【强制】定义 DO/DTO/VO 等 POJO 类时，不要设定任何属性默认值。如 POJO 类的 createTime 默认值为 new Date()，但是这个属性在数据提取时并没有置入具体值，在更新其它字段时又附带更新了此字段，导致创建时间被修改成当前时间。
6. 【强制】序列化类新增属性时，请不要修改 serialVersionUID 字段，避免反序列失败；如果 完全不兼容升级，避免反序列化混乱，那么请修改 serialVersionUID 值。（只有当你需要用文件/网络流传送对象才需要在写入/发送端序列化，读取/接收端反序列化。）
7. 【强制】构造方法里面禁止加入任何业务逻辑，如果有初始化逻辑，请放在 init 方法中。（init 方法在调用构造方法的时候会自动调用）

```java
public class InitTest {

    {
        System.out.println("该语句在 init 方法体內部,在构造函数之前执行");
    }

    public InitTest(){
        System.out.println("构造函数");
    }
}
```

1. 【强制】POJO 类必须写 toString 方法。如果继承了另一个 POJO 类，注意在前面加一下 super.toString。
2. 【强制】禁止在 POJO 类中，同时存在对应属性 xxx 的 isXxx()和 getXxx()方法。 因为框架在调用属性 xxx 的提取方法时，并不能确定哪个方法一定是被优先调用到的。
3. 【推荐】使用索引访问用 String 的 split 方法得到的数组时，需做最后一个分隔符后有无内容的检查，否则会有抛 IndexOutOfBoundsException 的风险。
4. 【推荐】当一个类有多个构造方法，或者多个同名方法，这些方法应该按顺序放置在一起，便 于阅读，此条规则优先于下一条。
5. 【推荐】 类内方法定义的顺序依次是：公有方法或保护方法 > 私有方法 > getter / setter 方法。
6. 【推荐】setter 方法中，参数名称与类成员变量名称一致，this.成员名 = 参数名。在 getter/setter 方法中，不要增加业务逻辑，增加排查问题的难度。

```java
// 错误的实例
public Integer getData () {
    if (condition) {
        return this.data + 100;
    } else {
        return this.data - 100;
    }
}
```

##### （五）日期时间

1. 【强制】日期格式化时，传入 pattern 中表示年份统一使用小写的 y。

2. 【强制】在日期格式中分清楚大写的 M 和小写的 m，大写的 H 和小写的 h 分别指代的意义。

   1） 表示月份是大写的 M；

   2） 表示分钟则是小写的 m；

   3） 24 小时制的是大写的 H；

   4） 12 小时制的则是小写的 h。

3. 【推荐】使用枚举值来指代月份。如果使用数字，注意 Date，Calendar 等日期相关类的月份 month 取值在 0-11 之间。

4. 【强制】不允许在程序任何地方中使用`java.sql.Date和java.sql.Time`

##### （六）集合处理

1. 【强制】关于 hashCode 和 equals 的处理，遵循如下规则：

   1） 只要覆写 equals，就必须覆写 hashCode。

   2） 因为 Set 存储的是不重复的对象，依据 hashCode 和 equals 进行判断，所以 Set 存储的对象必须覆写 这两种方法。

   3） 如果自定义对象作为 Map 的键，那么必须覆写 hashCode 和 equals。

2. 【强制】判断所有集合内部的元素是否为空，使用 isEmpty()方法，而不是 size()==0 的方式。

3. 【强制】使用 Map 的方法 keySet()/values()/entrySet()返回集合对象时，不可以对其进行添 加元素操作，否则会抛出 UnsupportedOperationException 异常。

4. 【强制】使用集合转数组的方法，必须使用集合的 toArray(T[] array)，传入的是类型完全一 致、长度为 0 的空数组。

   如直接使用 toArray 无参方法存在问题，此方法返回值只能是 Object[]类，若强转其它类型数组将出现类型转换异常。

```
List<String> list = new ArrayList<>(2);
list.add("A");
list.add("B");
String[] array = (String[]) list.toArray();
// 运行结果：
Exception in thread "main" java.lang.ClassCastException: [Ljava.lang.Object; cannot be cast to [Ljava.lang.String;
	at Main.main(Main.java:16)

// 正确的方式：
String[] array = (String[]) list.toArray(new String[0]);
```

1. 【强制】在使用 Collection 接口任何实现类的 addAll()方法时，都要对输入的集合参数进行 NPE 判断。

   说明：在 ArrayList#addAll 方法的第一行代码即 Object[] a = c.toArray(); 其中 c 为输入集合参数，如果为 null，则直接抛出异常。

```
List<String> list = new ArrayList<>(2);
List<String> temp=null;
//应该首先判断temp是不是为null
list.addAll(temp);
//以上操作会抛出空指针异常
```

1. 【强制】使用工具类 Arrays.asList()把数组转换成集合时，不能使用其修改集合相关的方法， 它的 add/remove/clear 方法会抛出 UnsupportedOperationException 异常。

   说明：asList 的返回对象是一个 Arrays 内部类，并没有实现集合的修改方法。Arrays.asList 体现的是适配 器模式，只是转换接口，后台的数据仍是数组。

   String[] str = new String[] { "chen", "yang", "hao" };

   List list = Arrays.asList(str);

   第一种情况：list.add("yangguanbao"); 运行时异常。 第二种情况：str[0] = "change"; 也会随之修改，反之亦然。

```
//正确的操作
String[] str = new String[] { "chen", "yang", "hao" };
//不是这样操作 List<String> array = Arrays.asList(str);
List<String> array = new ArrayList<>(Arrays.asList(str));
array.add("xiang");
```

1. 【强制】泛型通配符<? extends T>来接收返回的数据，此写法的泛型集合不能使用 add 方法， 而<? super T>不能使用 get 方法，两者在接口调用赋值的场景中容易出错。

   说明：扩展说一下 PECS(Producer Extends Consumer Super)原则：第一、频繁往外读取内容的，适合用<? extends T>。第二、经常往里插入的，适合用<? super T>

2. 【强制】在无泛型限制定义的集合赋值给泛型限制的集合时，在使用集合元素时，需要进行 instanceof 判断，避免抛出 ClassCastException 异常。

```
List<String> generics = null;
List notGenerics = new ArrayList(10);
notGenerics.add(new Object());
notGenerics.add(new Integer(1));
generics = notGenerics;
// 若没有if判断,此处抛出 ClassCastException 异常
if(generics.get(0) instanceof String){
    String string = generics.get(0);
}
```

1. 【强制】不要在 foreach 循环里进行元素的 remove/add 操作。remove 元素请使用 Iterator 方式，如果并发操作，需要对 Iterator 对象加锁。

```
List<String> list = new ArrayList<>();
list.add("1");
list.add("2");
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()){
    String item = iterator.next();
    if (item.equals("2")) {
        iterator.remove();
    }
}
System.out.println(list.toString());
```

1. 【推荐】集合初始化时，指定集合初始值大小。

   实例：HashMap 需要放置 1024 个元素，由于没有设置容量初始大小，随着元素增加而被迫不断扩容， resize()方法总共会调用 8 次，反复重建哈希表和数据迁移。当放置的集合元素个数达千万级时会影响程序 性能。

2. 【推荐】使用 entrySet 遍历 Map 类集合 KV，而不是 keySet 方式进行遍历。 entrySet 只是遍历了一次就把 key 和 value 都放到了 entry 中，效率更高。如果是 JDK8，使用 Map.forEach 方法。

```
Map<Integer,Integer> map = new HashMap<>(16);for(int i=0;i<10;i++){    map.put(i,i);}System.out.println("通过Map.entrySet遍历key和value");for (Map.Entry<Integer, Integer> entry : map.entrySet()) {    System.out.println("key= " + entry.getKey() + " and value= " + entry.getValue());}map.forEach((k,v)->{    System.out.println("key= " + k + " and value= " + v);});
```

1. 【推荐】高度注意 Map 类集合 K/V 能不能存储 null 的情况，如以下表格。

| 集合类            | key           | value         | super       | 说明       |
| ----------------- | ------------- | ------------- | ----------- | ---------- |
| Hashtable         | 不允许为 null | 不允许为 null | Dictionary  | 线程安全   |
| ConcurrentHashMap | 不允许为 null | 不允许为 null | AbstractMap | 锁分段技术 |
| TreeMap           | 不允许为 null | 允许为 null   | AbstractMap | 线程不安全 |
| HashMap           | 允许为 null   | 允许为 null   | AbstractMap | 线程不安全 |

##### （七）并发处理

1. 【强制】获取单例对象需要保证线程安全，其中的方法也要保证线程安全。如资源驱动类、工具类、单例工厂类都需要注意。

2. 【强制】线程资源必须通过线程池提供，不允许在应用中自行显式创建线程。

   说明：线程池的好处是减少在创建和销毁线程上所消耗的时间以及系统资源的开销，解决资源不足的问题。如果不使用线程池，有可能造成系统创建大量同类线程而导致消耗完内存或者“过度切换”的问题。

3. 【强制】SimpleDateFormat 是线程不安全的类，一般不要定义为 static 变量，如果定义为 static， 必须加锁，或者使用 DateUtils 工具类。

4. 【强制】必须回收自定义的 ThreadLocal 变量，尤其在线程池场景下，线程经常会被复用， 如果不清理自定义的 ThreadLocal 变量，可能会影响后续业务逻辑和造成内存泄露等问题。 尽量在代理中使用 try-finally 块进行回收。

```
objectThreadLocal.set(userInfo);try {    // ...} finally {    objectThreadLocal.remove();}
```

1. 【强制】高并发时，同步调用应该去考量锁的性能损耗。能用无锁数据结构，就不要用锁；能 锁区块，就不要锁整个方法体；能用对象锁，就不要用类锁。
2. 【强制】对多个资源、数据库表、对象同时加锁时，需要保持一致的加锁顺序，否则可能会造 成死锁。
3. 【强制】在使用尝试机制来获取锁的方式中，进入业务代码块之前，必须先判断当前线程是否 持有锁。锁的释放规则与锁的阻塞等待方式相同。
4. 【推荐】资金相关的金融敏感信息，使用悲观锁策略。总的来说，悲观锁遵循一锁、二判、三更新、四释放的原则。
5. 【推荐】避免 Random 实例被多线程使用，虽然共享该实例是线程安全的，但会因竞争同一 seed 导致的性能下降。

##### （八）控制语句

1. 【强制】在一个 switch 块内，每个 case 要么通过 continue/break/return 等来终止，要么注释说明程序将继续执行到哪一个 case 为止；在一个 switch 块内，都必须包含一个 default 语句并且放在最后，即使它什么代码也没有。
2. 【强制】当 switch 括号内的变量类型为 String 并且此变量为外部参数时，必须先进行 null 判断，否则抛出异常。

```
    private static void method(String par){        if(par==null){            return; // 正确的应该添加该if语句        }        switch (par){            case "xzm":{                System.out.println(1);break;            }            case "null":{                System.out.println(2);break;            }            default:{                System.out.println(3);            }        }    }    public static void main(String[] args) {        method(null);    }
```

1. 【强制】在 if/else/for/while/do 语句中必须使用大括号。如即使只有一行代码，也禁止不采用大括号的编码方式：`if (condition) statements;`

2. 【强制】三目运算符 condition? 表达式 1 : 表达式 2 中，高度注意表达式 1 和 2 在类型对齐时，可能抛出因自动拆箱导致的 NPE 异常。

   说明：以下两种场景会触发类型对齐的拆箱操作：

   1） 表达式 1 或表达式 2 的值只要有一个是原始类型。 2） 表达式 1 或表达式 2 的值的类型不一致，会强制拆箱升级成表示范围更大的那个类型。

   ```
   Integer a = 1;Integer b = 2;Integer c = null;Boolean flag = false;// a*b的结果是int类型，那么c会强制拆箱成int类型，抛出空指针异常Integer result = (flag?a*b:c);
   ```

3. 【强制】在高并发场景中，避免使用”等于”判断作为中断或退出的条件。

   说明：如果并发控制没有处理好，容易产生等值判断被“击穿”的情况，使用大于或小于的区间判断条件来代替。

4. 【推荐】公开接口需要进行入参保护，尤其是批量操作的接口。如某业务系统，提供一个用户批量查询的接口，API 文档上有说最多查多少个，但接口实现上没做任何 保护，导致调用方传了一个 1000 的用户 id 数组过来后，查询信息后内存溢出。

5. 【参考】下列情形，需要进行参数校验：

   1） 调用频次低的方法。

   2） 执行时间开销很大的方法。此情形中，参数校验时间几乎可以忽略不计，但如果因为参数错误导致 中间执行回退，或者错误，那得不偿失。

   3） 需要极高稳定性和可用性的方法。

   4） 对外提供的开放接口，不管是 RPC/API/HTTP 接口。 5） 敏感权限入口。

##### （九）注释规约

1. 【强制】类、类属性、类方法的注释必须使用 Javadoc 规范，使用/\*_内容_/格式，不得使用 // xxx 方式。
2. 【强制】所有的抽象方法（包括接口中的方法）必须要用 Javadoc 注释、除了返回值、参数、 异常说明外，还必须指出该方法做什么事情，实现什么功能。
3. 【强制】所有的类都必须添加创建者和创建日期。 说明：在设置模板时，注意 IDEA 的@author 为`${USER}`，而 eclipse 的@author 为`${user}`，大小写有区别，而日期的设置统一为 yyyy/MM/dd 的格式。
4. 【强制】方法内部单行注释，在被注释语句上方另起一行，使用//注释。方法内部多行注释使 用/\* \*/注释，注意与代码对齐。
5. 【强制】所有的枚举类型字段必须要有注释，说明每个数据项的用途。

##### （十）前后端公约

1. 【强制】前后端交互的 API，需要明确协议、域名、路径、请求方法、请求内容、状态码、响应体。

   说明：

   1） 协议：生产环境必须使用 HTTPS。

   2） 路径：每一个 API 需对应一个路径，表示 API 具体的请求地址：

   a） 代表一种资源，只能为名词，推荐使用复数，不能为动词，请求方法已经表达动作意义。

   b） URL 路径不能使用大写，单词如果需要分隔，统一使用下划线。

   c） 路径禁止携带表示请求内容类型的后缀，比如".json",".xml"，通过 accept 头表达即可。 3） 请求方法：对具体操作的定义，常见的请求方法如下：

   a） GET：从服务器取出资源。

   b） POST：在服务器新建一个资源。

   c） PUT：在服务器更新资源。

   d） DELETE：从服务器删除资源。 4） 请求内容：URL 带的参数必须无敏感信息或符合安全要求；body 里带参数时必须设置 Content-Type。 5） 响应体：响应体 body 可放置多种数据类型，由 Content-Type 头来确定。

2. 【强制】前后端数据列表相关的接口返回，如果为空，则返回空数组[]或空集合{}，这样可以减少前端很多琐碎的 null 判断。

```
// 如果类不是太多，我们可以在DTO上加上该注释@JsonInclude(JsonInclude.Include.NON_NULL)// 如果较多，例如在springboot中我们可以采用#设置全局，Null值不返回到前端  jackson:     default-property-inclusion: non_null
```

1. 【强制】服务端发生错误时，返回给前端的响应信息必须包含 HTTP 状态码，errorCode、 errorMessage、用户提示信息四个部分。
2. 【强制】在前后端交互的 JSON 格式数据中，所有的 key 必须为小写字母开始的 lowerCamelCase 风格，符合英文表达习惯，且表意完整，这一点在实习中遇到，不能将数据库中的字段直接返回给前端。
3. 【强制】errorMessage 是前后端错误追踪机制的体现，可以在前端输出到 type="hidden" 文字类控件中，或者用户端的日志中，帮助我们快速地定位出问题。
4. 【强制】对于需要使用超大整数的场景，服务端一律使用 String 字符串类型返回，禁止使用 Long 类型。如通常在订单号或交易号大于等于 16 位，大概率会出现前后端单号不一致的情况，所以需要装换成 String。

```
Long a = 123L;String s = String.valueOf(a);
```

1. HTTP 请求通过 URL 传递参数时，不能超过 2048 字节。 说明：不同浏览器对于 URL 的最大长度限制略有不同，并且对超出最大长度的处理逻辑也有差异，2048 字节是取所有浏览器的最小值。
2. 【强制】HTTP 请求通过 body 传递内容时，必须控制长度，超出最大长度后，后端解析会出错。
3. 【强制】在翻页场景中，用户输入参数的小于 1，则前端返回第一页参数给后端；后端发现用 户输入的参数大于总页数，直接返回最后一页。
4. 【强制】服务器内部重定向必须使用 forward；外部重定向地址必须使用 URL 统一代理模块 生成，否则会因线上采用 HTTPS 协议而导致浏览器提示“不安全”，并且还会带来 URL 维护不一致的问题。

```
@RequestMapping(value="/testredirect",method = { RequestMethod.POST, RequestMethod.GET }) public  String testredirect(HttpServletRequest request){    //把username参数传递到request中    request.setAttribute("username", "xiangzhimin");    return "forward:/user/index";  }
```

1. 【推荐】前后端的时间格式统一为"yyyy-MM-dd HH:mm:ss"，统一为 GMT。

```
//后端返回给前端的时间格式@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone="GMT+8")private Date date;//前端封装成后端的日期数据格式@DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")private Date date;
```

##### （十一）其他

1. 【强制】在使用正则表达式时，利用好其预编译功能，可以有效加快正则匹配速度。

   不要在方法体内定义：Pattern pattern = Pattern.compile(“规则”);

2. 【推荐】任何数据结构的构造或初始化，都应指定大小，避免数据结构无限增长吃光内存。

### 二、异常日志

##### （一）错误码

1. 【强制】错误码不体现版本号和错误等级信息。

   说明：错误码以不断追加的方式进行兼容。错误等级由日志和错误码本身的释义来决定。

2. 【强制】全部正常，但不得不填充错误码时返回五个零：00000。

3. 【强制】错误码不能直接输出给用户作为提示信息使用。

##### （二）异常处理

1. 【强制】Java 类库中定义的可以通过预检查方式规避的 RuntimeException 异常不应该通过 catch 的方式来处理，比如：NullPointerException，IndexOutOfBoundsException 等等。

```
/*无法通过预检查的异常除外，比如，在解析字符串形式的数字时，可能存在数字格式错误，不得不通过 catch NumberFormatException 来实现。*///正确if (obj != null) {...}//错误try { obj.method(); } catch (NullPointerException e) {…}
```

1. 【强制】异常捕获后不要用来做流程控制，条件控制。毕竟条件判断的效率高于异常捕获。
2. 【强制】捕获异常是为了处理它，不要捕获了却什么都不处理而抛弃之，如果不想处理它，请 将该异常抛给它的调用者。最外层的业务使用者，必须处理异常，将其转化为用户可以理解的内容。
3. 【强制】事务场景中，抛出异常被 catch 后，如果需要回滚，一定要注意手动回滚事务。
4. 【强制】finally 块必须对资源对象、流对象进行关闭，有异常也要做 try-catch。在 JDK7 及以上版本，可使用 try-with-resources 方式。
5. 不要在 finally 块中使用 return。

> try 块中的 return 语句执行成功后，并不马上返回，而是等 finally 执行结束，如果 finally 语句中有 return 语句，将会覆盖 try 块中的 return，导致意想不到的错误。

1. 【强制】捕获异常与抛异常，必须是完全匹配，或者捕获异常是抛异常的父类。

2. 【推荐】方法的返回值可以为 null，不强制返回空集合，或者空对象等，必须添加注释充分说 明什么情况下会返回 null 值。

3. 【推荐】防止 NPE，是程序员的基本修养，注意 NPE 产生的场景：

   1） 返回类型为基本数据类型，return 包装数据类型的对象时，自动拆箱有可能产生 NPE。如 public int f() { return Integer 对象}， 如果为 null，自动解箱抛 NPE。 2） 数据库的查询结果可能为 null。

   3） 集合里的元素即使 isNotEmpty，取出的数据元素也可能为 null。

   4） 远程调用返回对象时，一律要求进行空指针判断，防止 NPE。

   5） 对于 Session 中获取的数据，建议进行 NPE 检查，避免空指针。 6） 级联调用 obj.getA().getB().getC()；一连串调用，易产生 NPE。

##### （三）日志规约

1. 【强制】应用中不可直接使用日志系统（Log4j、Logback）中的 API，而应依赖使用日志框架 （SLF4J、JCL--Jakarta Commons Logging）中的 API，使用门面模式的日志框架，有利于维护和 各个类的日志处理方式统一。

   说明：日志框架（SLF4J、JCL--Jakarta Commons Logging）的使用方式（推荐使用 SLF4J）

```
// 使用 SLF4J：import org.slf4j.Logger;import org.slf4j.LoggerFactory;private static final Logger logger = LoggerFactory.getLogger(Test.class);//使用 JCL：import org.apache.commons.logging.Log;import org.apache.commons.logging.LogFactory;private static final Log log = LogFactory.getLog(Test.class);
```

1. 所有日志文件至少保存 15 天，因为有些异常具备以“周”为频次发生的特点。对于当天日志，以“应用名.log”来保存，保存在/home/admin/应用名/logs/目录下，过往日志格式为: {logname}.log.{保存日期}，日期格式：yyyy-MM-dd。
2. 【强制】在日志输出时，字符串变量之间的拼接使用占位符的方式。

> 因为 String 字符串的拼接会使用 StringBuilder 的 append()方式，有一定的性能损耗。使用占位符仅是替换动作，可以有效提升性能。
>
> 正确方式：logger.debug("Processing trade with id: {} and symbol: {}", id, symbol);

1. 【强制】生产环境禁止直接使用 System.out 或 System.err 输出日志或使用 e.printStackTrace()打印异常堆栈。

### 三、单元测试

1. 【强制】单元测试代码必须写在如下工程目录：src/test/java，不允许写在业务代码目录下。

### 四、安全规约

1. 【强制】隶属于用户个人的页面或者功能必须进行权限控制校验。 说明：防止没有做水平权限校验就可随意访问、修改、删除别人的数据，比如查看他人的私信内容。

2. 【强制】用户输入的 SQL 参数严格使用参数绑定或者 METADATA 字段值限定，防止 SQL 注入， 禁止字符串拼接 SQL 访问数据库。

3. 【强制】用户请求传入的任何参数必须做有效性验证。

   说明：忽略参数校验可能导致：

   - npage size 过大导致内存溢出
   - 恶意 order by 导致数据库慢查询
   - 缓存击穿
   - SSRF
   - 任意重定向
   - SQL 注入，Shell 注入，反序列化注入
   - 正则输入源串拒绝服务 ReDoS

   Java 代码用正则来验证客户端的输入，有些正则写法验证普通用户输入没有问题，但是如果攻击人员使用的是特殊构造的字符串来验证，有可能导致死循环的结果。

4. 【强制】禁止向 HTML 页面输出未经安全过滤或未正确转义的用户数据。

### 五、MYSQL 数据库

##### （一）建表规约

1. 【强制】表达是与否概念的字段，必须使用 is_xxx 的方式命名，数据类型是 unsigned tinyint （1 表示是，0 表示否）。

   任何字段如果为非负数，必须是 unsigned。

   POJO 类中的任何布尔类型的变量，都不要加 is 前缀，所以，需要在设置从 is_xxx 到 Xxx 的映射关系。数据库表示是与否的值，使用 tinyint 类型，坚持 is_xxx 的命名方式是为了明确其取值含 义与取值范围。 正例：表达逻辑删除的字段名 is_deleted，1 表示删除，0 表示未删除。

2. 【强制】表名、字段名必须使用小写字母或数字，禁止出现数字开头，禁止两个下划线中间只出现数字。数据库字段名的修改代价很大，因为无法进行预发布，所以字段名称需要慎重考虑。

> MySQL 在 Windows 下不区分大小写，但在 Linux 下默认是区分大小写。因此，数据库名、表名、 字段名，都不允许出现任何大写字母，避免节外生枝。

```
正例：aliyun_admin，rdc_config，level3_name反例：AliyunAdmin，rdcConfig，level_3_name
```

1. 【强制】表名不使用复数名词。表名应该仅仅表示表里面的实体内容，不应该表示实体数量，对应于 DO 类名也是单数形式，符合表达习惯。
2. 【强制】主键索引名为`pk_字段名`；唯一索引名为`uk_字段名`；普通索引名则为`idx_字段名`

> 说明：pk* 即 primary key；uk* 即 unique key；idx\_ 即 index 的简称。

1. 【强制】小数类型为 decimal，禁止使用 float 和 double。

> 说明：在存储的时候，float 和 double 都存在精度损失的问题，很可能在比较值的时候，得到不正确的 结果。如果存储的数据范围超过 decimal 的范围，建议将数据拆成整数和小数并分开存储。

1. 【强制】如果存储的字符串长度几乎相等，使用 char 定长字符串类型。
2. 【强制】varchar 是可变长字符串，不预先分配存储空间，长度不要超过 5000，如果存储长度 大于此值，定义字段类型为 text，独立出来一张表，用主键来对应，避免影响其它字段索引效率。
3. 【强制】表必备三字段：id, create_time, update_time。
4. 【参考】合适的字符存储长度，不但节约数据库表空间、节约索引存储，更重要的是提升检索速度。

##### （二）索引规约

1. 【强制】业务上具有唯一特性的字段，即使是组合字段，也必须建成唯一索引。

> 说明：不要以为唯一索引影响了 insert 速度，这个速度损耗可以忽略，但提高查找速度是明显的；另外， 即使在应用层做了非常完善的校验控制，只要没有唯一索引，根据墨菲定律，必然有脏数据产生。

```
CREATE TABLE IF NOT EXISTS `表名`(   `id` INT(11) NOT NULL AUTO_INCREMENT,#AUTO_INCREMENT 自增长列   `name` INT(11) NOT NULL,   `age` INT(11) NOT NULL,   PRIMARY KEY ( `id` ),   KEY (`name`,`age`))ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='表备注';
```

1. 【强制】超过三个表禁止 join。需要 join 的字段，数据类型保持绝对一致；多表关联查询时， 保证被关联的字段需要有索引。

   [关于 join 的讲解传送](https://www.cnblogs.com/reaptomorrow-flydream/p/8145610.html)

2. 【强制】在 varchar 字段上建立索引时，必须指定索引长度，没必要对全字段建立索引，根据 实际文本区分度决定索引长度。

```
# 建表create table mytable(    id int not null,    username varchar(16) not null,    index [indexname] (username(length))    //unique [indexname] (username(length)));alter table mytable add unique [indexname] (username(length))
```

1. 【推荐】SQL 性能优化的目标：至少要达到 range 级别，要求是 ref 级别，如果可以是 consts 最好。

   说明：

   1） consts 单表中最多只有一个匹配行（主键或者唯一索引），在优化阶段即可读取到数据。

   2） ref 指的是使用普通的索引（normal index）。 3） range 对索引进行范围检索。

2. 【推荐】防止因字段类型不同造成的隐式转换，导致索引失效。

> MySQL 索引使用：字段为 varchar 类型时，条件要使用”包起来，才能正常走索引

##### （三）SQL 语句

1. 【强制】不要使用`count(列名)`或 `count(常量)`来替代` count(*)`，`count(*)`是 SQL92 定义的标准统计行数的语法，跟数据库无关，跟 NULL 和非 NULL 无关。

```
count(*)包括了所有的列,相当于行数,在统计结果的时候,不会忽略列值为NULL。count(1)包括了忽略所有列,用1代表代码行,在统计结果的时候,不会忽略列值为NULL。count(列名)只包括列名那一列,在统计结果的时候,会忽略列值为空（这里的空不是只空字符串或者0,而是表示null）的计数，,即某个字段值为NULL时,不统计。
```

1. 【强制】`count(distinct col)`计算该列除 NULL 之外的不重复行数，注意`count(distinct col1, col2)` 如果其中一列全为 NULL，那么即使另一列有不同的值，也返回为 0。

2. 【强制】当某一列的值全是 NULL 时，count(col)的返回结果为 0，但 sum(col)的返回结果为 NULL，因此使用 sum()时需注意 NPE 问题。

   > 可以使用如下方式来避免 sum 的 NPE 问题：SELECT IFNULL(SUM(column), 0) FROM table;

3. 【强制】使用 ISNULL()来判断是否为 NULL 值。 说明：NULL 与任何值的直接比较都为 NULL。

   1） NULL<>NULL 的返回结果是 NULL，而不是 false。

   2） NULL=NULL 的返回结果是 NULL，而不是 true。

   3） NULL<>1 的返回结果是 NULL，而不是 true。

4. 【强制】代码中写分页查询逻辑时，若 count 为 0 应直接返回，避免执行后面的分页语句。

5. 【强制】禁止使用存储过程，存储过程难以调试和扩展，更没有移植性。

6. 【强制】对于数据库中表记录的查询和变更，只要涉及多个表，都需要在列名前加表的别名（或 表名）进行限定。

##### （四）ORM 映射

1. 【强制】在表查询中，一律不要使用 \* 作为查询的字段列表，需要哪些字段必须明确写明。

   说明：1）增加查询分析器解析成本。2）增减字段容易与 resultMap 配置不一致。3）无用字段增加网络消耗，尤其是 text 类型的字段。

2. 【强制】POJO 类的布尔属性不能加 is，而数据库字段必须加 is\_，要求在 resultMap 中进行 字段与属性之间的映射。

3. 【强制】不要用 resultClass 当返回参数，即使所有类属性名与数据库字段一一对应，也需要定义`<resultMap>`；反过来，每一个表也必然有一个`<resultMap>`与之对应。

4. 【强制】sql.xml 配置参数使用：#{}，#param# 不要使用${} 此种方式容易出现 SQL 注入。

5. 【强制】不允许直接拿 HashMap 与 Hashtable 作为查询结果集的输出。

   > 反例：某同学为避免写一个 xxx，直接使用 HashTable 来接收数据库返回结 果，结果出现日常是把 bigint 转成 Long 值，而线上由于数据库版本不一样，解析成 BigInteger，导致线 上问题。

6. 【强制】更新数据表记录时，必须同时更新记录对应的 update_time 字段值为当前时间。

7. 【参考】@Transactional 事务不要滥用。事务会影响数据库的 QPS，另外使用事务的地方需 要考虑各方面的回滚方案，包括缓存回滚、搜索引擎回滚、消息补偿、统计修正等。

### 六、工程结构

##### （一）应用分层

1. 【参考】（分层异常处理规约）在 DAO 层，产生的异常类型有很多，无法用细粒度的异常进行 catch，使用 catch(Exception e)方式，并 throw new DAOException(e)，不需要打印日志，因为日志在 Manager/Service 层一定需要捕获并打印到日志文件中去，如果同台服务器再打日志，浪费性能和存储。在 Service 层出现异常时，必须记录出错日志到磁盘，尽可能带上参数信息， 相当于保护案发现场。Manager 层与 Service 同机部署，日志方式与 DAO 层处理一致，如果是单独部署，则采用与 Service 一致的处理方式。Web 层绝不应该继续往上抛异常，因为已经处于顶层，如果意识到这个异常将导致页面无法正常渲染，那么就应该直接跳转到友好错误页面，尽量加上友好的错误提示信息。开放接口层要将异常处理成错误码和错误信息方式返回。
2. 【参考】分层领域模型规约：
   - DO（Data Object）：此对象与数据库表结构一一对应，通过 DAO 层向上传输数据源对象。
   - DTO（Data Transfer Object）：数据传输对象，Service 或 Manager 向外传输的对象。
   - BO（Business Object）：业务对象，可以由 Service 层输出的封装业务逻辑的对象。
   - Query：数据查询对象，各层接收上层的查询请求。**注意超过 2 个参数的查询封装，禁止使用 Map 类 来传输**。
   - VO（View Object）：显示层对象，通常是 Web 向模板渲染引擎层传输的对象。

##### （二）二方库依赖

1. 不要使用不稳定的工具包或者 Utils 类。

##### （三）服务器

1. 【推荐】高并发服务器建议调小 TCP 协议的 time_wait 超时时间。
2. 【推荐】调大服务器所支持的最大文件句柄数（File Descriptor，简写为 fd）。
3. 【推荐】给 JVM 环境参数设置-XX:+HeapDumpOnOutOfMemoryError 参数，让 JVM 碰到 OOM 场景时输出 dump 信息。
4. 【推荐】在线上生产环境，JVM 的 Xms 和 Xmx 设置一样大小的内存容量，避免在 GC 后调整 堆大小带来的压力。
5. 【参考】服务器内部重定向必须使用 forward；外部重定向地址必须使用 URL Broker 生成，否 则因线上采用 HTTPS 协议而导致浏览器提示“不安全“。此外，还会带来 URL 维护不一致的问题。
