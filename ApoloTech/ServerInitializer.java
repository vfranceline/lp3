package ApoloTech;

import java.util.concurrent.CountDownLatch;

// --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
// Esta classe também é uma "tarefa" que será executada por uma thread do pool.
public class ServerInitializer implements Runnable{
    private final CountDownLatch latch; // O "portão" compartilhado novamente

    public ServerInitializer(CountDownLatch latch){
        this.latch = latch;
    }

    @Override
    public void run(){
        // A thread do pool (ex: "pool-1-thread-1") começa a executar aqui.
        try {
            // 1. Chama o método que vai esperar
            waitForInitialization();

            // 3. O 'await()' foi liberado!
            // Esta linha SÓ executa DEPOIS que a contagem do latch
            // chegou a zero (ou seja, os 4 módulos chamaram 'countDown()').
            startServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void waitForInitialization(){
        try {
            // --- TÓPICO 14: COUNTDOWNLATCH ---
            // 3. Esperar (Aguardar no portão)
            // Este é o comando que bloqueia a thread.
            // A thread que executou 'latch.await()' (a thread do Servidor)
            // é movida do estado RUNNABLE para o estado "WAITING" (Tópico 2).
            // Ela fica "dormindo" (sem usar CPU) até que a contagem do latch
            // chegue a 0.
            System.out.println("[Servidor] Aguardando módulos essenciais...");
            latch.await();
            
            // Quando a 4ª thread (o último módulo) chama 'countDown()',
            // o latch chega a 0 e "acorda" esta thread.
            // Ela volta para o estado RUNNABLE, pronta para continuar.
            System.out.println("servidor está on");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void startServer(){
        // Esta é a lógica de negócios que só podia acontecer
        // após as dependências estarem carregadas.
        System.out.println("Servidor principal online: pronto para aceitar conexões (Socket.bind())");
    }
}
