package lista.ex3;

import java.util.*;

public class dicionario {
    private Map<String, String> bancoDePalavras;

    public dicionario() {
        bancoDePalavras = new HashMap<>();
        
        // Adicionando algumas palavras de exemplo
        bancoDePalavras.put("TCP", "Transmission Control Protocol. Um protocolo de transporte orientado à conexão.");
        bancoDePalavras.put("IP", "Internet Protocol. O principal protocolo de comunicação usado para rotear pacotes na Internet.");
        bancoDePalavras.put("ROS", "Robot Operating System. Um framework para desenvolvimento de software para robôs.");
        bancoDePalavras.put("DNS", "Domain Name System. Sistema que traduz nomes de domínio legíveis por humanos em endereços IP.");
        bancoDePalavras.put("API", "Application Programming Interface. Um conjunto de regras que permite que diferentes softwares se comuniquem.");
    }

    public String buscarSignificado(String palavra){
        if (palavra == null || palavra.trim().isEmpty()){
            return "ERROR: cadê a palavra?";
        }

        String significado = bancoDePalavras.get(palavra.toUpperCase());

        if(significado == null){
            return "SIGNIFICADO NÃO ENCONTRADO";
        } else {
            return significado;
        }
    }
}
