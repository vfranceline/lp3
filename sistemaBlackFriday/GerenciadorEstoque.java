package sistemaBlackFriday;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.*; // Importa os Locks

public class GerenciadorEstoque {

    // --- TÓPICO 16: LOCK (ReentrantReadWriteLock) ---
    // Esta é a ferramenta de sincronização mais avançada.
    // Ela é melhor que 'synchronized' (Tópico 5) por ser mais flexível
    // e permitir uma otimização: múltiplos LEITORES ou um único ESCRITOR.
    //
    // 1. Cria o Lock de Leitura-Escrita
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    // 2. Pega o "cadeado de leitura" (ReadLock)
    // VÁRIAS threads podem adquirir este cadeado ao mesmo tempo,
    // desde que nenhuma thread esteja com o cadeado de escrita.
    private final Lock readLock = readWriteLock.readLock(); 

    // 3. Pega o "cadeado de escrita" (WriteLock)
    // Apenas UMA thread pode adquirir este cadeado, e ela só
    // consegue se NENHUMA outra thread estiver lendo ou escrevendo.
    private final Lock writeLock = readWriteLock.writeLock();

    // --- TÓPICO 7: COLEÇÕES PARA CONCORRÊNCIA ---
    // O estoque em si é guardado em um ConcurrentHashMap.
    // Ele é thread-safe para operações simples (como .get() ou .put()).
    private final ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap(50);

    public GerenciadorEstoque(){
        concurrentHashMap.put("Notebook", 20);
        concurrentHashMap.put("Mouse", 50);
        concurrentHashMap.put("Teclado", 30);
        concurrentHashMap.put("Monitor", 15);
        concurrentHashMap.put("Headset", 25);
    }

    // Método de LEITURA (Consulta)
    public int consultarEstoque(String produto){
        // --- TÓPICO 16: Read Lock ---
        // Adquire o cadeado de LEITURA.
        // Vários Consumidores podem chamar 'consultarEstoque'
        // ao mesmo tempo, sem bloquear uns aos outros.
        readLock.lock();
        try {
            // Seção Crítica (Leitura)
            int qntProduto = concurrentHashMap.get(produto);
            return qntProduto;
        } finally {
            // É CRUCIAL liberar o lock em um bloco 'finally'
            // para garantir que ele seja liberado mesmo se
            // uma exceção ocorrer.
            readLock.unlock();
        }
    }

    // Método de ESCRITA (Reserva)
    public boolean reservarEstoque(String produto, int qntReservar){
        // --- TÓPICO 16: Write Lock ---
        // Adquire o cadeado de ESCRITA.
        // Apenas UMA thread (Consumidor) pode estar aqui dentro por vez.
        // Enquanto esta thread estiver aqui, NENHUMA outra thread
        // pode ler (consultarEstoque) ou escrever (reservar/devolver).
        writeLock.lock();
        try{
            // --- Início da Seção Crítica (Escrita) ---
            // A operação 'reservar' é "composta" (ler-e-depois-escrever)
            // e precisa ser atômica. O WriteLock garante isso.
            Integer qntProduto = concurrentHashMap.get(produto);
            if (qntProduto != null){
                if (qntProduto >= qntReservar){
                    int novaQnt = qntProduto-qntReservar;
                    // Atualiza o estoque
                    concurrentHashMap.replace(produto, novaQnt);
                    return true; // Sucesso
                } else return false; // Sem estoque
            }
            else return false; // Produto não existe
            
        } finally {
            // Libera o cadeado de ESCRITA
            writeLock.unlock();
        }
    }

    // (devolverEstoque é outro método de ESCRITA, igual a reservar)
    public void devolverEstoque(String produto, int qntDevolver){
        writeLock.lock();
        try{
            Integer qntProduto = concurrentHashMap.get(produto);
            if (qntProduto != null){
                int novaQnt = qntProduto + qntDevolver;
                concurrentHashMap.replace(produto, novaQnt);
            }
        } finally {
            writeLock.unlock();
        }
    }

    // (getEstoqueCompleto usa o ReadLock para ler o mapa todo)
    public Map<String, Integer> getEstoqueCompleto() {
        readLock.lock(); // Trava para leitura
        try {
            // Retorna uma nova cópia do mapa
            return new HashMap<>(this.concurrentHashMap);
        } finally {
            readLock.unlock(); // Libera a trava
        }
    }

}
