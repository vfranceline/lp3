package lista.ex2;

import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        try {
            // 1. Conecta-se ao servidor (TCP)
            Socket client = new Socket("localhost", 12345);

            String fileName = "lista/ex2/Chiikawa queen never cry.jpeg";
            
            // 2. Abre o ARQUIVO LOCAL que queremos enviar
            FileInputStream fis = new FileInputStream(fileName);
            
            // 3. Prepara o "cano de saída" para a rede.
            // Usamos DataOutputStream porque ele é bom para enviar
            // dados brutos (bytes) e texto (como o nome do arquivo).
            DataOutputStream dos = new DataOutputStream(client.getOutputStream());

            // 4. ENVIA o NOME do arquivo primeiro, para o servidor saber
            // como salvar o arquivo.
            dos.writeUTF(fileName);

            // 5. Prepara um "balde" (buffer) para enviar o arquivo em pedaços
            byte[] buffer = new byte[4096]; // 4KB por vez
            int bytesLidos;

            // 6. Loop para ler o arquivo do disco e enviar pela rede
            // 'fis.read(buffer)' lê bytes do arquivo e enche o "balde".
            // Retorna -1 quando o arquivo acaba.
            while ((bytesLidos = fis.read(buffer)) != -1) {
                // 'dos.write' envia os bytes lidos (o "balde") pela rede
                dos.write(buffer, 0, bytesLidos);
            }
            
            // 7. Fecha tudo. Fechar o 'dos' sinaliza ao servidor
            // que o envio de dados terminou.
            fis.close();
            dos.close();
            client.close();

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}