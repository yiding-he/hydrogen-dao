使用 DAO 对象来进行查询的方式有以下几种。

### 简单的带参数查询
~~~java
// 简单查询
List<Row> users = dao.query(
        "select * from users where id in(?,?,?)", 
        1, 2, 3
);
~~~

使用简单的带参数查询，只需要调用 query() 方法，在 SQL  中用问号"?"指定参数位置，并在后面写上对应数量的参数值即可。只要你写过 JDBC 调用 PreparedStatement 来执行查询，就会很熟悉这种方式。

query() 方法返回一个 Row 对象列表，Row 是 Map<String, Object> 的子类，它的 key 不区分大小写。

### 查询结果包装成 Pojo

你可以指定将查询结果包装成什么样的 Pojo 对象。hydrogen-dao 有一套固定的命名转换规则，将查询结果的字段名匹配到 Pojo 类的属性名上。下面是几个例子：


字段名         |属性名
---|---
user_name       |userName
address         |address
_my_member_id | MyMemberId
class              | CLASS


注意，当使用了像 `abstract`/`private`/`protected`/`static`/`void`/`interface`/`enum`/`class` 等 Java 关键字来做字段名时，因为这些名字不可能转为 Pojo 类的属性名，hydrogen-dao 将其转换为大写。如果你有一条查询语句返回了一个名为 `class` 的字段，而你想用 Pojo 来接收它，你可以在 Pojo 中定义一个名为 "`CLASS`" 的属性。

~~~java
// 查询结果包装成 Pojo
List<User> users = dao.query(User.class, 
        "select * from users where id in(?,?,?)",
         1, 2, 3);
~~~

### 带参数名的查询

有些童鞋不喜欢 "?" 作为占位符，希望每个参数都有名字。hydrogen-dao 提供一个叫 MappedCommand 的类，下面是一个例子：

~~~java
MappedCommand mappedCommand = new MappedCommand(
        "select * from USER where USERNAME=#username# and ROLE in (#role#)")
        .setParam("username", "admin")
        .setParam("role", new int[]{1, 2, 3, 4, 5, 6});

List<User> users = dao.query(User.class, mappedCommand);
~~~

### 只取第一条查询结果

DAO 提供 queryFirst() 方法，其参数与 query() 方法类同，不过返回值是单个的 Row 对象或 Pojo 对象。该方法只返回查询结果中的第一条记录。

> 注意：不管查询本身是返回多条记录还是单条记录，queryFirst() 方法都只返回第一条找到的记录。