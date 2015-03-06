#hydrogen-dao

hydrogen-dao 是一个 Java 的轻量级的数据库访问库，依赖标准的 JDBC 接口。下面是一个使用例子：

### 查询记录

~~~Java
DAO dao = getDAO();

List<User> userList = dao.query(
        User.class,                                         // 包装类
        "select * from USER where NAME like ? and ROLE=?",  // 语句
        "admin%", 3);                                       // 参数
        
for (User user: userList) {
    System.out.println("user name: " + user.getName());
}
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

使用方法参考 [WIKI](http://git.oschina.net/yidinghe/hydrogen-dao/wikis/home)。