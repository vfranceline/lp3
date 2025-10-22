package lista.ex1;

import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class EcoServer {
    public static void main(String[] args) {
        try {
            // 1. Cria o "posto de escuta" (ServerSocket) na porta 12345.
            ServerSocket server = new ServerSocket(12345);
            System.out.println("server listening to port 12345");

            // 2. Loop infinito para aceitar clientes (um de cada vez)
            while (true) {
                // 3. BLOQUEIA e ESPERA por um "telefonema" (conexão) de cliente.
                // A thread 'main' "dorme" aqui até um cliente conectar.
                Socket client = server.accept(); // "Atende o telefone"
                System.out.println("client connected " + client.getInetAddress().getHostAddress());

                // --- Início do Atendimento ao Cliente ---
                
                // 4. Prepara-se para RECEBER a mensagem do cliente
                ObjectInputStream entradaCliente = new ObjectInputStream((client.getInputStream()));
                
                // 5. Lê o objeto (String) que o cliente enviou.
                // Bloqueia aqui até o cliente enviar algo.
                String msg = (String)entradaCliente.readObject();
                System.out.println("msg recived: " + msg);

                // 6. Prepara-se para "ECOAR" (enviar de volta) a mensagem
                ObjectOutputStream saida = new ObjectOutputStream(client.getOutputStream());
                
                // 7. Envia o MESMO objeto (msg) de volta para o cliente
                saida.writeObject(msg);
                saida.flush(); // Garante o envio

                // 8. Fecha a conexão com ESTE cliente
                entradaCliente.close();
                saida.close();
                client.close();
                
                // --- Fim do Atendimento ---
                // O loop 'while' volta ao topo e chama 'server.accept()'
                // para esperar o PRÓXIMO cliente.
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}