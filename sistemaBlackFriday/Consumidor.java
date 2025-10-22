package sistemaBlackFriday;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

// --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
// A tarefa do Consumidor.
public class Consumidor implements Runnable{
    private final String nome;
    private GerenciadorEstoque manager; // O recurso crítico (Tópico 16)
    private PriorityBlockingQueue<Pedido> fila; // A fila compartilhada (Tópico 7)
    AtomicInteger processados; // O contador atômico (Tópico 8)
    AtomicInteger rejeitados; // O contador atômico (Tópico 8)

    public Consumidor(String nome, GerenciadorEstoque manager, PriorityBlockingQueue<Pedido> fila, AtomicInteger processados, AtomicInteger rejeitados){
        this.nome = nome;
        this.manager = manager;
        this.fila = fila;
        this.processados = processados;
        this.rejeitados = rejeitados;
    }

    @Override
    public void run() {
        while (true) {
            try {
                // --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA (FILAS) ---
                // 'fila.poll(2, TimeUnit.SECONDS)' tenta retirar um item.
                // 'poll()' é BLOQUEANTE COM TIMEOUT.
                // 1. Se houver item: Retorna o item (o de maior prioridade).
                // 2. Se a fila estiver vazia: A thread "dorme"
                //    (Estado: TIMED_WAITING - Tópico 2) por até 2 segundos.
                // 3. Se 2 segundos passarem e nada chegar: Retorna 'null'.
                Pedido pedido = fila.poll(2, TimeUnit.SECONDS);

                if (pedido == null){
                    // Se 'poll' retornou null, significa que a fila
                    // está vazia há 2 segundos. Assumimos que
                    // os Produtores terminaram e não há mais trabalho.
                    break; // Sai do loop 'while' e a thread morre (TERMINATED).
                } else {
                    // Item pego! Simula tempo de processamento
                    Thread.sleep(randomTempo());
                    
                    // --- TÓPICO 16: LOCK ---
                    // Tenta reservar o estoque. Esta chamada
                    // vai adquirir o 'writeLock' (Tópico 16)
                    // dentro do 'manager' de forma segura.
                    if (manager.reservarEstoque(pedido.getProduto(), pedido.getQuantidade())){
                        // Sucesso! Atualiza o contador atômico (Tópico 8)
                        System.out.println("[" + nome + "] Processado com sucesso: " + pedido.toString());
                        processados.incrementAndGet();
                    } else {
                        // Falha (sem estoque). Atualiza o outro contador (Tópico 8)
                        System.out.println("[" + nome + "]  REJEITADO (sem estoque): " + pedido.toString());
                        rejeitados.incrementAndGet();
                    }
                }                
            } catch (InterruptedException e) {
                System.err.println(e);
                break;
            }
        }
    }
    
    public int randomTempo(){
        Random random = new Random();
        int tempo = random.nextInt(100, 300);
        return tempo;
    }

}
