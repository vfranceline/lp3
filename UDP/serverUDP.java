package UDP;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

import java.io.IOException;
import javax.swing.JOptionPane;

// destinatario

public class serverUDP {
    public static void main(String[] args) {
        // 1. Validação do argumento (porta)
        if(args.length != 1) {
            System.out.println("fala a porta q tu quer ouvir, pae");
            System.exit(0);
        }

        try {
            // 2. Definir a porta de escuta
            int port = Integer.parseInt(args[0]);

            // 3. Criar o "Socket" (a "Caixa de Correio" do servidor)
            // O servidor DEVE se "amarrar" (bind) a uma porta específica
            // para que os clientes saibam para onde enviar os pacotes.
            DatagramSocket ds = new DatagramSocket(port);
            System.out.println("ouvindo da porta: " + port);

            // 4. Preparar um "envelope vazio" para receber dados
            // Precisamos alocar um espaço (buffer) ANTES de receber.
            // Se a mensagem for maior que 256 bytes, ela será cortada.
            byte[] msg = new byte[256];
            DatagramPacket pkg = new DatagramPacket(msg, msg.length);

            // 5. BLOQUEAR e Esperar pelo pacote
            // A thread 'main' "dorme" aqui (bloqueada em I/O).
            // Ela fica parada até que um DatagramPacket
            // chegue na porta 'port'.
            ds.receive(pkg);

            // 6. Pacote Recebido!
            // Os dados estão em 'pkg.getData()' (o buffer de 256 bytes).
            // Convertemos para String e usamos .trim() para
            // remover o lixo (bytes nulos) do final do buffer.
            JOptionPane.showMessageDialog(null, new String(pkg.getData()).trim(), "mensagem recebida aqui, painho",1);
            
            // 7. Fechar o socket
            ds.close();
            
            // Este servidor é o mais simples possível: ele recebe UMA
            // mensagem e depois termina.

        } catch (IOException ioe) {
            // TODO: handle exception
        }
    }
}