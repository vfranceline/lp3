package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

// remetente

public class clientUDP {
    public static void main(String[] args) {
        //garantindo que o cliente vai fornecer os parametros necessarios
        if(args.length != 3){
            System.out.println("uso correto: <nome da maquina> <porta> <mensagem>");
            System.exit(0);
        }

        try {
            //pega o endereço do destinatario e a porta
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]); //transformando a string(passada como parametro) em int
            byte[] msg = args[2].getBytes(); //pegando a msg que vai ser enviada

            //criando o pkg de dados (a carta) com a msg, o tamanho, o endereço e a porta do DESTINATARIO
            DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);

            // criando um socket udp (a caixa de correio) e enviando o pkg
            DatagramSocket ds = new DatagramSocket();
            ds.send(pkg);
            System.out.println("mensagem send para: " + addr.getHostAddress() + "\n" + "port: " + port + "\n" + "mensagem: " + args[2]);
            ds.close();
        } catch (IOException ioe) {
            // TODO: handle exception
        }
    }
}
