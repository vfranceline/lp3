package archive.ClientServerTCP;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

import javax.swing.JOptionPane;

public class ClienteTCPBasico {
    public static void main(String[] args){
        try{
            Socket cliente = new Socket("localhost", 12345);
            ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
            Date data_atual = (Date)entrada.readObject();
            
            JOptionPane.showMessageDialog(null, "Data recebida do servidor:"+ data_atual.toString());
            entrada.close();
            System.out.println("conexão encerrada");
        }
        catch(Exception e){
            System.out.println("erro: " + e.getMessage());
        }
    }
}
