# HSF SPI

HSF 用自己的方式重新实现了一套 SPI 机制，不使用 Java 原生的 SPI 机制。

- @Name：用于标注一个实现，相当于给这个拓展实现起了个别名。
- @Order：当一个接口类有多个拓展时，拓展的先后顺序能够通过该注解进行定义，例如，用于责任链的构造顺序。
- @Scope：用来描述一个扩展是否多实例，默认为单例，如果使用多例，则会在线程请求时，才创建并存放于 ThreadLocal 当中。
- @Tag：用来描述一个服务实现的标记,可以使用这个注解形容一个接口的若干服务扩展，例如，当我们需要加载具有某类 tag 的扩展类时，我们可以指定 tag 列表，当指定的扩展实现类含有指定的 tag 时，则说明该扩展类符合条件。
- @Shared：用来描述一个服务接口类型，被标注的服务是一个共享服务，表示该服务的实例将放置在 Shared Container 中。
- HSFServiceContainer：该类为 hsf 加载 spi 的一个门面容器类，调用方统一使用该类提供的方法加载扩展服务类。
- AppServiceContainer：该类为 spi 的具体实现类，你可以把它称为应用服务加载器。
- ApplicationModel：代表了一个应用实例，持有了应用类所对应的类加载器，而 AppServiceContainer 只要委托该类加载器加载拓展服务实例类即可。

# HSFServiceContainer

HSFServiceContainer 作为加载 SPI 的门面容器类，提供了一系列的方法来加载拓展服务类，以下是它的代码，注意观察几点：

- SHARED_CONTAINER 作为共享的容器，是最顶层的容器，无需指定父类容器以及用户应用模型（而其他的 AppServiceContainer 需要）；
- 方法 createAppServiceContainer()决定了通过 HSFServiceContainer 创建的容器，他们的父容器一定是 SHARED_CONTAINER
- 一系列重载的 getInstances(...)方法中调用了 AppServiceContainer 中的同名方法，而该方法中内含了委派加载的逻辑。

```java
public class HSFServiceContainer {
  // 共享的容器，它不隶属与任何一个应用
  public static final AppServiceContainer SHARED_CONTAINER = new AppServiceContainer();

  // 创建一个AppServiceContainer
  public static AppServiceContainer createAppServiceContainer(
    ApplicationModel applicationModel
  ) {
    return new AppServiceContainer(applicationModel, SHARED_CONTAINER);
  }

  // 根据一个接口类型，获取容器中的一个服务实例
  public static <T> T getInstance(Class<T> classType) {
    AppServiceContainer appServiceContainer = getAppServiceContainer(classType);
    return appServiceContainer.getInstance(classType);
  }

  // 根据一个接口类型，获取容器中所有的拓展服务实例
  public static <T> List<T> getInstances(Class<T> classType, String... tags) {
    AppServiceContainer appServiceContainer = getAppServiceContainer(classType);
    return appServiceContainer.getInstances(classType, tags);
  }

  /**
   * 根据接口类型，返回所有的扩展实例
   * 可以传入一组名称，如果该名称的类型是可选Optional，通过withDefault可以控制是否加载默认的实现
   * @param classType   接口类型
   * @param names       名称列表，如果传递空表示所有的类型
   * @param withDefault 是否包含默认
   * @param <T>         类型
   * @return 实现列表, 如果不存在返回为空集合
   */
  public static <T> List<T> getInstances(
    Class<T> classType,
    String[] names,
    boolean withDefault
  ) {
    AppServiceContainer appServiceContainer = getAppServiceContainer(classType);
    return appServiceContainer.getInstances(
      classType,
      names,
      new String[] {},
      withDefault
    );
  }

  /**
   * 根据接口类型获取合适的 AppServiceContainer
   * 如果是@Shared，那么直接获取 SHARED_CONTAINER
   * 否则，根据上下文获取当前的 AppServiceContainer
   */
  private static <T> AppServiceContainer getAppServiceContainer(
    Class<T> classType
  ) {
    return AppServiceContainer.isSharedType(classType) ? SHARED_CONTAINER
      : ApplicationModelFactory.getCurrentApplication().getServiceContainer();
  }
}
```
