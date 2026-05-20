import { defineStore } from 'pinia'
import { ref } from 'vue'
import { diariaService } from '../services/diariaService'
import { getApiErrorMessage } from '../services/api'

export const useDiariaStore = defineStore('diaria', () => {
  const diarias = ref([])
  const diariasPendentes = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  const total = ref(0)

  const solicitar = async (dados) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.solicitar(dados)
      diarias.value.unshift(response.data)
      return response.data
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao solicitar diária')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const listarPorContratante = async (contratanteId, page = 0, size = 10) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.listarPorContratante(contratanteId, page, size)
      diarias.value = response.data.content || []
      total.value = response.data.totalElements || 0
      return diarias.value
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao listar diárias')
    } finally {
      isLoading.value = false
    }
  }

  const listarPendentesPrestador = async (contratadoId, page = 0, size = 10) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.listarPendentesPrestador(contratadoId, page, size)
      diariasPendentes.value = response.data.content || []
      total.value = response.data.totalElements || 0
      return diariasPendentes.value
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao listar diárias pendentes')
    } finally {
      isLoading.value = false
    }
  }

  const aceitar = async (id, idPrestador) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.aceitar(id, idPrestador)
      diariasPendentes.value = diariasPendentes.value.filter(d => d.id !== id)
      return response.data
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao aceitar diária')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const avaliar = async (id, nota) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.avaliar(id, nota)
      const index = diarias.value.findIndex(d => d.id === id)
      if (index > -1) diarias.value[index] = response.data
      return response.data
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao avaliar diária')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const cancelar = async (id, idUsuario) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.cancelar(id, idUsuario)
      const index = diarias.value.findIndex(d => d.id === id)
      if (index > -1) diarias.value[index] = response.data
      return response.data
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao cancelar diária')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    diarias,
    diariasPendentes,
    isLoading,
    error,
    total,
    solicitar,
    listarPorContratante,
    listarPendentesPrestador,
    aceitar,
    avaliar,
    cancelar
  }
})
