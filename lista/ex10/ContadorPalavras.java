package lista.ex10;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

public class ContadorPalavras implements Runnable{
    private final String nomeArquivo;
    private CountDownLatch latch;
    private ConcurrentHashMap<String, Integer> map;

    public ContadorPalavras (String nomeArquivo, CountDownLatch latch, ConcurrentHashMap<String, Integer> map){
        this.nomeArquivo = nomeArquivo;
        this.latch = latch;
        this.map = map;
    }

    @Override
    public void run() {
        int contagemLocal = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(nomeArquivo))) {
            
            System.out.println(">>> [Thread] Lendo arquivo: " + nomeArquivo);
            String line;
            
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Evita contar linhas em branco
                    contagemLocal += line.split("\\s+").length; // Divide por espaços
                }
            }
            
            map.put(nomeArquivo, contagemLocal);
            
        } catch (IOException e) {
            System.err.println("!!! Erro ao ler " + nomeArquivo + ": " + e.getMessage());
        } finally {
            latch.countDown();
        }

    }

    public int randomTempo(){
        Random random = new Random();
        int tempo = random.nextInt(3000, 10000); // 3 a 10 segundos
        return tempo;
    }
}
