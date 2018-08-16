
## Maven 配置

hydrogen-dao 没有发布到中心库，因此你需要下载源代码自己编译安装。

新建一个空的 Maven 项目，在 pom.xml 中添加下面的依赖关系：

```xml
<!--演示用内存 SQL 数据库 HSqlDB-->
<dependency>
    <groupId>org.hsqldb</groupId>
    <artifactId>hsqldb</artifactId>
    <version>2.3.4</version>
</dependency>
<!--hydrogen-dao 本身-->
<dependency>
    <groupId>com.hyd</groupId>
    <artifactId>hydrogen-dao</artifactId>
    <version>2.5.0</version>
</dependency>
<!--输出日志-->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.1.2</version>
</dependency>
```