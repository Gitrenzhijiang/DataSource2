package com.ren.ds.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ThreadTest {
    
    List<Integer> list = new ArrayList<>();
    Lock lock = new ReentrantLock(false);
    Condition empty = lock.newCondition();
    Condition notEmpty = lock.newCondition();
    public static void main(String[] args) {
        new ThreadTest().start();
    }
    void start() {
        Create c = new Create();
        
        Destory destory = new Destory();
        
        new Thread(c).start();
        new Thread(destory).start();
        
    }
    int i = 0;
    class Create implements Runnable {
        
        @Override
        public void run() {
            for (;;) {
                lock.lock();
                try {
                    while (list.size() >= 16) {
                        empty.await();
                    }
                    
                    System.out.println("add:" + ++i);
                    list.add(i);
                    notEmpty.signal();
                } catch(Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
        
    };
    
    class Destory implements Runnable {
        
        @Override
        public void run() {
            for (;;) {
                lock.lock();
                try {
                    while (list.size() == 0) {
                        notEmpty.await();
                    }
                    System.out.println("destory:" + list.remove(list.size()-1));
                    i--;
                    empty.signal();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            }
        }
        
    };
}


