package com.hyd.dao.repositories;

import com.alibaba.fastjson.JSON;
import com.hyd.dao.DAOUtils;
import com.hyd.dao.models.Blog;
import org.junit.Test;

/**
 * @author yiding.he
 */
public class BlogRepositoryTest {

    static {
        DAOUtils.setupDataSource("jdbc:sqlite:U:/Documents/demo.sqlite", null, null);
    }

    @Test
    public void testQueryBlog() throws Exception {
        BlogRepository blogRepository = new BlogRepository();
        blogRepository.setDao(DAOUtils.getDAO());

        Blog blog = blogRepository.queryById(1);
        System.out.println(JSON.toJSONString(blog, true));
    }
}