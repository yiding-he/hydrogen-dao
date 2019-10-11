<p>
  <a href="https://github.com/996icu/996.ICU/blob/master/LICENSE">
    <img alt="996icu" src="https://img.shields.io/badge/license-NPL%20(The%20996%20Prohibited%20License)-blue.svg">
  </a>
  <a href="https://www.apache.org/licenses/LICENSE-2.0">
    <img alt="code style" src="https://img.shields.io/badge/license-Apache%202-4EB1BA.svg?style=flat-square">
  </a>
</p>

# hydrogen-dao

![如何使用](https://user-images.githubusercontent.com/900606/66099687-6e705e80-e5da-11e9-903c-fd7aee85f042.png)

hydrogen-dao 是一个 Java 的轻量级的数据库访问库，依赖标准的 JDBC 接口。主要功能有：

* 执行带参数的查询和更新；
* 查询结果自动转为 Java Bean；
* 根据参数值来动态组装 SQL 语句；
* 简化数据库事务；
* 简化分页查询和批处理；
* 连接池管理，状态查看。

使用方法参考源码下的 `docs` 目录。

当前分支的版本为 `3.5.0`。

## 添加依赖关系

请在 pom.xml 的 `<dependencies>` 元素当中添加下面的内容：

```xml
<dependency>
    <groupId>com.github.yiding-he</groupId>
    <artifactId>hydrogen-dao</artifactId>
    <version>${hydrogen-dao.version}</version>
</dependency>
```

## 示例

### 初始化

```java
// 初始化 DataSource 对象
DataSource dataSource = new com.zaxxer.hikari.HikariDataSource();
... 

// 初始化 DataSources 对象。DataSources 中可包含多个数据源。
com.hyd.dao.DataSources dataSources = new DataSources();
dataSources.setDataSource("default", dataSource);

// 获得针对某个数据源的 DAO 对象
DAO dao = dataSources.getDAO("default");
```

### Spring Boot 自动初始化

如果你的项目是基于 Spring Boot，那么可以简化上面的过程。首先添加下面的依赖关系：

```xml
<dependency>
	<groupId>com.github.yiding-he</groupId>
	<artifactId>spring-boot-starter-hydrogen-dao</artifactId>
	<version>${hydrogen-dao.version}</version>
</dependency>
```

然后在 `application.properties` 中配置数据源：

```properties
spring.datasource.url = [JDBC URL]
spring.datasource.driver-class-name = [JDBC Driver]
spring.datasource.username = [USERNAME]
spring.datasource.password = [PASSWORD]
```

这样就可以在代码中直接获取 DAO 对象了，例如：

```java
@Controller
public class HomeController {

  @Autowired
  private DAO dao;  // 直接获取 DAO 对象
}
```

### 查询记录

```Java
List<User> userList = dao.query(
        User.class,                                         // 包装类（可选）
        "select * from USER where NAME like ? and ROLE=?",  // 语句
        "admin%", 3);                                       // 参数（可选）
        
userList.forEach(user -> {
    System.out.println("user name: " + user.getName());
});
```

### 执行带参数名的 SQL

```Java
MappedCommand cmd = 
        new MappedCommand("update USERS set ROLE=#role# where ID in(#userid#)")
        .setParam("role", "admin")
        .setParam("userid", 1, 2, 3, 4);  // 数组或 List 都可以
dao.execute(cmd);
```

> `MappedCommand` 并非用字符串替换来生成最终 SQL，而仍然使用 PreparedStatement 并设置每个参数，以保证安全性。

### 构造动态查询条件

_不用写恶心的 `where 1=1` 了_

```Java
dao.query(SQL
        .Select("ID", "NAME", "DESCRIPTION")
        .From("USERS")
        .Where("ID in ?", 10, 22, 135)                 // 会自动扩展为 "ID in (?,?,?)"。也可以用 List 作为参数
        .And(disabled != null, "DISABLED=?", disabled) // 仅当变量 disabled 值不为 null 时才会加入该查询条件
        .AndIfNotEmpty("DISABLED=?", disabled)         // 效果同上
);
```

### 执行事务

```Java
final DAO dao = getDAO();

DAO.runTransaction(() -> {  // 所有事务都以 Runnable 的方式执行，简单明了
    dao.execute("insert into USER(id,name) values(?,?)", 1, "user1");
    throw new RuntimeException();    // 之前的 insert 将会回滚，同时抛出该异常
});
```

## 更新

#### 2019-10-11

* 版本号更新到 3.5.0；
* 用 Swing 重写代码生成工具；
* 删除之前的基于 JavaFX 的代码生成工具；

#### 2019-10-02

* 将 Spring Boot 自动初始化移到单独的模块独立发布

#### 2019-10-01

* hydrogen-dao 版本 3.3.0 发布到了 Maven 中心库。

#### 2019-08-12

* Spring Boot 自动配置现在只支持单数据源，因为 Spring JDBC 本身只支持这么做。

#### 2019-03-11

* 版本号升级到 3.3.0-SNAPSHOT
* 允许自定义数据库字段名和类属性名之间的映射规则，参见 `DataSources.setColumnNameConverter()`

#### 2019-01-17

* 修改 Spring Boot 自动配置部分的[相关文档](docs/09-spring-boot-autoconfig.md)

#### 2018-07-09

* 实现基于 JavaFX 的代码生成工具，在 Maven 目录结构下生成 Pojo、Repository 
及对应的单元测试代码。[视频演示](https://www.bilibili.com/video/av22590671/)
* 修复若干 BUG，详见日志

#### 2018-04-21

* 删除对 Apache commons-lang3 的依赖关系
* 修复 Spring Boot Auto Configuration 的问题
* 自动配置时根据 JDBC URL 来猜测 Driver 类，无需手动指定
* 自动配置当没有找到可用的连接池时，使用 com.hyd.dao.database.NonPooledDataSource

#### 2018-04-07

* 版本号升级到 3.0.0 开发版
* 添加 Spring Boot Auto Configuration，使用 spring.datasource 配置来自动创建 DAO 对象

#### 2017-12-22:

* 修复了 insert 对象的时候无法正确映射父类成员的问题。

#### 2017-12-20:

* 版本号升级到 2.6.0-SNAPSHOT
* Java 依赖版本更新到 8.0
* RowIterator 新增 setRowPreProcessor() 方法，用于返回 Row 对象前进行预处理。
* DAO.queryIterator() 方法新增 Consumer<Row> 类型的参数。

#### 2017-04-12:

* 修复了 MySQL 下插入对象时表的字段名如果是 MySQL 保留关键字的话会执行失败的问题

#### 2017-01-01:

* commons-lang 依赖关系升级到 commons-lang3
* commons-dbcp 依赖关系升级到 commons-dbcp2
* Java 最低要求升级到 1.7
* 版本号升级到 2.5.0-SNAPSHOT

#### 2016-10-19:

* 添加对 H2 数据库的支持

#### 2015-12-27:

* 修复 Page 类计算总页数不正确的 BUG

#### 2015-05-28:

* 修复了一个对 SQL.Generatable 对象调用多次 toCommand() 方法返回的内容不一致的 BUG

#### 2015-03-22:

* 将数据库差异集中到 CommandBuilderHelper 的子类中去，去掉 DefaultExecutor 的子类。
* 添加对 HSQLDB 分页查询的支持

#### 2015-03-20: 

* 以自适应的方式支持 logback/log4j/log4j2 三种日志输出框架。使用 hydrogen-dao 的项目可以自行选择。
* 版本升级到 2.3.0-SNAPSHOT。

## 文档

具体的文档都在源代码 docs 目录下。

