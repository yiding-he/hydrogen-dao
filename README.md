#hydrogen-dao

hydrogen-dao 是一个 Java 的轻量级的数据库访问库，依赖标准的 JDBC 接口。主要功能有：

* 连接池管理，状态查看
* 跨数据库的事务
* 根据参数值来动态组装 select/insert/update/delete 语句，免除大量的 if-else
* 简化分页查询和批处理

使用方法参考 [WIKI](http://git.oschina.net/yidinghe/hydrogen-dao/wikis/home)

##更新

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

####2016-10-19:

* 添加对 H2 数据库的支持

####2015-12-27:

* 修复 Page 类计算总页数不正确的 BUG

####2015-05-28:

* 修复了一个对 SQL.Generatable 对象调用多次 toCommand() 方法返回的内容不一致的 BUG

####2015-03-22:

* 将数据库差异集中到 CommandBuilderHelper 的子类中去，去掉 DefaultExecutor 的子类。
* 添加对 HSQLDB 分页查询的支持

####2015-03-20: 

* 以自适应的方式支持 logback/log4j/log4j2 三种日志输出框架。使用 hydrogen-dao 的项目可以自行选择。
* 版本升级到 2.3.0-SNAPSHOT。

##使用例子

### 查询记录

~~~Java
DAO dao = getDAO();

List<User> userList = dao.query(
        User.class,                                         // 包装类（可选）
        "select * from USER where NAME like ? and ROLE=?",  // 语句
        "admin%", 3);                                       // 参数（可选）
        
for (User user: userList) {
    System.out.println("user name: " + user.getName());
}
~~~

### 执行带参数名的 SQL

~~~Java
MappedCommand cmd = 
        new MappedCommand("update USERS set ROLE=#role# where ID in(#userid#)")
        .setParam("role", "admin")
        .setParam("userid", 1, 2, 3, 4);  // 数组或 List 都可以
dao.execute(cmd);
~~~

### 构造动态查询条件

_不用写恶心的 `where 1=1` 了_

~~~Java
dao.query(SQL.Select("ID", "NAME", "DESCRIPTION")
        .From("USERS")
        .Where("ID in ?", 10, 22, 135)                 // 会自动扩展为 "ID in (?,?,?)"
        .And(disabled != null, "DISABLED=?", disabled) // 仅当变量 disabled 值不为 null 时才会按照该条件查询
        .AndIfNotEmpty("DISABLED=?", disabled)         // 效果同上
);
~~~

### 执行事务

~~~Java
final DAO dao = getDAO();

DAO.runTransactionWithException(new Runnable() {  // 所有事务都以 Runnable 的方式执行，简单明了
    public void run() {
        dao.execute("insert into USER(id,name) values(?,?)", 1, "user1");
        throw new RuntimeException();    // 之前的 insert 将会回滚，同时异常抛出
    }
});
~~~