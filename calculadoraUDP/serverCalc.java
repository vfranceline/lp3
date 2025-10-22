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
            // 1. Definir a porta de escuta
            int port = Integer.parseInt(args[0]);

            // 2. Abrir o Socket UDP (A "Caixa de Correio" do Servidor)
            // O servidor DEVE especificar em qual porta ele está "ouvindo".
            DatagramSocket ds = new DatagramSocket(port);

            System.out.println("ouvindo a porta: " + port);

            // 3. Preparar o Pacote de Recebimento
            // Criamos um "envelope vazio" (um buffer) para receber os dados.
            // Precisamos alocar espaço (ex: 256 bytes) ANTES.
            byte[] msg = new byte[256];
            DatagramPacket pkg = new DatagramPacket(msg, msg.length);

            // 4. Aguardar o Recebimento
            // A thread 'main' vai BLOQUEAR aqui (estado RUNNABLE,
            // mas bloqueada em uma chamada de sistema/IO).
            // Ela fica "dormindo" até que um pacote UDP chegue
            // nesta porta (porta 'port').
            ds.receive(pkg);

            // 5. Pacote Recebido!
            // Os dados estão em 'pkg.getData()'. Convertemos para String.
            // .trim() é importante para remover os bytes extras (nulos)
            // do buffer de 256 bytes que não foram usados.
            JOptionPane.showMessageDialog(null, new String(pkg.getData()).trim(), "resultado da operação", 1);

            // 6. Fechar o socket
            ds.close();
            
            // Este servidor é iterativo e simples: ele ouve UM pacote
            // e depois termina.
        }

        catch(IOException ioe){}
    }
}
