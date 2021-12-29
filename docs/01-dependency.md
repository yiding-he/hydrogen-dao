
## Maven 配置

hydrogen-dao 的发布地址：

https://mvnrepository.com/artifact/com.github.yiding-he/hydrogen-dao

### 配置依赖关系：

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
    <groupId>com.github.yiding-he</groupId>
    <artifactId>hydrogen-dao</artifactId>
    <version>${hydrogen-dao.version}</version>
</dependency>
<!--输出日志-->
<dependency>
    <groupId>ch.qos.logback</groupId>
    <artifactId>logback-classic</artifactId>
    <version>1.1.2</version>
</dependency>
```