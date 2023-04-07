# Object Mapping

Object mapping 的技术分类：

- 运行期 反射调用 set/get 或者是直接对成员变量赋值 。 该方式通过 invoke 执行赋值，实现时一般会采用 beanutil, Javassist 等开源库。这类的代表：Dozer,ModelMaper

- 编译期 动态生成 set/get 代码的 class 文件 ，在运行时直接调用该 class 文件。该方式实际上扔会存在 set/get 代码，只是不需要自己写了。 这类的代表：MapStruct,Selma,Orika
