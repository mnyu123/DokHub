<template>
    <div>
      <!-- 탭별 제목 -->
    <h3 class="mb-3" v-if="selectedTab === 'clip'">클립 채널</h3>
    <h3 class="mb-3" v-else-if="selectedTab === 'song'">노래 채널</h3>
    <h3 class="mb-3" v-else>본채널</h3>
  
      <!-- 채널 카드 리스트 -->
    <div
      class="card mb-3"
      v-for="(channel, idx) in filteredChannels"
      :key="idx"
    >
      <div class="row g-0">
        <!-- 썸네일 이미지 -->
        <div class="col-4 col-md-3">
          <img
            :src="channel.videoPreviewUrl"
            class="img-fluid rounded-start"
            alt="video preview"
          />
        </div>
        <!-- 채널 이름 & 링크 -->
        <div class="col-8 col-md-9">
          <div class="card-body">
            <h5 class="card-title">{{ channel.channelName }}</h5>
            <a
              class="btn btn-link p-0"
              :href="channel.channelLink"
              target="_blank"
              rel="noopener noreferrer"
            >
              채널 바로가기
            </a>
          </div>
        </div>
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
          // const response = await axios.get('/api/channels') // 로컬 환경에서
          // 실제 환경
          const response = await axios.get(`https://dokhub-backend2.fly.dev/api/channels`)
          this.allChannels = response.data
        } catch (error) {
          console.error(error)
        }
      }
    }
  }
  </script>
  
  <style scoped>
  /* card, img-fluid은 Bootstrap 클래스 활용
     여기서 추가로 커스텀할 부분 있으면 작성 */
  </style>
  