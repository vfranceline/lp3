package calculadoraUDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class clientUDP {
    public static void main(String[] args) {
        // Validação dos argumentos de linha de comando
        if (args.length != 5) {
            System.out.println("uso correto: <nome da maquina> <porta> <num1> <operação> <num2>");
            System.exit(0);
        }

        try{
            // 1. Preparar os dados
            
            // Converte o nome da máquina (ex: "localhost") em um endereço IP.
            InetAddress addr = InetAddress.getByName(args[0]);
            int port = Integer.parseInt(args[1]);
            
            // Pega os dados da operação
            float num1 = Float.parseFloat(args[2]);
            byte[] operacao = args[3].getBytes(); // Pega o primeiro byte do operador (ex: '+')
            float num2 = Float.parseFloat(args[4]);

            // 2. Executar a lógica (localmente)
            // O cliente cria a calculadora, faz o cálculo e
            // converte o *resultado* em bytes.
            Calculadora calc = new Calculadora(num1, operacao[0], num2);
            byte[] dados = calc.calcular().getBytes(); // 'dados' é o array de bytes do resultado

            // 3. Preparar o Pacote UDP (A "Carta")
            // Um DatagramPacket é um "envelope" que contém:
            // - Os dados (dados)
            // - O tamanho dos dados (dados.length)
            // - O endereço de destino (addr)
            // - A porta de destino (port)
            DatagramPacket pkg = new DatagramPacket(dados, dados.length, addr, port);

            // 4. Enviar o Pacote
            // DatagramSocket é a "caixa de correio" do cliente.
            // Não especificamos porta, o SO escolhe uma porta livre.
            DatagramSocket ds = new DatagramSocket();
            
            // 'send()' dispara o pacote e "esquece" (fire-and-forget).
            // Não há garantia de entrega.
            ds.send(pkg);
            
            System.out.println("resultado enviado para: " + addr.getHostAddress() + "\n" + "porta: " + port);
            
            // Fecha a "caixa de correio"
            ds.close();
        }

        catch(IOException ioe) {}
    }
}
