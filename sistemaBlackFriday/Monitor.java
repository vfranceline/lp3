package sistemaBlackFriday;

import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Monitor implements Runnable{
    private GerenciadorEstoque manager;
    private PriorityBlockingQueue<Pedido> fila;
    AtomicInteger processados;
    AtomicInteger rejeitados;
    AtomicInteger qntPedidoGerado;
    private int tempoDecorrido = 0;
    private int processadosAnterior = 0;

    // --- TÓPICO 9: MULTITHREAD - EM ESPERA (VOLATILE) ---
    // Esta variável controla o loop 'while' no método 'run()'.
    // Ela será acessada por duas threads:
    // 1. A thread 'Monitor' (que lê)
    // 2. A thread 'main' (que escreve 'false' no final)
    //
    // 'volatile' garante a VISIBILIDADE. Quando a thread 'main'
    // mudar 'isRunning' para 'false', a thread 'Monitor'
    // tem a garantia de que verá essa mudança e sairá do loop.
    volatile boolean isRunning = true;

    public Monitor(GerenciadorEstoque manager, PriorityBlockingQueue<Pedido> fila, AtomicInteger processados, AtomicInteger rejeitados, AtomicInteger qntPedidoGerado){
        this.manager = manager;
        this.fila = fila;
        this.processados = processados;
        this.rejeitados = rejeitados;
        this.qntPedidoGerado = qntPedidoGerado;
    }

    @Override
    public void run() {
        while (this.isRunning) {
            try {
                Thread.sleep(2000);

                // --- TÓPICOS 7 e 8 (Leitura Segura) ---
                // A thread 'Monitor' lê os dados enquanto
                // Produtores e Consumidores estão escrevendo.
                // Estas leituras são seguras pois:
                // - fila.size() (Tópico 7) é thread-safe.
                // - processados.get() (Tópico 8) é thread-safe.
                
                int processadosAtuais = processados.get();
                int taxa = (processadosAtuais-processadosAnterior)/2;
                processadosAnterior = processadosAtuais;
                System.out.println("┌─────────────────────────────────────┐");
                System.out.println("│       MONITORAMENTO EM TEMPO REAL   │");
                System.out.println("├─────────────────────────────────────┤");
                System.out.println("│ Fila atual        :  " + fila.size() +" pedidos     │");
                System.out.println("│ Pedidos gerados   :  " + this.qntPedidoGerado +"             │");
                System.out.println("│ Processados       :  " + this.processados + "             │");
                System.out.println("│ Rejeitados        :  " + this.rejeitados + "             │");
                System.out.println("│ Taxa processamento:  " + taxa + " ped/s      │");
                System.out.println("│ Tempo decorrido   :  " + tempoDecorrido + "s              │");
                System.out.println("└─────────────────────────────────────┘");
                tempoDecorrido += 2;
                
            } catch (InterruptedException e) {
                System.err.println("Monitor interrompido: " + e.getMessage());
                break;         
            }
        }
    }

    public void setRunning(boolean isRunning) {
        this.isRunning = isRunning;
    }
    
}
