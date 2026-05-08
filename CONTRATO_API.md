# Contrato da API - DiariasJa

Base path local sugerido: `http://localhost:8080`

Formato padrão: `application/json`

Autenticação privada: `Authorization: Bearer <token>`

## Regras Gerais

- Datas usam o formato ISO `YYYY-MM-DD`.
- Rotas paginadas seguem o padrão do Spring Data:
  - `page`: número da página, iniciando em `0`.
  - `size`: quantidade de itens por página.
  - `sort`: campo e direção, exemplo `nome,asc`.
- Respostas paginadas retornam um objeto `Page`, com campos como `content`, `pageable`, `totalElements`, `totalPages`, `size`, `number`, `first`, `last` e `empty`.

## Autenticação

### POST `/api/auth/login`

Rota pública. Autentica um usuário e retorna um token JWT em texto puro.

Request:

```json
{
  "email": "cliente@email.com",
  "senha": "senha12345"
}
```

Validações:

- `email`: obrigatório.
- `senha`: obrigatória.

Response `200 OK`:

```text
eyJhbGciOiJIUzI1NiJ9...
```

Erros:

- `401 Unauthorized`: credenciais inválidas.
- `404 Not Found`: credenciais inválidas quando o e-mail não existe.
- `400 Bad Request`: campos inválidos.

## Usuários

### POST `/api/usuarios/cadastrar`

Rota pública. Cadastra um usuário contratante ou profissional.

Request:

```json
{
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "senha": "senha12345",
  "dataNascimento": "1995-04-20",
  "tipo": "CONTRATANTE"
}
```

Campos:

| Campo | Tipo | Obrigatório | Regras |
| --- | --- | --- | --- |
| `nome` | string | sim | não pode ser vazio |
| `email` | string | sim | formato de e-mail |
| `senha` | string | sim | mínimo 8 caracteres |
| `dataNascimento` | date | sim | `YYYY-MM-DD` |
| `tipo` | enum | sim | `CONTRATANTE` ou `CONTRATADO` |

Response `201 Created`:

```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "dataNascimento": "1995-04-20",
  "tipo": "CONTRATANTE",
  "ativo": true
}
```

Erros:

- `400 Bad Request`: validação inválida ou e-mail já cadastrado.
- `409 Conflict`: violação de integridade de dados.

### GET `/api/usuarios/profissionais`

Rota pública. Lista usuários ativos do tipo `CONTRATADO`.

Query params:

| Param | Tipo | Default |
| --- | --- | --- |
| `page` | number | `0` |
| `size` | number | `12` |
| `sort` | string | `nome` |

Response `200 OK`:

```json
{
  "content": [
    {
      "id": 2,
      "nome": "Joao Prestador",
      "email": "joao@email.com",
      "dataNascimento": "1990-01-15",
      "tipo": "CONTRATADO",
      "ativo": true
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 12,
  "number": 0
}
```

### GET `/api/usuarios/me`

Rota privada. Retorna o perfil do usuário autenticado pelo token.

Headers:

```http
Authorization: Bearer <token>
```

Response `200 OK`:

```json
{
  "id": 1,
  "nome": "Maria Silva",
  "email": "maria@email.com",
  "dataNascimento": "1995-04-20",
  "tipo": "CONTRATANTE",
  "ativo": true
}
```

Erros:

- `401 Unauthorized`: token ausente, inválido ou expirado.
- `404 Not Found`: usuário do token não encontrado.

## Categorias

### POST `/api/categorias`

Rota privada. Cria uma categoria de serviço.

Headers:

```http
Authorization: Bearer <token>
```

Request:

```json
{
  "nome": "Faxina",
  "descricao": "Limpeza residencial"
}
```

Campos:

| Campo | Tipo | Obrigatório | Regras |
| --- | --- | --- | --- |
| `nome` | string | sim | não pode ser vazio |
| `descricao` | string | não | texto livre |

Response `201 Created`:

```json
{
  "id": 1,
  "nome": "Faxina",
  "descricao": "Limpeza residencial"
}
```

### GET `/api/categorias/{id}`

Rota pública. Busca uma categoria pelo ID.

Response `200 OK`:

```json
{
  "id": 1,
  "nome": "Faxina",
  "descricao": "Limpeza residencial"
}
```

Erros:

- `404 Not Found`: categoria não encontrada.

### GET `/api/categorias`

Rota pública. Lista categorias com paginação e filtro opcional por nome.

Query params:

| Param | Tipo | Default |
| --- | --- | --- |
| `nome` | string | sem filtro |
| `page` | number | `0` |
| `size` | number | `10` |
| `sort` | string | `nome` |

Response `200 OK`:

```json
{
  "content": [
    {
      "id": 1,
      "nome": "Faxina",
      "descricao": "Limpeza residencial"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

## Diárias

Todas as rotas de diárias são privadas.

### POST `/api/diarias/solicitar`

Solicita uma diária para um profissional.

Headers:

```http
Authorization: Bearer <token>
```

Request:

```json
{
  "contratanteId": 1,
  "contratadoId": 2,
  "categoriaId": 1,
  "dataServico": "2026-05-10"
}
```

Campos:

| Campo | Tipo | Obrigatório | Regras |
| --- | --- | --- | --- |
| `contratanteId` | number | sim | usuário deve ser `CONTRATANTE` |
| `contratadoId` | number | sim | usuário deve ser `CONTRATADO` |
| `categoriaId` | number | sim | categoria existente |
| `dataServico` | date | sim | data atual ou futura |

Regras de negócio:

- O contratante não pode contratar a si mesmo.
- A diária nasce com status `PENDENTE`.

Response `201 Created`:

```json
{
  "id": 10,
  "nomeContratante": "Maria Silva",
  "nomeContratado": "Joao Prestador",
  "nomeCategoria": "Faxina",
  "dataServico": "2026-05-10",
  "status": "PENDENTE"
}
```

Erros:

- `400 Bad Request`: validação ou regra de negócio inválida.
- `401 Unauthorized`: token ausente ou inválido.
- `404 Not Found`: contratante, contratado ou categoria não encontrados.

### PATCH `/api/diarias/{id}/aceitar`

Profissional aceita uma diária pendente.

Headers:

```http
Authorization: Bearer <token>
```

Query params:

| Param | Tipo | Obrigatório |
| --- | --- | --- |
| `idProfissional` | number | sim |

Exemplo:

```http
PATCH /api/diarias/10/aceitar?idProfissional=2
```

Regras de negócio:

- O usuário autenticado deve ter o mesmo ID enviado em `idProfissional`.
- Apenas o profissional escalado pode aceitar.
- Apenas diárias `PENDENTE` podem ser aceitas.
- Ao aceitar, o status muda para `CONFIRMADA`.

Response `200 OK`:

```json
{
  "id": 10,
  "nomeContratante": "Maria Silva",
  "nomeContratado": "Joao Prestador",
  "nomeCategoria": "Faxina",
  "dataServico": "2026-05-10",
  "status": "CONFIRMADA"
}
```

Erros:

- `400 Bad Request`: diária não está pendente ou profissional inválido.
- `403 Forbidden`: token pertence a outro usuário.
- `404 Not Found`: diária ou usuário autenticado não encontrado.

### PATCH `/api/diarias/{id}/avaliar`

Avalia uma diária concluída.

Headers:

```http
Authorization: Bearer <token>
```

Query params:

| Param | Tipo | Obrigatório | Regras |
| --- | --- | --- | --- |
| `nota` | number | sim | inteiro entre `1` e `5` |

Exemplo:

```http
PATCH /api/diarias/10/avaliar?nota=5
```

Regras de negócio:

- Apenas diárias `CONCLUIDA` podem ser avaliadas.
- A nota deve estar entre `1` e `5`.

Response `200 OK`:

```json
{
  "id": 10,
  "nomeContratante": "Maria Silva",
  "nomeContratado": "Joao Prestador",
  "nomeCategoria": "Faxina",
  "dataServico": "2026-05-10",
  "status": "CONCLUIDA"
}
```

Erros:

- `400 Bad Request`: diária não concluída ou nota inválida.
- `404 Not Found`: diária não encontrada.

Observação: o DTO de resposta atual não retorna a nota salva.

### PATCH `/api/diarias/{id}/cancelar`

Contratante cancela uma diária.

Headers:

```http
Authorization: Bearer <token>
```

Query params:

| Param | Tipo | Obrigatório |
| --- | --- | --- |
| `idUsuario` | number | sim |

Exemplo:

```http
PATCH /api/diarias/10/cancelar?idUsuario=1
```

Regras de negócio:

- O usuário autenticado deve ter o mesmo ID enviado em `idUsuario`.
- Apenas o contratante da diária pode cancelar.
- Diárias `CONCLUIDA` ou `CANCELADA` não podem mais ser canceladas.
- Ao cancelar, o status muda para `CANCELADA`.

Response `200 OK`:

```json
{
  "id": 10,
  "nomeContratante": "Maria Silva",
  "nomeContratado": "Joao Prestador",
  "nomeCategoria": "Faxina",
  "dataServico": "2026-05-10",
  "status": "CANCELADA"
}
```

Erros:

- `400 Bad Request`: usuário sem permissão pela regra de negócio ou diária não cancelável.
- `403 Forbidden`: token pertence a outro usuário.
- `404 Not Found`: diária ou usuário autenticado não encontrado.

### GET `/api/diarias/contratante/{contratanteId}`

Lista diárias de um contratante.

Headers:

```http
Authorization: Bearer <token>
```

Query params:

| Param | Tipo | Default |
| --- | --- | --- |
| `page` | number | `0` |
| `size` | number | `10` |
| `sort` | string | `dataServico` |

Response `200 OK`:

```json
{
  "content": [
    {
      "id": 10,
      "nomeContratante": "Maria Silva",
      "nomeContratado": "Joao Prestador",
      "nomeCategoria": "Faxina",
      "dataServico": "2026-05-10",
      "status": "PENDENTE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

### GET `/api/diarias/profissional/{contratadoId}/pendentes`

Lista diárias pendentes de um profissional.

Headers:

```http
Authorization: Bearer <token>
```

Query params:

| Param | Tipo | Default |
| --- | --- | --- |
| `page` | number | `0` |
| `size` | number | `10` |
| `sort` | string | `dataServico` |

Response `200 OK`:

```json
{
  "content": [
    {
      "id": 10,
      "nomeContratante": "Maria Silva",
      "nomeContratado": "Joao Prestador",
      "nomeCategoria": "Faxina",
      "dataServico": "2026-05-10",
      "status": "PENDENTE"
    }
  ],
  "totalElements": 1,
  "totalPages": 1,
  "size": 10,
  "number": 0
}
```

## Enums

### `TipoUsuario`

```text
CONTRATANTE
CONTRATADO
```

### `StatusDiaria`

```text
PENDENTE
CONFIRMADA
CONCLUIDA
CANCELADA
```

## Erros Padronizados

### `400 Bad Request` - validação

```json
{
  "timestamp": "2026-05-04T10:30:00",
  "status": 400,
  "erro": "Erro de validação de campos",
  "campos": {
    "email": "Email inválido",
    "senha": "A senha deve conter no mínimo 8 caracteres"
  }
}
```

### `400 Bad Request` - regra de negócio

```json
{
  "timestamp": "2026-05-04T10:30:00",
  "status": 400,
  "erro": "Argumento inválido",
  "mensagem": "Não pode contratar a si mesmo"
}
```

### `404 Not Found`

```json
{
  "timestamp": "2026-05-04T10:30:00",
  "status": 404,
  "erro": "Recurso não encontrado",
  "mensagem": "Diária não encontrada"
}
```

### `409 Conflict`

```json
{
  "timestamp": "2026-05-04T10:30:00",
  "status": 409,
  "erro": "Violação de integridade de dados",
  "mensagem": "Email/Recurso já existe"
}
```

### `500 Internal Server Error`

```json
{
  "timestamp": "2026-05-04T10:30:00",
  "status": 500,
  "erro": "Erro interno no servidor",
  "mensagem": "Ocorreu um erro inesperado. Tente novamente mais tarde."
}
```

## Swagger

Quando a aplicação estiver rodando, a documentação automática fica disponível em:

- Swagger UI: `/swagger-ui.html`
- OpenAPI JSON: `/v3/api-docs`

## Observações Técnicas

- O CORS atual libera origem `http://localhost:3000`.
- O backend usa autenticação stateless com JWT.
- A rota `POST /api/categorias` exige autenticação pela configuração de segurança, embora as rotas `GET /api/categorias/**` sejam públicas.
- Os endpoints de listagem por `contratanteId` e `contratadoId` exigem token, mas o código atual não valida se o ID da URL pertence ao usuário autenticado.
- O contrato reflete o código atual do backend.
