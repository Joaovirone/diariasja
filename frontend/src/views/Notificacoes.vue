<template>
  <div class="container">
    <div class="page-header">
      <div>
        <h1>Notificações</h1>
        <p>Mensagens, oportunidades e atualizações importantes em um único lugar.</p>
      </div>
      <button type="button" class="btn" :disabled="unreadCount === 0" @click="notificacaoStore.markAllRead">
        Marcar como lidas
      </button>
    </div>

    <section class="notifications-layout">
      <div class="notification-summary">
        <strong>{{ unreadCount }}</strong>
        <span>não lidas</span>
        <p>{{ isContratante ? 'Acompanhe respostas de profissionais e status das diárias.' : 'Veja oportunidades novas e mensagens de contratantes.' }}</p>
      </div>

      <div class="notification-list" v-if="activeNotifications.length">
        <article
          v-for="notification in activeNotifications"
          :key="notification.id"
          class="notification-card unread"
        >
          <span class="notification-icon">{{ notification.type === 'message' ? 'MSG' : 'JOB' }}</span>
          <div>
            <div class="notification-title">
              <h2>{{ notification.title }}</h2>
              <small>{{ notification.time }}</small>
            </div>
            <p>{{ notification.description }}</p>
            <div class="notification-actions">
              <router-link :to="notification.to" class="btn btn-primary btn-small">{{ notification.action }}</router-link>
              <button type="button" class="btn btn-small" @click="notificacaoStore.markRead(notification.id)">
                Marcar como lida
              </button>
            </div>
          </div>
        </article>
      </div>

      <div v-else class="empty-notifications">
        <strong>Nenhuma notificação nova</strong>
        <p>Quando chegar uma mensagem ou oportunidade, ela aparece aqui.</p>
      </div>

      <details v-if="readNotifications.length" class="read-section">
        <summary>Ver notificações lidas ({{ readNotifications.length }})</summary>
        <article v-for="notification in readNotifications" :key="notification.id" class="notification-card read-card">
          <span class="notification-icon">{{ notification.type === 'message' ? 'MSG' : 'JOB' }}</span>
          <div>
            <div class="notification-title">
              <h2>{{ notification.title }}</h2>
              <small>{{ notification.time }}</small>
            </div>
            <p>{{ notification.description }}</p>
          </div>
        </article>
      </details>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted } from 'vue'
import { useAuthStore } from '../stores/authStore'
import { useNotificacaoStore } from '../stores/notificacaoStore'

const authStore = useAuthStore()
const notificacaoStore = useNotificacaoStore()

const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const unreadCount = computed(() => notificacaoStore.unreadCount)
const activeNotifications = computed(() => notificacaoStore.activeNotifications)
const readNotifications = computed(() => notificacaoStore.readNotifications)

onMounted(() => {
  notificacaoStore.loadForUser(authStore.user?.tipo)
})
</script>

<style scoped>
.notifications-layout {
  display: grid;
  grid-template-columns: minmax(340px, 0.28fr) minmax(0, 1fr);
  gap: 1rem;
}

.notification-summary,
.notification-card {
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: var(--surface);
  box-shadow: var(--shadow);
}

.notification-summary {
  min-height: 100%;
  padding: 1.5rem;
  background: linear-gradient(140deg, #101828, #0e7c72);
  color: white;
}

.notification-summary strong {
  display: block;
  font-size: 4rem;
}

.notification-summary span {
  display: block;
  font-weight: 900;
  text-transform: uppercase;
}

.notification-summary p {
  margin-top: 1rem;
  color: #d1faf4;
}

.notification-list {
  display: grid;
  gap: 0.85rem;
}

.read-section {
  grid-column: 2;
  margin-top: 1rem;
}

.read-section summary {
  cursor: pointer;
  font-weight: 900;
  color: var(--dark);
  margin-bottom: 0.85rem;
}

.notification-card {
  display: grid;
  grid-template-columns: auto 1fr;
  gap: 1rem;
  padding: 1.2rem;
  min-height: 128px;
}

.notification-card.unread {
  border-color: #0f766e;
  box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.08), var(--shadow);
}

.read-card {
  opacity: 0.72;
  margin-bottom: 0.75rem;
}

.empty-notifications {
  padding: 2rem;
  border: 1px solid var(--border-color);
  border-radius: 20px;
  background: var(--surface);
  box-shadow: var(--shadow);
  text-align: center;
}

.empty-notifications p {
  color: var(--muted);
  margin-top: 0.35rem;
}

.notification-icon {
  width: 54px;
  height: 54px;
  display: grid;
  place-items: center;
  border-radius: 16px;
  background: #ccfbf1;
  color: #115e59;
  font-size: 0.78rem;
  font-weight: 900;
}

.notification-title {
  display: flex;
  align-items: baseline;
  justify-content: space-between;
  gap: 1rem;
}

.notification-title h2 {
  font-size: 1.15rem;
}

.notification-title small,
.notification-card p {
  color: var(--muted);
}

.notification-card p {
  margin-top: 0.35rem;
}

.notification-actions {
  display: flex;
  flex-wrap: wrap;
  gap: 0.6rem;
  margin-top: 1rem;
}

@media (max-width: 900px) {
  .notifications-layout {
    grid-template-columns: 1fr;
  }

  .read-section {
    grid-column: auto;
  }
}
</style>
