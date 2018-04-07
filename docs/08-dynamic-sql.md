hydrogen-dao 提供 `com.hyd.dao.SQL` 类用于组建简单的动态 SQL 语句。下面是一个例子：

```java
String name = null;
String maxAge = 50;

dao.execute(SQL
        .Update("user_table")
        .Set("last_update", new Date())
        .Where(name != null, "name=?", name)
        .And("age <= ?", maxAge));
```

上面的例子中，因为 name 值为空，所以最后生成的语句就是

```sql
update user_table set last_update = ? where age <= ?
```

而不会包含 name 条件。

`SQL` 类的具体使用方法，请参考单元测试。