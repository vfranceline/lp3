package lista.ex2;

import java.io.*;
import java.net.*;
import javax.swing.ImageIcon;

public class server {
    public static void main(String[] args) {
        try {
            // 1. Inicia o "posto de escuta"
            ServerSocket server = new ServerSocket(12345);
            System.out.println("ouvindo da porta 12345");

            while (true) {
                // 2. BLOQUEIA e ESPERA por um cliente
                Socket client = server.accept();
                System.out.println("cliente conectado, aguardando o envio do arquivo");

                // --- Início do Recebimento do Arquivo ---
                
                // 3. Prepara o "cano de entrada" da rede
                // Usamos DataInputStream para "ler" o que o DataOutputStream enviou.
                DataInputStream arquivo = new DataInputStream(client.getInputStream());

                // 4. Lê o NOME do arquivo primeiro (que o cliente enviou com writeUTF)
                String nomeArquivo = arquivo.readUTF();
                
                // 5. Prepara o "cano de saída" para o DISCO LOCAL.
                // Cria um arquivo vazio com o nome recebido.
                FileOutputStream fos = new FileOutputStream(nomeArquivo);            

                // 6. Prepara o "balde" (buffer) para receber os dados da rede
                byte[] buffer = new byte[4096];
                int bytesLidos;

                // 7. Loop para ler da rede e salvar no disco
                // 'arquivo.read(buffer)' lê os bytes vindos da rede
                // e enche o "balde". Retorna -1 quando o cliente
                // fecha a conexão (sinalizando fim do arquivo).
                while ((bytesLidos = arquivo.read(buffer)) != -1) {
                    // 'fos.write' escreve os bytes do "balde"
                    // no arquivo local do servidor
                    fos.write(buffer, 0, bytesLidos);
                }

                System.out.println("arquivo salvo no servidor!!");

                // (Bônus: Exibe a imagem recebida em um JOptionPane)
                ImageIcon imagem = new ImageIcon(nomeArquivo);
                javax.swing.JOptionPane.showMessageDialog(null, "", "Imagem Recebida", javax.swing.JOptionPane.INFORMATION_MESSAGE, imagem);

                // 8. Fecha tudo (para este cliente)
                fos.close();
                arquivo.close();
                client.close();

                // --- Fim do Recebimento ---
                // Volta ao topo do 'while' para esperar o próximo cliente.
            }

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        
    }
}