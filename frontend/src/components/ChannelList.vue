<template>
  <div>
    <!-- 1) 최신순 모드: 클립 단위 그리드 -->
    <div v-if="sortBy === 'latest'">
      <h2 class="text-2xl font-bold mb-4">최신 클립 목록</h2>
      <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-6">
        <div
          v-for="clip in displayedClips"
          :key="clip.videoId"
          class="card bg-base-100 shadow-md hover:shadow-lg transition"
        >
          <!-- 썸네일: 영상 링크 -->
          <a :href="`https://youtu.be/${clip.videoId}`" target="_blank" class="block">
            <figure>
              <img
                :src="getHighRes(clip.thumbnailUrl)"
                @error="$event.target.src = clip.thumbnailUrl"
                class="w-full h-64 object-cover rounded-t-md"
                alt="클립 썸네일"
              />
            </figure>
          </a>
          <div class="p-3">
            <!-- 영상 제목: 영상 링크 -->
            <a
              :href="`https://youtu.be/${clip.videoId}`"
              target="_blank"
              class="font-semibold truncate block hover:underline"
            >
              {{ clip.videoTitle }}
            </a>
            <!-- 채널명: 채널 링크 -->
            <a
              :href="clip.channelLink"
              target="_blank"
              class="text-sm text-gray-500 mt-1 block hover:underline"
            >
              {{ clip.channelName }}
            </a>
          </div>
        </div>
      </div>
    </div>

    <!-- 2) 최신순이 아닐 때: 채널 단위 리스트 (변경 없음) -->
    <div v-else>
      <p class="font-bold mb-2">총 채널 개수 : {{ totalCount }}</p>
      <div
        v-for="c in displayedChannels"
        :key="c.channelId"
        class="card lg:card-side bg-base-100 shadow-xl mb-6 animate-fadeIn"
      >
        <figure class="basis-1/3">
          <img
            :src="getHighRes(c.thumbnailUrl)"
            @error="$event.target.src = c.thumbnailUrl"
            class="img-fixed rounded-l-xl"
            alt="채널 썸네일"
          />
        </figure>
        <div class="card-body basis-2/3">
          <h2 class="card-title">{{ c.channelName }}</h2>
          <a :href="c.channelLink" target="_blank" class="link link-primary">
            채널 바로가기
          </a>
          <div class="mt-2">
            <h3 class="font-semibold mb-1">최신 영상</h3>
            <div v-if="!c.recentVideos?.length" class="text-sm opacity-60">없음</div>
            <div v-else class="grid grid-cols-2 md:grid-cols-3 gap-2">
              <a
                v-for="v in c.recentVideos"
                :key="v.videoId"
                :href="`https://youtu.be/${v.videoId}`"
                target="_blank"
              >
                <img
                  :src="getHighRes(v.thumbnailUrl)"
                  @error="$event.target.src = v.thumbnailUrl"
                  class="img-fixed rounded"
                  :alt="v.videoTitle"
                />
                <p class="text-xs mt-1 truncate">{{ v.videoTitle }}</p>
              </a>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 3) 페이징 -->
    <div class="flex justify-center items-center gap-4 my-8">
      <button class="btn" @click="prevPage" :disabled="isPrevDisabled">이전</button>
      <span>
        {{ sortBy === 'latest' ? clipPage + 1 : channelPage + 1 }} /
        {{ sortBy === 'latest' ? clipMaxPage : channelMaxPage }}
      </span>
      <button class="btn" @click="nextPage" :disabled="isNextDisabled">다음</button>
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