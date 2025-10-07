package lista.ex2;

import java.io.*;
import java.net.*;

import javax.swing.ImageIcon;

public class server {
    public static void main(String[] args) {
        try {
            ServerSocket server = new ServerSocket(12345);
            System.out.println("ouvindo da porta 12345");

            while (true) {
                Socket client = server.accept();
                
                System.out.println("cliente conectado, aguardando o envio do arquivo");

                DataInputStream arquivo = new DataInputStream(client.getInputStream());

                //leu o nome do arquivo enviado pelo client e criou um arquivo vazio com o mesmo nome no disco do server
                String nomeArquivo = arquivo.readUTF();
                // byte[] conteudoArquivo = arquivo.readAllBytes();

                FileOutputStream fos = new FileOutputStream(nomeArquivo);            

                byte[] buffer = new byte[4096];
                int bytesLidos;

                while ((bytesLidos = arquivo.read(buffer)) != -1) {
                    fos.write(buffer, 0, bytesLidos);
                }

                System.out.println("arquivo salvo no servidor!!");

                ImageIcon imagem = new ImageIcon(nomeArquivo);

                javax.swing.JOptionPane.showMessageDialog(null, "", "Imagem Recebida", javax.swing.JOptionPane.INFORMATION_MESSAGE, imagem);

                fos.close();
                arquivo.close();
                client.close();

            }

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
        
    }
}
