package sistemaBlackFriday;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

public class GerenciadorEstoque {
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    private final Lock readLock = readWriteLock.readLock(); 
    private final Lock writeLock = readWriteLock.writeLock();
    private final ConcurrentHashMap<String, Integer> concurrentHashMap = new ConcurrentHashMap(50);

    public GerenciadorEstoque(){
        concurrentHashMap.put("Notebook", 20);
        concurrentHashMap.put("Mouse", 50);
        concurrentHashMap.put("Teclado", 30);
        concurrentHashMap.put("Monitor", 15);
        concurrentHashMap.put("Headset", 25);
    }

    public int consultarEstoque(String produto){
        readLock.lock();
        try {
            int qntProduto = concurrentHashMap.get(produto);
            return qntProduto;
        } finally {
            readLock.unlock();
        }
    }

    public boolean reservarEstoque(String produto, int qntReservar){
        writeLock.lock();
        try{
            Integer qntProduto = concurrentHashMap.get(produto);
            if (qntProduto != null){
                if (qntProduto >= qntReservar){
                    int novaQnt = qntProduto-qntReservar;
                    concurrentHashMap.replace(produto, novaQnt);
                    return true;
                } else return false;
            }
            else return false;
            
        } finally {
            writeLock.unlock();
        }
    }

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
