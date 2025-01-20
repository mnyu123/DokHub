<template>
  <div>
    <!-- 로딩 스피너 -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p>채널 목록을 불러오는 중입니다...</p>
    </div>

    <!-- 데이터가 로드된 후 채널 리스트 표시 -->
    <div v-else>
      <h3 class="mb-3" v-if="selectedTab === 'clip'">클립 채널</h3>
      <h3 class="mb-3" v-else-if="selectedTab === 'song'">노래 채널</h3>
      <h3 class="mb-3" v-else>본채널</h3>

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
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ChannelList",
  props: ["selectedTab"],
  data() {
    return {
      allChannels: [],
      loading: true, // 로딩 상태 추가
    };
  },
  computed: {
    filteredChannels() {
      // 현재 탭에 해당하는 채널만 필터링
      return this.allChannels.filter((ch) => ch.category === this.selectedTab);
    },
  },
  mounted() {
    this.fetchChannels();
  },
  methods: {
    async fetchChannels() {
      try {
        this.loading = true; // 로딩 시작
        const response = await axios.get(
          `https://dokhub-backend2.fly.dev/api/channels`
        );
        this.allChannels = response.data;
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false; // 로딩 종료
      }
    },
  },
};
</script>

<style scoped>
.text-center {
  text-align: center;
}

.my-5 {
  margin-top: 3rem;
  margin-bottom: 3rem;
}

.spinner-border {
  width: 3rem;
  height: 3rem;
}
</style>
