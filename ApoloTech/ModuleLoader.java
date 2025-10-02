package ApoloTech;

import java.util.concurrent.CountDownLatch;

// representa o carregamento de cada módulo

// De acordo com os requisitos, essa classe precisa implementar Runnable. O que você colocaria dentro do método run() dela para:

// Simular o tempo de carregamento?
// Avisar que o carregamento terminou?
// Sinalizar para o CountDownLatch que um módulo concluiu sua tarefa?


public class ModuleLoader implements Runnable {
    private String moduleName;
    private CountDownLatch latch;

    public ModuleLoader(String moduleName, CountDownLatch latch){
        this.moduleName = moduleName;
        this.latch = latch;
    }

    @Override
    public void run() {
        System.out.println("carregando modulo:" + moduleName);
        try {
            if (moduleName.equalsIgnoreCase("configuração")){
                Thread.sleep(6000);
            } else if (moduleName.equalsIgnoreCase("cache")){
                Thread.sleep(9000);
            } else if (moduleName.equalsIgnoreCase("logs")){
                Thread.sleep(4000);
            } else if (moduleName.equalsIgnoreCase("segurança")){
                Thread.sleep(12000);
            } else {
                System.out.println("nome do modulo incorreto");
            }

            System.out.println("carregamento encerrado");
            latch.countDown();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
