package com.hyd.dao.src;

import com.hyd.dao.DataSources;
import org.junit.Test;

/**
 * (description)
 * created at 2018/4/8
 *
 * @author yidin
 */
public class RepositoryCodeGeneratorTest {

    @Test
    public void testGenerateCode() throws Exception {
        new RepositoryCodeGenerator(new DataSources())
                .forDataSource("default")
                .fromTable("users")
                .toFile("src/test/java/com/hyd/dao/UserRepository.java", "UTF-8");
    }
}