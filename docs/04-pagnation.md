`DAO` 对象提供 `queryPage()` 方法来执行分页查询，该方法返回一个 `Page` 对象。`Page` 类是 List 的子类，它多了一个 `total` 属性，表示查询结果的总记录数。

在执行 `queryPage()` 方法时，`DAO` 实际执行了两次查询，一次是查询总记录数，一次是查询指定范围的记录列表。

~~~java
// 分页查询
String sql = "select * from users where name like ?";
int pageSize = 10;  // 页大小
int pageIndex = 2;  // 页号，0 表示第一页

// 分页查询。为了获取总记录数，实际上查询了两次
Page<User> page = dao.queryPage(
        User.class, sql, pageSize, pageIndex, "Adm%");

System.out.println("Total count: " + page.getTotal());

for (User user: page) {
    System.out.println(user);
}
~~~

