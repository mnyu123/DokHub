import { reactive } from 'vue'
import api from '@/api'

export const liveStatus = reactive({
  liveOn: false,
  initialized: false,
  loading: false
})

let requestInFlight = null

export async function refreshLiveStatus() {
  if (requestInFlight) return requestInFlight

  liveStatus.loading = true
  requestInFlight = api.get('/api/live/status', { timeout: 5000 })
    .then(({ data }) => {
      const previous = liveStatus.liveOn
      liveStatus.liveOn = data?.livestatus === 'on'
      liveStatus.initialized = true
      return { liveOn: liveStatus.liveOn, started: !previous && liveStatus.liveOn }
    })
    .catch((error) => {
      console.error('refreshLiveStatus error:', error)
      return { liveOn: liveStatus.liveOn, started: false }
    })
    .finally(() => {
      liveStatus.loading = false
      requestInFlight = null
    })

  return requestInFlight
}
