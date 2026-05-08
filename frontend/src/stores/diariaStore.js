import { defineStore } from 'pinia'
import { ref } from 'vue'
import { diariaService } from '../services/diariaService'

export const useDiariaStore = defineStore('diaria', () => {
  const diarias = ref([])
  const minhasDiarias = ref([])
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
      error.value = err.response?.data || 'Erro ao solicitar diária'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const listar = async (page = 0, size = 10) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.listar(page, size)
      diarias.value = response.data.content || response.data
      total.value = response.data.totalElements || response.data.length
      return diarias.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao listar diárias'
    } finally {
      isLoading.value = false
    }
  }

  const listarMinhas = async (page = 0, size = 10) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.listarMintidas(page, size)
      minhasDiarias.value = response.data.content || response.data
      return minhasDiarias.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao listar minhas diárias'
    } finally {
      isLoading.value = false
    }
  }

  const aceitar = async (id, idProfissional) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await diariaService.aceitar(id, idProfissional)
      const index = diarias.value.findIndex(d => d.id === id)
      if (index > -1) {
        diarias.value[index] = response.data
      }
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Erro ao aceitar diária'
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
      const index = minhasDiarias.value.findIndex(d => d.id === id)
      if (index > -1) {
        minhasDiarias.value[index] = response.data
      }
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Erro ao avaliar diária'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    diarias,
    minhasDiarias,
    isLoading,
    error,
    total,
    solicitar,
    listar,
    listarMinhas,
    aceitar,
    avaliar
  }
})
