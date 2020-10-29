package com.hyd.dao;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.util.Arrays;

public class PageTest {

    @Test
    public void testJackson() throws Exception {
        Page<String> page = new Page<>();
        page.setTotal(100);
        page.setPageIndex(5);
        page.setPageSize(10);
        page.addAll(Arrays.asList("1", "2", "3"));

        ObjectMapper objectMapper = new ObjectMapper();
        System.out.println(objectMapper.writeValueAsString(page));

        System.out.println(JSON.toJSONString(page));
    }
}
