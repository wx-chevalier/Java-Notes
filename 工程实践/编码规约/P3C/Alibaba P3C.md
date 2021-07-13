# Alibaba P3C

# Maven

代码质量审查，需要用到阿里规约的检查，尽管建议大家在客户端自己的 IDE 中进行了扫描，但是难免会有人忘记。因此，在 CI 过程中，利用 maven 插件做一次统一的自动化扫描。

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <!-- ...... -->

    <properties>
        <maven.compiler.source>1.8</maven.compiler.source>
        <maven.compiler.target>1.8</maven.compiler.target>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-compiler-plugin</artifactId>
                <version>3.6.1</version>
                <configuration>
                    <source>1.8</source>
                    <target>1.8</target>
                    <encoding>UTF-8</encoding>
                    <!-- java8 保留参数名编译参数，支持 action 带参 -->
                    <compilerArgument>-parameters</compilerArgument>
                    <compilerArguments>
                        <verbose/>
                    </compilerArguments>
                </configuration>
            </plugin>

            <!-- 编码规约扫描命令：mvn pmd:pmd-->
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-pmd-plugin</artifactId>
                <version>3.8</version>
                <configuration>
                    <rulesets>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-comment.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-concurrent.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-constant.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-exception.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-flowcontrol.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-naming.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-oop.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-orm.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-other.xml</ruleset>
                        <ruleset>src/main/resources/p3c-pmd/rulesets/java/ali-set.xml</ruleset>
                    </rulesets>
                    <printFailingErrors>true</printFailingErrors>
                </configuration>
                <executions>
                    <!-- 绑定pmd:check到verify生命周期 -->
                    <execution>
                        <id>pmd-check-verify</id>
                        <phase>verify</phase>
                        <goals>
                            <goal>check</goal>
                        </goals>
                    </execution>
                    <!-- 绑定pmd:pmd到site生命周期 -->
                    <execution>
                        <id>pmd-pmd-site</id>
                        <phase>site</phase>
                        <goals>
                            <goal>pmd</goal>
                        </goals>
                    </execution>
                </executions>
                <!-- p3c依赖 -->
                <dependencies>
                    <dependency>
                        <groupId>com.alibaba.p3c</groupId>
                        <artifactId>p3c-pmd</artifactId>
                        <version>1.3.6</version>
                    </dependency>
                </dependencies>
            </plugin>
        </plugins>
    </build>
    <!-- 用于生成错误到代码内容的链接 -->
    <reporting>
        <plugins>
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jxr-plugin</artifactId>
                <version>2.3</version>
            </plugin>
        </plugins>
    </reporting>
</project>
```

配置完毕后，执行 `mvn site` 即可以生成报告，找到 target 目录下的 index.html 点击打开；找到 target 目录下的 pmd.html 打开。

## CI 集成

根据需求绑定到不同的生命周期，例如：绑定到 package 阶段，修改 executions 即可。

```xml
<!--绑定pmd:check到verify生命周期-->
<execution>
    <id>pmd-check-verify</id>
    <phase>package</phase>
    <goals>
        <goal>check</goal>
    </goals>
</execution>
```

根据需求控制每次需要检查的规约，例如：只检查命名规范，则需要修改 rulesets 标签中的内容，只保留 naming 这一条规则。注释其他的规则。

```xml
<rulesets>
	<!--<ruleset>rulesets/java/ali-comment.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-concurrent.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-constant.xml</ruleset>-->
    <!--<ruleset>rulesets/java/ali-exception.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-flowcontrol.xml</ruleset>-->
	 <ruleset>rulesets/java/ali-naming.xml</ruleset>
	<!--<ruleset>rulesets/java/ali-oop.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-orm.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-other.xml</ruleset>-->
	<!--<ruleset>rulesets/java/ali-set.xml</ruleset>-->
</rulesets>
```

# Gradle

# IDE

## IDEA

!['Alibaba Java Coding Guidelines' plugin](https://github.com/alibaba/p3c/raw/master/idea-plugin/doc/images/install_2.png)
