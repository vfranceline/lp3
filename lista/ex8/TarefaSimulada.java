package lista.ex8;

import java.util.Random;

public class TarefaSimulada implements Runnable{
    private final String tarefa;

    public TarefaSimulada (String nomeTarefa){
        this.tarefa = nomeTarefa;
    }

    @Override
    public void run() {
        try {
            System.out.println(">>> [" + tarefa + "] INICIADA.");
            Thread.sleep(randomTempo()); // Simula o trabalho
            System.out.println("<<< [" + tarefa + "] FINALIZADA.");
        } catch (Exception e) {
            System.err.println("[" + tarefa + "] foi interrompida.");
        }
    }

    public int randomTempo(){
        Random random = new Random();
        int tempo = random.nextInt(3000, 10000); // 3 a 10 segundos
        return tempo;
    }
}
