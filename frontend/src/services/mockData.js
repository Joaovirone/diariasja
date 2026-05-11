export const MOCK_TOKEN_PREFIX = 'mock-token'

export const MOCK_USERS = [
  {
    id: 1,
    nome: 'João Vitor',
    email: 'contratante@gmail.com',
    senha: '123456',
    dataNascimento: '1995-01-01',
    tipo: 'CONTRATANTE',
    ativo: true
  },
  {
    id: 2,
    nome: 'Pedro Henrique',
    email: 'autonomo@gmail.com',
    senha: '123456',
    dataNascimento: '1990-01-01',
    tipo: 'CONTRATADO',
    ativo: true,
    categoriaPrincipal: 'Faxina',
    valorBase: 180,
    avaliacao: 4.9,
    bairro: 'Centro',
    descricao: 'Limpeza residencial completa, organização leve e materiais básicos inclusos.',
    fotoUrl: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: 3,
    nome: 'Ana Clara',
    email: 'ana.clara@gmail.com',
    senha: '123456',
    dataNascimento: '1988-04-12',
    tipo: 'CONTRATADO',
    ativo: true,
    categoriaPrincipal: 'Jardinagem',
    valorBase: 220,
    avaliacao: 4.8,
    bairro: 'Jardim América',
    descricao: 'Poda, limpeza de jardim, retirada de folhas e manutenção de vasos.',
    fotoUrl: 'https://images.unsplash.com/photo-1494790108377-be9c29b29330?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: 4,
    nome: 'Marcos Lima',
    email: 'marcos.lima@gmail.com',
    senha: '123456',
    dataNascimento: '1985-07-22',
    tipo: 'CONTRATADO',
    ativo: true,
    categoriaPrincipal: 'Manutenção',
    valorBase: 260,
    avaliacao: 4.7,
    bairro: 'Vila Nova',
    descricao: 'Pequenos reparos, troca de tomadas, torneiras, ajustes e instalações simples.',
    fotoUrl: 'https://images.unsplash.com/photo-1506794778202-cad84cf45f1d?auto=format&fit=crop&w=600&q=80'
  },
  {
    id: 5,
    nome: 'Beatriz Santos',
    email: 'beatriz.santos@gmail.com',
    senha: '123456',
    dataNascimento: '1992-11-03',
    tipo: 'CONTRATADO',
    ativo: true,
    categoriaPrincipal: 'Faxina',
    valorBase: 200,
    avaliacao: 5,
    bairro: 'Boa Vista',
    descricao: 'Faxina detalhada para casas e apartamentos, com foco em cozinha e banheiros.',
    fotoUrl: 'https://images.unsplash.com/photo-1544005313-94ddf0286df2?auto=format&fit=crop&w=600&q=80'
  }
]

export const MOCK_CATEGORIAS = [
  { id: 1, nome: 'Faxina', descricao: 'Limpeza residencial' },
  { id: 2, nome: 'Jardinagem', descricao: 'Manutenção de jardins' },
  { id: 3, nome: 'Manutenção', descricao: 'Pequenos reparos domésticos' }
]

const STORAGE_KEY = 'mockDiarias'

const DEFAULT_MOCK_DIARIAS = [
  {
    id: 101,
    nomeContratante: 'João Vitor',
    nomeContratado: 'Pedro Henrique',
    contratanteId: 1,
    contratadoId: 2,
    categoriaId: 1,
    nomeCategoria: 'Faxina',
    dataServico: new Date(Date.now() + 86400000 * 2).toISOString().slice(0, 10),
    status: 'PENDENTE',
    valorServico: 180
  },
  {
    id: 102,
    nomeContratante: 'João Vitor',
    nomeContratado: 'Pedro Henrique',
    contratanteId: 1,
    contratadoId: 2,
    categoriaId: 3,
    nomeCategoria: 'Manutenção',
    dataServico: new Date(Date.now() + 86400000 * 5).toISOString().slice(0, 10),
    status: 'CONFIRMADA',
    valorServico: 240
  }
]

export const getMockUserByCredentials = (email, senha) => {
  return MOCK_USERS.find(user => user.email === email && user.senha === senha)
}

export const getMockCurrentUser = () => {
  const savedUser = localStorage.getItem('user')
  if (!savedUser) return null

  try {
    const parsedUser = JSON.parse(savedUser)
    const freshMock = MOCK_USERS.find(user => user.id === parsedUser.id)
    if (!freshMock) return parsedUser
    const { senha, ...user } = freshMock
    localStorage.setItem('user', JSON.stringify(user))
    return user
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
  if (!saved) {
    saveMockDiarias(DEFAULT_MOCK_DIARIAS)
    return DEFAULT_MOCK_DIARIAS
  }

  try {
    const parsedDiarias = JSON.parse(saved)
    if (Array.isArray(parsedDiarias) && parsedDiarias.length === 0) {
      saveMockDiarias(DEFAULT_MOCK_DIARIAS)
      return DEFAULT_MOCK_DIARIAS
    }
    return parsedDiarias
  } catch {
    saveMockDiarias(DEFAULT_MOCK_DIARIAS)
    return DEFAULT_MOCK_DIARIAS
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
    nomeContratado: contratado?.nome || 'Autônomo',
    contratanteId: dados.contratanteId,
    contratadoId: dados.contratadoId,
    categoriaId: dados.categoriaId,
    nomeCategoria: categoria?.nome || 'Categoria',
    dataServico: dados.dataServico,
    status: 'PENDENTE',
    valorServico: dados.valorServico || 180
  }

  saveMockDiarias([diaria, ...diarias])
  return diaria
}
