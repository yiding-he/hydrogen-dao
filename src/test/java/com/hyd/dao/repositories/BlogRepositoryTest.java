package com.hyd.dao.repositories;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.DAOUtils;
import com.hyd.dao.models.Blog;
import org.junit.Test;

import java.util.List;

/**
 * @author yiding.he
 */
public class BlogRepositoryTest {

    static {
        DAOUtils.setupDataSource("jdbc:sqlite:U:/Documents/demo.sqlite", null, null);
    }

    private BlogRepository getBlogRepository() {
        BlogRepository blogRepository = new BlogRepository();
        blogRepository.setDao(DAOUtils.getDAO());
        return blogRepository;
    }

    @Test
    public void testQueryBlog() throws Exception {
        BlogRepository blogRepository = getBlogRepository();

        Blog blog = blogRepository.queryById(1);
        System.out.println(JSON.toJSONString(blog, true));
    }

    @Test
    public void testQueryAllBlog() throws Exception {
        List<Blog> blogs = getBlogRepository().queryAll();
        for (Blog blog : blogs) {
            System.out.println(JSON.toJSONString(blog));
        }
    }
}