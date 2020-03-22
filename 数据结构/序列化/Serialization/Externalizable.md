# Serializable 和 Externalizable

Java 类通过实现 java.io.Serializable 接口以启用其序列化功能。未实现此接口的类将无法进行序列化或反序列化。可序列化类的所有子类型本身都是可序列化的。

如果读者看过 Serializable 的源码，就会发现，他只是一个空的接口，里面什么东西都没有。Serializable 接口没有方法或字段，仅用于标识可序列化的语义。但是，如果一个类没有实现这个接口，想要被序列化的话，就会抛出 java.io.NotSerializableException 异常。

原因是在执行序列化的过程中，会执行到以下代码：

if (obj instanceof String) {
writeString((String) obj, unshared);
} else if (cl.isArray()) {
writeArray(obj, desc, unshared);
} else if (obj instanceof Enum) {
writeEnum((Enum<?>) obj, desc, unshared);
} else if (obj instanceof Serializable) {
writeOrdinaryObject(obj, desc, unshared);
} else {
if (extendedDebugInfo) {
throw new NotSerializableException(
cl.getName() + "\n" + debugInfoStack.toString());
} else {
throw new NotSerializableException(cl.getName());
}
}
在进行序列化操作时，会判断要被序列化的类是否是 Enum、Array 和 Serializable 类型，如果都不是则直接抛出 NotSerializableException。

Java 中还提供了 Externalizable 接口，也可以实现它来提供序列化能力。

Externalizable 继承自 Serializable，该接口中定义了两个抽象方法：writeExternal()与 readExternal()。

当使用 Externalizable 接口来进行序列化与反序列化的时候需要开发人员重写 writeExternal()与 readExternal()方法。否则所有变量的值都会变成默认值。

transient

transient 关键字的作用是控制变量的序列化，在变量声明前加上该关键字，可以阻止该变量被序列化到文件中，在被反序列化后，transient 变量的值被设为初始值，如 int 型的是 0，对象型的是 null。

自定义序列化策略

在序列化过程中，如果被序列化的类中定义了 writeObject 和 readObject 方法，虚拟机会试图调用对象类里的 writeObject 和 readObject 方法，进行用户自定义的序列化和反序列化。

如果没有这样的方法，则默认调用是 ObjectOutputStream 的 defaultWriteObject 方法以及 ObjectInputStream 的 defaultReadObject 方法。

用户自定义的 writeObject 和 readObject 方法可以允许用户控制序列化的过程，比如可以在序列化的过程中动态改变序列化的数值。

所以，对于一些特殊字段需要定义序列化的策略的时候，可以考虑使用 transient 修饰，并自己重写 writeObject 和 readObject 方法，如 java.util.ArrayList 中就有这样的实现。
