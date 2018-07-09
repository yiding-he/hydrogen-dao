package com.hyd.dao.src.repositories;

import com.hyd.dao.src.models.Blog;
import com.hyd.daotests.JUnitRuleTestBase;
import org.junit.Test;

import java.util.List;

/**
 * @author yiding.he
 */
public class BlogRepositoryTest extends JUnitRuleTestBase {

    private BlogRepository getRepository() {
        BlogRepository blogRepository = new BlogRepository();
        blogRepository.setDao(dao);
        return blogRepository;
    }

    @Test
    public void testQueryAll() throws Exception {
        List<Blog> blogs = getRepository().queryAll();
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
    }

    @Test
    public void testInsertBlog() throws Exception {
        Blog blog = new Blog();
        blog.setId(999);
        blog.setTitle("ttttt");
        blog.setContent("dfqwliufqwiuefhqliuwefhuiqweyfiw");
        blog.setHidden(true);

        getRepository().insert(blog);
        System.out.println(getRepository().queryById(999));
    }

    @Test
    public void testQueryOne() throws Exception {
        System.out.println(getRepository().queryById(5));
    }
}
