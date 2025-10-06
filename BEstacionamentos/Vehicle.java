package BEstacionamentos;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class Vehicle implements Runnable{

    protected final String tipo;
    protected final int idVehicle;
    private final ParkingManager manager;

    public Vehicle(String tipo, int idVehicle, ParkingManager manager){
        this.tipo = tipo;
        this.idVehicle = idVehicle;
        this.manager = manager;
    }

    @Override
    public void run(){
        try {
            System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") chegou ao portão de entrada");
            manager.getSemaphore_entrada().acquire();
            if (tipo.equalsIgnoreCase("PRIORITARIO")){
                getVagaPrioridade();
            } else {
                getVagaRegular();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    public void saidaPrioridade(){
        try {
            manager.getSemaphore_saida().acquire();
            manager.getSemaphore_vaga_prioridade().release();
            manager.getSemaphore_saida().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void saidaRegular(){
        try {
            manager.getSemaphore_saida().acquire();
            manager.getSemaphore_vaga_regular().release();
            manager.getSemaphore_saida().release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVagaRegular(){
        try {
            if (manager.getSemaphore_vaga_regular().tryAcquire()){
                manager.getSemaphore_entrada().release();
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") conseguiu vaga REGULAR");
                Thread.sleep(randomTempo());
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") está estacionado por " + randomTempo() + "ms");
                saidaRegular();
            } else {
                manager.getSemaphore_entrada().release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void getVagaPrioridade(){
        try{
            if (manager.getSemaphore_vaga_prioridade().tryAcquire()){
                manager.getSemaphore_entrada().release();
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") conseguiu vaga PRIORIDADE");
                Thread.sleep(randomTempo());
                System.out.println("Veiculo #" + idVehicle + "(" + tipo + ") está estacionado por " + randomTempo() + "ms");
                saidaPrioridade();
            } else {
                getVagaRegular();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public int randomTempo(){
        Random random = new Random();

        int tempo = random.nextInt(1000, 5000);
        return tempo;
    }
}