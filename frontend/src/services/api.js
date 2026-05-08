import axios from 'axios'

const API_URL = import.meta.env.VITE_API_URL || 'http://localhost:8080'

const api = axios.create({
  baseURL: `${API_URL}/api`,
  headers: {
    'Content-Type': 'application/json'
  }
})

export const getApiErrorMessage = (error, fallback = 'Erro ao processar a solicitação') => {
  const data = error.response?.data

  if (typeof data === 'string') return data
  if (data?.mensagem) return data.mensagem
  if (data?.erro) return data.erro
  if (data?.message) return data.message
  if (data?.campos) return Object.values(data.campos).join(', ')

  return fallback
}

api.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401 && window.location.pathname !== '/login') {
      localStorage.removeItem('token')
      localStorage.removeItem('user')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export default api
