package ApoloTech;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class MainApp {
    public static void main(String[] args) {
        // 1. Criando o Latch (a Barreira / o "Portão de Largada")
        // O PDF exige que o servidor espere por 4 módulos.
        // Por isso, inicializamos o "portão" com uma contagem de 4.
        // O portão só abrirá quando 'countDown()' for chamado 4 vezes.
        CountDownLatch latch = new CountDownLatch(4);

        // 2. Criar o ExecutorService
        // Em vez de criar threads manualmente (ex: new Thread(r).start()),
        // usamos um "Pool de Threads" (ExecutorService).
        // 'newCachedThreadPool()' é um pool que cresce e encolhe
        // dinamicamente. Ele cria novas threads se necessário ou
        // reutiliza threads que já terminaram tarefas anteriores.
        // Isso é ótimo para muitas tarefas curtas.
        ExecutorService executor = Executors.newCachedThreadPool();

        // 3. Criar os 4 ModuleLoaders e 1 ServerInitializer
        // Criamos as 4 tarefas de carregamento de módulo.
        // IMPORTANTE: Todas as 5 tarefas (4 módulos + 1 servidor)
        // recebem A MESMA instância do 'latch'. É assim que
        // elas se comunicam.
        ModuleLoader configLoader = new ModuleLoader("configuração", latch);
        ModuleLoader cacheLoader = new ModuleLoader("cache", latch);
        ModuleLoader segurancaLoader = new ModuleLoader("segurança", latch);
        ModuleLoader logLoader = new ModuleLoader("log", latch);

        // Esta é a tarefa do servidor que vai ESPERAR pelas outras.
        ServerInitializer server = new ServerInitializer(latch);

        // 4. Submeter todos para o ExecutorService
        // "Entregamos" as tarefas para o pool de threads.
        // O pool decide qual thread (trabalhador) vai executar qual tarefa.
        
        // Submetemos o servidor PRIMEIRO (ou em qualquer ordem).
        // Ele vai rodar imediatamente, mas vai "travar" no 'latch.await()'
        // e ficar no estado WAITING (Tópico 2).
        executor.execute(server);

        // Submetemos os 4 módulos para serem executados em paralelo.
        executor.execute(configLoader);
        executor.execute(cacheLoader);
        executor.execute(segurancaLoader);
        executor.execute(logLoader);
        

        System.out.println("finalizando pae");


        // 5. Chamar o shutdown()
        // Isso "desliga" o pool de threads de forma graciosa.
        // Ele não aceita novas tarefas (execute() daria erro),
        // mas as tarefas que já estão na fila (as 5 que submetemos)
        // serão concluídas. A aplicação só encerra de fato
        // depois que todas as tarefas terminam.
        executor.shutdown();   
    }
   

}
