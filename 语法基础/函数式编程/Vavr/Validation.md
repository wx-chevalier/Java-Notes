# Vavr Validation

Vavr 将 Applicative Functor 的概念从函数式编程世界带到了 Java 中。用最简单的话来说，应用型 Functor 使我们能够执行一系列的操作，同时积累结果。vavr.control.Validation 这个类有利于错误的积累。请记住，通常情况下，一个程序一旦遇到错误就会终止。

但是，Validation 会继续处理和积累错误，让程序以批处理的方式对其进行处理。考虑到我们正在按姓名和年龄注册用户，我们要先接受所有输入，并决定是创建一个 Person 实例还是返回一个错误列表。这就是我们的 Person 类。

```java
public class Person {
    private String name;
    private int age;

    // standard constructors, setters and getters, toString
}
```

接下来，我们创建一个名为 PersonValidator 的类。每个字段都将由一个方法进行验证，另一个方法可以用来将所有结果合并成一个 Validation 实例。

```java
class PersonValidator {
    String NAME_ERR = "Invalid characters in name: ";
    String AGE_ERR = "Age must be at least 0";

    public Validation<Seq<String>, Person> validatePerson(
      String name, int age) {
        return Validation.combine(
          validateName(name), validateAge(age)).ap(Person::new);
    }

    private Validation<String, String> validateName(String name) {
        String invalidChars = name.replaceAll("[a-zA-Z ]", "");
        return invalidChars.isEmpty() ?
          Validation.valid(name)
            : Validation.invalid(NAME_ERR + invalidChars);
    }

    private Validation<String, Integer> validateAge(int age) {
        return age < 0 ? Validation.invalid(AGE_ERR)
          : Validation.valid(age);
    }
}
```

年龄的规则是它应该是一个大于 0 的整数，而名字的规则是它不应该包含特殊字符。

```java
@Test
public void whenValidationWorks_thenCorrect() {
    PersonValidator personValidator = new PersonValidator();

    Validation<List<String>, Person> valid =
      personValidator.validatePerson("John Doe", 30);

    Validation<List<String>, Person> invalid =
      personValidator.validatePerson("John? Doe!4", -1);

    assertEquals(
      "Valid(Person [name=John Doe, age=30])",
        valid.toString());

    assertEquals(
      "Invalid(List(Invalid characters in name: ?!4,
        Age must be at least 0))",
          invalid.toString());
}
```

一个有效的值包含在 Validation.Valid 实例中，一个验证错误的列表包含在 Validation.Invalid 实例中。所以任何验证方法都必须返回两者之一。

# Links

- https://www.baeldung.com/vavr-validation-api
