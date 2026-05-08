import api from './api'

export const authService = {
  login(email, senha) {
    return api.post('/auth/login', { email, senha })
  },

  logout() {
    localStorage.removeItem('token')
    localStorage.removeItem('user')
  },

  getToken() {
    return localStorage.getItem('token')
  },

  setToken(token) {
    localStorage.setItem('token', token)
  },

  isLoggedIn() {
    return !!this.getToken()
  }
}
