package BEstacionamentos;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ParkingSimulator {
    public static void main(String[] args) {
        Random random = new Random();
        ParkingManager PManager = new ParkingManager();

        ExecutorService executor = Executors.newFixedThreadPool(4);


        System.out.println("=== Sistema de Estacionamento Inteligente === \n === GERANDO VEICULOS ===");

        for(int i = 1; i <= 20; i++){
            //sorteia um numero entre 0 a 99, se for menor doq 30 é prioritario
            String tipo = random.nextInt(100) < 30 ? "PRIORITARIO" : "NORMAL";
            Vehicle vehicle = new Vehicle(tipo, i, PManager);
            System.out.println("Gerado: Veiculo #" + i + "(" + tipo + ")");
            executor.execute(vehicle);
        }

        executor.shutdown();

        try {
            System.out.println("\n === INICIANDO SIMULAÇÃO ===\n");
            if (!executor.awaitTermination(30, TimeUnit.SECONDS)) {
                System.err.println("tempo limite atingido! forçando o encerramento aq, vai q né");
                executor.shutdownNow();
            }
        } catch (Exception e) {
            executor.shutdownNow();
        }

        System.out.println("\n === SIMULAÇÃO CONCLUIDA ===");
        PManager.printStats();
        
    }
}
