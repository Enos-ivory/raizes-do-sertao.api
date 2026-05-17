========================================================================
API RAÍZES DO NORDESTE
========================================================================

Este repositório contém o backend completo da API RESTful para a rede de
restaurantes Raízes do Nordeste, desenvolvida como Projeto Multidisciplinar
para a trilha de Back-end (Análise e Desenvolvimento de Sistemas).

A aplicação adota boas práticas de Engenharia de Software, Programação
Defensiva, segurança com autenticação JWT e conformidade nativa com as
diretrizes da LGPD (Lei Geral de Proteção de Dados).

------------------------------------------------------------------------
1. TECNOLOGIAS UTILIZADAS
------------------------------------------------------------------------
- Linguagem: Java (versão 17)
- Framework Principal: Spring Boot 3.x
- Segurança: Spring Security + JWT (JSON Web Tokens) + BCrypt
- Persistência de Dados: Spring Data JPA + Hibernate
- Banco de Dados: MySQL / MariaDB
- Gerenciador de Dependências: Maven
- Testes de API: Postman
- Documentação da API: Swagger UI / OpenAPI 3 (Responsável por mapear,
  testar e expor de forma interativa todos os contratos dos endpoints)

------------------------------------------------------------------------
2. CONFIGURAÇÃO DO AMBIENTE LOCAL (.env)
------------------------------------------------------------------------
Por razões de segurança cibernética e privacidade, as chaves confidenciais
não são escritas diretamente no código-fonte. Você deve criar um arquivo
chamado exatamente ".env" na raiz do projeto (no mesmo nível do arquivo
pom.xml) contendo apenas as credenciais locais do banco de dados:

Conteúdo do arquivo .env:
------------------------------------------------------------------------
DB_USER=seu_usuario_do_mysql
DB_PASS=sua_senha_do_mysql
------------------------------------------------------------------------

------------------------------------------------------------------------
3. CONFIGURAÇÃO DA APLICAÇÃO (application.properties)
------------------------------------------------------------------------
O mapeamento das propriedades do sistema está definido em
src/main/resources/application.properties da seguinte forma:

------------------------------------------------------------------------
spring.application.name=domain

spring.datasource.url=jdbc:mysql://localhost:3306/projeto_back_raizes
spring.datasource.username=${DB_USER}
spring.datasource.password=${DB_PASS}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.hibernate.ddl-auto=update
api.security.token.secret=${JWT_SECRET:12345678}
------------------------------------------------------------------------

[MIGRATIONS] - ESTRATÉGIA DE EVOLUÇÃO DO BANCO DE DADOS:
Para atender aos critérios de portabilidade do projeto, o sistema adota a
estratégia de Migrações Automáticas controladas pelo Hibernate ORM através
da diretriz "ddl-auto=update". Isso elimina a necessidade de scripts SQL
manuais, fazendo com que o mapeamento e a evolução estrutural das tabelas
sejam atualizados em tempo de execução de forma nativa pela API.

------------------------------------------------------------------------
4. ESTRATÉGIA DE SEED (CARGA INICIAL DE DADOS AUTOMÁTICA)
------------------------------------------------------------------------
Para garantir que a API inicie pronta para testes operacionais, o projeto
implementa a interface CommandLineRunner por meio da classe DataInitializer.
A cada inicialização do servidor, o sistema verifica se as tabelas estão
vazias e realiza o Seed automático dos seguintes registros:

- 1 Usuário Administrador Base:
    * E-mail: admin@raizesdonordeste.com
    * Senha: admin123 (criptografada em hash via BCrypt)
- 3 Produtos Iniciais no Cardápio:
    * Baião de Dois
    * Carne de Sol com Macaxeira
    * Suco de Caju Nativo

Isso permite testar o login administrativo e a listagem de produtos de
forma imediata, sem nenhuma dependência de inserções manuais na base.

------------------------------------------------------------------------
5. DOCUMENTAÇÃO DA API (SWAGGER)
------------------------------------------------------------------------
A API conta com documentação viva e interativa via Swagger. Com o servidor
rodando localmente, todas as rotas da aplicação, payloads de