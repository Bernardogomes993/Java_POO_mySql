#  Sistema Bancário Java & JDBC

Uma aplicação bancária desenvolvida em Java com persistência de dados em MySQL via JDBC. 
O projeto implementa os principais conceitos de Programação Orientada a Objetos (POO), 
arquitetura em camadas (DAO/Service) e controlo transacional.

##  Funcionalidades
- **Gestão de Clientes e Contas:** Criação simultânea de cliente e conta com integridade referencial.
- **Tipos de Conta:** Suporte para Conta Corrente e Conta Poupança com regras de negócio específicas.
- **Operações Bancárias:**
  - Depósito com validação de montante positivo.
  - Levantamento com controlo de saldo disponível.
  - Transferência bancária atómica entre contas (com `commit` e `rollback`).
- **Consultas e Relatórios:**
  - Consulta detalhada de conta com dados do titular via `INNER JOIN`.
  - Listagem completa de contas e impressão de extratos.
 
  ---

 ##  Tecnologias e Padrões Utilizados
 
- **Linguagem:** Java (JDK 17+)
- **Base de Dados:** MySQL 8.0+
- - **Acesso a Dados:** JDBC (`PreparedStatement`, `ConnectionFactory`, Gestão de Transações)
- **Padrões de Projeto:**
 - **DAO (Data Access Object):** Isolamento das consultas SQL.
 - **Service Layer:** Centralização da lógica de negócio e gestão de conexões/transações.
 - **POO:** Herança, Polimorfismo, Encapsulamento e Exceções Personalizadas (`SaldoInsuficienteException`, `ValorInvalidoException`).

 - ##  Estrutura do Projeto

  ```
src/
├── banco/
│   ├── database/       # Conexão JDBC (ConnectionFactory)
│   ├── exception/      # Exceções de negócio personalizadas
│   ├── model/          # Modelos de domínio (Cliente, Conta, ContaCorrente, ContaPoupanca)
│   ├── dao/            # Acesso e persistência na BD (ClienteDAO, ContaDAO)
│   ├── service/        # Regras de negócio e transações (BancoService)
│   └── Main.java       # Interface de linha de comandos (CLI)
└── schema.sql          # Script de criação das tabelas MySQL
``
