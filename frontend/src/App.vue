<template>
  <div id="app" class="app-container">
    <nav v-if="isLoggedIn" class="navbar">
      <div class="navbar-brand">
        <router-link to="/dashboard" class="logo">Diárias JÁ</router-link>
      </div>
      <ul class="navbar-menu">
        <li><router-link to="/dashboard">Dashboard</router-link></li>
        <li><router-link to="/diarias">Minhas Diárias</router-link></li>
        <li><router-link to="/categorias">Categorias</router-link></li>
        <li><router-link to="/perfil">Perfil</router-link></li>
        <li><a href="#" @click.prevent="logout">Sair</a></li>
      </ul>
    </nav>
    <main class="main-content">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/authStore'

const router = useRouter()
const authStore = useAuthStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)

onMounted(() => {
  authStore.loadToken()
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 1rem 2rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.navbar-brand .logo {
  font-size: 1.5rem;
  font-weight: bold;
  text-decoration: none;
  color: white;
}

.navbar-menu {
  list-style: none;
  display: flex;
  gap: 2rem;
  margin: 0;
  padding: 0;
}

.navbar-menu a {
  color: white;
  text-decoration: none;
  transition: opacity 0.3s;
}

.navbar-menu a:hover {
  opacity: 0.8;
}

.main-content {
  flex: 1;
  padding: 2rem;
  background: #f5f7fa;
}

@media (max-width: 768px) {
  .navbar {
    flex-direction: column;
    gap: 1rem;
  }

  .navbar-menu {
    gap: 1rem;
  }
}
</style>
