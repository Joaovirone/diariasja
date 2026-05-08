import api from './api'

export const diariaService = {
  solicitar(dados) {
    return api.post('/diarias/solicitar', dados)
  },

  listar(page = 0, size = 10) {
    return api.get('/diarias', {
      params: { page, size }
    })
  },

  obterPorId(id) {
    return api.get(`/diarias/${id}`)
  },

  aceitar(id, idProfissional) {
    return api.patch(`/diarias/${id}/aceitar`, null, {
      params: { idProfissional }
    })
  },

  avaliar(id, nota) {
    return api.patch(`/diarias/${id}/avaliar`, null, {
      params: { nota }
    })
  },

  cancelar(id) {
    return api.patch(`/diarias/${id}/cancelar`)
  },

  listarMintidas(page = 0, size = 10) {
    return api.get('/diarias/minhas', {
      params: { page, size }
    })
  }
}
