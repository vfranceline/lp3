package lista.ex1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EcoClient {
    public static void main(String[] args) {
        try {
            Socket client = new Socket("localhost", 12345);
            System.out.println("Connecting to eco server. Write your message");

            //preparando para enviar a mensagem do server
            ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
            
            //lendo a mensagem do teclado
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            String userMSG = userIn.readLine();
            
            //enviando a mensagem
            saida.writeObject(userMSG);
            saida.flush();

            //preparando para ouvir o eco do servidor
            ObjectInputStream entrada = new ObjectInputStream(client.getInputStream());
            String msg = (String)entrada.readObject();

            System.out.println("eco: " + msg);
            entrada.close();
            saida.close();
            System.out.println("connection finished");

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}