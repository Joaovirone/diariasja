import api from './api'

export const usuarioService = {
  obterPerfil() {
    return api.get('/usuarios/perfil')
  },

  atualizar(dados) {
    return api.put('/usuarios/perfil', dados)
  },

  criarConta(dados) {
    return api.post('/usuarios', dados)
  },

  obterPorId(id) {
    return api.get(`/usuarios/${id}`)
  },

  listar(page = 0, size = 10) {
    return api.get('/usuarios', {
      params: { page, size }
    })
  }
}
