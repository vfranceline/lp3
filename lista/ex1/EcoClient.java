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
            // 1. Conecta-se ao servidor na "localhost" (esta máquina) na porta 12345
            // Isso é o "telefonema" para o servidor.
            Socket client = new Socket("localhost", 12345);
            System.out.println("Connecting to eco server. Write your message");

            // 2. Prepara-se para ENVIAR uma mensagem (um Objeto String)
            // ObjectOutputStream é um "cano" que envia objetos Java serializados.
            ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
            
            // 3. Lê a mensagem do TECLADO do usuário
            BufferedReader userIn = new BufferedReader(new InputStreamReader(System.in));
            String userMSG = userIn.readLine(); // Bloqueia aqui até o usuário dar "Enter"
            
            // 4. ENVIA a String (como um objeto) para o servidor
            saida.writeObject(userMSG);
            saida.flush(); // Garante que a mensagem foi enviada agora

            // 5. Prepara-se para OUVIR o eco (resposta) do servidor
            // ObjectInputStream é um "cano" que recebe objetos Java.
            ObjectInputStream entrada = new ObjectInputStream(client.getInputStream());
            
            // 6. Lê o objeto (a String) que o servidor enviou de volta
            String msg = (String)entrada.readObject(); // Bloqueia aqui até a resposta chegar

            // 7. Imprime o eco
            System.out.println("eco: " + msg);
            
            // 8. Fecha tudo
            entrada.close();
            saida.close();
            System.out.println("connection finished");

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}