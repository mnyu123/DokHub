<template>
  <div>
    <!-- 로딩 스피너 -->
    <div v-if="loading" class="flex justify-center items-center my-10">
      <span class="loading loading-spinner loading-lg"></span>
    </div>

    <div v-else>
      <!-- 페이지네이션 시 스크롤 기준점 -->
       <div ref="gridTopRef" class="h-0"></div>
      <!-- 1) replay 탭을 제외한 모든 탭에서: 클립 그리드 -->
      <div v-if="isGridMode" class="mt-8">
        <h2 class="text-4xl font-bold text-white mb-6">클립</h2>
        <div class="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 gap-8">
          <div
            v-for="clip in displayedClips"
            :key="clip.videoId"
            class="card bg-base-100 shadow-md hover:shadow-lg transition py-4"
          >
            <a
              :href="`https://youtu.be/${clip.videoId}`"
              target="_blank"
              rel="noopener noreferrer"
              class="block"
              @click="trackVideoClick(clip, props.selectedTab)"
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
                target="_blank"
                rel="noopener noreferrer"
                class="font-semibold truncate block hover:text-primary hover:underline"
                @click="trackVideoClick(clip, props.selectedTab)"
              >
                {{ clip.videoTitle }}
              </a>
              <a
                :href="clip.channelLink"
                target="_blank"
                rel="noopener noreferrer"
                class="text-sm text-gray-500 mt-2 block hover:text-primary hover:underline"
              >
                {{ clip.channelName }}
              </a>
            </div>
          </div>
        </div>
      </div>

            <!-- 2) replay 탭일 때만: 채널 리스트 -->
      <div v-else class="space-y-4 mt-8">
        <h2 class="text-3xl font-bold">리플레이 재생목록</h2>
        <!-- 백엔드에서 재생목록 아이템을 받아 세로 카드로 나열 -->
        <div
          v-for="it in replayItems"
          :key="it.videoId"
          class="card bg-base-100 shadow-md hover:shadow-lg transition"
        >
          <a
            :href="`https://www.youtube.com/watch?v=${it.videoId}`"
            target="_blank"
            rel="noopener noreferrer"
            class="flex items-stretch"
            @click="trackVideoClick({
    videoId: it.videoId,
    videoTitle: it.videoTitle,
    channelName: '독케익 다시보기',
    channelId: REPLAY_CHANNEL_ID
  }, 'replay')"
          >
            <img
              :src="getHighRes(it.thumbnailUrl)"
              @error="$event.target.src = defaultImg"
              class="w-48 h-28 object-cover rounded-l-md"
              alt="리플레이 썸네일"
            />
            <div class="p-3 flex-1">
              <p class="font-semibold line-clamp-2">{{ it.videoTitle }}</p>
              <p v-if="it.publishedAt" class="text-xs opacity-70 mt-1">
                {{ new Date(it.publishedAt).toLocaleString('ko-KR') }}
              </p>
            </div>
          </a>
        </div>

        <!-- 아이템이 없을 때: 채널 재생목록 페이지로 유도 -->
        <div v-if="!replayItems.length" class="text-center">
          <div class="alert alert-info my-4">
            채널 영상을 불러오지 못했습니다. [독케익다시보기] 유튜브 채널에서 직접 확인해 주세요.
          </div>
          <a :href="channelVideosUrl" target="_blank" rel="noopener noreferrer" class="btn btn-primary">유튜브 채널 영상으로 이동</a>
        </div>
      </div>

      <!-- 3) 페이징 -->
      <div v-if="isGridMode" class="flex justify-center items-center gap-6 my-8">
        <button class="btn btn-sm" @click="prevPage" :disabled="isPrevDisabled">
          이전
        </button>
        <span class="text-lg font-medium">페이지 {{ currentPage }}</span>
        <button class="btn btn-sm" @click="nextPage" :disabled="isNextDisabled">
          다음
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted, nextTick } from 'vue'
import api from '@/api'
import defaultImg from '@/assets/default_thumbnail.svg'

const props = defineProps({
  selectedTab: { type: String, required: true },
  excludedVideoIds: { type: Array, default: () => [] }
})

const REPLAY_CHANNEL_ID = 'UCzdsBMcTdToWM4S72p49Dew'
const CHANNEL_BATCH_SIZE = 12
const CLIPS_PER_PAGE = 12

const gridTopRef = ref(null)
const allChannels = ref([])
const totalCount = ref(0)
const replayItems = ref([])
const loading = ref(true)
const loadedChannelPage = ref(0)
const clipPage = ref(0)
const channelVideosUrl = `https://www.youtube.com/channel/${REPLAY_CHANNEL_ID}/videos`

const isGridMode = computed(() => props.selectedTab !== 'replay')
const excludedSet = computed(() => new Set(props.excludedVideoIds))

const allClips = computed(() => {
  const unique = new Map()
  allChannels.value.forEach(channel => {
    (channel.recentVideos || []).forEach(video => {
      if (!video?.videoId || excludedSet.value.has(video.videoId)) return
      unique.set(video.videoId, {
        ...video,
        channelName: channel.channelName,
        channelLink: channel.channelLink,
        channelId: channel.channelId
      })
    })
  })
  return [...unique.values()].sort((a, b) => new Date(b.publishedAt) - new Date(a.publishedAt))
})

const displayedClips = computed(() => {
  const start = clipPage.value * CLIPS_PER_PAGE
  return allClips.value.slice(start, start + CLIPS_PER_PAGE)
})

const currentPage = computed(() => clipPage.value + 1)
const hasMoreChannelPages = computed(() =>
  (loadedChannelPage.value + 1) * CHANNEL_BATCH_SIZE < totalCount.value
)
const hasLoadedNextClipPage = computed(() =>
  (clipPage.value + 1) * CLIPS_PER_PAGE < allClips.value.length
)
const isPrevDisabled = computed(() => clipPage.value <= 0)
const isNextDisabled = computed(() => !hasLoadedNextClipPage.value && !hasMoreChannelPages.value)

async function fetchTotalCount() {
  const { data } = await api.get(`/api/channels/${props.selectedTab}/totalCount`)
  totalCount.value = Number(data) || 0
}

async function fetchChannelPage(page, append = false) {
  const { data } = await api.get(`/api/channels/${props.selectedTab}`, {
    params: { page, size: CHANNEL_BATCH_SIZE }
  })
  const channels = Array.isArray(data) ? data : []
  allChannels.value = append ? [...allChannels.value, ...channels] : channels
  loadedChannelPage.value = page
}

async function loadChannelData() {
  loading.value = true
  try {
    if (props.selectedTab === 'replay') {
      await fetchReplayChannelVideos()
      return
    }
    await Promise.all([fetchTotalCount(), fetchChannelPage(0)])
  } catch (error) {
    console.error('loadChannelData error:', error)
    allChannels.value = []
  } finally {
    loading.value = false
  }
}

function prevPage() {
  if (clipPage.value > 0) clipPage.value--
  scrollToGrid()
}

async function nextPage() {
  if (loading.value) return
  loading.value = true
  try {
    while (!hasLoadedNextClipPage.value && hasMoreChannelPages.value) {
      await fetchChannelPage(loadedChannelPage.value + 1, true)
    }
    if (hasLoadedNextClipPage.value) clipPage.value++
  } catch (error) {
    console.error('nextPage error:', error)
  } finally {
    loading.value = false
    await nextTick()
    scrollToGrid()
  }
}

function scrollToGrid() {
  gridTopRef.value?.scrollIntoView({ behavior: 'smooth', block: 'start' })
}

function getHighRes(url) {
  return url ? url.replace(/default\.jpg$/, 'maxresdefault.jpg') : defaultImg
}

async function fetchReplayChannelVideos() {
  const { data } = await api.get(`/api/channel-videos/${REPLAY_CHANNEL_ID}`, {
    params: { maxResults: 25 }
  })
  replayItems.value = Array.isArray(data) ? data : []
}

async function trackVideoClick(item, category) {
  try {
    await api.post('/api/metrics/video-click', {
      videoId: item.videoId,
      videoTitle: item.videoTitle,
      category,
      channelName: item.channelName || '',
      channelId: item.channelId || ''
    })
  } catch (error) {
    console.error('trackVideoClick error:', error)
  }
}

watch(() => props.excludedVideoIds, () => {
  clipPage.value = 0
})

onMounted(loadChannelData)
</script>
