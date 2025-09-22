package archive.ClientServerTCP;

import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class ServidorTCPBasico {
    public static void main (String[] args){
        try{
            ServerSocket servidor = new ServerSocket(12345);
            System.out.println("servidor ouvindo a porta 12345");
            while (true) {
                Socket cliente = servidor.accept();
                System.out.println("cliente conectado" + cliente.getInetAddress().getHostAddress());
                ObjectOutputStream saida = new ObjectOutputStream((cliente.getOutputStream()));
                saida.flush();
                saida.writeObject(new Date());
                saida.close();
                cliente.close();
            }
        }
        catch (Exception e){
            System.out.println("erro: " + e.getMessage());
        }
        finally{}
    }
}

