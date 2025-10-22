package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// remetente

public class clientUDP {
    public static void main(String[] args) {
        // 1. Validação dos argumentos de linha de comando
        // O programa precisa de 3 argumentos: <host> <porta> <mensagem>
        if(args.length != 3){
            System.out.println("uso correto: <nome da maquina> <porta> <mensagem>");
            System.exit(0);
        }

        try {
            // 2. Preparar os dados para envio
            
            // Converte o nome do host (ex: "localhost") em um endereço IP
            InetAddress addr = InetAddress.getByName(args[0]); 
            // Converte a porta (String) em um número (int)
            int port = Integer.parseInt(args[1]);
            // Converte a mensagem (String) em um array de bytes
            byte[] msg = args[2].getBytes();

            // 3. Criar o "Pacote" (a "Carta")
            // O DatagramPacket é o "envelope" UDP.
            // Ele contém:
            // - A mensagem (msg)
            // - O tamanho da mensagem (msg.length)
            // - O endereço de destino (addr)
            // - A porta de destino (port)
            DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);

            // 4. Criar o "Socket" (a "Caixa de Correio" do cliente)
            // DatagramSocket é por onde enviamos o pacote.
            // Como não especificamos uma porta, o sistema operacional
            // escolhe uma porta livre aleatória para este cliente.
            DatagramSocket ds = new DatagramSocket();
            
            // 5. Enviar o pacote
            // 'send' é a ação de "colocar a carta no correio".
            // É "fire-and-forget": o cliente envia e não tem
            // garantia nenhuma de que o pacote chegou.
            ds.send(pkg);
            
            System.out.println("mensagem send para: " + addr.getHostAddress() + "\n" + "port: " + port + "\n" + "mensagem: " + args[2]);
            
            // 6. Fechar o socket (liberar a porta)
            ds.close();
        } catch (IOException ioe) {
            // TODO: handle exception
        }
    }
}