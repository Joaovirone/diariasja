import api from './api'

export const categoriaService = {
  listar(page = 0, size = 10) {
    return api.get('/categorias', {
      params: { page, size }
    })
  },

  obterPorId(id) {
    return api.get(`/categorias/${id}`)
  },

  criar(dados) {
    return api.post('/categorias', dados)
  },

  atualizar(id, dados) {
    return api.put(`/categorias/${id}`, dados)
  },

  deletar(id) {
    return api.delete(`/categorias/${id}`)
  }
}
