package com.hyd.dao.springboot;

import com.hyd.dao.DAO;
import com.hyd.dao.spring.SpringAutoConfiguration;
import com.hyd.dao.springboot.SpringBootApplicationBase.Conf;
import com.hyd.daotests.JUnitRuleTestBase;
import java.util.function.Supplier;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {Conf.class}, properties = {
    "debug=true",
    "spring.profiles.active=h2"
})
@RunWith(SpringRunner.class)
public abstract class SpringBootApplicationBase extends JUnitRuleTestBase {

    @Autowired
    private DAO dao;

    @Override
    protected Supplier<DAO> getDAOSupplier() {
        return () -> this.dao;
    }

    @Configuration
    @Import({
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        SpringAutoConfiguration.class
    })
    public static class Conf {

    }
}
