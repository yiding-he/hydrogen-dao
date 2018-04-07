`DAO.execute()` 方法可以用来执行所有的 `insert`/`update`/`delete` 语句，以及 DDL 语句（如 `create table`）。

只要不是在事务中执行，`execute()` 方法都会自动提交。

下面是执行 SQL 的几种方式：

### 1. 直接执行语句并带参数

~~~java
dao.execute("insert into USER(id, username) values(?,?)", 1, "admin");
~~~

### 2. 执行 `Command` 对象

`com.hyd.dao.database.commandbuilder.Command` 对象包含了要执行的语句和相关参数。`Command` 类的目的是将语句和参数绑在一起传递。

~~~java
Command command = new Command();
command.setStatement("insert into USER(id, username) values(?,?)");
command.setParams(Arrays.asList(1, "admin"));
dao.execute(command);
~~~

### 3. 执行 `MappedCommand` 对象

`com.hyd.dao.database.commandbuilder.MappedCommand` 对象支持为参数起名。目前只有 `MappedCommand` 支持这种方式，其他方式执行查询或变更，都是用 "?" 作为参数占位符。

~~~java
MappedCommand cmd = new MappedCommand("insert into USER(username) values (#username#)");
cmd.setParam("username", "user1");
dao.execute(cmd);
~~~

### 4 根据参数动态生成的条件

com.hyd.dao.SQL 类帮助生成一个包含动态条件的语句。当条件不满足时，相关的条件不会出现在 SQL 语句中，并继续保证 SQL 的合法性。具体使用方法请参考 SQL 类的单元测试。

~~~java
dao.execute(SQL
        .Update("USER")
        .Set("ROLE=?", roleId)
        .Where("USERID>?", 10)
        .And(username != null, "USERNAME=?", username) // 如果 username 为 null，则本条件不会生成
);
~~~

### 5 批处理：执行 com.hyd.dao.BatchCommand 对象

BatchCommand 对象用于执行批处理语句，比如批量插入：

~~~java
BatchCommand bc = new BatchCommand("insert into USER(id, username) values(?,?)");
bc.addParams(1, "user1");
bc.addParams(2, "user2");
bc.addParams(3, "user3");
dao.execute(bc);
~~~

> 注意：批处理不是当成事务来执行的。每次调用 `execute()` 方法执行批处理时，其执行效果取决于 `java.sql.Statement#executeBatch` 方法的执行效果。如果要将批处理作为事务执行，请参考[事务处理](07-transaction.md)。

### 6 批处理的返回值

`execute(BatchCommand)` 方法的返回值等于每条执行的语句所变更的记录数的总和。例如第一条语句更新了 3 条记录，第二条更新了 7 条记录，那么 `execute()` 方法将返回 10。