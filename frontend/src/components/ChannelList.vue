<template>
  <div>
    <!-- 1) 최신 클립 모드 -->
    <div
      v-if="sortBy === 'latest'"
      class="mt-8"
    >
      <!-- 타이틀 강조색(text-primary) + 위아래 여백 -->
      <h2
        class="text-4xl font-bold text-white mb-6"
      >
        최신 클립 목록
      </h2>

      <!-- 클립 카드 그리드: gap-8 으로 카드 사이 여백 증가 -->
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
        <div
          v-for="clip in displayedClips"
          :key="clip.videoId"
          class="card bg-base-100 shadow-md hover:shadow-lg transition py-4"
        >
          <!-- 썸네일 클릭 → 영상 이동 -->
          <a
            :href="`https://youtu.be/${clip.videoId}`"
            target="_blank"
            class="block"
          >
            <figure>
              <img
                :src="getHighRes(clip.thumbnailUrl)"
                class="w-full h-64 object-cover rounded-t-md"
                alt="클립 썸네일"
              />
            </figure>
          </a>
          <div class="px-4 pt-3 pb-4">
            <!-- 제목: hover 시 강조색 + 밑줄 -->
            <a
              :href="`https://youtu.be/${clip.videoId}`"
              class="font-semibold truncate block hover:text-primary hover:underline"
            >
              {{ clip.videoTitle }}
            </a>
            <!-- 채널명: hover 시 강조색 -->
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

    <!-- 2) 최신 클립 모드가 아닐 때: 기존 채널 리스트 (gap-8만 적용) -->
    <div v-else class="space-y-8 mt-8">
      <p class="font-bold mb-4">총 채널 개수 : {{ totalCount }}</p>
      <div
        v-for="c in displayedChannels"
        :key="c.channelId"
        class="card lg:card-side bg-base-100 shadow-xl animate-fadeIn py-4"
      >
        <figure class="basis-1/3">
          <img
            :src="getHighRes(c.thumbnailUrl)"
            class="w-full h-64 object-cover rounded-l-xl"
            alt="채널 썸네일"
          />
        </figure>
        <div class="card-body basis-2/3">
          <h2 class="card-title">{{ c.channelName }}</h2>
          <a :href="c.channelLink" target="_blank" class="link link-primary">
            채널 바로가기
          </a>
          <!-- 이하 생략 -->
        </div>
      </div>
    </div>

    <!-- 3) 페이징 -->
    <div class="flex justify-center items-center gap-6 my-8">
      <button class="btn btn-sm" @click="prevPage" :disabled="isPrevDisabled">
        이전
      </button>
      <span class="text-lg font-medium">
        {{ sortBy==='latest' ? clipPage+1 : channelPage+1 }} /
        {{ sortBy==='latest' ? clipMaxPage : channelMaxPage }}
      </span>
      <button class="btn btn-sm" @click="nextPage" :disabled="isNextDisabled">
        다음
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'

const props = defineProps({ selectedTab: String })

// state
const allChannels = ref([])
const totalCount  = ref(0)
const loading     = ref(true)
const channelPage = ref(0)
const clipPage    = ref(0)
const size        = 9
const sortBy      = ref('latest')

// 1) 클립 목록 계산
const displayedClips = computed(() => {
  const clips = allChannels.value.flatMap(c =>
    (c.recentVideos || []).map(v => ({
      ...v,
      channelName: c.channelName,
      channelLink: c.channelLink  // 채널 링크 추가
    }))
  )
  clips.sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt))
  const start = clipPage.value * size
  return clips.slice(start, start + size)
})

// 2) 채널별 뷰
const displayedChannels = computed(() => {
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

// 페이징 계산
const channelMaxPage = computed(() => Math.ceil(totalCount.value / size))
const clipMaxPage    = computed(() => {
  const totalClips = allChannels.value.flatMap(c => c.recentVideos || []).length
  return Math.ceil(totalClips / size)
})
const isPrevDisabled = computed(() =>
  sortBy.value === 'latest' ? clipPage.value <= 0 : channelPage.value <= 0
)
const isNextDisabled = computed(() =>
  sortBy.value === 'latest'
    ? clipPage.value >= clipMaxPage.value - 1
    : channelPage.value >= channelMaxPage.value - 1
)

// API 호출
async function fetchTotalCount() {
  const url = `http://localhost:8080/api/channels/${props.selectedTab}/totalCount`
  const { data } = await axios.get(url)
  totalCount.value = data
}
async function fetchChannels() {
  loading.value = true
  const url = `http://localhost:8080/api/channels/${props.selectedTab}`
  const { data } = await axios.get(url, { params: { page: channelPage.value, size } })
  allChannels.value = data
  loading.value = false
}

// 초기/탭 변경
async function loadChannelData() {
  await Promise.all([fetchTotalCount(), fetchChannels()])
}
onMounted(loadChannelData)
watch(() => props.selectedTab, () => {
  channelPage.value = 0
  clipPage.value    = 0
  loadChannelData()
})

// 정렬 변경
watch(sortBy, val => {
  if (val === 'latest') clipPage.value = 0
  else {
    channelPage.value = 0
    loadChannelData()
  }
})

// 페이지 이동
function prevPage() {
  if (sortBy.value === 'latest' && clipPage.value > 0) {
    clipPage.value--
  }
  else if (channelPage.value > 0) {
    channelPage.value--
    loadChannelData()
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}
function nextPage() {
  if (sortBy.value === 'latest' && clipPage.value < clipMaxPage.value - 1) {
    clipPage.value++
  }
  else if (channelPage.value < channelMaxPage.value - 1) {
    channelPage.value++
    loadChannelData()
  }
  window.scrollTo({ top: 0, behavior: 'smooth' })
}

// 썸네일 고해상도
function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg')
}
</script>