package estudando_de_vdd.TCP;

import java.net.ServerSocket;
import java.net.Socket;

public class serverTCP {
    public static void main(String[] args) {
        try {
            //criando um posto de escuta na porta 12345
            ServerSocket server = new ServerSocket(12345);
            System.out.println("server listening to port 12345");

            while (true) {
                Socket client = server.accept();   
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }
}
