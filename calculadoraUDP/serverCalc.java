package calculadoraUDP;

import java.net.DatagramSocket;

import javax.swing.JOptionPane;

import java.io.IOException;
import java.net.DatagramPacket;

public class serverCalc {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Informe a porta a ser ouvida");
            System.exit(0);
        }

        try{
            int port = Integer.parseInt(args[0]);

            DatagramSocket ds = new DatagramSocket(port);

            System.out.println("ouvindo a porta: " + port);

            byte[] msg = new byte[256];

            DatagramPacket pkg = new DatagramPacket(msg, msg.length);
            ds.receive(pkg);

            JOptionPane.showMessageDialog(null, new String(pkg.getData()).trim(), "resultado da operação", 1);

            ds.close();
        }

        catch(IOException ioe){}
    }
}
