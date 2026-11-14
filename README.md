# 📺 Coverage Test Java - Operadora de TV

Projeto desenvolvido em Java com foco em **testes de unidade** e análise de **cobertura de código (code coverage)**, simulando as regras de negócio de gerenciamento de assinaturas de uma operadora de TV a cabo.

---

## 📌 Visão Geral

O sistema gerencia planos de canais, assinantes, concessão cumulativa de descontos e processos de cancelamento. A suíte de testes foi estruturada com **JUnit 5**, abordando desde fluxos padrão (caminho feliz) até exceções, limites de regras de negócio e métodos não-determinísticos (probabilísticos).

---

## 🚀 Funcionalidades & Regras de Negócio

### 1. Planos de Canais & Preços
| Plano | Preço Mensal Padrão |
| :--- | :--- |
| `PLANO_CARO` | R$ 120,00 |
| `PLANO_SUPER_CARO` | R$ 220,00 |
| `PLANO_HIPER_CARO` | R$ 320,00 |
| `NOVO_PLANO_AINDA_SEM_PRECO` | Lança `IllegalArgumentException` |

### 2. Gestão de Contratos e Assinantes
- Cadastro de assinantes vinculado a um CPF único.
- Validação obrigatória de dados: nome e CPF não podem ser nulos ou vazios.

### 3. Concessão de Descontos & Cálculo de Mensalidade
- Tipos de desconto permitidos:
  - `DESCONTINHO`: 3% de desconto.
  - `DESCONTAO`: 30% de desconto.
- **Teto de desconto:** O sistema possui um limite de descontos acumulados por assinante (`MAXIMO_DESCONTO_PERMITIDO_POR_ASSINANTE = 35%`).
- Cálculo proporcional do valor final da mensalidade com base nos descontos concedidos.

### 4. Cancelamento de Assinatura
- Simulação de latência de atendimento (`Thread.sleep`).
- Comportamento probabilístico (50% de chance de efetivar o cancelamento ou lançar adiamento `RuntimeException: "Só mais um momento senhor(a) ..."`).

---

## 🧪 Estratégia de Testes e Cobertura

Os testes unitários em [`src/test/java/OperadoraDeTVTest.java`](src/test/java/OperadoraDeTVTest.java) cobrem:

- ✅ **Ponto de entrada:** Execução do método `main`.
- ✅ **Consulta de preços:** Cobertura de todos os planos disponíveis e tratamento de planos inválidos/sem preço.
- ✅ **Criação de contratos e validações:** Rejeição de assinantes com campos obrigatórios nulos ou em branco.
- ✅ **Cálculos de mensalidade:** Aplicação de descontos individuais e combinados.
- ✅ **Tratamento de exceções:**
  - Consulta e operações sobre CPF inexistente.
  - Tipos de desconto inválidos.
  - Tentativa de concessão de descontos além do teto permitido.
- ✅ **Fluxos probabilísticos:** Estratégia resiliente em laço de repetição para forçar e validar os dois caminhos possíveis (sucesso e retenção/adiamento) no processo de cancelamento.

---

## 🛠️ Tecnologias Utilizadas

- **Linguagem:** Java 21
- **Gerenciador de Dependências & Build:** Apache Maven
- **Framework de Testes:** JUnit Jupiter 5.11.0
- **Plugin de Execução de Testes:** Maven Surefire Plugin 3.5.2

---

## 📁 Estrutura do Projeto

```text
coverage-test-java/
├── pom.xml                               # Configurações do Maven e dependências
├── README.md                             # Documentação do repositório
└── src/
    ├── main/
    │   └── java/
    │       └── OperadoraDeTV.java        # Classe principal com regras de negócio
    └── test/
        └── java/
            └── OperadoraDeTVTest.java    # Suíte de testes unitários (JUnit 5)
```

---

## ⚙️ Como Executar

### Pré-requisitos
- [JDK 21](https://adoptium.net/) instalado e configurado no `PATH`.
- [Apache Maven 3.8+](https://maven.apache.org/) instalado.

### Clonar o repositório
```bash
git clone git@github.com:fsoaz/coverage-test-java.git
cd coverage-test-java
```

### Compilar o projeto
```bash
mvn clean compile
```

### Executar os testes unitários
```bash
mvn test
```

---

## 💡 Sugestões de Evolução

- **Métricas de Cobertura com JaCoCo:** Adicionar o `jacoco-maven-plugin` ao `pom.xml` para gerar relatórios detalhados em HTML com cobertura de linhas e branches.
- **Injeção de Dependências:** Isolar o gerador de números aleatórios (`Random`) e a pausa (`Thread.sleep`) no fluxo de cancelamento para permitir *mocks* e testes 100% determinísticos e rápidos.
