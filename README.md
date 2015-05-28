#hydrogen-dao

hydrogen-dao 是一个 Java 的轻量级的数据库访问库，依赖标准的 JDBC 接口。主要功能有：

* 连接池管理，状态查看
* 跨数据库的事务
* 根据参数值来动态组装 select/insert/update/delete 语句，免除大量的 if-else
* 简化分页查询和批处理

使用方法参考 [WIKI](http://git.oschina.net/yidinghe/hydrogen-dao/wikis/home)

##更新

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
        .setParam("userid", 1, 2, 3, 4);
dao.execute(cmd);
~~~

### 构造动态查询条件

~~~Java
dao.query(SQL.Select("ID", "NAME", "DESCRIPTION")
        .From("USERS")
        .Where("ID in ?", new int[]{10, 22, 135})      // 会自动扩展为 "ID in (?,?,?)"
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
        throw new Exception();    // 之前的 insert 将会回滚，同时异常抛出
    }
});
~~~