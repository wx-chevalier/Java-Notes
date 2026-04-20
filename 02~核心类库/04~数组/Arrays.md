# Arrays

java.util.Arrays 类能方便地操作数组，它提供的所有方法都是静态的。具有以下功能：

- 给数组赋值：通过 fill 方法。
- 对数组排序：通过 sort 方法,按升序。
- 比较数组：通过 equals 方法比较数组中元素值是否相等。
- 查找数组元素：通过 binarySearch 方法能对排序好的数组进行二分查找法操作。

具体说明请查看下表：

| 序号 | 方法和说明                                                                                                                                                                                                                                                                                                                                     |
| :--- | :--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------- |
| 1    | **public static int binarySearch(Object[] a, Object key)** 用二分查找算法在给定数组中搜索给定值的对象(Byte,Int,double 等)。数组在调用前必须排序好的。如果查找值包含在数组中，则返回搜索键的索引；否则返回 (-(_插入点_) - 1)                                                                                                                    |
| 2    | **public static boolean equals(long[] a, long[] a2)** 如果两个指定的 long 型数组彼此*相等*，则返回 true。如果两个数组包含相同数量的元素，并且两个数组中的所有相应元素对都是相等的，则认为这两个数组是相等的。换句话说，如果两个数组以相同顺序包含相同的元素，则两个数组是相等的。同样的方法适用于所有的其他基本数据类型（Byte，short，Int 等） |
| 3    | **public static void fill(int[] a, int val)** 将指定的 int 值分配给指定 int 型数组指定范围中的每个元素。同样的方法适用于所有的其他基本数据类型（Byte，short，Int 等）                                                                                                                                                                          |
| 4    | **public static void sort(Object[] a)** 对指定对象数组根据其元素的自然顺序进行升序排列。同样的方法适用于所有的其他基本数据类型（Byte，short，Int 等）                                                                                                                                                                                          |

```java
import java.util.Arrays;

public class TestArrays {
    public static void output(int[] array) {
        if (array != null) {
            for (int i = 0; i < array.length; i++) {
                System.out.print(array[i] + " ");
            }
        }
        System.out.println();
    }

    public static void main(String[] args) {
        int[] array = new int[5];
        // 填充数组
        Arrays.fill(array, 5);
        System.out.println("填充数组：Arrays.fill(array, 5)：");
        TestArrays.output(array);
        // 将数组的第2和第3个元素赋值为8
        Arrays.fill(array, 2, 4, 8);
        System.out.println("将数组的第2和第3个元素赋值为8：Arrays.fill(array, 2, 4, 8)：");
        TestArrays.output(array);
        int[] array1 = { 7, 8, 3, 2, 12, 6, 3, 5, 4 };
        // 对数组的第2个到第6个进行排序进行排序
        Arrays.sort(array1, 2, 7);
        System.out.println("对数组的第2个到第6个元素进行排序进行排序：Arrays.sort(array,2,7)：");
        TestArrays.output(array1);
        // 对整个数组进行排序
        Arrays.sort(array1);
        System.out.println("对整个数组进行排序：Arrays.sort(array1)：");
        TestArrays.output(array1);
        // 比较数组元素是否相等
        System.out.println("比较数组元素是否相等:Arrays.equals(array, array1):" + "\n" + Arrays.equals(array, array1));
        int[] array2 = array1.clone();
        System.out.println("克隆后数组元素是否相等:Arrays.equals(array1, array2):" + "\n" + Arrays.equals(array1, array2));
        // 使用二分搜索算法查找指定元素所在的下标（必须是排序好的，否则结果不正确）
        Arrays.sort(array1);
        System.out.println("元素3在array1中的位置：Arrays.binarySearch(array1, 3)：" + "\n" + Arrays.binarySearch(array1, 3));
        // 如果不存在就返回负数
        System.out.println("元素9在array1中的位置：Arrays.binarySearch(array1, 9)：" + "\n" + Arrays.binarySearch(array1, 9));
    }
}
```

# 复制与扩容

数组容量如果不够用可以使用 Arrays.copyOf() 进行扩容：

```java
Array.copy(E[] e,newLength);
```

其第一个形参指的是需要扩容的数组，后面是扩容后的大小，其内部实现其实是使用了 System.arrayCopy(); 在内部重新创建一个长度为 newLength 类型是 E 的数组。

```java
import java.util.Arrays;
public class Main {
    public static void main(String[] args) {
        int[] a= {10,20,30,40,50};
        a= Arrays.copyOf(a,a.length+1);
        for(int i=0;i<a.length;i++) {
            System.out.println(a[i]);
        }
    }
}
```

默认补 **0**，输出结果为：**10 20 30 40 50 0**

# 排序

Arrays.sort 并不是单一的排序，而是插入排序，快速排序，归并排序三种排序的组合，为此我画了个流程图：

![排序流程图](https://s2.ax1x.com/2020/02/06/16bl7V.png)

O(nlogn)只代表增长量级，同一个量级前面的常数也可以不一样，不同数量下面的实际运算时间也可以不一样。

- 数量非常小的情况下（就像上面说到的，少于 47 的），插入排序等可能会比快速排序更快所以数组少于 47 的会进入插入排序。
- 快排数据越无序越快（加入随机化后基本不会退化），平均常数最小，不需要额外空间，不稳定排序。
- 归排速度稳定，常数比快排略大，需要额外空间，稳定排序。

## 基本类型排序

`Arrays.sort(int[] a)`，这种形式是对于一个数组的元素进行排序，按照从小到大的顺序：

```java
import java.util.Arrays;

public class sort1 {
	public static void main(String args[]) {
		int[] a= {9,8,7,6,4,5,3,1,2};
		Arrays.sort(a);
		for(int i=0;i<a.length;i++) {
			System.out.print(a[i]+"");
		}

	}

}
```

`Arrays.sort(int [] a,int fromIndex,int toIndex)` 这种形式是对数组部分排序，也就是对数组 a 的下标从 fromIndex 到 toIndex-1 的元素排序，注意：下标为 toIndex 的元素不参与排序：

```java
import java.util.Arrays;

public class sort2 {
	public static void main(String args[]) {
		int[] a= {9,2,5,1,6,1,4,3,};
		Arrays.sort(a,0,3);
		for(int i=0;i<a.length;i++) {
			System.out.print(a[i]+"");
		}
	}

}
```

## 自定义排序器

```java
import java.util.Arrays;
import java.util.Comparator;

public class sort3 {
	public static void main(String args[]) {
		Integer[] a= {9,5,6,1,3,2,4,7,8,0};
		Comparator cmp=new MyComparator();
		Arrays.sort(a);
		for(int i=0;i<a.length;i++) {
			System.out.print(a[i]+"");
		}
	}

}
class MyComparator implements Comparator<Integer>{

	@Override
	public int compare(Integer o1, Integer o2) {
		// TODO Auto-generated method stub
		if(o1<o2) {
			return 1;
		}
		else if(o1>o2) {
			return -1;

		}else {
			return 0;
		}
	}

}
```

## 对象排序

当我们给一个整型数组或者浮点型之类的数组排序的时候，很简单就可以达到我们排序的目的，无非是排序算法的问题。那么，如果我们现在想根据对象的一个属性值给一个对象数组进行排序。假如我们现在有一个 Car 类型，Car 类中有一个 double 型的 speed 属性用来描述车辆的速度，现在我们想根据车速来对一个 Car 数组中的车辆进行排序：

```java
public class Car{
	private double speed;//车辆的速度属性

	public Car(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}
}
```

用 Array.sort()方法实现对车辆排序的代码：

```java
public class Car implements Comparable<Car>{
	private double speed;

	public Car(double speed) {
		this.speed = speed;
	}

	public double getSpeed() {
		return speed;
	}

	public void setSpeed(double speed) {
		this.speed = speed;
	}

	@Override
	public int compareTo(Car newCar) {
		return Double.compare(this.getSpeed(),newCar.getSpeed());
	}
}
```

# 搜索

- `public static int binarySearch(Object[] a, Object key)`：用二分查找算法在给定数组中搜索给定值的对象(Byte,Int,double 等)。数组在调用前必须排序好的。如果查找值包含在数组中，则返回搜索键的索引；否则返回 (-(插入点) - 1)。
