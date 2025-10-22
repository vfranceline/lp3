package sistemaBlackFriday;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

public class Produtor implements Runnable{
    //campo final não pode ser alterado dps de inicializado!!!!!
    private final String nome;
    private final int qntPedido;
    AtomicInteger qntPedidoGerado;
    private PriorityBlockingQueue<Pedido> fila;
    static AtomicInteger idPedido = new AtomicInteger();

    public Produtor(String nome, int qntPedido, PriorityBlockingQueue<Pedido> fila, AtomicInteger qntPedidoGerado){
        this.nome = nome;
        this.qntPedido = qntPedido;
        this.qntPedidoGerado = qntPedidoGerado;
        this.fila = fila;
    }

    @Override
    public void run(){
        System.out.println("[" + nome + "] Iniciando geração de " + qntPedido + " pedidos");
        for(int i = 0; i < qntPedido; i++){
            try {
                int id = idPedido.incrementAndGet();
                long timestampCriacao = System.currentTimeMillis();
                Pedido pedido = new Pedido(id, randomNomeCliente(), randomProduto(), randomQnt(), randomPrioridade(), timestampCriacao);
                fila.put(pedido);
                System.out.println("[" + nome + "] Gerou " + pedido.toString());
                qntPedidoGerado.incrementAndGet();
                Thread.sleep(randomTempo());
            } catch (InterruptedException e) {
                System.out.println("[" + nome + "] Fui interrompido, parando de gerar...");
                break;
            }
        }
    }


    public int randomTempo(){
        Random random = new Random();

        int tempo = random.nextInt(50, 200);
        return tempo;
    }

    public Pedido.Prioridade randomPrioridade(){
        Pedido.Prioridade[] tiposPrioridades = Pedido.Prioridade.values();
        Random random = new Random();

        int randomIndex = random.nextInt(tiposPrioridades.length);
        Pedido.Prioridade randomPrioridade = tiposPrioridades[randomIndex];

        return randomPrioridade;
    }
    
    public String randomProduto(){
        String[] tiposProdutos = {"Notebook", "Mouse", "Teclado", "Monitor", "Headset"};
        Random random = new Random();

        int randomIndex = random.nextInt(tiposProdutos.length);
        String randomProduto = tiposProdutos[randomIndex];

        return randomProduto;
    }

    public String randomNomeCliente(){
        String[] nomes = {
            "Ana Silva",
            "Bruno Souza",
            "Carla Mendes",
            "Daniel Oliveira",
            "Eduarda Lima",
            "Felipe Santos",
            "Gabriela Costa",
            "Henrique Almeida",
            "Isabela Rocha",
            "João Pereira"
        };

        Random random = new Random();

        int randomIndex = random.nextInt(nomes.length);
        String randomNomeCliente = nomes[randomIndex];

        return randomNomeCliente;
    }

    public int randomQnt(){
        Random random = new Random();

        int qnt = random.nextInt(1, 15);
        return qnt;
    }
}
