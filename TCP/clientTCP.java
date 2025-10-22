package TCP;

import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.Date;

import javax.swing.JOptionPane;

//esse new socket é o ato de "discar" para o servidor
//qnd essa conexão é estabelecida, ambos podem criar fluxos de entrada(InputStream)
//e saida (OutputStream) para trocar infos

public class clientTCP {
    public static void main(String[] args){
        try{
            // 1. O "telefonema" (Discar).
            // Tenta se conectar ao servidor em "localhost" na porta 12345.
            // Se o servidor não estiver rodando (em 'accept()'),
            // isto dará um erro (Connection refused).
            Socket client = new Socket("localhost", 12345);

            // 2. Cria um "cano de entrada" (ObjectInputStream)
            // para RECEBER dados (objetos) DO SERVIDOR.
            ObjectInputStream entrada = new ObjectInputStream(client.getInputStream());

            // 3. Lê o objeto que o servidor enviou.
            // A thread do cliente BLOQUEIA aqui e "espera"
            // até que o servidor envie o objeto 'Date'.
            Date data_atual = (Date)entrada.readObject();

            // 4. (Opcional) Exibe a data recebida em uma janela
            JOptionPane.showMessageDialog(null, "data de hj, recebida do servidor: " + data_atual.toString(), "TCP", 0);
            
            // 5. Fecha a conexão ("Desliga o telefone")
            entrada.close();
            System.out.println("conexão finished");
        }
        catch(Exception e){
            System.out.println("error: " + e.getMessage());
        }
    }
}
