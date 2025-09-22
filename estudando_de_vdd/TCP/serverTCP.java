package estudando_de_vdd.TCP;

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
            //criando um posto de escuta na porta 12345
            ServerSocket server = new ServerSocket(12345);
            System.out.println("server listening to port 12345");

            // Loop infinito para aceitar múltiplas conexões de clientes
            while (true) {
                //bloqueia aqui até um cliente tentar se conectar
                //quando um cliente se conecta, esse metodo accept() retorna um socket
                //q representa a conexão direta com aquele cliente q acabou de se conectar

                // Aguarda e aceita uma conexão de um cliente
                Socket client = server.accept();

                // Exibe o endereço IP do cliente conectado
                System.out.println("client connected " + client.getInetAddress().getHostAddress()); 

                //cria um "cano de saida" para enviar dados ao cliente
                ObjectOutputStream saida = new ObjectOutputStream((client.getOutputStream()));
                saida.flush();
                saida.writeObject(new Date()); //envia o obj date para o cliente
                saida.close();
                client.close(); //fecha a conexão com este cliente em especifico
            }
        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        finally{}
    }
}
