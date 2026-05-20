<template>
  <div class="container">
    <div class="page-header">
      <div>
        <h1>Chat</h1>
        <p>Converse com {{ isContratante ? 'prestadores de serviço' : 'contratantes' }} e combine os detalhes da diária.</p>
      </div>
    </div>

    <section class="chat-layout">
      <aside class="conversation-list">
        <div class="conversation-head">
          <strong>Conversas</strong>
          <span>{{ conversations.length }}</span>
        </div>
        <button
          v-for="conversation in conversations"
          :key="conversation.id"
          type="button"
          class="conversation-button"
          :class="{ active: conversation.id === activeConversationId }"
          @click="activeConversationId = conversation.id"
        >
          <span class="avatar">{{ initials(conversation.name) }}</span>
          <span>
            <strong>{{ conversation.name }}</strong>
            <small>{{ conversation.service }}</small>
            <em>{{ lastMessage(conversation.id) }}</em>
          </span>
        </button>
      </aside>

      <section class="chat-panel">
        <header class="chat-header">
          <div class="chat-person">
            <span class="avatar large">{{ initials(activeConversation.name) }}</span>
            <div>
              <strong>{{ activeConversation.name }}</strong>
              <span>{{ activeConversation.service }}</span>
            </div>
          </div>
          <div class="chat-meta">
            <span class="badge badge-success">Online</span>
            <router-link class="btn btn-small" to="/diarias">Ver diárias</router-link>
          </div>
        </header>

        <div class="messages" ref="messagesEl">
          <article
            v-for="message in activeMessages"
            :key="message.id"
            class="message"
            :class="{ mine: message.senderId === currentUserId }"
          >
            <p>{{ message.text }}</p>
            <time>{{ message.time }}</time>
          </article>
        </div>

        <form class="composer" @submit.prevent="sendMessage">
          <input
            v-model="draft"
            type="text"
            placeholder="Digite sua mensagem"
            aria-label="Digite sua mensagem"
          />
          <button class="btn btn-primary" type="submit">Enviar</button>
        </form>
      </section>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, ref, watch } from 'vue'
import { useAuthStore } from '../stores/authStore'

const STORAGE_KEY = 'diariasJaChatMessages'
const authStore = useAuthStore()
const draft = ref('')
const messagesEl = ref(null)
const currentUserId = computed(() => authStore.user?.id || 0)
const isContratante = computed(() => authStore.user?.tipo === 'CONTRATANTE')
const activeConversationId = ref('principal')

const conversations = computed(() => isContratante.value
  ? [
      { id: 'principal', name: 'Pedro Henrique', service: 'Faxina residencial', otherId: 2 },
      { id: 'ana-clara', name: 'Ana Clara', service: 'Jardinagem', otherId: 3 },
      { id: 'marcos-lima', name: 'Marcos Lima', service: 'Manutenção', otherId: 4 },
      { id: 'beatriz-santos', name: 'Beatriz Santos', service: 'Faxina detalhada', otherId: 5 }
    ]
  : [
      { id: 'principal', name: 'João Vitor', service: 'Faxina residencial', otherId: 1 }
    ])

const defaultMessages = computed(() => ({
  principal: [
    {
      id: 'seed-1',
      senderId: activeConversation.value.otherId,
      text: isContratante.value ? 'Oi! Posso te atender na data solicitada. Quer combinar o horário?' : 'Oi! Preciso de ajuda com uma faxina completa. Você tem disponibilidade?',
      time: '09:10'
    },
    {
      id: 'seed-2',
      senderId: currentUserId.value,
      text: isContratante.value ? 'Perfeito. Pode ser às 8h? O apartamento tem dois quartos.' : 'Tenho sim. Posso chegar às 8h e levar os materiais básicos.',
      time: '09:12'
    }
  ],
  'ana-clara': [
    { id: 'seed-3', senderId: 3, text: 'Consigo fazer poda e limpeza do quintal na sexta.', time: '11:05' }
  ],
  'marcos-lima': [
    { id: 'seed-4', senderId: 4, text: 'Posso avaliar os reparos e confirmar o valor antes da visita.', time: '14:22' }
  ],
  'beatriz-santos': [
    { id: 'seed-5', senderId: 5, text: 'Tenho disponibilidade para faxina detalhada nesta semana.', time: '16:40' }
  ]
}))

const savedMessages = ref(loadMessages())

const activeConversation = computed(() => conversations.value.find(item => item.id === activeConversationId.value) || conversations.value[0])
const validConversationIds = computed(() => conversations.value.map(item => item.id))
const scopedSavedMessages = computed(() => Object.fromEntries(
  Object.entries(savedMessages.value).filter(([id]) => validConversationIds.value.includes(id))
))
const allMessages = computed(() => ({ ...defaultMessages.value, ...scopedSavedMessages.value }))
const activeMessages = computed(() => allMessages.value[activeConversationId.value] || [])

function loadMessages() {
  try {
    return JSON.parse(localStorage.getItem(STORAGE_KEY)) || {}
  } catch {
    return {}
  }
}

function persistMessages() {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(savedMessages.value))
}

function initials(name) {
  return name.split(' ').map(part => part[0]).join('').slice(0, 2).toUpperCase()
}

function lastMessage(conversationId) {
  const messages = allMessages.value[conversationId] || []
  return messages[messages.length - 1]?.text || 'Sem mensagens ainda'
}

function now() {
  return new Intl.DateTimeFormat('pt-BR', { hour: '2-digit', minute: '2-digit' }).format(new Date())
}

function appendMessage(message) {
  const id = activeConversationId.value
  savedMessages.value = {
    ...savedMessages.value,
    [id]: [...(savedMessages.value[id] || []), message]
  }
  persistMessages()
}

function sendMessage() {
  const text = draft.value.trim()
  if (!text) return

  appendMessage({
    id: `msg-${Date.now()}`,
    senderId: currentUserId.value,
    text,
    time: now()
  })
  draft.value = ''

  window.setTimeout(() => {
    appendMessage({
      id: `reply-${Date.now()}`,
      senderId: activeConversation.value.otherId,
      text: isContratante.value ? 'Combinado. Vou deixar tudo registrado por aqui.' : 'Perfeito, obrigado pelo retorno. Vamos seguir assim.',
      time: now()
    })
  }, 650)
}

watch(activeMessages, async () => {
  await nextTick()
  if (messagesEl.value) {
    messagesEl.value.scrollTop = messagesEl.value.scrollHeight
  }
}, { deep: true, immediate: true })
</script>

<style scoped>
.chat-layout {
  display: grid;
  grid-template-columns: minmax(360px, 0.34fr) minmax(0, 1fr);
  min-height: calc(100vh - 78px - 8.5rem);
  background: var(--surface);
  border: 1px solid var(--border-color);
  border-radius: 22px;
  box-shadow: var(--shadow);
  overflow: hidden;
}

.conversation-list {
  padding: 1rem;
  border-right: 1px solid var(--border-color);
  background:
    linear-gradient(180deg, rgba(14, 124, 114, 0.08), transparent 14rem),
    rgba(249, 250, 251, 0.78);
  display: grid;
  align-content: start;
  gap: 0.65rem;
}

.conversation-head {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 0.25rem 0.25rem 0.75rem;
}

.conversation-head strong {
  color: var(--dark);
}

.conversation-head span {
  min-width: 30px;
  height: 30px;
  display: grid;
  place-items: center;
  border-radius: 999px;
  background: var(--primary);
  color: white;
  font-weight: 900;
}

.conversation-button {
  width: 100%;
  min-height: 92px;
  border-radius: 16px;
  padding: 0.85rem;
  background: rgba(255, 255, 255, 0.82);
  border: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  gap: 0.75rem;
  text-align: left;
}

.conversation-button.active {
  border-color: var(--primary);
  background: linear-gradient(135deg, rgba(14, 124, 114, 0.14), rgba(255, 255, 255, 0.86));
  box-shadow: 0 0 0 4px rgba(15, 118, 110, 0.1);
}

.avatar {
  width: 42px;
  height: 42px;
  border-radius: 14px;
  display: grid;
  place-items: center;
  background: var(--primary-soft);
  color: var(--primary);
  font-weight: 900;
}

.avatar.large {
  width: 54px;
  height: 54px;
  border-radius: 18px;
}

.conversation-button strong,
.conversation-button small {
  display: block;
}

.conversation-button small {
  color: var(--muted);
  margin-top: 0.2rem;
}

.conversation-button em {
  display: block;
  max-width: 230px;
  margin-top: 0.35rem;
  color: var(--muted);
  font-style: normal;
  font-size: 0.82rem;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.chat-panel {
  min-width: 0;
  display: grid;
  grid-template-rows: auto 1fr auto;
}

.chat-header {
  min-height: 92px;
  padding: 1rem 1.25rem;
  border-bottom: 1px solid var(--border-color);
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chat-person,
.chat-meta {
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.chat-header strong,
.chat-header span {
  display: block;
}

.chat-header span {
  color: var(--muted);
  margin-top: 0.2rem;
}

.messages {
  padding: 1.5rem;
  overflow-y: auto;
  background:
    radial-gradient(circle at 10% 12%, rgba(14, 124, 114, 0.08), transparent 20rem),
    var(--soft);
  display: flex;
  flex-direction: column;
  gap: 0.7rem;
}

.message {
  max-width: min(620px, 82%);
  padding: 0.85rem 1rem;
  border-radius: 18px 18px 18px 6px;
  background: var(--surface);
  border: 1px solid var(--border-color);
}

.message.mine {
  align-self: flex-end;
  background: linear-gradient(135deg, var(--primary), var(--sergipe-green));
  color: white;
  border-color: transparent;
  border-radius: 18px 18px 6px 18px;
}

.message time {
  display: block;
  margin-top: 0.35rem;
  font-size: 0.75rem;
  color: var(--muted);
}

.message.mine time {
  color: #ccfbf1;
}

.composer {
  padding: 1rem;
  border-top: 1px solid var(--border-color);
  display: grid;
  grid-template-columns: 1fr auto;
  gap: 0.75rem;
}

@media (max-width: 820px) {
  .chat-layout {
    grid-template-columns: 1fr;
  }

  .conversation-list {
    border-right: 0;
    border-bottom: 1px solid var(--border-color);
  }

  .composer {
    grid-template-columns: 1fr;
  }
}
</style>
