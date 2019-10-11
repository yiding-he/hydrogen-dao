package com.hyd.dao.springboot;

import static org.junit.Assert.assertNotNull;

import com.hyd.dao.DAO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HydrogenDaoBasicTest {

    @Autowired
    private DAO dao;

    @Test
    public void emptyTest() throws Exception {
        assertNotNull(this.dao);
    }

    @Test
    public void testInsert() throws Exception {
        this.dao.execute("insert into task set content=?", "这是一个未完成的任务。");
        System.out.println(this.dao.queryFirst("select * from task"));
    }
}
