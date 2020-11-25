# ModelMapper

ModelMapper 是一个从对象到对象（object-to-object）的框架，能将 Java Bean（Pojo）对象从一种表现形式转化为另一种表现形式。它采用“通过约定来配置”的方式，自动匹配不同的对象映射，同时具备满足某些特殊需求的高级功能。这与.NET 的 AutoMapper 库很类似（但不是直接移植）。ModelMapper 能用更加紧凑的代码对 Java 对象进行映射，在更简单的情况下甚至可以实现零配置。它支持以下特性：

- 基于名称的对象属性映射
- 复制公开的、受保护的和私有的字段
- 略过某些字段
- 可用转换器来影响映射（如将字符串转换为小写）
- 在不同类型的字段间进行映射（如将字符串转换为数字）
- 采用不同的条件进行映射
- 默认条件不充分时采用松散的映射策略
- 对映射过程进行验证以确保所有字段都被处理
- 对特殊情况下的映射过程进行完全可定制化的控制
- 与 Guice 或 Spring 集成

在企业应用中，将对象从一种形式转换成另一种是非常普遍的模式。例如，某领域模型从数据库中加载，并需要在 GUI 上显示给用户。其原始数据库格式会包含大量用于生命周期的属性，而屏幕前的用户可能只关心其中的一两个字段。所以很多时候，用于数据库的 Pojo（JPA 实体）与用于 GUI 的 Pojo 是不同的。这正是 ModelMapper 试图解决的问题。一般来说，当信息在企业应用内的层之间发生改变时，就会发生对象转换。其他会发生对象转换的场景包括：

- 多个对象聚合成一个
- 在已存在的对象中计算一些额外的元数据
- 转换对象以便发送到外部系统中
- 未定义的属性里赋予默认值
- 通过某种方式来转换已有的属性（对象自映射）

# 快速开始

```java
// 源模型
class Order {
  Customer customer;
  Address billingAddress;
}

class Customer {
  Name name;
}

class Name {
  String firstName;
  String lastName;
}

class Address {
  String street;
  String city;
}

// 目标模型
class OrderDTO {
  String customerFirstName;
  String customerLastName;
  String billingStreet;
  String billingCity;
}
```

我们可以使用 ModelMapper 隐式地将一个订单实例映射到一个新的 OrderDTO。

```java
ModelMapper modelMapper = new ModelMapper();
OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

assertEquals(order.getCustomer().getName().getFirstName(), orderDTO.getCustomerFirstName());
assertEquals(order.getCustomer().getName().getLastName(), orderDTO.getCustomerLastName());
assertEquals(order.getBillingAddress().getStreet(), orderDTO.getBillingStreet());
assertEquals(order.getBillingAddress().getCity(), orderDTO.getBillingCity());
```

## 自定义转换

自定义有很多转换,比如 Provider，Converter，Condition，PropertyMap 等,下面是个综合的例子.

```java
/**
  * 简单类到类自定义字段
*/
@Test
public void testModelToDTOByDe(){
    User user = new User();
    user.setId(1L);
    user.setNickname("张三");
    user.setEmail("101@qq.com");
    user.setHonor("测试荣誉");
    ModelMapper modelMapper = new ModelMapper();

    // 转换内容提供者
    Provider<String> personProvider = new AbstractProvider<String>() {
        public String get() {
            return "自定义提供者";
        }
    };

    // 创建自定义转换规则
    Converter<String, String> toUppercase = new AbstractConverter<String, String>() {
        protected String convert(String source) {
            System.out.println(source);
            return source == null ? null : source.toUpperCase();
        }
    };

    // 创建自定义条件转换
    Condition<Long,?> gt2 = context -> {
        System.out.println(context.getSource());
        return context.getSource() > 2;
    };

    // 创建自定义映射规则
    PropertyMap<User,UserDTO> propertyMap = new PropertyMap<User, UserDTO>() {
        @Override
        protected void configure() {
            using(toUppercase).map(source.getNickname(),destination.getHonor());// 使用自定义转换规则
            with(personProvider).map(source.getHonor(),destination.getNickname());// 使用自定义属性提供覆盖
            map(source.getAvatar()).setAvatar(null);// 主动替换属性
            skip(destination.getEmail());
            when(gt2).map().setId(1L);// 过滤属性
        }
    };

    // 添加映射器
    modelMapper.addMappings(propertyMap);
    modelMapper.validate();

    // 转换
    UserDTO userDTO = modelMapper.map(user,UserDTO.class);
    System.out.println(userDTO);
}
```

Provider,Converter,Condition 三个都算是转换前奏,所有的转换规则都是在 PropertyMap 里面配置.所以分析这个里面的配置即可.

- `using(toUppercase).map(source.getNickname(),destination.getHonor());` 首先 toUppercase 是一个 Converter,也就是 sources 的 nickname 会经过这个转换器,然后才设置到 destination 的 honor 中。

- `with(personProvider).map(source.getHonor(),destination.getNickname());` personProvider 类似一个 Bean 工厂,当使用这个的时候,对于 sources 调用 getHonor()的时候实际上是调用 personProvider 的 get 方法.所以结果 nickname='自定义提供者'

- `map(source.getAvatar()).setAvatar(null);` // 主动替换属性，可以主动重设某些属性

- `skip(destination.getEmail());` 过滤指定属性

- `when(gt2).map().setId(1L);` 条件过滤属性,当满足 gt2 的时候才会调用 setId 方法.
