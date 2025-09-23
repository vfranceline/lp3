# LP3 - Atividades de Linguagem de Programação III

Este repositório contém uma coleção de projetos desenvolvidos na disciplina de Linguagem de Programação III, explorando conceitos de programação de redes com sockets (TCP e UDP), threads e concorrência em Java.

## Projetos

### 1\. Comunicação UDP Simples

Implementação de um cliente e servidor que se comunicam via protocolo UDP.

  * **`UDP/clientUDP.java`**: Envia uma mensagem para um servidor UDP.
  * **`UDP/serverUDP.java`**: Recebe uma mensagem de um cliente UDP.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac UDP/serverUDP.java UDP/clientUDP.java
    ```
2.  **Inicie o servidor:**
    ```bash
    java UDP.serverUDP <porta>
    ```
3.  **Execute o cliente:**
    ```bash
    java UDP.clientUDP <host> <porta> <mensagem>
    ```

### 2\. Calculadora UDP

Uma calculadora cliente-servidor que utiliza o protocolo UDP para comunicação.

  * **`calculadoraUDP/clientUDP.java`**: Envia uma operação matemática para o servidor.
  * **`calculadoraUDP/serverCalc.java`**: Recebe a operação, calcula e exibe o resultado.
  * **`calculadoraUDP/Calculadora.java`**: Classe que implementa as operações da calculadora.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac calculadoraUDP/serverCalc.java calculadoraUDP/clientUDP.java calculadoraUDP/Calculadora.java
    ```
2.  **Inicie o servidor:**
    ```bash
    java calculadoraUDP.serverCalc <porta>
    ```
3.  **Execute o cliente:**
    ```bash
    java calculadoraUDP.clientUDP <host> <porta> <num1> <operação> <num2>
    ```
    *Exemplo:* `java calculadoraUDP.clientUDP localhost 9876 10 + 5`

### 3\. Comunicação TCP Simples

Implementação de um cliente e servidor que se comunicam via protocolo TCP.

  * **`TCP/clientTCP.java`**: Conecta-se a um servidor TCP e recebe a data e hora atual.
  * **`TCP/serverTCP.java`**: Aguarda conexões de clientes e envia a data e hora atual.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac TCP/serverTCP.java TCP/clientTCP.java
    ```
2.  **Inicie o servidor:**
    ```bash
    java TCP.serverTCP
    ```
3.  **Execute o cliente:**
    ```bash
    java TCP.clientTCP
    ```

### 4\. Chat Multi-cliente com Sockets e Threads

Um sistema de chat que permite que vários clientes se conectem e conversem em tempo real. O servidor utiliza threads para lidar com cada cliente simultaneamente.

  * **`chat/ChatClient.java`**: O cliente de chat que se conecta ao servidor.
  * **`chat/ChatServer.java`**: O servidor de chat que gerencia as conexões e o broadcast de mensagens.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac chat/ChatServer.java chat/ChatClient.java
    ```
2.  **Inicie o servidor:**
    ```bash
    java chat.ChatServer
    ```
3.  **Execute um ou mais clientes em terminais separados:**
    ```bash
    java chat.ChatClient
    ```

### 5\. Exemplos de Threads

Este diretório contém exemplos que demonstram o uso de threads e sincronização em Java.

  * **`Threads/threads.java`**: Demonstra a criação de threads implementando a interface `Runnable` e usando expressões lambda.
  * **`Threads/MySynchronized.java`**: Exemplo de uso de blocos sincronizados para garantir o acesso seguro a recursos compartilhados entre múltiplas threads.
  * **`Threads/myRunnable.java`**: Uma implementação simples da interface `Runnable`.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac Threads/threads.java Threads/MySynchronized.java Threads/myRunnable.java
    ```
2.  **Execute os exemplos:**
    ```bash
    java Threads.threads
    java Threads.MySynchronized
    ```