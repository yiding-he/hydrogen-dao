package com.hyd.dao.util;

import org.junit.Test;

import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * (description)
 * created at 2017/11/10
 *
 * @author yidin
 */
public class LockerTest {

    @Test
    public void testLock() throws Exception {
        Lock lock = new ReentrantLock();

        lock.lock();
        lock.lock();
        lock.lock();

        System.out.println("Hello!");

        lock.unlock();
        lock.unlock();
        lock.unlock();
        lock.unlock();  // error
    }

    @Test
    public void lockAndRun() throws Exception {
        Runnable runnable = () -> {
            System.out.println("Thread " + Thread.currentThread().getName() + " running...");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        };

        String[] keys = new String[] {"1111", "2222", "3333", "4444", "5555"};
        Random random = new Random();

        for (int i = 0; i < 100; i++) {
            String key = keys[random.nextInt(keys.length)];
            new Thread(() -> Locker.lockAndRun(key, runnable)).start();
        }

        Thread.sleep(60000);
    }

    @Test
    public void testLockMultipleTimes() throws Exception {
        String key1 = "lockKey";
        String key2 = "lockKey";

        Locker.lockAndRun(key1, () -> {
            Locker.lockAndRun(key2, () -> {
                Locker.lockAndRun(key1, () -> {
                    Locker.lockAndRun(key2, () -> {
                        Locker.lockAndRun(key1, () -> {
                            Locker.lockAndRun(key2, () -> {
                                Locker.lockAndRun(key1, () -> {
                                    Locker.lockAndRun(key2, () -> {
                                        System.out.println("Hey!");
                                    });
                                });
                            });
                        });
                    });
                });
            });
        });
    }
}