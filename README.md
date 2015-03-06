#hydrogen-dao

hydrogen-dao 是一个 Java 的轻量级的数据库访问库，依赖标准的 JDBC 接口。下面是一个使用例子：

~~~Java
    DAO dao = getDAO();
    
    List<Row> rows = dao.query(
            "select * from USER where USERNAME like ? and ROLE=?", 
            "admin%", 3);
            
    for (Row row: rows) {
        System.out.println("username: " + row.getString("username"));
    }
~~~


使用方法参考 [WIKI](http://git.oschina.net/yidinghe/hydrogen-dao/wikis/home)。