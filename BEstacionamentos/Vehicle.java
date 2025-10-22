package BEstacionamentos;

import java.util.Random;

// --- TÓPICO 4: CRIAÇÃO COM RUNNABLE ---
// Ao implementar Runnable, dizemos que esta classe é uma "tarefa"
// que pode ser executada por uma Thread.
public class Vehicle implements Runnable{

    private final String tipo;
    private final int idVehicle;
    private final ParkingManager manager; // O recurso compartilhado!!
    private boolean estacionou;

    public Vehicle(String tipo, int idVehicle, ParkingManager manager){
        this.tipo = tipo;
        this.idVehicle = idVehicle;
        this.manager = manager;
        this.estacionou = false;
    }

    @Override
    public void run(){
        // Este é o "corpo" da thread. Uma das 4 threads do pool
        // vai executar este código.
        try {
            System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") chegou ao portão de entrada");
            
            // --- TÓPICO 15: SEMAPHORE (acquire) ---
            // Tenta adquirir a permissão do portão de entrada (que só tem 1).
            // 'acquire()' é um método BLOQUEANTE.
            // Se o semáforo de entrada tiver 0 permissões (porque outro
            // carro está entrando), esta thread ficará "dormindo" aqui.
            // (Estado: WAITING - Tópico 2).
            // Quando a outra thread chamar 'release()' no semáforo de entrada,
            // esta thread será "acordada" e continuará.
            manager.getSemaphore_entrada().acquire(); // 'acquire()' é um método BLOQUEANTE.
            
            // Se chegou aqui, a thread (veículo) "passou" pelo portão.
            estacionou = false;

            // Lógica de negócio: tenta a vaga correta
            if (tipo.equalsIgnoreCase("PRIORITARIO")){
                getVagaPrioridade();
            } else {
                getVagaRegular();
            }

            // Atualiza a estatística atômica (Tópico 8)
            if (estacionou){
                manager.incrementaEntrada();
            } else {
                // (Opcional) Poderia chamar manager.incrementaDesistencia() aqui
                // se a lógica de 'getVagaRegular' não tivesse encontrado vaga.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // Lógica para sair da vaga prioritária
    public void saidaPrioridade(){
        try {
            // Pede permissão para o portão de SAÍDA (Mutex)
            manager.getSemaphore_saida().acquire(); // Bloqueia se saída estiver ocupada
            
            // --- TÓPICO 15: SEMAPHORE (release) ---
            // DEVOLVE a permissão da vaga prioritária.
            // Isso incrementa o contador do 'semaphore_vaga_prioridade'
            // (ex: de 0 para 1), acordando
            // outra thread que possa estar esperando (em acquire()) por ela.
            manager.getSemaphore_vaga_prioridade().release();
            
            // Libera o portão de SAÍDA para o próximo carro.
            manager.getSemaphore_saida().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lógica para sair da vaga regular
    public void saidaRegular(){
        try {
            manager.getSemaphore_saida().acquire();
            
            // DEVOLVE a permissão da vaga regular.
            manager.getSemaphore_vaga_regular().release();
            
            manager.getSemaphore_saida().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lógica para tentar pegar vaga regular
    public void getVagaRegular(){
        try {
            // --- TÓPICO 15: SEMAPHORE (tryAcquire) ---
            // 'tryAcquire()' é NÃO-BLOQUEANTE.
            // Ele tenta pegar uma permissão (vaga).
            // - Se conseguir (retorna true): A vaga é nossa.
            // - Se não conseguir (retorna false): O estacionamento está cheio.
            if (manager.getSemaphore_vaga_regular().tryAcquire()){
                // 1. CONSEGUIU A VAGA!
                
                // IMPORTANTE: Libera o portão de ENTRADA
                // para o próximo carro poder TENTAR.
                manager.getSemaphore_entrada().release();
                
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") conseguiu vaga REGULAR");
                this.estacionou = true;
                
                // --- TÓPICO 2: CICLO DE VIDA (TIMED_WAITING) ---
                // Simula o tempo que o carro fica estacionado.
                // A thread "dorme" por um tempo aleatório,
                // entrando no estado TIMED_WAITING e não usando CPU.
                Thread.sleep(randomTempo());
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") está estacionado por " + randomTempo() + "ms");
                
                // 2. Acordou! Agora vai sair.
                saidaRegular();
            } else {
                // 3. NÃO CONSEGUIU VAGA (Estacionamento regular lotado)
                // Apenas libera o portão de ENTRADA e "desiste".
                manager.getSemaphore_entrada().release();
                // (Aqui poderia chamar incrementaDesistencia())
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Lógica para pegar vaga prioritária
    public void getVagaPrioridade(){
        try{
            // Tenta pegar vaga prioritária (não-bloqueante)
            if (manager.getSemaphore_vaga_prioridade().tryAcquire()){
                // 1. Conseguiu vaga prioritária
                manager.getSemaphore_entrada().release(); // Libera portão
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") conseguiu vaga PRIORIDADE");
                this.estacionou = true;
                
                Thread.sleep(randomTempo()); // Fica estacionado (TIMED_WAITING)
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") está estacionado por " + randomTempo() + "ms");

                saidaPrioridade(); // Sai da vaga
            } else {
                // 2. NÃO Conseguiu vaga prioritária (lotada)
                // Um carro prioritário PODE tentar uma vaga regular.
                // (Note que o portão de entrada AINDA ESTÁ "travado"
                // por esta thread, então ela pode tentar a outra vaga
                // sem que outro carro "fure a fila").
                getVagaRegular();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // Gera tempo aleatório para 'sleep'
    public int randomTempo(){
        Random random = new Random();
        int tempo = random.nextInt(1000, 5000); // 1 a 5 segundos
        return tempo;
    }
}