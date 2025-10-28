package lista.ex10;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ContadorParalelo {
    private static final String[] ARQUIVOS_PARA_PROCESSAR = {
        "arquivo1.txt",
        "arquivo2.txt",
        "arquivo3.txt"
    };

    public static void main(String[] args) {
        int tam = ARQUIVOS_PARA_PROCESSAR.length;
        CountDownLatch latch = new CountDownLatch(tam);
        ConcurrentHashMap<String, Integer> resultados = new ConcurrentHashMap<>();
        ExecutorService pool = Executors.newFixedThreadPool(tam);

        for (int i = 1; i <= tam; i++){
            ContadorPalavras contadorPalavras = new ContadorPalavras("arquivo" + i + ".txt", latch, resultados);
            pool.execute(contadorPalavras);
        }

        pool.shutdown();
    
        try {
            System.out.println(">>> Aguardando todas as threads de contagem terminarem...");
            
            // A thread 'main' "dorme" aqui (WAITING) até o latch chegar a 0
            latch.await(); //
            
            // --- 4. PROCESSAR RESULTADOS (SÓ RODA DEPOIS DO AWAIT) ---
            System.out.println(">>> Todas as threads terminaram. Somando resultados...");
            
            int total = 0;

            for(Integer contagemParcial : resultados.values()){
                total += contagemParcial;
            }

            System.out.println("==========================================");
            System.out.println("  TOTAL DE PALAVRAS (em " + tam + " arquivos): " + total);
            System.out.println("==========================================");
            System.out.println("Detalhes por arquivo: " + resultados.toString());
        } catch (Exception e) {
            pool.shutdownNow();
        }
    }
}
