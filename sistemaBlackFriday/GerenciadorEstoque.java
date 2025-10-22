package sistemaBlackFriday;

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

}
