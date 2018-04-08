对于 Spring Boot 项目，hydrogen-dao 提供了开箱即用的 DAO 对象。方法如下：

1. 在 pom.xml 中加入 spring-boot-autoconfigure 的依赖关系；
1. 在 pom.xml 中加入访问数据库必要的依赖关系（JDBC 驱动和连接池）；
1. 在 Spring Boot 配置中加入 `spring.datasource.url`、`spring.datasource.username` 
和 `spring.datasource.password` 三个配置。

然后不需要编写任何额外代码，就可以直接在任何 Spring bean 当中使用了：

```java
@Service
public class UserService {
    
    @Autowired
    private com.hyd.dao.DAO dao;
    
    ...
}
```

