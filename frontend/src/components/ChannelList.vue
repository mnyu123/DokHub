<template>
  <div>
    <!-- 로딩 스피너 -->
    <div v-if="loading" class="text-center my-5 spinner-container">
      <div class="spinner-border text-primary" role="status">
        <span class="visually-hidden">Loading...</span>
      </div>
      <p>채널 목록을 불러오는 중입니다...</p>
    </div>

    <!-- 데이터 표시 -->
    <div v-else>
      <!-- 정렬 및 총 클립 개수 표시 -->
      <nav class="mb-3 d-flex justify-content-between align-items-center sort-nav">
        <div class="fw-bold">
          총 채널 개수 : {{ totalCount }}
        </div>
        <div>
          <!-- 기본 부트스트랩 btn 클래스를 제거하고 커스텀 클래스(sort-btn)만 적용 -->
          <button 
            class="sort-btn me-2"
            @click="sortBy = 'latest'"
            :class="{ active: sortBy === 'latest' }"
          >
            최신순
          </button>
          <button 
            class="sort-btn"
            @click="sortBy = 'name'"
            :class="{ active: sortBy === 'name' }"
          >
            이름순
          </button>
        </div>
      </nav>

      <!-- 채널 리스트 -->
      <div class="channel-card card mb-3" v-for="(channel, idx) in sortedChannels" :key="idx">
        <div class="row g-0">
          <!-- 썸네일 -->
          <div class="col-4 col-md-3">
            <img
              :src="channel.thumbnailUrl && channel.thumbnailUrl.trim() !== '' ? channel.thumbnailUrl : require('@/assets/doksame3.gif')"
              class="img-fluid rounded-start channel-thumbnail"
              alt="video preview"
            />
          </div>

          <!-- 채널 정보 -->
          <div class="col-8 col-md-9">
            <div class="card-body">
              <h5 class="card-title">{{ channel.channelName }}</h5>
              <a
                class="channel-link"
                :href="channel.channelLink"
                target="_blank"
                rel="noopener noreferrer"
              >
                채널 바로가기
              </a>

              <!-- 최신 영상 섹션 -->
              <div class="mt-3">
                <h6>최신 영상</h6>
                <div v-if="!channel.recentVideos || channel.recentVideos.length === 0" class="text-muted">
                  최신 영상 정보가 없습니다.
                </div>
                <div class="row" v-else>
                  <div 
                    class="col-6 col-md-4 mb-2 video-card"
                    v-for="(video, index) in channel.recentVideos" 
                    :key="index"
                  >
                    <a 
                      :href="`https://youtu.be/${video.videoId}`" 
                      target="_blank"
                      class="text-decoration-none"
                    >
                      <img
                        :src="video.thumbnailUrl || require('@/assets/doksame3.gif')"
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
          class="page-btn me-3" 
          @click="prevPage" 
          :disabled="page <= 0"
        >
          이전
        </button>
        <span>Page {{ page + 1 }} / {{ maxPage }}</span>
        <button 
          class="page-btn ms-3" 
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
      channelCache: {},
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
        const url = `http://localhost:8080/api/channels/${this.selectedTab}/totalCount`;
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
        const url = `http://localhost:8080/api/channels/${this.selectedTab}`;
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
    async prevPage() {
      if (this.page > 0) {
        this.page--;
        await this.fetchChannels();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },
    async nextPage() {
      if (this.page < this.maxPage - 1) {
        this.page++;
        await this.fetchChannels();
        window.scrollTo({ top: 0, behavior: 'smooth' });
      }
    },
  },
};
</script>

<style scoped>
/* 로딩 스피너 페이드인 효과 */
.spinner-container {
  animation: fadeIn 1s ease-in;
}
@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 채널 카드 애니메이션 및 호버 효과 */
.channel-card {
  animation: cardFadeIn 0.5s ease-in-out;
  transition: transform 0.3s ease, box-shadow 0.3s ease;
}
.channel-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.3);
}
@keyframes cardFadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 채널 썸네일 호버 효과 */
.channel-thumbnail {
  transition: transform 0.3s ease;
}
.channel-thumbnail:hover {
  transform: scale(1.05);
}

/* 정렬 버튼 커스텀 스타일 */
.sort-nav .sort-btn {
  padding: 8px 16px;
  border: none;
  background-color: transparent;
  color: inherit;
  font-weight: bold;
  border-radius: 4px;
  transition: background-color 0.3s ease, transform 0.3s ease;
}
.sort-nav .sort-btn:hover {
  background-color: rgba(13, 110, 253, 0.2);
  transform: scale(1.05);
}
.sort-nav .sort-btn.active {
  background-color: #0d6efd;
  color: white;
  transform: scale(1.05);
}

/* 페이징 버튼 커스텀 스타일 */
.page-btn {
  padding: 10px 20px;
  border: none;
  background-color: #0d6efd;
  color: #fff;
  border-radius: 4px;
  transition: background-color 0.3s ease, transform 0.3s ease;
  cursor: pointer;
}
.page-btn:disabled {
  background-color: #555;
  cursor: not-allowed;
}
.page-btn:hover:not(:disabled) {
  background-color: #0b5ed7;
  transform: scale(1.05);
}

/* 채널 링크 호버 효과 */
.channel-link {
  transition: color 0.3s ease;
}
.channel-link:hover {
  color: #0d6efd;
}
</style>
