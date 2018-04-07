hydrogen-dao 支持跨数据库的事务，只要每个数据库都支持事务即可。

DAO 对象提供 `runTransaction()` 方法用于执行事务。该方法接受一个 `Runnable` 对象，该对象即代表要执行的事务，其 `run()` 方法中的所有数据库操作（仅限于 hydrogen-dao 的数据库操作，其他框架的操作不受管理）都会作为事务的一部分。

当出现错误需要回滚事务时，在 `run()` 方法中抛出 `RuntimeException` 即可。下面是一个例子：

```java
final DAO dao = getDAO();
final User user1 = new User(111L, "user01", "pass01");
final User user2 = new User(222L, "user02", "pass02");

try {
    DAO.runTransactionWithException(new Runnable() {

            public void run() {
                dao.insert(user1);
                dao.insert(user2);

                // 模拟事务执行失败，两个 insert 都会回滚
                throw new RuntimeException("Transaction aborted.");
            }
    });
} catch (TransactionException e) {
    e.printStackTrace();
}
```

将事务包装成 `Runnable` 的好处在于让该事务的逻辑变得独立可复用。

### 嵌套事务

hydrogen-dao 支持嵌套事务，但要注意，每层事务都会把持一个数据库连接，直到该事务提交或回滚。因此在数据库连接有限的情况下，请不要执行层次过多的事务；在极端情况下，当事务层次超过连接池的最大连接数时，整个应用都可能阻塞无响应。