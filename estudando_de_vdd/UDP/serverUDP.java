package estudando_de_vdd.UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.io.IOException;
import javax.swing.JOptionPane;

// destinatario

public class serverUDP {
    public static void main(String[] args) {
        if(args.length != 1) {
            System.out.println("fala a porta q tu quer ouvir, pae");
            System.exit(0);
        }

        try {
            //define a porta a ser ouvida
            int port = Integer.parseInt(args[0]); //transformando o arg para int

            //criando um socket udp atrelado a essa porta para receber pacotes
            DatagramSocket ds = new DatagramSocket(port);
            System.out.println("ouvindo da porta: " + port);

            //abrindo um espaço para o pkg novo q tá vindo aí
            byte[] msg = new byte[256];
            DatagramPacket pkg = new DatagramPacket(msg, msg.length);

            //programa bloqueia até receber um pacote
            ds.receive(pkg);

            //exibindo a msg na tela
            JOptionPane.showMessageDialog(null, new String(pkg.getData()).trim(), "mensagem recebida aqui, painho",1);
            ds.close();

        } catch (IOException ioe) {
            // TODO: handle exception
        }
    }
}
