package com.hyd.dao.mate.util;

import org.junit.Test;

import java.util.Random;

public class LockerTest {

    @Test
    public void testMultiThread() throws Exception {
        class Task implements Runnable {

            final String id;

            Task(String id) {
                this.id = id;
            }

            @Override
            public void run() {
                try {
                    Thread.sleep(1000);
                    System.out.println("Task " + id + " finished.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        String[] ids = new String[] {"1", "2", "3", "4", "5"};
        Random random = new Random();
        for (int i = 0; i < 100; i++) {
            String id = ids[random.nextInt(ids.length)];
            new Thread(() -> Locker.lockAndRun(id, new Task(id))).start();
        }

        Thread.sleep(25000);
    }
}
