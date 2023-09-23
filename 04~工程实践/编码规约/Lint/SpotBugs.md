# SpotBugs

SpotBugs 是 Findbugs 的继任者，（Findbugs 已经于 2016 年后不再维护），用于对代码进行静态分析，查找相关的漏洞。目前 SpotBugs 3.1.3 自带检测器，其中有 90 余种 Bad practice，155 余种 Correctness，9 种 Experimental，2 种 Internationalization，17 种 Malicious code vulnerability，46 种 Multithreaded correctness,4 种 Bogus random noise，37 种 Performance，11 种 Security,87 种 Dodgy。

- Bad practice 不佳实践：常见代码错误，用于静态代码检查时进行缺陷模式匹配(如重写 equals 但没重写 hashCode，或相反情况等)
- Correctness 可能导致错误的代码(如空指针引用、无限循环等)
- Experimental 实验性
- Internationalization 国际化相关问题（如错误的字符串转换等）
- Malicious code vulnerability 可能受到的恶意攻击（如访问权限修饰符的定义等）
- Multithreaded correctness 多线程的正确性（如多线程编程时常见的同步，线程调度问题等）
- BogusMultithreaded correctness 多线程的正确性（如多线程编程时常见的同步，线程调度问题等）
- Performance 运行时性能问题（如由变量定义，方法调用导致的代码低效问题等）
- Security 安全问题（如 HTTP，SQL，DB 等）
- Dodgy code 导致自身错误的代码（如未确认的强制转换、冗余的空值检查等）

# Maven

Maven 插件方式使用 spotbugs 及相关插件：

```xml
<plugin>
	<groupId>com.github.spotbugs</groupId>
	<artifactId>spotbugs-maven-plugin</artifactId>
	<version>3.1.1</version>
	<configuration>
		<plugins>
			<plugin>
				<groupId>com.h3xstream.findsecbugs</groupId>
				<artifactId>findsecbugs-plugin</artifactId>
				<version>LATEST</version>
			</plugin>
			<plugin>
				<groupId>com.mebigfatguy.fb-contrib</groupId>
				<artifactId>fb-contrib</artifactId>
				<version>7.4.3.sb</version>
			</plugin>
		</plugins>
	</configuration>
</plugin>
```

# Gradle

```js
buildscript {
    repositories {
        mavenLocal()
        maven { url "https://plugins.gradle.org/m2/" }
    }
    dependencies {
        classpath "com.diffplug.spotless:spotless-plugin-gradle:3.24.1"
        classpath "gradle.plugin.com.github.spotbugs:spotbugs-gradle-plugin:2.0.0"
    }
}

plugins {
    id "com.github.johnrengelman.shadow" version "5.1.0"
    id 'org.springframework.boot' version "2.1.7.RELEASE"
}

apply plugin: "com.github.spotbugs"

spotbugs {
    includeFilter = file("$rootDir/buildscripts/spotbugs-filter.xml")
}

tasks.withType(com.github.spotbugs.SpotBugsTask) {
    reports {
        xml.enabled = false
        html.enabled = true
    }
}

spotless {
    format 'misc', {
        target '**/*.gradle', '**/.gitignore'

        trimTrailingWhitespace()
        endWithNewline()
    }
    java {
        removeUnusedImports()
        googleJavaFormat()
    }
    freshmark {
        target '**/*.md'
    }
}
```

其中 spotbugs-filter.xml 定义如下：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<FindBugsFilter
  xmlns="https://github.com/spotbugs/filter/3.0.0"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
  xsi:schemaLocation="https://github.com/spotbugs/filter/3.0.0 https://raw.githubusercontent.com/spotbugs/spotbugs/3.1.0/spotbugs/etc/findbugsfilter.xsd">
  <Match>
  </Match>
</FindBugsFilter>
```
