package TCP;

import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

//o server socket é como o atendente da central telefonica, esperando por chamadas na porta
//escolhida. o server.accept() atende uma chamada e cria uma linha direta (socket) com quem
//ligou (o cliente) 

public class serverTCP {
    public static void main(String[] args) {
        try {
            // 1. Cria o "posto de escuta" (a central telefônica) na porta 12345.
            // O ServerSocket apenas "ouve" por pedidos de conexão.
            ServerSocket server = new ServerSocket(12345);
            System.out.println("server listening to port 12345");

            // 2. Loop infinito para aceitar múltiplas conexões (uma de cada vez)
            while (true) {
                // 3. BLOQUEIA aqui e ESPERA.
                // A thread 'main' do servidor "dorme" (estado WAITING/BLOCKED)
                // até que um cliente tente se conectar.
                //
                // 4. Quando um cliente se conecta, o accept() "atende o telefone"
                // e retorna um 'Socket' (a ligação direta com *aquele* cliente).
                Socket client = server.accept();

                // 5. O atendimento ao cliente começa AGORA.
                // (Note que se outro cliente tentar conectar *agora*,
                // ele ficará na fila esperando o loop terminar).
                System.out.println("client connected " + client.getInetAddress().getHostAddress()); 

                // 6. Cria um "cano de saida" (ObjectOutputStream)
                // para enviar dados (objetos) AO CLIENTE.
                ObjectOutputStream saida = new ObjectOutputStream((client.getOutputStream()));
                saida.flush(); // Limpa o "cano"
                
                // 7. ENVIA o objeto (a data atual) para o cliente.
                saida.writeObject(new Date()); 
                
                // 8. Fecha a conexão com ESTE cliente.
                saida.close();
                client.close(); // "Desliga o telefone"
                
                // 9. O loop 'while' volta ao topo e chama 'server.accept()'
                // para esperar o PRÓXIMO cliente.
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        finally{}
    }
}