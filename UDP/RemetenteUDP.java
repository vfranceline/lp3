package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class RemetenteUDP {
    public static void main(String[] args){
        if(args.length != 3){
            System.out.println("uso correto: <nome da maquina> <porta> <mensagem>");
            System.exit(0);
        }

        try{
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            byte[] msg = args[2].getBytes();

            DatagramPacket pkg = new DatagramPacket(msg, msg.length, addr, port);

            DatagramSocket ds = new DatagramSocket();

            ds.send(pkg);
            System.out.println("mensagem enviada para: " + addr.getHostAddress() + "\n" + "porta: " + port + "\n" + "mensagem: " + args[2]);
            ds.close();
        }

        catch(IOException ioe){}
    }
}
