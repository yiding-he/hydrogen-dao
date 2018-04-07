# hydrogen-dao 介绍

hydrogen-dao 是一个轻量级的 JDBC 数据库操作工具，专注于简化数据库的连接管理 SQL 执行。其主要功能有：

* 连接池管理，状态查看
* 跨数据库的事务
* 根据参数值来动态组装 select/insert/update/delete 语句，免除大量的 if-else
* 简化分页查询和批处理

大部分功能都由 com.hyd.dao.DAO 提供。

### 功能列表

hydrogen-dao 还提供更多非常方便的操作数据库方式，包括 **直接插入 Pojo/Map 对象到数据库** ， **构建动态条件的查询** 等。下面从配置开始逐一介绍。

1. 添加依赖关系（Maven）
1. 配置数据源
1. 查询记录
1. 分页查询
1. 执行SQL
1. 插入 Pojo/Map
1. 事务处理
1. 构建带动态条件的语句

![dao-demo1.gif](http://git.oschina.net/uploads/images/2015/0322/171100_27e64522_298739.gif)

### 数据库兼容性

本人开发经历有限，现在能做到比较好的兼容性的数据库是 Oracle/MySQL/HSQLDB，其他数据库若有不兼容之处，还望不吝指出，我会尽快解决！

### 关于 Hibernate 和 MyBatis 等其他 ORM 框架

hydrogen-dao 与其他 ORM 框架之间相互独立，你可以同时用 hydrogen-dao 和其他 ORM 框架，hydrogen-dao 仍然会按自己的方式正常运作。但这样做对项目来说显然是不太好的。
