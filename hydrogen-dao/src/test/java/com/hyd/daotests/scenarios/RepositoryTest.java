package com.hyd.daotests.scenarios;

import com.hyd.dao.repository.Repository;
import com.hyd.daotests.TestBase;
import com.hyd.daotests.model.Blog;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public interface RepositoryTest extends TestBase {

    default Repository<Blog> getRepository() {
        return new Repository<>(Blog.class, getDao());
    }

    @Test
    default void testRepositoryQueryById() {
        var blog = getRepository().queryById(1);
        assertNotNull(blog);
        System.out.println(blog);
    }

    @Test
    default void testRepositoryQueryByInstance() {
        var blog = new Blog();
        blog.setId(2L);
        var list = getRepository().queryByInstance(blog);
        assertEquals(1, list.size());
        assertEquals(2L, list.get(0).getId());
    }

    @Test
    default void testCustomRepositoryQuery() {
        var nonHiddenBlogs = getRepository().query(select -> select.Where("hidden=?", "false"));
        assertEquals(2, nonHiddenBlogs.size());
    }

    @Test
    default void testRepositoryQueryPage() {
        var page = getRepository().queryPage(
            select -> select.Where("id<?", 100), 2, 0
        );
        assertEquals(2, page.getList().size());
        assertEquals(3, page.getTotal());
        assertEquals(2, page.getTotalPage());
    }
}
