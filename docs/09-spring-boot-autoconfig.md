对于 Spring Boot 项目，hydrogen-dao 提供了开箱即用的 DAO 对象。你可以在
 application.properties 当中配置单个或多个数据源，然后得到可用的 DAO 对象。
 
## 单数据源

在 Spring Boot 配置中加入配置如下例子所示：

```properties
spring.datasource.url=jdbc:h2:./target/db/default
spring.datasource.username=sa
```

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

Spring Boot 的自动配置暂不支持多数据源，你可以分别为每个数据源配置不同的 @Bean，然后在 `DataSources` 类中使用，下面是一个例子：

```java
@Bean
@ConfigurationProperties("spring.datasource.ds1")
public DataSource ds1(DataSources dataSources) {
    DataSource dataSource = 
        org.springframework.boot.jdbc.DataSourceBuilder.create().build();
    dataSources.setDataSource("ds1", dataSource);
    return dataSource;
}
```

```java
@Autowired
private DataSources dataSources;

public void showTables() {
    DAO ds1 = this.dataSources.getDAO("ds1");
    ds1.query("show tables").forEach(System.out::println);
}
```
