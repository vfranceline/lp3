package ApoloTech;

import java.util.concurrent.CountDownLatch;

// representa o carregamento de cada módulo

// --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
// Ao implementar 'Runnable', esta classe se torna uma "tarefa".
// O código que a thread deve executar está dentro do método 'run()'.
public class ModuleLoader implements Runnable {
    private String moduleName;
    private CountDownLatch latch; //o "portão" compartilhado 

    public ModuleLoader(String moduleName, CountDownLatch latch){
        this.moduleName = moduleName;
        this.latch = latch;
    }

    @Override
    public void run() {
        // Esta é a lógica que a thread do Executor vai executar.
        System.out.println("carregando modulo:" + moduleName);
        try {
            // --- TÓPICO 2: CICLO DE VIDA DE THREAD ---
            // Simula o tempo de carregamento de cada módulo.
            // Thread.sleep() pausa a thread atual.
            // Ela entra no estado "TIMED_WAITING" (esperando com tempo limite).
            // Durante este tempo, ela NÃO consome CPU, permitindo que
            // outras threads (como outros módulos) rodem.
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

            // --- TÓPICO 14: COUNTDOWNLATCH ---
            // 2. Sinalizar Conclusão (o "click" do portão)
            // Este é o comando que "avisa" ao Latch que este módulo terminou.
            // Ele decrementa a contagem interna do latch (ex: de 4 para 3).
            // Esta operação é "thread-safe".
            latch.countDown();
            
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Quando o método 'run()' termina, a thread entra no estado
        // TERMINATED (Tópico 2) e o ExecutorService pode
        // designá-la para outra tarefa ou dispensá-la.

    }
}
