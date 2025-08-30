<template>
  <div>
    <!-- 로딩 스피너 -->
    <div v-if="loading" class="flex justify-center items-center my-10">
      <span class="loading loading-spinner loading-lg"></span>
    </div>

    <div v-else>
      <!-- 1) replay 탭을 제외한 모든 탭에서: 클립 그리드 -->
      <div v-if="isGridMode" class="mt-8">
        <h2 class="text-4xl font-bold text-white mb-6">최신 클립 목록</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
          <div
            v-for="clip in displayedClips"
            :key="clip.videoId"
            class="card bg-base-100 shadow-md hover:shadow-lg transition py-4"
          >
            <a
              :href="`https://youtu.be/${clip.videoId}`"
              target="_blank"
              class="block"
            >
              <img
                :src="getHighRes(clip.thumbnailUrl)"
                @error="$event.target.src = defaultImg"
                class="w-full h-64 object-cover rounded-t-md"
                alt="클립 썸네일"
              />
            </a>
            <div class="px-4 pt-3 pb-4">
              <a
                :href="`https://youtu.be/${clip.videoId}`"
                class="font-semibold truncate block hover:text-primary hover:underline"
              >
                {{ clip.videoTitle }}
              </a>
              <a
                :href="clip.channelLink"
                class="text-sm text-gray-500 mt-2 block hover:text-primary hover:underline"
              >
                {{ clip.channelName }}
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 2) replay 탭일 때만: 채널 리스트 -->
      <div v-else class="space-y-8 mt-8">
        <p class="font-bold mb-4">총 채널 개수 : {{ totalCount }}</p>
        <div
          v-for="c in pagedChannels"
          :key="c.channelId"
          class="card lg:card-side bg-base-100 shadow-xl animate-fadeIn py-4"
        >
          <img
            :src="getHighRes(c.thumbnailUrl)"
            @error="$event.target.src = defaultImg"
            class="w-full lg:w-1/3 h-64 object-cover rounded-l-xl"
            alt="채널 썸네일"
          />
          <div class="card-body basis-2/3">
            <h2 class="card-title">{{ c.channelName }}</h2>
            <a :href="c.channelLink" target="_blank" class="link link-primary">
              채널 바로가기
            </a>
          </div>
        </div>
      </div>

      <!-- 3) 페이징 -->
      <div class="flex justify-center items-center gap-6 my-8">
        <button class="btn btn-sm" @click="prevPage" :disabled="isPrevDisabled">
          이전
        </button>
        <span class="text-lg font-medium">{{ currentPage }} / {{ maxPage }}</span>
        <button class="btn btn-sm" @click="nextPage" :disabled="isNextDisabled">
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import defaultImg from '@/assets/doksame3.gif'

const props       = defineProps({ selectedTab: String })
const allChannels = ref([])
const totalCount  = ref(0)
const loading     = ref(true)
const channelPage = ref(0)
const clipPage    = ref(0)
const size        = 9
const skipCount = computed(() =>
   props.selectedTab === 'clip' ? 7 : 0
)
const sortBy      = ref('latest')

// replay 탭만 제외하고 클립 그리드 모드
const isGridMode = computed(() => props.selectedTab !== 'replay')

// 모든 클립(flat) → 최신순 정렬
const allClips = computed(() =>
  allChannels.value
    .flatMap(c =>
      (c.recentVideos || []).map(v => ({
        ...v,
        channelName: c.channelName,
        channelLink: c.channelLink
      }))
    )
    .sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt))
)

// 화면에 보여줄 클립
const displayedClips = computed(() => {
  const start = skipCount.value + clipPage.value * size
  return allClips.value.slice(start, start + size)
})

// 채널 정렬
const sortedChannels = computed(() => {
  const list = [...allChannels.value]
  if (sortBy.value === 'latest') {
    return list.sort((a, b) =>
      new Date(b.recentVideos[0]?.publishedAt || 0) -
      new Date(a.recentVideos[0]?.publishedAt || 0)
    )
  }
  if (sortBy.value === 'name') {
    return list.sort((a, b) => a.channelName.localeCompare(b.channelName))
  }
  if (sortBy.value === 'random') {
    return list.sort(() => Math.random() - 0.5)
  }
  return list
})

// 화면에 보여줄 채널 페이지
const pagedChannels = computed(() =>
  sortedChannels.value.slice(channelPage.value * size, (channelPage.value + 1) * size)
)

// 페이징 계산
const maxPage = computed(() =>
  isGridMode.value
    ? Math.ceil((allClips.value.length - skipCount.value) / size)
    : Math.ceil(totalCount.value / size)
)

const currentPage = computed(() =>
  isGridMode.value ? clipPage.value + 1 : channelPage.value + 1
)

const isPrevDisabled = computed(() =>
  isGridMode.value ? clipPage.value <= 0 : channelPage.value <= 0
)

const isNextDisabled = computed(() =>
  isGridMode.value
    ? clipPage.value >= maxPage.value - 1
    : channelPage.value >= maxPage.value - 1
)

// API 호출 (prod/dev 양쪽 모두 주석 형태로 유지)
async function fetchTotalCount() {
  // const url = `/api/channels/${props.selectedTab}/totalCount`  // production
  const url = `http://localhost:8080/api/channels/${props.selectedTab}/totalCount`  // dev
  totalCount.value = (await axios.get(url)).data
}
async function fetchChannels() {
  // const url = `/api/channels/${props.selectedTab}`  // production
  const url = `http://localhost:8080/api/channels/${props.selectedTab}`  // dev
  allChannels.value = (await axios.get(url, {
    params: { page: channelPage.value, size }
  })).data
}

async function loadChannelData() {
  loading.value = true
  try {
    if (props.selectedTab === 'replay') {
      await Promise.all([fetchTotalCount(), fetchChannels()])
    } else {
      await fetchChannels()
      totalCount.value = 0
    }
  } finally {
    loading.value = false
  }
}

// 초기/탭 변경 시 데이터 로드
onMounted(loadChannelData)
watch(() => props.selectedTab, () => {
  channelPage.value = 0
  clipPage.value    = 0
  loadChannelData()
})

// 페이지 이동
function prevPage() {
  if (isGridMode.value && clipPage.value > 0) {
    clipPage.value--
  } else if (!isGridMode.value && channelPage.value > 0) {
    channelPage.value--
    loadChannelData()
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
function nextPage() {
  if (isGridMode.value && clipPage.value < maxPage.value - 1) {
    clipPage.value++
  } else if (!isGridMode.value && channelPage.value < maxPage.value - 1) {
    channelPage.value++
    loadChannelData()
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 썸네일 고해상도 변환
function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg')
}
</script>