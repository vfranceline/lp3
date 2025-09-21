package UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

import javax.swing.JOptionPane;

public class ReceptorUDP {
    public static void main(String[] args) {
      if(args.length != 1) {
        System.out.println("Informe a porta a ser ouvida");
        System.exit(0);
      }

      try {
        //Converte o argumento recebido para inteiro (numero da porta)
        int port = Integer.parseInt(args[0]);
        //Cria o DatagramSocket para aguardar mensagens, neste momento o método fica bloqueando
        //até o recebimente de uma mensagem
        DatagramSocket ds = new DatagramSocket(port);
        System.out.println("Ouvindo a porta: " + port);
        //Preparando o buffer de recebimento da mensagem
        byte[] msg = new byte[256];
        //Prepara o pacote de dados
        DatagramPacket pkg = new DatagramPacket(msg, msg.length);
        //Recebimento da mensagem
        ds.receive(pkg);
        JOptionPane.showMessageDialog(null,new String(pkg.getData()).trim(),
         "Mensagem recebida",1);
        ds.close();
      }

      catch(IOException ioe) {}
  }
}

