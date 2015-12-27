package com.hyd.dao;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * (description)
 * created at 15-12-27
 *
 * @author Yiding
 */
public class PageTest {

    @Test
    public void testGetTotalPage() throws Exception {
        Page page;

        page = new Page(100, 3, 10);
        assertEquals(10, page.getTotalPage());

        page = new Page(99, 3, 10);
        assertEquals(10, page.getTotalPage());

        page = new Page(90, 3, 10);
        assertEquals(9, page.getTotalPage());

        page = new Page(0, 0, 10);
        assertEquals(0, page.getTotalPage());
    }
}