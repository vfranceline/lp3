package ApoloTech;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainApp {
    public static void main(String[] args) {
        // 1. Criar o CountDownLatch
        CountDownLatch latch = new CountDownLatch(4);

        // 2. Criar o ExecutorService
        ExecutorService executor = Executors.newCachedThreadPool();

        // 3. Criar os 4 ModuleLoaders e 1 ServerInitializer
        ModuleLoader configLoader = new ModuleLoader("configuração", latch);
        ModuleLoader cacheLoader = new ModuleLoader("cache", latch);
        ModuleLoader segurancaLoader = new ModuleLoader("segurança", latch);
        ModuleLoader logLoader = new ModuleLoader("log", latch);

        // 4. Submeter todos para o ExecutorService

        ServerInitializer server = new ServerInitializer(latch);
        executor.execute(configLoader);
        executor.execute(cacheLoader);
        executor.execute(segurancaLoader);
        executor.execute(logLoader);
        executor.execute(server);

        System.out.println("finalizando pae");


        // 5. Chamar o shutdown()
        executor.shutdown();   
    }
   

}
