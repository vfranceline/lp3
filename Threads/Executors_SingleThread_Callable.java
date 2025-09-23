package Threads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Executors_SingleThread_Callable {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new myRunnable());
        executor.shutdown();
    }

    public static class myRunnable implements Runnable{
        public void run(){
            String nome = Thread.currentThread().getName();
            System.out.println(nome + ": LP3");
        }
    }
}
