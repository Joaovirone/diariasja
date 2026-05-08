export const MOCK_TOKEN_PREFIX = 'mock-token'

export const MOCK_USERS = [
  {
    id: 1,
    nome: 'Contratante',
    email: 'contratante@gmail.com',
    senha: '123456',
    dataNascimento: '1995-01-01',
    tipo: 'CONTRATANTE',
    ativo: true
  },
  {
    id: 2,
    nome: 'Autonomo',
    email: 'autonomo@gmail.com',
    senha: '123456',
    dataNascimento: '1990-01-01',
    tipo: 'CONTRATADO',
    ativo: true
  }
]

export const MOCK_CATEGORIAS = [
  { id: 1, nome: 'Faxina', descricao: 'Limpeza residencial' },
  { id: 2, nome: 'Jardinagem', descricao: 'Manutenção de jardins' },
  { id: 3, nome: 'Manutenção', descricao: 'Pequenos reparos domésticos' }
]

const STORAGE_KEY = 'mockDiarias'

export const getMockUserByCredentials = (email, senha) => {
  return MOCK_USERS.find(user => user.email === email && user.senha === senha)
}

export const getMockCurrentUser = () => {
  const savedUser = localStorage.getItem('user')
  if (!savedUser) return null

  try {
    return JSON.parse(savedUser)
  } catch {
    return null
  }
}

export const isMockToken = () => {
  return localStorage.getItem('token')?.startsWith(MOCK_TOKEN_PREFIX)
}

export const makeMockPage = (content, page = 0, size = 10) => ({
  content,
  totalElements: content.length,
  totalPages: content.length > 0 ? 1 : 0,
  size,
  number: page,
  first: page === 0,
  last: true,
  empty: content.length === 0
})

export const getMockDiarias = () => {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (!saved) return []

  try {
    return JSON.parse(saved)
  } catch {
    return []
  }
}

export const saveMockDiarias = (diarias) => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(diarias))
}

export const addMockDiaria = (dados) => {
  const contratante = MOCK_USERS.find(user => user.id === dados.contratanteId)
  const contratado = MOCK_USERS.find(user => user.id === dados.contratadoId)
  const categoria = MOCK_CATEGORIAS.find(item => item.id === dados.categoriaId)
  const diarias = getMockDiarias()

  const diaria = {
    id: Date.now(),
    nomeContratante: contratante?.nome || 'Contratante',
    nomeContratado: contratado?.nome || 'Autonomo',
    nomeCategoria: categoria?.nome || 'Categoria',
    dataServico: dados.dataServico,
    status: 'PENDENTE'
  }

  saveMockDiarias([diaria, ...diarias])
  return diaria
}
