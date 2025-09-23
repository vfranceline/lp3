package Threads;

public class Volatile {

    private static int numero = 0;
    private static boolean preparado = false;

    private static class myRunnable implements Runnable{
        @Override
        public void run(){
            while (!preparado){
                Thread.yield();
            }
            System.out.println("num: " + numero);
        }
    }

    public static void main(String[] args) {
        Thread t0 = new Thread(new myRunnable());
        t0.start();
        numero = 42;
        preparado = true;
    }

}
