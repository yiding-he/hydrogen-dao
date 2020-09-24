package com.hyd.dao.mysql;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import com.hyd.dao.DAO;
import com.hyd.dao.command.BatchCommand;
import com.hyd.dao.database.executor.DefaultExecutor;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class TestUpdateMillionBlogs {

    public static void main(String[] args) {

        ((Logger) LoggerFactory.getLogger(DefaultExecutor.class)).setLevel(Level.ERROR);

        DAO dao = TestBase.initDao();
        Random random = new Random();

        for (int i = 0; i < 10000; i++) {
            int blogId = random.nextInt(1000000);

            BatchCommand batchCommand = new BatchCommand("update blog set last_update=current_timestamp where id=?");
            for (int j = 0; j < 100; j++) {
                batchCommand.addParams(blogId);
            }

            dao.execute(batchCommand);

            if (i % 100 == 0) {
                System.out.println((i * 100) + " blogs updated.");
            }
        }
    }
}
