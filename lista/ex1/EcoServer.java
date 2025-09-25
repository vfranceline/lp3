package lista.ex1;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EcoServer {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(12345);
            System.out.println("server listening to port 12345");

            while (true) {
                Socket client = server.accept();
                System.out.println("client connected " + client.getInetAddress().getHostAddress());

                //preparando para receber a mensagem do cliente
                ObjectInputStream entradaCliente = new ObjectInputStream((client.getInputStream()));
                String msg = (String)entradaCliente.readObject();
                System.out.println("msg recived: " + msg);

                //prepara para ecoar a mensagem de volta
                ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
                saida.writeObject(msg);
                saida.flush(); //garante que a mensagem foi enviada

                //fechando conexões
                entradaCliente.close();
                saida.close();
                client.close();
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
