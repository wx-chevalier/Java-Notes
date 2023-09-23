# ModelMapper

ModelMapper 是轻量级的帮助我们在不同的 Java POJO 之间进行转换的工具库，其引入方式如下：

```xml
<dependency>
  <groupId>org.modelmapper</groupId>
  <artifactId>modelmapper</artifactId>
  <version>2.3.0</version>
</dependency>
```

# 基础使用

## Mapping

```java
// Source Model
// Assume getters and setters on each class
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

// Destination Model
// Assume getters and setters
class OrderDTO {
  String customerFirstName;
  String customerLastName;
  String billingStreet;
  String billingCity;
}
```

然后可以使用 ModelMapper 来将 order 实例映射到 OrderDTO：

```java
ModelMapper modelMapper = new ModelMapper();
OrderDTO orderDTO = modelMapper.map(order, OrderDTO.class);

assertEquals(order.getCustomer().getName().getFirstName(), orderDTO.getCustomerFirstName());
assertEquals(order.getCustomer().getName().getLastName(), orderDTO.getCustomerLastName());
assertEquals(order.getBillingAddress().getStreet(), orderDTO.getBillingStreet());
assertEquals(order.getBillingAddress().getCity(), orderDTO.getBillingCity());
```

## Explicit Mapping

很多时候我们也可以手动地指明映射规则：

```java
modelMapper.addMappings(mapper -> {
  mapper.map(src -> src.getBillingAddress().getStreet(),
      Destination::setBillingStreet);
  mapper.map(src -> src.getBillingAddress().getCity(),
      Destination::setBillingCity);
});
```

# Spring 中 Entity 与 DTO 的转化

## DTO 层

以 Post 为例，我们的 DTO 定义如下：

```java
public class PostDto {
  private static final SimpleDateFormat dateFormat = new SimpleDateFormat(
    "yyyy-MM-dd HH:mm"
  );

  private Long id;

  private String title;

  private String url;

  private String date;

  private UserDto user;

  public Date getSubmissionDateConverted(String timezone)
    throws ParseException {
    dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
    return dateFormat.parse(this.date);
  }

  public void setSubmissionDate(Date date, String timezone) {
    dateFormat.setTimeZone(TimeZone.getTimeZone(timezone));
    this.date = dateFormat.format(date);
  }
// standard getters and setters
}
```

这里值得注意的是对于两个日期相关的处理方法：

- getSubmissionDateConverted() 方法根据传入的 timezone 将当前时间转化为服务端内置的 Post 实例所需要的时区。
- setSubmissionDate 则是将 Post 时区转化为用户当前的时区，以返回给用户。

## Service 层

服务层的操作如下：

```java
public List<Post> getPostsList(
  int page, int size, String sortDir, String sort) {

    PageRequest pageReq
     = PageRequest.of(page, size, Sort.Direction.fromString(sortDir), sort);

    Page<Post> posts = postRepository
      .findByUser(userService.getCurrentUser(), pageReq);

    return posts.getContent();
}
```

## Controller 层

Controller 中会调用对应的 Service 中的方法来获取数据，并且将其转化为用户侧的 DTO 数据：

```java
@Controller
class PostRestController {

    @Autowired
    private IPostService postService;

    @Autowired
    private IUserService userService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseBody
    public List<PostDto> getPosts(...) {
        //...
        List<Post> posts = postService.getPostsList(page, size, sortDir, sort);
        return posts.stream()
          .map(this::convertToDto)
          .collect(Collectors.toList());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @ResponseBody
    public PostDto createPost(@RequestBody PostDto postDto) {
        Post post = convertToEntity(postDto);
        Post postCreated = postService.createPost(post));
        return convertToDto(postCreated);
    }

    @GetMapping(value = "/{id}")
    @ResponseBody
    public PostDto getPost(@PathVariable("id") Long id) {
        return convertToDto(postService.getPostById(id));
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void updatePost(@RequestBody PostDto postDto) {
        Post post = convertToEntity(postDto);
        postService.updatePost(post);
    }
}
```

我们实际的转化函数定义如下：

```java
private PostDto convertToDto(Post post) {
    PostDto postDto = modelMapper.map(post, PostDto.class);
    postDto.setSubmissionDate(post.getSubmissionDate(),
        userService.getCurrentUser().getPreference().getTimezone());
    return postDto;
}

private Post convertToEntity(PostDto postDto) throws ParseException {
    Post post = modelMapper.map(postDto, Post.class);
    post.setSubmissionDate(postDto.getSubmissionDateConverted(
      userService.getCurrentUser().getPreference().getTimezone()));

    if (postDto.getId() != null) {
        Post oldPost = postService.getPostById(postDto.getId());
        post.setRedditID(oldPost.getRedditID());
        post.setSent(oldPost.isSent());
    }
    return post;
}
```
