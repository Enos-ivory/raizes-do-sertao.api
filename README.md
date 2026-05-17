
API RAÍZES DO NORDESTE


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
pom.xml) contendo apenas as credenciais do banco de dados:

Conteúdo do arquivo .env:
------------------------------------------------------------------------
DB_USER=seu_usuario_do_mysql
DB_PASS=sua_senha_do_mysql
------------------------------------------------------------------------

* Nota: Certifique-se de criar o schema no seu MySQL antes de iniciar:
  CREATE DATABASE projeto_back_raizes;

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

------------------------------------------------------------------------
4. DOCUMENTAÇÃO DA API (SWAGGER)
------------------------------------------------------------------------
A API conta com documentação viva e interativa via Swagger. Com o servidor
rodando localmente, todas as rotas da aplicação, payloads de requisição
e códigos de resposta HTTP podem ser visualizados e testados diretamente
pelo navegador.

Link de acesso ao Swagger UI:
-> http://localhost:8080/swagger-ui/index.html

------------------------------------------------------------------------
5. EXECUÇÃO DAS SUÍTES DE TESTES (POSTMAN)
------------------------------------------------------------------------
Dentro do diretório /postman deste repositório, encontra-se o arquivo
JSON contendo todas as requisições prontas para testar a API.

Como o Postman não exporta valores locais de variáveis por segurança,
siga os passos abaixo ao importar a coleção para evitar URLs travadas:

1. Importe o arquivo JSON da coleção no seu Postman.
2. Clique em cima do nome da coleção mãe ("Raízes do Nordeste").
3. Na janela central, acesse a sub-aba "Variables".
4. Localize a variável "base_url".
5. Na coluna "Initial Value" (Valor Inicial), preencha com:
   http://localhost:8080
6. Pressione Ctrl + S para salvar.

------------------------------------------------------------------------
6. COMO INICIALIZAR O PROJETO
------------------------------------------------------------------------
1. Abra o terminal na pasta raiz do projeto.
2. Garanta que o banco de dados foi criado e o arquivo .env configurado.
3. Execute o comando Maven:
   mvn spring-boot:run
4. A API estará pronta para responder na porta 8080.
   ========================================================================
