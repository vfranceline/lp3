package BEstacionamentos;

import java.util.concurrent.Semaphore;

public class ParkingManager {
    private final Semaphore semaphore_vaga_regular;
    private final Semaphore semaphore_vaga_prioridade;
    private final Semaphore semaphore_entrada;
    private final Semaphore semaphore_saida;
    private final int MAX_PARKING_SPOT_REGULAR = 5;
    private final int MAX_PARKING_SPOT_PRIORITY = 2;
    private final int MAX_ENTRANCE_GATE = 1;
    private final int MAX_EXIT_GATE = 1;

    public ParkingManager(){
        semaphore_vaga_regular = new Semaphore(MAX_PARKING_SPOT_REGULAR);
        semaphore_vaga_prioridade = new Semaphore(MAX_PARKING_SPOT_PRIORITY);
        semaphore_entrada = new Semaphore(MAX_ENTRANCE_GATE);
        semaphore_saida = new Semaphore(MAX_EXIT_GATE);
    }

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

    public void printStats(){
        
    }

}
