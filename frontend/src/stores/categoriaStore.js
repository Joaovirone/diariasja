import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoriaService } from '../services/categoriaService'
import { getApiErrorMessage } from '../services/api'

export const useCategoriaStore = defineStore('categoria', () => {
  const categorias = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  const total = ref(0)

  const listar = async (page = 0, size = 10, nome = '') => {
    isLoading.value = true
    error.value = null
    try {
      const response = await categoriaService.listar(page, size, nome)
      categorias.value = response.data.content || []
      total.value = response.data.totalElements || 0
      return categorias.value
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao listar categorias')
    } finally {
      isLoading.value = false
    }
  }

  const criar = async (dados) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await categoriaService.criar(dados)
      categorias.value.unshift(response.data)
      return response.data
    } catch (err) {
      error.value = getApiErrorMessage(err, 'Erro ao criar categoria')
      throw err
    } finally {
      isLoading.value = false
    }
  }

  return {
    categorias,
    isLoading,
    error,
    total,
    listar,
    criar
  }
})
