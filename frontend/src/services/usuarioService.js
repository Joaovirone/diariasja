import api from './api'
import { createRegisteredMockUser, getMockCurrentUser, getRegisteredMockUsers, isMockToken, makeMockPage, MOCK_USERS } from './mockData'

export const usuarioService = {
  obterPerfil() {
    if (isMockToken()) {
      return Promise.resolve({ data: getMockCurrentUser() })
    }

    return api.get('/usuarios/me')
  },

  criarConta(dados) {
    return api.post('/usuarios/cadastrar', dados).catch((err) => {
      if (!err.response) {
        return Promise.resolve({ data: createRegisteredMockUser(dados) })
      }

      throw err
    })
  },

  listarPrestadores(page = 0, size = 12, sort = 'nome') {
    if (isMockToken()) {
      const prestadores = [...MOCK_USERS, ...getRegisteredMockUsers()]
        .filter(user => user.tipo === 'CONTRATADO' && user.ativo)
        .map(({ senha, ...user }) => user)

      return Promise.resolve({ data: makeMockPage(prestadores, page, size) })
    }

    return api.get('/usuarios/profissionais', {
      params: { page, size, sort }
    })
  }
}
