# 💰 Controle Financeiro Pessoal

Aplicativo de controle financeiro pessoal desenvolvido em **Java 21** com **Spring Boot 3**.

## Funcionalidades

- **Autenticação** — Cadastro e login com JWT (JSON Web Tokens)
- **Contas** — Gerencie contas correntes, poupanças, carteiras e cartões de crédito
- **Transações** — Registre receitas, despesas e transferências entre contas
- **Categorias** — Categorias padrão do sistema + criação de categorias personalizadas
- **Orçamentos** — Defina limites de gastos mensais por categoria com alertas de progresso
- **Relatórios** — Resumo financeiro mensal (receitas vs despesas, taxa de poupança)

## Tecnologias

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot 3.3 | Framework web |
| Spring Data JPA | Acesso a dados |
| Spring Security | Autenticação e autorização |
| JWT (jjwt) | Tokens de autenticação |
| H2 Database | Banco em memória (desenvolvimento) |
| PostgreSQL | Banco de dados (produção) |
| Swagger/OpenAPI | Documentação da API |
| Lombok | Redução de boilerplate |
| Maven | Gerenciamento de dependências |

## Pré-requisitos

- **Java 21** (JDK)
- **Maven 3.9+**

## Como Executar

```bash
# Clonar o repositório
git clone <url-do-repositorio>
cd personal-finance

# Executar em modo desenvolvimento (H2 em memória)
./mvnw spring-boot:run

# Ou com Maven instalado:
mvn spring-boot:run
```

A aplicação estará disponível em: `http://localhost:8080`

## Documentação da API (Swagger)

Após iniciar a aplicação, acesse:
- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8080/api-docs

## Console do H2 (Desenvolvimento)

Acesse: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:financas_db`
- **Usuário**: `sa`
- **Senha**: *(vazio)*

## Endpoints da API

### Autenticação (Público)
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/auth/register` | Cadastrar novo usuário |
| POST | `/api/auth/login` | Fazer login |
| GET | `/api/auth/profile` | Obter perfil (requer token) |

### Contas (Requer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/accounts` | Criar conta |
| GET | `/api/accounts` | Listar contas |
| GET | `/api/accounts/summary` | Resumo de saldos |
| DELETE | `/api/accounts/{id}` | Excluir conta |

### Transações (Requer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/transactions` | Registrar transação |
| GET | `/api/transactions?startDate=&endDate=` | Listar por período |
| GET | `/api/transactions/account/{id}` | Listar por conta |
| DELETE | `/api/transactions/{id}` | Excluir (reverte saldo) |

### Categorias (Requer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/categories` | Listar categorias |
| POST | `/api/categories` | Criar categoria |
| DELETE | `/api/categories/{id}` | Excluir categoria |

### Orçamentos (Requer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| POST | `/api/budgets` | Criar orçamento |
| GET | `/api/budgets?month=&year=` | Listar por mês/ano |
| PATCH | `/api/budgets/{id}` | Atualizar limite |
| DELETE | `/api/budgets/{id}` | Excluir orçamento |

### Relatórios (Requer Token)
| Método | Endpoint | Descrição |
|---|---|---|
| GET | `/api/analytics/monthly-summary?month=&year=` | Resumo mensal |

## Estrutura do Projeto

```
src/main/java/com/financas/personal/
├── PersonalFinanceApplication.java    # Classe principal
├── config/                            # Configurações (Security, JWT, Swagger, DataInit)
├── exception/                         # Tratamento global de erros
└── domain/
    ├── user/                          # Usuários e Autenticação
    ├── account/                       # Contas Financeiras
    ├── category/                      # Categorias de Transação
    ├── transaction/                   # Transações e Relatórios
    └── budget/                        # Orçamentos Mensais
```

## Licença

Este projeto foi desenvolvido para fins de estudo e uso pessoal.
