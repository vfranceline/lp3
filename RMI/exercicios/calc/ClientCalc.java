package RMI.exercicios.calc;

import java.rmi.Naming;

public class ClientCalc {
    public static void main(String[] args) {
        try {
            String url = "rmi://localhost:12345/CalculadoraService";
            Calculadora calc = (Calculadora) Naming.lookup(url);

            System.out.println("testando calculadora remota");

            double num1 = 10;
            double num2 = 5;

            System.out.println(num1 + " + " + num2 + " = " + calc.soma(num1, num2));
            System.out.println(num1 + " - " + num2 + " = " + calc.subtracao(num1, num2));
            System.out.println(num1 + " * " + num2 + " = " + calc.multiplicacao(num1, num2));
            System.out.println(num1 + " / " + num2 + " = " + calc.divisao(num1, num2));

        } catch (Exception e) {
            System.out.println("Erro: " + e.toString());
        }
    }
}
