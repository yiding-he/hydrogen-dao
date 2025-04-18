对于 Spring Boot 项目，hydrogen-dao 提供了开箱即用的 DAO 对象。你可以在
 `application.properties` 当中配置单个或多个数据源，然后得到可用的 DAO 对象。

## 单数据源

假设项目中已经存在数据源配置，并且 SpringBoot 能正确根据配置创建数据源对象：

```properties
spring.datasource.url=jdbc:h2:./target/db/default
spring.datasource.username=sa
```

然后在适当的地方引入 hydrogen-dao 的配置类：

```java
@Import(com.hyd.dao.spring.SpringAutoConfiguration.class)
```

就可以直接使用 DAO 对象了：

```java
@Service
public class UserService {
    
    @Autowired
    private DAO dao;
    
    // ...
}
```

## 多数据源

你需要分别为每个数据源配置不同的 @Bean ，下面是一个例子：

```java
@Bean
@ConfigurationProperties("spring.datasource.ds1")
public DataSource ds1() {
    DataSource dataSource = 
        org.springframework.boot.jdbc.DataSourceBuilder.create().build();
    DataSources.getInstance().setDataSource("ds1", dataSource);
    return dataSource;
}

@Bean
public DAO ds1Dao() {
    return new DAO("ds1");
}
```

然后就可以直接使用了：

```java
@Autowired
private DAO ds1Dao;

public void showTables() {
    ds1Dao.query("show tables").forEach(System.out::println);
}
```

实际例子可参考单元测试代码中的 `src/test/java/com/hyd/daotests/springboot/MultiDataSourceConf.java`
