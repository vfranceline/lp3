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

Um sistema de chat que permite que vários clientes se conectem e conversem em tempo real. O servidor utiliza threads para lidar com cada cliente simultaneamente (usando `CopyOnWriteArrayList` para gerenciamento de clientes).

  * **`chat/ChatClient.java`**: O cliente de chat (que usa `AtomicBoolean` e uma thread leitora).
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
  * **`Threads/MySynchronized.java`**: Exemplo de uso de blocos `synchronized` para garantir o acesso seguro a recursos compartilhados.
  * **`Threads/myRunnable.java`**: Uma implementação simples da interface `Runnable` (que demonstra uma condição de corrida).
  * **`Threads/Volatile.java`**: Demonstra o problema de visibilidade de memória que `volatile` resolve.
  * **`Threads/Executors_SingleThread_Callable.java`**: Exemplo de uso de `Executors.newSingleThreadExecutor()`.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac Threads/threads.java Threads/MySynchronized.java Threads/myRunnable.java Threads/Volatile.java Threads/Executors_SingleThread_Callable.java
    ```
2.  **Execute os exemplos:**
    ```bash
    java Threads.threads
    java Threads.MySynchronized
    ```

### 6\. Exercícios da Lista (Sockets TCP)

Implementações para a lista de exercícios focada em TCP.

  * **`lista/ex1/EcoServer.java` / `EcoClient.java`**: (Exercício 1) Servidor de Eco que devolve a mensagem recebida.
  * **`lista/ex2/server.java` / `client.java`**: (Exercício 2) Servidor que recebe arquivos (ex: uma imagem) do cliente via TCP.

**Como executar (Ex1):**

1.  `javac lista/ex1/EcoServer.java lista/ex1/EcoClient.java`
2.  `java lista.ex1.EcoServer`
3.  `java lista.ex1.EcoClient`

**Como executar (Ex2):**

1.  `javac lista/ex2/server.java lista/ex2/client.java`
2.  `java lista.ex2.server`
3.  `java lista.ex2.client` (certifique-se que o caminho da imagem em `client.java` está correto)

### 7\. ApoloTech (CountDownLatch)

Simulação de inicialização de um servidor que precisa esperar por 4 módulos de inicialização (como cache, logs, etc.) antes de ficar online. Demonstra o uso de `CountDownLatch` para coordenação de threads.

  * **`ApoloTech/MainApp.java`**: Classe principal que cria o `CountDownLatch(4)` e o `ExecutorService`.
  * **`ApoloTech/ServerInitializer.java`**: Tarefa que "espera" no `latch.await()`.
  * **`ApoloTech/ModuleLoader.java`**: Tarefas que simulam o carregamento e chamam `latch.countDown()`.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac ApoloTech/MainApp.java ApoloTech/ModuleLoader.java ApoloTech/ServerInitializer.java
    ```
2.  **Execute a simulação:**
    ```bash
    java ApoloTech.MainApp
    ```

### 8\. BEstacionamentos (Semaphore)

Simulação de um sistema de estacionamento inteligente que gerencia vagas regulares (5) e prioritárias (2), além de portões de entrada e saída (1 de cada). Demonstra o uso de `Semaphore` para controlar o acesso a recursos limitados e `AtomicInteger` para estatísticas.

  * **`BEstacionamentos/ParkingSimulator.java`**: Classe principal que inicia o `ExecutorService` e gera 20 veículos.
  * **`BEstacionamentos/ParkingManager.java`**: Gerencia os semáforos (`new Semaphore(5)`, `new Semaphore(2)`, `new Semaphore(1)`).
  * **`BEstacionamentos/Vehicle.java`**: Tarefa (Runnable) que representa um veículo tentando `acquire()` e `release()` dos semáforos.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac BEstacionamentos/ParkingSimulator.java BEstacionamentos/ParkingManager.java BEstacionamentos/Vehicle.java
    ```
2.  **Execute a simulação:**
    ```bash
    java BEstacionamentos.ParkingSimulator
    ```

### 9\. Sistema BlackFriday (Produtor-Consumidor)

Uma simulação complexa de um e-commerce na Black Friday, implementando o padrão Produtor-Consumidor.

  * **Produtores** (3 threads) geram pedidos (API, Web, Mobile).

  * **Fila** (`PriorityBlockingQueue`) armazena pedidos, ordenando por prioridade e tempo.

  * **Consumidores** (5 threads) processam os pedidos (`poll()`).

  * **Estoque** (`GerenciadorEstoque`) é um recurso crítico protegido por `ReentrantReadWriteLock`.

  * **Monitor** (1 thread) exibe estatísticas em tempo real (usando `AtomicInteger` e `volatile`).

  * **`sistemaBlackFriday/SistemaBlackFriday.java`**: Classe principal que configura e gerencia os `ExecutorService`s.

  * **`sistemaBlackFriday/Produtor.java`**: Tarefa que gera `Pedido`s e usa `fila.put()`.

  * **`sistemaBlackFriday/Consumidor.java`**: Tarefa que consome `Pedido`s com `fila.poll(timeout)`.

  * **`sistemaBlackFriday/Pedido.java`**: Classe de dados que implementa `Comparable` para a fila de prioridade.

  * **`sistemaBlackFriday/GerenciadorEstoque.java`**: Protege o estoque com `readLock.lock()` e `writeLock.lock()`.

  * **`sistemaBlackFriday/Monitor.java`**: Tarefa que lê os `AtomicInteger`s e é parada via flag `volatile boolean isRunning`.

**Como executar:**

1.  **Compile os arquivos:**
    ```bash
    javac sistemaBlackFriday/*.java
    ```
2.  **Execute a simulação:**
    ```bash
    java sistemaBlackFriday.SistemaBlackFriday
    ```
