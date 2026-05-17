# 
# 🍽️ API Raízes do Nordeste

Este repositório contém o backend completo da API RESTful para a rede de restaurantes **Raízes do Nordeste**, desenvolvida como Projeto Multidisciplinar para a trilha de Back-end (Análise e Desenvolvimento de Sistemas).

A aplicação foi projetada utilizando **Java** com o framework **Spring Boot**, adotando boas práticas de Engenharia de Software, Programação Defensiva, segurança com autenticação **JWT** e conformidade nativa com a **LGPD** (Lei Geral de Proteção de Dados).

---

## 🚀 Tecnologias Utilizadas

- **Linguagem:** Java (versão 17 ou superior)
- **Framework Principal:** Spring Boot 3.x
- **Segurança:** Spring Security + JWT (JSON Web Tokens) + BCrypt (Hash de senhas)
- **Persistência de Dados:** Spring Data JPA + Hibernate
- **Banco de Dados:** MySQL 
- **Gerenciador de Dependências:** Maven
- **Testes de API:** Postman

---

## 🛠️ 1. Configuração do Ambiente e Banco de Dados

Por razões de segurança cibernética e em conformidade com a LGPD, a API não armazena credenciais confidenciais em texto claro no código-fonte.

Para rodar o projeto localmente, você precisará criar um arquivo chamado **`.env`** na raiz do projeto (mesmo nível do arquivo `pom.xml`) e definir as variáveis de ambiente necessárias.

### 1.1 Conteúdo do arquivo `.env`:
Crie o arquivo `.env` e preencha com as suas credenciais locais:

```env
# Usuário de acesso ao seu SGBD (geralmente root)
DB_USER=seu_usuario

# Senha de acesso ao seu SGBD
DB_PASS=sua_senha

# Chave secreta de alta entropia para a assinatura dos tokens JWT
JWT_SECRET=sua_chave_secreta_super_segura_para_assinatura_do_token