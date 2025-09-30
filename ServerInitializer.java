package ApoloTech;

import java.util.concurrent.CountDownLatch;

public class ServerInitializer {
    private CountDownLatch latch = new CountDownLatch(4);

    void waitForInitialization(){
        try {
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
