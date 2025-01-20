<template>
    <div>
      <h2 v-if="selectedTab === 'clip'">클립 채널</h2>
      <h2 v-else-if="selectedTab === 'song'">노래 채널</h2>
      <h2 v-else-if="selectedTab === 'main'">본채널</h2>
  
      <div v-for="(channel, index) in filteredChannels" :key="index" class="channel-card">
        <img :src="channel.videoPreviewUrl" alt="video preview" class="preview-img" />
        <div class="channel-info">
          <h3>{{ channel.channelName }}</h3>
          <a :href="channel.channelLink" target="_blank" rel="noopener noreferrer">
            채널 바로가기
          </a>
        </div>
      </div>
    </div>
  </template>
  
  <script>
  import axios from 'axios'
  
  export default {
    name: 'ChannelList',
    props: ['selectedTab'],
    data() {
      return {
        allChannels: []
      }
    },
    computed: {
      filteredChannels() {
        // 현재 탭에 해당하는 채널만 필터링
        return this.allChannels.filter(ch => ch.category === this.selectedTab)
      }
    },
    mounted() {
      // 컴포넌트 마운트 시 API 호출
      this.fetchChannels()
    },
    methods: {
      async fetchChannels() {
        try {
          // CORS 문제 피하려면 절대주소 X
          // axios.get('http://localhost:8080/api/channels') // <- 이렇게 하면 CORS 에러 발생
          // 아래처럼 '/api/...' 형식으로
          const response = await axios.get('/api/channels')
          this.allChannels = response.data
        } catch (error) {
          console.error(error)
        }
      }
    }
  }
  </script>
  
  <style scoped>
  .channel-card {
    border: 1px solid #ddd;
    padding: 10px;
    margin: 10px 0;
    display: flex;
    align-items: center;
  }
  .preview-img {
    width: 120px;
    height: 90px;
    object-fit: cover;
    margin-right: 10px;
  }
  .channel-info a {
    color: blue;
    text-decoration: underline;
  }
  </style>
  