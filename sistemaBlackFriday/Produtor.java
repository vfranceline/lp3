package sistemaBlackFriday;

import java.util.Random;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;

// --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
// É uma tarefa que será executada por uma thread do pool.
public class Produtor implements Runnable{
    //campo final não pode ser alterado dps de inicializado!!!!! se precisar mudar a variavel (ex: contador) NÃO usar final
    private final String nome;
    private final int qntPedido;
    AtomicInteger qntPedidoGerado;
    private PriorityBlockingQueue<Pedido> fila; // A fila compartilhada

    // --- TÓPICO 8: CLASSES ATÔMICAS ---
    // Este ID é 'static', ou seja, COMPARTILHADO por todas
    // as threads Produtor.
    // Usar 'AtomicInteger' garante que o 'incrementAndGet()'
    // seja atômico. Não haverá dois pedidos com o mesmo ID,
    // mesmo que duas threads o chamem ao mesmo tempo.
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
                // Cria um pedido com ID único (Tópico 8)
                int id = idPedido.incrementAndGet();
                long timestampCriacao = System.currentTimeMillis();
                Pedido pedido = new Pedido(id, randomNomeCliente(), randomProduto(), randomQnt(), randomPrioridade(), timestampCriacao);

                // --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA (FILAS) ---
                // 'fila.put(pedido)' insere o pedido na fila.
                // A fila (PriorityBlockingQueue) é thread-safe.
                //
                // 'put()' é um método BLOQUEANTE. Se a fila estiver
                // cheia (capacidade 50), esta thread Produtor
                // ficará "dormindo" aqui (Estado: WAITING - Tópico 2)
                // até que um Consumidor tire um item e libere espaço.
                fila.put(pedido);
                System.out.println("[" + nome + "] Gerou " + pedido.toString());

                // Atualiza o contador atômico (Tópico 8)
                qntPedidoGerado.incrementAndGet();
                
                // Pausa (TIMED_WAITING - Tópico 2)
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
