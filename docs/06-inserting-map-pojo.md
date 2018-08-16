`DAO.insert()` 方法可以用于将 Pojo/Map 对象插入到数据库表。下面是一个例子：

```java
User user = new User();
user.setId(100);
user.setName("user1");
dao.insert(user, "T_USERS");
```

Pojo 属性名和表字段名之间的转换规则，参见[本页](03-querying.md)。

如果要插入 Map 对象，其 key 的值必须和字段名一致，不区分大小写（大多数数据库是这样）。下面是一个例子：

```java
Map<String, Object> map= new HashMap<>();
map.put("user_id", 1);
map.put("user_name", "someone");
dao.insert(map, "T_SYS_USER");
```

> 注意：当使用这种方式插入记录时，hydrogen-dao 会从数据库查询要插入的表包含哪些字段，用于生成 insert 语句。如果出现数据库兼容性问题以致执行出错，那么建议换用 `dao.execute()` 方法，直接执行 insert 语句。
