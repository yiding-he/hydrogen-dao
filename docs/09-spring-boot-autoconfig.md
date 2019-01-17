对于 Spring Boot 项目，hydrogen-dao 提供了开箱即用的 DAO 对象。你可以在
 application.properties 当中配置单个或多个数据源，然后得到可用的 DAO 对象。
 
## 单数据源

在 Spring Boot 配置中加入配置如下例子所示：

```properties
hydrogen-dao.data-sources.default.url=jdbc:h2:./target/db/default
hydrogen-dao.data-sources.default.username=sa
```

其中 `default` 是默认的 DAO 名字。

然后就可以直接在任何 Spring bean 当中使用了：

```java
@Service
public class UserService {
    
    @Autowired
    private DAO dao;
    
    // ...
}
```

## 多数据源

当项目配置了多个数据源时，每个数据源要分别起名字。下面是一个例子：

```properties
hydrogen-dao.data-sources.db1.url=jdbc:h2:./target/db/db1
hydrogen-dao.data-sources.db1.username=sa
hydrogen-dao.data-sources.db2.url=jdbc:h2:./target/db/db2
hydrogen-dao.data-sources.db2.username=sa
hydrogen-dao.data-sources.db3.url=jdbc:h2:./target/db/db3
hydrogen-dao.data-sources.db3.username=sa
```

项目启动后就会对应有三个可用的 DAO 对象。我们可以通过 DataSources 对象获取它们：

```java
@Service
public class UserService {
    
    @Autowired
    private DataSources dataSources;
    
    public void insertUser(User user) {
        DAO db1Dao = dataSources.getDAO("db1");
        db1Dao.insert(user, "user_table");
    }
}
```

或者在 `@Configuration` 类当中定义：

```java
@Configuration
public class Config {
    
    @Bean
    public DAO db1Dao(DataSources dataSources) {
        return dataSources.getDAO("db1");
    }
}

@Service
public class UserService {
    
    @Autowired
    private DAO db1Dao;
    
    // ...
}
```
