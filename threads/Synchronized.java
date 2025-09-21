package threads;

public class Synchronized {
    
    public static int i = 0;

    public static void main(String[] args){
        MyRunnable r = new MyRunnable();

        Thread t0 = new Thread(r);
        Thread t1 = new Thread(r);
        Thread t2 = new Thread(r);
        Thread t3 = new Thread(r);

        t0.start();
        t1.start();
        t2.start();
        t3.start();
    }

    public static void imprime(){
        synchronized (Synchronized.class){
            i++;
            String name = Thread.currentThread().getName();
            System.out.println(name + ":" + i);
        }
    }

    public static class MyRunnable implements Runnable {
        @Override
        public void run(){
            imprime();
        }
        
    }
}
