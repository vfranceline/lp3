package lista.ex2;

import java.io.*;
import java.net.*;

public class client {
    public static void main(String[] args) {
        try {
            //conectar ao servidor
            Socket client = new Socket("localhost", 12345);

            String fileName = "lista/ex2/Chiikawa queen never cry.jpeg";
            //file input para pegar o nome do arquivo
            FileInputStream fis = new FileInputStream(fileName);
            

            DataOutputStream dos = new DataOutputStream(client.getOutputStream());

            dos.writeUTF(fileName);

            byte[] buffer = new byte[4096];
            int bytesLidos;

            while ((bytesLidos = fis.read(buffer)) != -1) {
                dos.write(buffer, 0, bytesLidos);
            }
            
            fis.close();
            dos.close();
            client.close();

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage());
        }
    }
}
