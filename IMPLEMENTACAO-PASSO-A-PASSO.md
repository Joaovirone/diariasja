# Documentação de Implementação

Este documento explica a estrutura do projeto, o papel de cada pasta e exemplos de classes para que a implementação coletiva siga um padrão único.

## Visão Geral do Projeto

O projeto é uma API Spring Boot com suporte a banco de dados MySQL, Redis e documentação OpenAPI/Swagger.
A base já implementada usa as convenções de Spring Boot e separação de responsabilidades em camadas.

---

## Estrutura do Projeto

### Raiz do projeto

- `pom.xml` - gerenciamento de dependências do Maven e plugins do Spring Boot.
- `docker-compose.yaml` - composição local de serviços Docker para MySQL e Redis.
- `src/main/java` - código-fonte Java da aplicação.
- `src/main/resources` - configurações, arquivos de recursos e migrações Flyway.

---

## `src/main/java/com/diariasja/aws`

Este pacote principal contém as camadas da aplicação.

### `controller`

Responsabilidade: expor os endpoints HTTP e receber/retonar dados JSON.

Exemplo: `CategoriaServicoController.java`

- Define rotas com `@RestController` e `@RequestMapping`.
- Recebe requisições e transfere a lógica para o serviço.
- Retorna `ResponseEntity` com status HTTP apropriado.

O padrão por endpoint:
- `@PostMapping` para criação.
- `@GetMapping` para leitura.
- `@PutMapping` para atualização.
- `@DeleteMapping` para exclusão.

### `service`

Responsabilidade: lógica de negócio e regras da aplicação.

Exemplo: `CategoriaServicoService.java`

- Anotações: `@Service`.
- Contém lógica de criação, leitura e validação.
- Chama o `repository` para persistência.
- Lança exceções customizadas quando necessário.

Padrões esperados:
- Validar entrada antes de salvar.
- Não acessar diretamente o banco de dados aqui.
- Tratar regras de domínio e transações.

### `repository`

Responsabilidade: acesso a dados e consultas ao banco.

Exemplo: `CategoriaServicoRepository.java`

- Extende `JpaRepository<Entidade, TipoID>`.
- Define métodos de consulta derivados, por exemplo `findByNome(String nome)`.
- Não contém lógica de negócio.

Este pacote deve ser usado apenas para comunicação com o banco.

### `entity`

Responsabilidade: representar tabelas do banco como objetos Java.

Exemplo: `CategoriaServico.java`

- Anotada com `@Entity` e `@Table`.
- Campos com `@Id`, `@GeneratedValue` e `@Column`.
- Deve refletir a estrutura da tabela SQL.
- Pode usar `lombok` para getters/setters com `@Data`.

### `config`

Responsabilidade: configurações da aplicação, beans e documentação.

Exemplo: `SwaggerConfig.java`

- Define a documentação OpenAPI.
- Centraliza beans de configuração que não são regras de negócio.
- Pode incluir configuração de CORS, formatadores, cache, etc.

### `security`

Responsabilidade: regras de segurança e autenticação/autorização.

Exemplo: `SecurityConfig.java`

- Define as rotas liberadas e as rotas que exigem autenticação.
- Desabilita CSRF quando necessário para APIs REST.
- Configura CORS e políticas de autorização.

É aqui que deve ficar todo o controle de acesso da API.

### `exception`

Responsabilidade: exceções customizadas e tratamento global.

Exemplo: `ResourceNotFoundException.java` e `GlobalExceptionHandler.java`

- `ResourceNotFoundException` representa erros de recurso não encontrado.
- `GlobalExceptionHandler` mapeia exceções para respostas HTTP específicas.
- Deve manter o tratamento de erro centralizado.

---

## `src/main/resources`

### `application.yaml`

Responsabilidade: configurações da aplicação.

Valores já definidos no projeto:

- `spring.application.name` - nome da aplicação.
- `spring.datasource.url` - conexão MySQL.
- `spring.datasource.username` / `password`.
- `spring.jpa.hibernate.ddl-auto` - modo de criação/atualização do schema.
- `spring.data.redis.host` e `spring.data.redis.port`.

### Variáveis de ambiente usadas

- `DB_HOST` - host do banco.
- `DB_PORT` - porta do banco (padrão 3306).
- `DB_NAME` - nome do schema.
- `DB_USER` - usuário do banco.
- `DB_PASSWORD` - senha do banco.
- `REDIS_HOST` - host do Redis.
- `REDIS_PORT` - porta do Redis (padrão 6379).

### Migrações Flyway

- `src/main/resources/db/migration` - scripts SQL de versionamento do banco.
- Cada arquivo deve ter nome no padrão `V{versao}__descrição.sql`.

---

## `docker-compose.yaml`

Responsabilidade: levantar serviços de desenvolvimento local.

Serviços definidos:

- `mysql`
  - Imagem: `mysql:latest`
  - Porta local: `3300` mapeada para `3306` do container.
  - Variáveis: `MYSQL_DATABASE`, `MYSQL_USER`, `MYSQL_PASSWORD`, `MYSQL_ROOT_PASSWORD`.

- `redis`
  - Imagem: `redis:latest`
  - Porta local: `6379` mapeada para `6379`.

Use o container `mysql` para testes locais. Quando o Spring Boot rodar dentro do Docker Compose, o host do banco pode ser `mysql`.

---

## Portas e endpoints principais

### Portas do sistema

- Aplicação Spring Boot: `8080` (padrão do Spring Boot quando não configurado explicitamente).
- MySQL no container: `3306`.
- MySQL exposto no host: `3300`.
- Redis: `6379`.

### Endpoints de exemplo

- `POST http://localhost:8080/api/categorias` - cria uma nova categoria.
- `GET http://localhost:8080/api/categorias` - lista todas as categorias.
- `http://localhost:8080/swagger-ui/index.html` - interface Swagger.
- `http://localhost:8080/v3/api-docs` - especificação OpenAPI.

---

## Guia de implementação coletiva

### 1. Criar uma nova entidade

1. Abra `src/main/java/com/diariasja/aws/entity`.
2. Crie a classe com `@Entity` e `@Table`.
3. Declare o `@Id` e `@GeneratedValue`.
4. Adicione colunas com `@Column` e atributos de validação.

### 2. Criar um repositório

1. Crie interface em `repository`.
2. Estenda `JpaRepository<MinhaEntidade, Long>`.
3. Adicione consultas customizadas com nomes claros, por exemplo `findByNome(String nome)`.

### 3. Criar o serviço

1. Crie classe em `service` com `@Service`.
2. Injete o repositório com `@Autowired`.
3. Implemente os métodos de negócio.
4. Trate erros e lance exceções customizadas.

### 4. Criar o controller

1. Crie classe em `controller` com `@RestController`.
2. Defina o caminho base com `@RequestMapping("/api/minha-rota")`.
3. Implemente métodos `@GetMapping`, `@PostMapping`, etc.
4. Retorne `ResponseEntity` com status adequado.

### 5. Atualizar configuração se necessário

- Se precisar adicionar novos beans, use `config`.
- Se precisar liberar nova rota ou proteger endpoint, atualize `security/SecurityConfig.java`.
- Se houver regra de negócio compartilhada, mantenha no `service`, não no `controller`.

---

## Convenções de pastas e responsabilidades

- `controller` -> interface HTTP.
- `service` -> regras de negócio.
- `repository` -> acesso a dados.
- `entity` -> modelo e persistência.
- `config` -> configuração geral e integração.
- `security` -> proteção de rotas e política de acesso.
- `exception` -> tratamento de erro centralizado.
- `resources` -> propriedades, templates e migrações.

---

## Boas práticas para o time

- Mantenha cada classe com responsabilidade única.
- Use nomes de rota descritivos e consistentes.
- Documente novas APIs com `SwaggerConfig` quando necessário.
- Use variáveis de ambiente no `application.yaml` para não deixar credenciais fixas.
- Verifique se o modelo de dados e as migrações Flyway estão sincronizados.

---

## Como executar localmente

1. Rodar o Docker Compose:
   ```bash
   docker compose up -d
   ```
2. Ajustar variáveis de ambiente se necessário:
   - `DB_HOST=localhost`
   - `DB_PORT=3300`
   - `DB_NAME=mydatabase`
   - `DB_USER=myuser`
   - `DB_PASSWORD=secret`
   - `REDIS_HOST=localhost`
   - `REDIS_PORT=6379`
3. Iniciar a aplicação:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Verificar se está disponível em `http://localhost:8080`.

---

## Observações finais

- O `target/` não deve ser versionado: é saída de build.
- Use o `docker-compose.yaml` apenas para ambiente de desenvolvimento.
- Para produção, confirme os valores de `DB_HOST`, `DB_PORT` e `REDIS_HOST` em variáveis de ambiente.
- As regras de segurança devem ser revisadas antes de liberar novas rotas públicas.
