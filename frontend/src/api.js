import axios from 'axios'

const configuredBaseUrl = (process.env.VUE_APP_API_BASE_URL || '').replace(/\/$/, '')

const api = axios.create({
  baseURL: configuredBaseUrl,
  timeout: 15000
})

export default api
