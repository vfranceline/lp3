package BEstacionamentos;

import java.util.concurrent.Semaphore;
import java.util.concurrent.atomic.AtomicInteger;

public class ParkingManager {
    // --- TÓPICO 15: SEMAPHORE (SEMÁFORO) ---
    // Um Semáforo é um "controlador de permissões".
    
    // 1. Semáforo de Vagas Regulares
    // new Semaphore(5) cria um semáforo que começa com 5 "permissões" (licenses).
    // Cada carro "NORMAL" precisará adquirir 1 dessas 5 permissões.
    private final Semaphore semaphore_vaga_regular = new Semaphore(5);
    
    // 2. Semáforo de Vagas Prioritárias
    // Começa com 2 permissões para as vagas prioritárias.
    private final Semaphore semaphore_vaga_prioridade = new Semaphore(2);

    // 3. Semáforos de Portão (Mutex)
    // Um semáforo com apenas 1 permissão é chamado de "Mutex" (Mutal Exclusion).
    // Ele funciona como um lock (Tópico 16) ou um bloco 'synchronized' (Tópico 5),
    // garantindo que apenas UMA thread (veículo) possa passar pelo portão
    // de entrada por vez.
    private final Semaphore semaphore_entrada = new Semaphore(1);
    
    // Garante que apenas UM veículo possa sair por vez.
    private final Semaphore semaphore_saida = new Semaphore(1);


    // --- TÓPICO 8: CLASSES ATÔMICAS ---
    // Se usássemos 'int carrosDesistiram = 0;', e duas threads
    // tentassem fazer 'carrosDesistiram++' ao mesmo tempo,
    // poderíamos ter uma "condição de corrida" (uma das atualizações ser perdida).
    //
    // 'AtomicInteger' resolve isso. O método 'incrementAndGet()'
    // é uma operação atômica (indivisível), garantindo que o contador
    // seja incrementado de forma segura por múltiplas threads
    // sem a necessidade de 'synchronized' ou 'Lock'.
    private final AtomicInteger carrosDesistiram = new AtomicInteger(0);
    private final AtomicInteger carrosEntraram = new AtomicInteger(0);

    // Getters para os semáforos, para que os Veículos possam acessá-los
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

    // Métodos atômicos para atualizar os contadores
    public void incrementaDesistencia(){
        carrosDesistiram.incrementAndGet(); // Operação thread-safe
    }

    public void incrementaEntrada(){
        carrosEntraram.incrementAndGet(); // Operação thread-safe
    }

    // Método para imprimir o estado final
    public void printStats(){
        System.out.println("=== STATUS DO ESTACIONAMENTO ===");
        // .availablePermits() retorna quantas permissões (vagas)
        // estão disponíveis no momento. É thread-safe.
        System.out.printf("vagas regulares disponiveis: %d/5 \n", semaphore_vaga_regular.availablePermits());
        System.out.printf("vagas prioritarias disponiveis: %d/2 \n", semaphore_vaga_prioridade.availablePermits());
        
        // .get() retorna o valor atual do AtomicInteger.
        System.out.println("total entradas: " + carrosEntraram.get());
        System.out.println("total de desistencias: " + carrosDesistiram.get() + "\n");
    }

}
