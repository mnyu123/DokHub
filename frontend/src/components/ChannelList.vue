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
      <div v-else class="space-y-4 mt-8">
        <h2 class="text-3xl font-bold">리플레이 재생목록</h2>
        <!-- 백엔드에서 재생목록 아이템을 받아 세로 카드로 나열 -->
        <div
          v-for="it in replayItems"
          :key="it.videoId"
          class="card bg-base-100 shadow-md hover:shadow-lg transition"
        >
          <a
            :href="`https://www.youtube.com/watch?v=${it.videoId}&list=${REPLAY_PLAYLIST_ID}`"
            target="_blank"
            class="flex items-stretch"
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
            재생목록을 불러오지 못했습니다. 유튜브에서 직접 확인해 주세요.
          </div>
          <a :href="channelPlaylistsUrl" target="_blank" class="btn btn-primary">유튜브 재생목록으로 이동</a>
        </div>
      </div>

      <!-- 3) 페이징 -->
      <div v-if="isGridMode" class="flex justify-center items-center gap-6 my-8">
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
// ▼ replay 재생목록 아이템 (백엔드에서 받아옴)
const replayItems = ref([])
// ▼ 요청하신 플레이리스트 ID 고정 및 채널 재생목록 이동용 링크
const REPLAY_PLAYLIST_ID  = 'PLJRYmIJL3iscMUMNM2KCk2GpGe0W_HAax'
const channelPlaylistsUrl = 'https://www.youtube.com/@%EC%A7%AD%EC%BC%80%EC%9D%B5/playlists'
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
/* eslint-disable-next-line no-unused-vars */
const pagedChannels = computed(() =>
  sortedChannels.value.slice(channelPage.value * size, (channelPage.value + 1) * size)
)

// 페이징 계산
const maxPage = computed(() => {
  if (isGridMode.value) {
    // 그리드(클립) 모드: skipCount로 음수 방지 + 최소 1페이지 보장
    const effective = Math.max(0, allClips.value.length - skipCount.value)
    return Math.max(1, Math.ceil(effective / size))
  } else {
    // replay 모드: totalCount가 0이어도 최소 1페이지 보장
    const total = Math.max(0, totalCount.value || 0)
    return Math.max(1, Math.ceil(total / size))
  }
})

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
/* eslint-disable-next-line no-unused-vars */
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

// API 호출 (prod/dev 양쪽 모두 주석 형태로 유지)
async function fetchPlaylistItems() {
  // const url = `/api/playlist/${REPLAY_PLAYLIST_ID}`  // production
  const url = `http://localhost:8080/api/playlist/${REPLAY_PLAYLIST_ID}`  // dev
  const { data } = await axios.get(url, { params: { maxResults: 25 } })
  replayItems.value = Array.isArray(data) ? data : []
}


async function loadChannelData() {
  loading.value = true
  try {
    if (props.selectedTab === 'replay') {
      // 재생목록을 백엔드에서 받아서 세로 목록으로 표시
      //await Promise.all([fetchTotalCount(), fetchChannels()])
      await fetchPlaylistItems()
      totalCount.value = 0 // replay에선 페이징 미사용
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