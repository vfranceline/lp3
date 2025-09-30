package ApoloTech

public class ModuleLoader implements Runnable{

    @Override
    public void run(){
        String nome = Thread.currentThread().getName();
        
        if (nome.equalsIgnoreCase("Configuração")){
            try {
                Thread.sleep(6000);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (nome.equalsIgnoreCase("Segurança")){
            try {
                Thread.sleep(12000);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (nome.equalsIgnoreCase("Cache")){
            try {
                Thread.sleep(9000);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (nome.equalsIgnoreCase("Logs")){
            try {
                Thread.sleep(4000);    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        System.out.println("Módulo [" + nome + "] carregado");
        latch.countDown();
    }
    
}