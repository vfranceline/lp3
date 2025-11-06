package exercicios.TRE;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ServicoVotacao extends Remote{
    
    //autenticação
    String autenticar(String cpf, String senha, boolean admin) throws RemoteException;
    boolean validarToken(String token) throws RemoteException;
}
