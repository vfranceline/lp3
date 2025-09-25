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
            //o cliente é criado e tenta se conectar ao servidor no endereço e 
            //na porta 12345
            //se o servidor não estiver escutando, isso vai dar erro? R: sim!
            Socket client = new Socket("localhost", 12345);

            //criando um "cano de entrada" para receber dados do servidor
            ObjectInputStream entrada = new ObjectInputStream(client.getInputStream());

            //vai ler o obj que o servidor enviou e armazena (ele já sabe exatamente o tipo de dado q vai receber)
            Date data_atual = (Date)entrada.readObject();

            JOptionPane.showMessageDialog(null, "data de hj, recebida do servidor: " + data_atual.toString(), "TCP", 0);
            entrada.close();
            System.out.println("conexão finished");
        }
        catch(Exception e){
            System.out.println("error: " + e.getMessage());
        }
    }
}
