package sistemaBlackFriday;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Consumidor implements Runnable{
    private final String nome;
    private GerenciadorEstoque manager;
    private PriorityBlockingQueue<Pedido> fila;
    AtomicInteger processados;
    AtomicInteger rejeitados;

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
                Pedido pedido = fila.poll(2, TimeUnit.SECONDS);
                if (pedido == null){
                    break;
                } else {
                    Thread.sleep(randomTempo());
                    if (manager.reservarEstoque(pedido.getProduto(), pedido.getQuantidade())){
                        System.out.println("[" + nome + "] Processado com sucesso: " + pedido.toString());
                        processados.incrementAndGet();
                    } else {
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
