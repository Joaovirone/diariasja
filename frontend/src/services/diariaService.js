import api from './api'
import {
  addMockDiaria,
  getMockDiarias,
  isMockToken,
  makeMockPage,
  saveMockDiarias
} from './mockData'

export const diariaService = {
  solicitar(dados) {
    if (isMockToken()) {
      return Promise.resolve({ data: addMockDiaria(dados) })
    }

    return api.post('/diarias/solicitar', dados)
  },

  listarPorContratante(contratanteId, page = 0, size = 10, sort = 'dataServico') {
    if (isMockToken()) {
      const diarias = getMockDiarias().filter(diaria => Number(diaria.contratanteId || 1) === Number(contratanteId))
      return Promise.resolve({ data: makeMockPage(diarias, page, size) })
    }

    return api.get(`/diarias/contratante/${contratanteId}`, {
      params: { page, size, sort }
    })
  },

  aceitar(id, idProfissional) {
    if (isMockToken()) {
      const diarias = getMockDiarias()
      const index = diarias.findIndex(diaria => diaria.id === Number(id))
      if (index > -1) {
        diarias[index] = { ...diarias[index], status: 'CONFIRMADA', contratadoId: idProfissional }
        saveMockDiarias(diarias)
        return Promise.resolve({ data: diarias[index] })
      }
    }

    return api.patch(`/diarias/${id}/aceitar`, null, {
      params: { idProfissional }
    })
  },

  avaliar(id, nota) {
    if (isMockToken()) {
      const diarias = getMockDiarias()
      const diaria = diarias.find(item => item.id === Number(id))
      return Promise.resolve({ data: diaria })
    }

    return api.patch(`/diarias/${id}/avaliar`, null, {
      params: { nota }
    })
  },

  cancelar(id, idUsuario) {
    if (isMockToken()) {
      const diarias = getMockDiarias()
      const index = diarias.findIndex(diaria => diaria.id === Number(id))
      if (index > -1) {
        diarias[index] = { ...diarias[index], status: 'CANCELADA' }
        saveMockDiarias(diarias)
        return Promise.resolve({ data: diarias[index] })
      }
    }

    return api.patch(`/diarias/${id}/cancelar`, null, {
      params: { idUsuario }
    })
  },

  listarPendentesProfissional(contratadoId, page = 0, size = 10, sort = 'dataServico') {
    if (isMockToken()) {
      const diarias = getMockDiarias().filter(diaria => {
        const belongsToProfessional = !diaria.contratadoId || Number(diaria.contratadoId) === Number(contratadoId)
        return belongsToProfessional && diaria.status !== 'CANCELADA'
      })
      return Promise.resolve({ data: makeMockPage(diarias, page, size) })
    }

    return api.get(`/diarias/profissional/${contratadoId}/pendentes`, {
      params: { page, size, sort }
    })
  }
}
