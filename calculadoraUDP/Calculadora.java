package calculadoraUDP;

import java.sql.Struct;

/**
 * Classe Calculadora para operações matemáticas básicas.
 * Esta é uma classe "POJO" (Plain Old Java Object).
 * Ela não tem conhecimento sobre redes ou threads.
 * Sua única responsabilidade é conter os dados e a lógica do cálculo.
 */
public final class Calculadora {

    private float a;
    private byte operacao;
    private float b;

    /**
     * Construtor padrão.
     */
    public Calculadora() {}

    /**
     * Construtor com parâmetros.
     */
    public Calculadora(float a, byte operacao, float b) {
        this.a = a;
        this.operacao = operacao;
        this.b = b;
    }

    // Getters
    public float getA() {
        return a;
    }

    public float getB() {
        return b;
    }

    public byte getOperacao() {
        return operacao;
    }

    // Setters
    public void setA(float a) {
        this.a = a;
    }

    public void setB(float b) {
        this.b = b;
    }

    public void setOperacao(byte operacao) {
        this.operacao = operacao;
    }

    /**
     * Soma os atributos a e b.
     */
    public float somar() {
        return a + b;
    }

    /**
     * Subtrai b de a.
     */
    public float subtrair() {
        return a - b;
    }

    /**
     * Multiplica a por b.
     */
    public float multiplicar() {
        return a * b;
    }

    /**
     * Executa o cálculo com base no 'byte' da operação
     * e retorna o resultado como uma String.
     */
    public String calcular(){
        // O cliente envia a operação (ex: '+') como um byte.
        switch (this.operacao) {
            case '+':
                return Float.toString(somar());
            case '-':
                return Float.toString(subtrair());
            case '*':
                return Float.toString(multiplicar());
            case '/':
                return Float.toString(dividir());
            default:
                throw new IllegalArgumentException("Operação inválida.");
        }
    }

    /**
     * Divide a por b.
     * @throws ArithmeticException se b for zero.
     */
    public float dividir() throws ArithmeticException {
        if (b == 0) {
            throw new ArithmeticException("Divisão por zero não é permitida.");
        }
        return a / b;
    }
}