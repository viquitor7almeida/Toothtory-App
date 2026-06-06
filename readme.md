# Toothory - Sistema de Gestão Odontológica

Sistema desktop para gestão de consultórios odontológicos desenvolvido em Java 17 com JavaFX.

---

## Tecnologias

* Java 17
* JavaFX 21
* SQLite JDBC
* Maven
* FXML

---

## Funcionalidades

### Dashboard

* Total de pacientes cadastrados
* Total de procedimentos cadastrados
* Total de consultas realizadas
* Faturamento do mês

### Pacientes

* Cadastro de pacientes
* Edição de pacientes
* Exclusão de pacientes
* Busca por nome

### Procedimentos

* Cadastro de procedimentos
* Edição de procedimentos
* Exclusão de procedimentos
* Busca por nome

### Consultas

* Registro de consultas
* Seleção de procedimento previamente cadastrado
* Registro manual de procedimento
* Histórico de atendimentos

### Persistência

* Banco de dados local SQLite
* Criação automática do banco na primeira execução

---

## Estrutura do Projeto

```text
src/main/java/com/toothtory/

├── MainApp.java
├── components/
│   ├── Sidebar
│   └── TopBar
├── controllers/
├── services/
├── domain/
│   ├── entities/
│   │   ├── Paciente
│   │   ├── Procedimento
│   │   └── Consulta
│   └── repositories/
├── infra/
│   ├── database/
│   └── repositories/
└── resources/
    └── views/
```

### Descrição dos Módulos

| Diretório             | Responsabilidade                                   |
| --------------------- | -------------------------------------------------- |
| `MainApp`             | Inicialização da aplicação e navegação entre telas |
| `components`          | Componentes reutilizáveis da interface             |
| `controllers`         | Controladores JavaFX responsáveis pelas telas      |
| `services`            | Regras de negócio da aplicação                     |
| `domain/entities`     | Entidades de domínio                               |
| `domain/repositories` | Contratos de persistência                          |
| `infra/database`      | Configuração e conexão com SQLite                  |
| `infra/repositories`  | Implementações dos repositórios                    |
| `resources/views`     | Arquivos FXML                                      |

---

## Como Executar

### Pré-requisitos

* Java 17 ou superior
* Maven 3.9+

### Compilar o Projeto

```bash
mvn clean compile
```

### Executar a Aplicação

```bash
mvn exec:java
```

---

## Banco de Dados

O banco SQLite é criado automaticamente durante a primeira execução.

Arquivo gerado:

```text
toothtory.db
```

Nenhuma configuração adicional é necessária.

---

## Navegação

A aplicação utiliza uma estrutura baseada em navegação dinâmica.

A Sidebar contém os seguintes módulos:

* Dashboard
* Pacientes
* Procedimentos
* Consultas

Ao selecionar uma opção, a tela correspondente é carregada na área central da aplicação sem reinicializar a janela principal.

---

## Regras de Negócio

### Paciente

* Nome é obrigatório
* Endereço é opcional
* E-mail é opcional
* Telefone é opcional

### Procedimento

* Nome é obrigatório
* Valor deve ser maior que zero

### Consulta

* Paciente é obrigatório
* Data e hora são obrigatórias
* Procedimento pode ser selecionado do catálogo ou informado manualmente

### Snapshot de Procedimento

Ao registrar uma consulta:

* O nome do procedimento é copiado para a consulta
* O valor do procedimento é copiado para a consulta

Dessa forma, alterações futuras no cadastro de procedimentos não impactam o histórico financeiro e clínico já registrado.

---

## Arquitetura

O projeto segue uma arquitetura em camadas baseada nos princípios do SOLID.

```text
Controller
    ↓
Service
    ↓
Repository Interface
    ↓
Repository Implementation
    ↓
SQLite
```

### Princípios Utilizados

* Separação de responsabilidades
* Inversão de dependência
* Injeção de dependência por construtor
* Baixo acoplamento
* Alta coesão

Fluxo da aplicação:

1. O Controller recebe as ações da interface.
2. O Service executa as regras de negócio.
3. O Repository realiza a persistência dos dados.
4. O SQLite armazena as informações localmente.
