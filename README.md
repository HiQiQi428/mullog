
# mullog

![Shippable branch](https://img.shields.io/shippable/5444c5ecb904a4b21567b0ff/master.svg) ![Maven metadata URI](https://img.shields.io/maven-metadata/v/http/central.maven.org/maven2/com/google/code/gson/gson/maven-metadata.xml.svg)

### Overview
Java 日志工具，实现了UDP输出、File输出、Console输出，总之很简洁（不是简单hh）有点喜欢造轮子，希望越造越大。
### Get started

##### step 1：下载源码用maven安装到本地

```sh
git clone https://github.com/HiQiQi428/mullog
cd mullog
mvn install
```

#### step 2：在你的项目里添加依赖

```
<dependency>
    <groupId>org.luncert</groupId>
    <artifactId>mullog</artifactId>
    <version>1.0</version>
</dependency>
```

#### step 3： 创建```mullog.properties```在 resources 目录下

```properties
udp.type=org.luncert.mullog.appender.UDPAppender
udp.host=localhost
udp.port=16000
udp.format=%T [%L] %S %S

console1.level=INFO
console1.type=org.luncert.mullog.appender.ConsoleAppender
console1.format=<Console1> %T %L [%t] %M %C %S %S %S

console2.level=INFO
console2.type=org.luncert.mullog.appender.ConsoleAppender
console2.format=<Console2> %S %S

// FileAppender好早写的了，说不定还有bug，抽空再看看吧
file.level=INFO
file.file=./test.log
file.type=org.luncert.mullog.appender.FileAppender
file.maxSize=1
file.format=<Test file> %S %S
```

##### format参数说明：

* %T：时间戳
* %L：日志等级
* %M：调用者方法名
* %C：调用者类名
* %t：线程名
* %S：调用时传入的参数

### step 4： 使用 Mullog

```java
public class HelloWorld {
    // 传入的参数是 mullog.properties 中定义的 appender 的名字，前面定义了有：file、console1、console2、udp四个 appender
    private static Mullog mullog = new Mullog("console1");
    
    public void say() {
        mullog.info("Hello", "World", "!");
        // 暂时切换 appender 输出，setTmpAppender 的返回值是 Optional<Mullog>
        mullog.setTmpAppender("console2").ifPresent((log) ->
                log.info("this is appender console2"));
    }
}
```
* 切换 appender：```mullog.setAppender("cnosole2");```。
* 更改 appender 日志等级：```mullog.getUsingAppender().setLogLevel(LogLevel.DEBUG)```。

### exp step：自定义 Appender

两种方式：

* 实现 ```org.luncert.mullog.appender.Appender```接口，肯定有点麻烦啊。
* 继承```org.luncert.mullog.appender.StandardAppender```抽象类，很简单，只需要实现```protected abstract void output(String data) throws Exception;```方法，定义如何输出消息就行了，不用管如何读取 mullog.properties 以及如何解析 format 字符串！

じゃね！