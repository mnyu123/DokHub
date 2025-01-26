<template>
  <div>
    <!-- 로딩 스피너 -->
    <div v-if="loading" class="text-center my-5">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p>채널 목록을 불러오는 중입니다...</p>
    </div>

    <!-- 데이터 표시 -->
    <div v-else>
      <!-- 정렬 버튼 추가 -->
      <nav class="mb-3 d-flex justify-content-end">
        <button 
          class="btn btn-outline-secondary me-2" 
          @click="sortBy = 'latest'"
          :class="{ 'btn-primary': sortBy === 'latest' }"
        >
          최신순
        </button>
        <button 
          class="btn btn-outline-secondary" 
          @click="sortBy = 'name'"
          :class="{ 'btn-primary': sortBy === 'name' }"
        >
          이름순
        </button>
      </nav>

      <!-- 채널 리스트 -->
      <div class="card mb-3" v-for="(channel, idx) in sortedChannels" :key="idx">
        <div class="row g-0">
          <!-- 썸네일 -->
          <div class="col-4 col-md-3">
            <img
              :src="channel.thumbnailUrl && channel.thumbnailUrl.trim() !== '' ? channel.thumbnailUrl : require('@/assets/doksame3.gif')"
              class="img-fluid rounded-start"
              alt="video preview"
            />
          </div>

          <!-- 채널 정보 -->
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

              <!-- 최신 영상 섹션 -->
              <div class="mt-3">
                <h6>최신 영상</h6>
                <!-- 빈 배열도 렌더링 가능하도록 조건 수정 -->
                <div v-if="!channel.recentVideos || channel.recentVideos.length === 0" class="text-muted">
                  최신 영상 정보가 없습니다.
                </div>
                <div class="row" v-else>
                  <div 
                    class="col-6 col-md-4 mb-2"
                    v-for="(video, index) in channel.recentVideos" 
                    :key="index"
                    >
                    <a 
                      :href="`https://youtu.be/${video.videoId}`" 
                      target="_blank"
                      class="text-decoration-none"
                    >
                    <img
                    :src="video.thumbnailUrl || require('@/assets/dok22.gif')"
                    class="img-thumbnail video-thumbnail"
                    :alt="video.videoTitle"
                      />
                      <p class="small text-muted mt-1 text-truncate">
                        {{ video.videoTitle }}
                      </p>
                    </a>
                  </div>
                </div>
              </div>
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
      sortBy: "latest",
      channelCache: {}, // 메모리 캐시
    };
  },
  computed: {
    sortedChannels() {
      return [...this.allChannels].sort((a, b) => {
        if (this.sortBy === "latest") {
          const aDate = a.recentVideos[0]?.publishedAt || 0;
          const bDate = b.recentVideos[0]?.publishedAt || 0;
          return new Date(bDate) - new Date(aDate);
        } else {
          return a.channelName.localeCompare(b.channelName);
        }
      });
    },
    maxPage() {
      return Math.ceil(this.totalCount / this.size);
    },
  },
  watch: {
    selectedTab: "fetchTotalCount",
    sortBy() {
      this.allChannels = [...this.allChannels];
    },
  },
  async mounted() {
    await this.fetchTotalCount();
    await this.fetchChannels();
  },
  methods: {
    async fetchTotalCount() {
      try {
        const url = `https://dokhub-1135265453.ap-northeast-2.elb.amazonaws.com/api/channels/${this.selectedTab}/totalCount`;
        const response = await axios.get(url);
        this.totalCount = response.data;
      } catch (error) {
        console.error("Failed to fetch total count:", error);
      }
    },
    async fetchChannels() {
      const cacheKey = `channels_${this.selectedTab}_page_${this.page}`;
      const cachedData = JSON.parse(localStorage.getItem(cacheKey));
      const cacheTimestamp = localStorage.getItem(`${cacheKey}_timestamp`);
      const isCacheValid =
        cacheTimestamp &&
        Date.now() - new Date(cacheTimestamp).getTime() < 3600000;

      if (cachedData && isCacheValid) {
        this.allChannels = cachedData;
        this.loading = false;
        return;
      }

      try {
        this.loading = true;
        const url = `https://dokhub-1135265453.ap-northeast-2.elb.amazonaws.com/api/channels/${this.selectedTab}`;
        const response = await axios.get(url, {
          params: { page: this.page, size: this.size },
        });

        this.allChannels = response.data;
        this.channelCache[this.page] = response.data;

        localStorage.setItem(cacheKey, JSON.stringify(response.data));
        localStorage.setItem(`${cacheKey}_timestamp`, new Date().toISOString());
      } catch (error) {
        console.error("Failed to fetch channels:", error);
      } finally {
        this.loading = false;
      }
    },
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
.video-thumbnail {
  transition: transform 0.2s;
  max-width: 120px;
}

.video-thumbnail:hover {
  transform: scale(1.05);
}

.text-truncate {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.btn-outline-secondary.btn-primary {
  background-color: #0d6efd;
  color: white !important;
}
</style>