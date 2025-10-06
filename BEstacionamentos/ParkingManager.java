package BEstacionamentos;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingManager {
    private final Semaphore semaphore_vaga_regular = new Semaphore(5);
    private final Semaphore semaphore_vaga_prioridade = new Semaphore(2);
    private final Semaphore semaphore_entrada = new Semaphore(1);
    private final Semaphore semaphore_saida = new Semaphore(1);

    private final AtomicInteger carrosDesistiram = new AtomicInteger(0);
    private final AtomicInteger carrosEntraram = new AtomicInteger(0);

    public Semaphore getSemaphore_entrada() {
        return semaphore_entrada;
    }

    public Semaphore getSemaphore_saida() {
        return semaphore_saida;
    }

    public Semaphore getSemaphore_vaga_regular() {
        return semaphore_vaga_regular;
    }

    public Semaphore getSemaphore_vaga_prioridade() {
        return semaphore_vaga_prioridade;
    }

    public void incrementaDesistencia(){
        carrosDesistiram.incrementAndGet();
    }

    public void incrementaEntrada(){
        carrosEntraram.incrementAndGet();
    }

    public void printStats(){
        System.out.println("=== STATUS DO ESTACIONAMENTO ===");
        System.out.printf("vagas regulares disponiveis: %d/5 \n", semaphore_vaga_regular.availablePermits());
        System.out.printf("vagas prioritarias disponiveis: %d/2 \n", semaphore_vaga_prioridade.availablePermits());
        System.out.println("total entradas: " + carrosEntraram.get());
        System.out.println("total de desistencias: " + carrosDesistiram.get() + "\n");
    }

}
