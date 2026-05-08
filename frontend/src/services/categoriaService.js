import api from './api'
import { isMockToken, makeMockPage, MOCK_CATEGORIAS } from './mockData'

export const categoriaService = {
  listar(page = 0, size = 10, nome = '', sort = 'nome') {
    if (isMockToken()) {
      const categorias = MOCK_CATEGORIAS.filter(categoria =>
        !nome || categoria.nome.toLowerCase().includes(nome.toLowerCase())
      )

      return Promise.resolve({ data: makeMockPage(categorias, page, size) })
    }

    return api.get('/categorias', {
      params: { page, size, nome: nome || undefined, sort }
    })
  },

  obterPorId(id) {
    if (isMockToken()) {
      return Promise.resolve({ data: MOCK_CATEGORIAS.find(categoria => categoria.id === Number(id)) })
    }

    return api.get(`/categorias/${id}`)
  },

  criar(dados) {
    if (isMockToken()) {
      const categoria = {
        id: Date.now(),
        ...dados
      }
      MOCK_CATEGORIAS.unshift(categoria)
      return Promise.resolve({ data: categoria })
    }

    return api.post('/categorias', dados)
  }
}
