import api from './api'
import { getMockCurrentUser, isMockToken, makeMockPage, MOCK_USERS } from './mockData'

export const usuarioService = {
  obterPerfil() {
    if (isMockToken()) {
      return Promise.resolve({ data: getMockCurrentUser() })
    }

    return api.get('/usuarios/me')
  },

  criarConta(dados) {
    return api.post('/usuarios/cadastrar', dados)
  },

  listarProfissionais(page = 0, size = 12, sort = 'nome') {
    if (isMockToken()) {
      const profissionais = MOCK_USERS
        .filter(user => user.tipo === 'CONTRATADO' && user.ativo)
        .map(({ senha, ...user }) => user)

      return Promise.resolve({ data: makeMockPage(profissionais, page, size) })
    }

    return api.get('/usuarios/profissionais', {
      params: { page, size, sort }
    })
  }
}
