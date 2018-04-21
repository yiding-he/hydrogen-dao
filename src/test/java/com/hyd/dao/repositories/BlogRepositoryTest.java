package com.hyd.dao.repositories;

import com.hyd.dao.DAOUtils;
import com.hyd.dao.models.Blog;
import org.junit.Test;

import java.util.List;

/**
 * @author yiding.he
 */
public class BlogRepositoryTest {

    static {
        DAOUtils.setupDataSource("jdbc:h2:U:/demo/demo.db", null, null);
    }

    private BlogRepository getRepository() {
        BlogRepository blogRepository = new BlogRepository();
        blogRepository.setDao(DAOUtils.getDAO());
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
        blog.setId(5);
        blog.setTitle("ttttt");
        blog.setContent("dfqwliufqwiuefhqliuwefhuiqweyfiw");

        getRepository().insert(blog);
    }

    @Test
    public void testQueryOne() throws Exception {
        System.out.println(getRepository().queryById(5));
    }
}
