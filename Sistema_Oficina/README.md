#  Sistema de Gestão de Oficina & Serviços

Sistema em **Java (POO)** com persistência de dados em **MySQL via JDBC**, 
desenvolvido para gerir serviços de uma loja de informática (Manutenção, Reparação, Diagnóstico e Limpeza),
associar componentes/peças e automatizar o cálculo de orçamentos e faturação.

##  Tecnologias Utilizadas

* **Linguagem:** Java (JDK 17+)
* **Conceitos:** Programação Orientada a Objetos (Herança, Polimorfismo, Encapsulamento, Abstração)
* **Base de Dados:** MySQL
* **Persistência:** JDBC (Java Database Connectivity) & Padrão DAO (Data Access Object)

##  Estrutura do Projeto
```
text
├── src/
│   ├── dao/          # CRUD e persistência (ServicoDAO, ComponenteDAO)
│   ├── database/     # Gestão da conexão com MySQL (ConnectionFactory)
│   └── model/        # Classes de negócio (Servico, Manutencao, Reparacao, etc.)
├── schema.sql        # Script de criação da base de dados e tabelas
├── db.properties.example # Modelo de configuração da base de dados
└── README.md
```


##  Como Executar o Projeto Localmente

### 1. Clonar o repositório
### 2. Configurar a Base de Dados
1. Abra o **MySQL Workbench** (ou cliente SQL de preferência).
2. Abra e execute o ficheiro `schema.sql` para criar a base de dados `oficina_db` e as tabelas necessárias.

### 3. Configurar as Credenciais de Acesso
1. Na raiz do projeto, crie uma cópia do ficheiro `db.properties.example` e renomeie-a para `db.properties`.
2. Altere o utilizador e a palavra-passe com as credenciais do seu MySQL local:
properties
db.url=jdbc:mysql://localhost:3306/oficina_db?useSSL=false&serverTimezone=UTC
db.user=root
db.password=sua_palavra_passe_aqui

### 4. Executar
Abra o projeto no **IntelliJ IDEA** (ou outra IDE) e execute a classe `Main.java`.

##  Funcionalidades

- [x] Registo e listagem de serviços polimórficos (Manutenção, Reparação, Diagnóstico, Limpeza).
- [x] Adição dinâmica de componentes com controlo de custos por serviço.
- [x] Conclusão de serviços com cálculo automático de tempo gasto e valor a pagar.
- [x] Atualização de preços (por serviço específico ou por tabela geral).
- [x] Ordenação de serviços concluídos por duração através de Views SQL.
