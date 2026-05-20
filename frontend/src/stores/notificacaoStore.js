import { defineStore } from 'pinia'
import { computed, ref } from 'vue'

const makeNotifications = (tipoUsuario) => {
  if (tipoUsuario === 'CONTRATANTE') {
    return [
      {
        id: 1,
        type: 'message',
        title: 'Pedro Henrique respondeu no chat',
        description: 'Ele confirmou disponibilidade e pediu detalhes do local.',
        time: 'Agora',
        action: 'Abrir chat',
        to: '/chat',
        read: false
      },
      {
        id: 2,
        type: 'job',
        title: 'Diária aguardando confirmação',
        description: 'Faxina residencial para esta semana ainda está pendente.',
        time: '10 min',
        action: 'Ver diária',
        to: '/diarias',
        read: false
      }
    ]
  }

  return [
    {
      id: 1,
      type: 'job',
      title: 'Nova oportunidade de trabalho',
      description: 'João Vitor publicou uma faxina residencial com valor sugerido de R$ 180,00.',
      time: 'Agora',
      action: 'Ver serviço',
      to: '/diarias',
      read: false
    },
    {
      id: 2,
      type: 'message',
      title: 'Mensagem de João Vitor',
      description: 'Ele enviou detalhes sobre horário, endereço e materiais.',
      time: '5 min',
      action: 'Abrir chat',
      to: '/chat',
      read: false
    },
    {
      id: 3,
      type: 'job',
      title: 'Serviço confirmado',
      description: 'A manutenção foi confirmada e entrou na sua agenda.',
      time: 'Ontem',
      action: 'Ver agenda',
      to: '/dashboard',
      read: true
    }
  ]
}

export const useNotificacaoStore = defineStore('notificacao', () => {
  const notifications = ref([])
  const loadedFor = ref(null)

  const unreadCount = computed(() => notifications.value.filter(item => !item.read).length)
  const activeNotifications = computed(() => notifications.value.filter(item => !item.read))
  const readNotifications = computed(() => notifications.value.filter(item => item.read))

  const loadForUser = (tipoUsuario) => {
    if (loadedFor.value === tipoUsuario && notifications.value.length) return
    loadedFor.value = tipoUsuario
    notifications.value = makeNotifications(tipoUsuario)
  }

  const markAllRead = () => {
    notifications.value = notifications.value.map(item => ({ ...item, read: true }))
  }

  const markRead = (id) => {
    notifications.value = notifications.value.map(item => (
      item.id === id ? { ...item, read: true } : item
    ))
  }

  return {
    notifications,
    unreadCount,
    activeNotifications,
    readNotifications,
    loadForUser,
    markAllRead,
    markRead
  }
})
