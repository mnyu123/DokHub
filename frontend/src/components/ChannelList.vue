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
  props: ["selectedTab"],
  data() {
    return {
      allChannels: [],
      loading: true,
      page: 0,
      size: 7,
      totalCount: 0,
    };
  },
  computed: {
    tabTitle() {
      if (this.selectedTab === "clip") return "클립 채널";
      if (this.selectedTab === "song") return "노래 채널";
      if (this.selectedTab === "main") return "본채널";
      return "채널 목록";
    },
    maxPage() {
      return Math.ceil(this.totalCount / this.size);
    },
  },
  async mounted() {
    await this.fetchTotalCount();
    await this.fetchChannels();
  },
  methods: {
    async fetchTotalCount() {
  try {
    let url = "";
    // 서버에서 페이징용 총 개수받아오는거
    if (this.selectedTab === "clip") {
     url = "https://dokhub-backend2.fly.dev/api/channels/clip/totalCount";
    } else if (this.selectedTab === "song") {
     url = "https://dokhub-backend2.fly.dev/api/channels/song/totalCount";
    } else {
     url = "https://dokhub-backend2.fly.dev/api/channels/main/totalCount";
    }
    const response = await axios.get(url);
    this.totalCount = response.data;
  } catch (error) {
    console.error(error);
  }
},
    // 서버에서 카테고리 별 채널정보 받아오는거
    async fetchChannels() {
      try {
        this.loading = true;
        let url = "";
        // 탭에 따라 다른 URL 호출
        if (this.selectedTab === "clip") {
          url = "https://dokhub-backend2.fly.dev/api/channels-clip";
        } else if (this.selectedTab === "song") {
          url = "https://dokhub-backend2.fly.dev/api/channels-song";
        } else {
          // 본채널
          url = "https://dokhub-backend2.fly.dev/api/channels-main";
        }

        const response = await axios.get(url, {
          params: {
            page: this.page,
            size: this.size,
          },
        });
        this.allChannels = response.data;
      } catch (error) {
        console.error(error);
      } finally {
        this.loading = false;
      }
    },
    // === [변경된 부분 끝] ===
    prevPage() {
      if (this.page > 0) {
        this.page--;
        this.fetchChannels();
      }
    },
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
