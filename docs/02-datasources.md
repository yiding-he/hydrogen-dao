hydrogen-dao 需要一个 JDBC 数据源来实现数据库访问。只需要标准的 `javax.sql.DataSource` 对象即可。

hydrogen-dao 支持同时管理多个数据源，不论它们各自属于什么类型的数据库。

### 创建 Datasources 对象

`com.hyd.dao.DataSources` 是管理数据源的类，并且是单例的。

```java
DataSources datasources = DataSources.getInstance();
```

### 创建数据源

接下来，你可以用任何数据库连接池类库（DBCP，c3p0，等等）创建一个包装好的 `javax.sql.DataSource` 对象，然后将其配置到 DataSources 里。例如配置一个 DBCP 的数据源：

```java
DataSource ds = new org.apache.commons.dbcp.BasicDataSource();
...
datasources.setDataSource("db1", ds);
```

### 获取 DAO 对象

配置好数据源后，就可以调用 DataSources 的 getDAO() 方法来获得 DAO 对象了。

```java
DAO dao = datasources.getDAO("db1");  // 这个 DAO 对象的所有操作都是针对 db1 数据库
```

### 完整例子

下面是一个完整的例子：

```java

// 1. 创建一个数据源
BasicDataSource dataSource = new BasicDataSource();
dataSource.setDriverClassName("org.hsqldb.jdbc.JDBCDriver");
dataSource.setUrl("jdbc:hsqldb:mem:demodb");
dataSource.setUsername("SA");

// 2. 将 DataSource 对象注册到 com.hyd.dao.DataSources 对象中
// 这两步通常会在 Spring 当中以配置的方式完成。
// DataSources 对象应该是全局唯一的。
DataSources.getInstance().setDataSource("demodb1", dataSource);

// 3. 创建 DAO 对象
DAO dao = new DAO("demodb1");

```

如果你用的是 Spring，则配置起来是这个样子：

```java
@Configuration
public class DbConfiguration {
    
    // 假设配置好了一个数据源
    @Bean
    public DataSources dataSources(DataSource dataSource) {
        DataSources.getInstance().setDataSource("db1", dataSource);
    }
}

@Component
public class UserService {
    
    public User findUser(Long userId) {
        return new DAO("db1").queryFirst(User.class, "select * from users where id=?", userId);
    }
}
```