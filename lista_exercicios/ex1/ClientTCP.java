package lista_exercicios.ex1;

import java.io.ObjectInputStream;
import java.net.Socket;

import javax.swing.JOptionPane;

// Crie um servidor TCP capaz de receber arquivos de clientes e salvá-los 
// no disco do servidor. O cliente deve ser capaz de enviar arquivos para 
// o servidor.

public class ClientTCP {
   public static void main(String[] args) {
      // Implementação do cliente TCP para enviar arquivos ao servidor
      try {
        Socket cliente = new Socket("localhost",12345);
        ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
        String teste = (String)entrada.readObject();

        JOptionPane.showMessageDialog(null, "mensagem recebida: " + teste);
      }
   } 
}
