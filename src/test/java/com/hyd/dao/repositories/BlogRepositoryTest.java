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
        DAOUtils.setupDataSource("jdbc:h2:U:/demo/demo.db", null, null);
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
    public void testInsertBlog() throws Exception {
        Blog blog = new Blog();
        blog.setId(100);
        blog.setTitle("Morning, how can I help you");
        blog.setContent("1. 1896年，清廷向德国订购三艘穹甲巡洋舰，海琛是最后来华的一艘，此后一直是清末主力舰之一。照片中的“海琛”号官兵阵容齐整，军服规范，看起来颇有战斗力。\n" +
                "\n" +
                "2. @shugeorg：此Von Chinas Göttern：Reisen in China（神佛在中国：中国行记）是德国人Friedrich Perzynsk 关于中国佛像研究的代表作品。内含北京、河北、广州、杭州、热河等地（涉及圆明园、睒子洞、易县三彩罗汉等）约八十幅老照片及版画图版。此德文本出版于1920年。\n" +
                "\n" +
                "3. 梁漱溟1911年于顺天中学高等学堂毕业，特借长兄方留日归来所带回日本大学生的服装鞋帽留影纪念。图片来源：世纪文景《梁漱溟日记》\n" +
                "\n" +
                "4. @头条新闻：【纪念建筑大师梁思成】梁思成先生因为他一生在建筑、规划领域的卓越成就而被大家敬仰、铭记，而他与妻子永远“建筑女神”林徽因的浪漫而又奋斗的伉俪一生，也成为后世的典范。他曾说“拆掉一座城楼像挖去我的一块肉，剥去一块城砖像剥去我的一层皮”。#那年此时#梁思成先生诞辰117周年。缅怀！\n" +
                "\n" +
                "5. @Vanessa_Zhang18转：今个听见有位大妈说：60年代勒紧裤腰带，我们造出原子弹，今天我们努把力，再勒勒……也能造出芯片！我对那大妈说：今天，你们把屎勒出来都没用！因为：当年造出原子弹的那帮人，都是万恶的西方列强培养的。");

        getBlogRepository().insert(blog);
    }

    @Test
    public void testQueryAllBlog() throws Exception {
        List<Blog> blogs = getBlogRepository().queryAll();
        for (Blog blog : blogs) {
            System.out.println(blog);
        }
    }
}