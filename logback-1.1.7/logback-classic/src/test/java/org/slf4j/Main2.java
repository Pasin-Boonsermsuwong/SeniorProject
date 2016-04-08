package org.slf4j;

import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;

import ch.qos.logback.classic.LoggerContext;

public class Main2 {
    static int len = 5;
    
    static LoggerContext lc = new LoggerContext();
    static volatile Future<?>[] otherFutures = new Future<?>[len];
    
    static public void main(String[] args) throws Exception {

        ScheduledExecutorService s = lc.getScheduledExecutorService();
        
        
        final Future<?>[] futures = new Future<?>[len];
        
        for (int i = 0; i < len; i++) {
            futures[i] = s.submit(new FirstRunnable(i));
        }
        for (int i = 0; i < len; i++) {
            futures[i].get();
            otherFutures[i].get();
        }
        
        s.shutdown();
    }

    static class FirstRunnable implements Runnable {
        int i = 0;
        FirstRunnable(int i) {
            this.i = i;
        }
        @Override
        public void run() {
            otherFutures[i] = lc.getScheduledExecutorService().submit(new OtherRunnable(i+1));
            System.out.println("First i=" + i);
            i++;
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        
    }
    
    static class OtherRunnable implements Runnable {
        int i = 0;
        OtherRunnable(int i) {
            this.i = i;
        }
        @Override
        public void run() {
            System.out.println("OtherRunnable i=" + i);
        }
        
    }
}
