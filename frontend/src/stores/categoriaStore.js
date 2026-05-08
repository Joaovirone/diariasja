import { defineStore } from 'pinia'
import { ref } from 'vue'
import { categoriaService } from '../services/categoriaService'

export const useCategoriaStore = defineStore('categoria', () => {
  const categorias = ref([])
  const isLoading = ref(false)
  const error = ref(null)
  const total = ref(0)

  const listar = async (page = 0, size = 10) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await categoriaService.listar(page, size)
      categorias.value = response.data.content || response.data
      total.value = response.data.totalElements || response.data.length
      return categorias.value
    } catch (err) {
      error.value = err.response?.data || 'Erro ao listar categorias'
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
      error.value = err.response?.data || 'Erro ao criar categoria'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const atualizar = async (id, dados) => {
    isLoading.value = true
    error.value = null
    try {
      const response = await categoriaService.atualizar(id, dados)
      const index = categorias.value.findIndex(c => c.id === id)
      if (index > -1) {
        categorias.value[index] = response.data
      }
      return response.data
    } catch (err) {
      error.value = err.response?.data || 'Erro ao atualizar categoria'
      throw err
    } finally {
      isLoading.value = false
    }
  }

  const deletar = async (id) => {
    isLoading.value = true
    error.value = null
    try {
      await categoriaService.deletar(id)
      categorias.value = categorias.value.filter(c => c.id !== id)
    } catch (err) {
      error.value = err.response?.data || 'Erro ao deletar categoria'
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
    criar,
    atualizar,
    deletar
  }
})
