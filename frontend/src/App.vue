<template>
  <div id="app" class="app-shell">
    <aside v-if="isLoggedIn" class="sidebar">
      <router-link to="/dashboard" class="brand">
        <span class="brand-mark">DJ</span>
        <span>
          <strong>Diárias Já</strong>
          <small>{{ roleLabel }}</small>
        </span>
      </router-link>

      <nav class="nav-list" aria-label="Navegação principal">
        <router-link to="/dashboard">Dashboard</router-link>
        <router-link to="/diarias">{{ isContratante ? 'Minhas diárias' : 'Serviços' }}</router-link>
        <router-link v-if="isContratante" to="/diarias/criar">Solicitar diária</router-link>
        <router-link to="/chat">Chat</router-link>
        <router-link to="/notificacoes" class="nav-with-badge">
          Notificações
          <span v-if="unreadCount">{{ unreadCount }}</span>
        </router-link>
        <router-link to="/perfil">Perfil</router-link>
        <router-link v-if="isContratante" to="/categorias">Categorias</router-link>
      </nav>

      <button class="logout-button" type="button" @click="logout">Sair</button>
    </aside>

    <div class="content-shell">
      <header v-if="isLoggedIn" class="topbar">
        <div>
          <span class="eyebrow">Bem-vindo de volta</span>
          <strong>{{ userName }}</strong>
        </div>
        <div class="topbar-actions">
          <router-link v-if="unreadCount" to="/notificacoes" class="notification-shortcut">{{ unreadCount }}</router-link>
          <router-link to="/chat" class="chat-shortcut">Abrir chat</router-link>
        </div>
      </header>

      <main class="main-content">
        <router-view />
      </main>
    </div>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from './stores/authStore'
import { useNotificacaoStore } from './stores/notificacaoStore'

const router = useRouter()
const authStore = useAuthStore()
const notificacaoStore = useNotificacaoStore()

const isLoggedIn = computed(() => authStore.isLoggedIn)
const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const roleLabel = computed(() => isContratante.value ? 'Contratante' : 'Autônomo')
const userName = computed(() => authStore.user?.nome || 'Usuário')
const unreadCount = computed(() => notificacaoStore.unreadCount)

onMounted(() => {
  authStore.loadToken()
  document.body.classList.remove('auth-screen')
  document.body.classList.remove('dark-theme')
  localStorage.removeItem('diariasJaTheme')
  if (authStore.user?.tipo) {
    notificacaoStore.loadForUser(authStore.user.tipo)
  }
})

const logout = () => {
  authStore.logout()
  router.push('/login')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  width: 100%;
  display: flex;
  background: transparent;
}

.sidebar {
  position: sticky;
  top: 0;
  width: 260px;
  height: 100vh;
  padding: 1.15rem;
  background:
    linear-gradient(180deg, rgba(17, 24, 39, 0.98), rgba(10, 35, 34, 0.98));
  color: white;
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
}

.brand {
  display: flex;
  gap: 0.8rem;
  align-items: center;
  color: white;
  text-decoration: none;
  padding: 0.65rem;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.06);
}

.brand-mark {
  width: 48px;
  height: 48px;
  display: grid;
  place-items: center;
  border-radius: 14px;
  background: linear-gradient(135deg, #25d0bd, #f9b572);
  color: #05231f;
  font-weight: 950;
  box-shadow: 0 14px 32px rgba(37, 208, 189, 0.18);
}

.brand strong,
.brand small {
  display: block;
}

.brand small,
.eyebrow {
  color: #98a2b3;
  font-size: 0.78rem;
  text-transform: uppercase;
  letter-spacing: 0;
}

.nav-list {
  display: grid;
  gap: 0.42rem;
}

.nav-list a,
.logout-button {
  color: #d0d5dd;
  text-decoration: none;
  border-radius: 14px;
  padding: 0.86rem 0.95rem;
  background: transparent;
  border: 0;
  text-align: left;
  font: inherit;
}

.nav-with-badge {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 0.75rem;
}

.nav-with-badge span,
.notification-shortcut {
  min-width: 26px;
  height: 26px;
  display: inline-grid;
  place-items: center;
  border-radius: 999px;
  background: #f97316;
  color: white;
  font-size: 0.78rem;
  font-weight: 900;
}

.nav-list a:hover,
.nav-list a.router-link-active {
  background: rgba(255, 255, 255, 0.1);
  color: white;
  box-shadow: inset 3px 0 0 #25d0bd;
}

.logout-button {
  margin-top: auto;
  cursor: pointer;
  background: rgba(220, 38, 38, 0.16);
  color: #fecaca;
  border: 1px solid rgba(248, 113, 113, 0.32);
  font-weight: 800;
}

.logout-button:hover {
  background: #dc2626;
  color: white;
  box-shadow: 0 14px 28px rgba(220, 38, 38, 0.25);
}

.content-shell {
  flex: 1;
  min-width: 0;
  min-height: 100vh;
  display: flex;
  flex-direction: column;
}

.topbar {
  height: 78px;
  padding: 0 2.25rem;
  border-bottom: 1px solid var(--border-color);
  background: rgba(255, 255, 255, 0.72);
  backdrop-filter: blur(18px);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.topbar strong {
  display: block;
  color: var(--dark);
}

.topbar-actions {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.chat-shortcut,
.notification-shortcut {
  color: var(--primary);
  font-weight: 700;
  text-decoration: none;
}

.chat-shortcut {
  padding: 0.55rem 0.8rem;
  border-radius: 999px;
  background: rgba(14, 124, 114, 0.08);
}

.notification-shortcut {
  color: white;
}

.main-content {
  flex: 1;
  padding: clamp(1.25rem, 2vw, 2.25rem);
}

@media (max-width: 860px) {
  .app-shell {
    display: block;
  }

  .sidebar {
    position: static;
    width: 100%;
    height: auto;
    border-radius: 0 0 16px 16px;
  }

  .nav-list {
    grid-template-columns: repeat(2, minmax(0, 1fr));
  }

  .logout-button {
    margin-top: 0;
  }

  .topbar {
    height: auto;
    padding: 1rem;
  }

  .main-content {
    padding: 1rem;
  }
}

@media (min-width: 1500px) {
  .main-content {
    padding: 2.5rem 3rem;
  }
}
</style>
