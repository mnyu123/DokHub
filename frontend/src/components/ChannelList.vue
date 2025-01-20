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
      <!-- 제목 표시 (선택) -->
      <h3 class="mb-3">
        {{ tabTitle }}
      </h3>

      <!-- 서버에서 이미 카테고리별로 필터한 채널 목록이 allChannels에 담김 -->
      <div class="card mb-3" v-for="(channel, idx) in allChannels" :key="idx">
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

      <!-- 페이징 버튼 -->
      <div class="d-flex justify-content-center align-items-center mt-4">
        <button 
          class="btn btn-secondary me-3" 
          @click="prevPage" 
          :disabled="page <= 0"
        >
          이전
        </button>

        <span>Page {{ page + 1 }} / {{ maxPage }}</span>

        <button 
          class="btn btn-secondary ms-3" 
          @click="nextPage" 
          :disabled="page >= maxPage - 1"
        >
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from "axios";

export default {
  name: "ChannelList",
  props: ["selectedTab"], // clip / song / main
  data() {
    return {
      allChannels: [],    // 서버에서 받은 채널 목록 (이미 서버가 카테고리 필터링 적용)
      loading: true,      // 로딩 상태
      page: 0,            // 현재 페이지(0부터 시작)
      size: 7,            // 페이지당 표시 개수
      totalCount: 0,      // 현재 카테고리의 전체 채널 개수
    };
  },
  computed: {
    // 탭 제목 (필요에 따라 사용)
    tabTitle() {
      if (this.selectedTab === "clip") return "클립 채널";
      if (this.selectedTab === "song") return "노래 채널";
      if (this.selectedTab === "main") return "본채널";
      return "채널 목록";
    },
    maxPage() {
      // 총 페이지 수 = ceil(전체 개수 / 페이지당 개수)
      return Math.ceil(this.totalCount / this.size);
    },
  },
  async mounted() {
    // 컴포넌트 마운트 시 전체 개수 & 페이지 데이터 호출
    await this.fetchTotalCount();  // 전체 채널 수 (현재 탭 기준)
    await this.fetchChannels();    // 해당 탭 / 페이지의 채널 목록
  },
  methods: {
    // 카테고리별 totalCount
    async fetchTotalCount() {
      try {
        const response = await axios.get(
          "https://dokhub-backend2.fly.dev/api/channels/totalCount",
          {
            params: {
              category: this.selectedTab, // clip/song/main
            },
          }
        );
        this.totalCount = response.data;
      } catch (error) {
        console.error(error);
      }
    },
    // 카테고리별 채널 목록 (페이징)
    async fetchChannels() {
      try {
        this.loading = true; // 로딩 시작
        const response = await axios.get(
          "https://dokhub-backend2.fly.dev/api/channels",
          {
            params: {
              category: this.selectedTab, // clip/song/main
              page: this.page,
              size: this.size,
            },
          }
        );
        this.allChannels = response.data; 
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false; // 로딩 종료
      }
    },
    // 이전 페이지
    prevPage() {
      if (this.page > 0) {
        this.page--;
        this.fetchChannels();
      }
    },
    // 다음 페이지
    nextPage() {
      if (this.page < this.maxPage - 1) {
        this.page++;
        this.fetchChannels();
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
