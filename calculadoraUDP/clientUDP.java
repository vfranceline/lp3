package estudando_de_vdd.calculadoraUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class clientUDP {
    public static void main(String[] args) {
        if (args.length != 5) {
            System.out.println("uso correto: <nome da maquina> <porta> <num1> <operação> <num2>");
            System.exit(0);
        }

        try{
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            float num1 = Float.parseFloat(args[2]);
            byte[] operacao = args[3].getBytes();
            float num2 = Float.parseFloat(args[4]);

            Calculadora calc = new Calculadora(num1, operacao[0], num2);
            byte[] dados = calc.calcular().getBytes();

            DatagramPacket pkg = new DatagramPacket(dados, dados.length, addr, port);
            DatagramSocket ds = new DatagramSocket();

            ds.send(pkg);
            System.out.println("resultado enviado para: " + addr.getHostAddress() + "\n" + "porta: " + port);
            ds.close();
        }

        catch(IOException ioe) {}
    }
}
