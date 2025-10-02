package ApoloTech;

import java.util.concurrent.CountDownLatch;

public class ServerInitializer implements Runnable{
    private final CountDownLatch latch;

    public ServerInitializer(CountDownLatch latch){
        this.latch = latch;
    }

    @Override
    public void run(){
        try {
            waitForInitialization();
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void waitForInitialization(){
        try {
            latch.await();
            System.out.println("servidor está on");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startServer(){
        System.out.println("Servidor principal online: pronto para aceitar conexões (Socket.bind())");
    }
}
