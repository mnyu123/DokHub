<template>
  <div>
    <!-- 로딩 -->
    <div v-if="loading" class="flex flex-col items-center my-10">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4">채널 목록을 불러오는 중입니다…</p>
    </div>

    <!-- 내용 -->
    <div v-else>
      <!-- 상단: 총 개수 + 정렬 -->
      <div class="flex justify-between items-center mb-4">
        <p class="font-bold">총 채널 개수 : {{ totalCount }}</p>

        <div class="join">
          <button class="btn join-item"
                  :class="sortBy==='latest' && 'btn-primary'"
                  @click="sortBy='latest'">
            최신순
          </button>
          <button class="btn join-item"
                  :class="sortBy==='name' && 'btn-primary'"
                  @click="sortBy='name'">
            이름순
          </button>
        </div>
      </div>

      <!-- 채널 카드 -->
      <div v-for="(c,idx) in sortedChannels" :key="idx"
           class="card lg:card-side bg-base-100 shadow-xl mb-6 animate-fadeIn">
        <!-- 썸네일 -->
        <figure class="basis-1/3">
          <img :src="c.thumbnailUrl?.trim() || placeholder"
               class="img-fixed rounded-l-xl">
        </figure>

        <!-- 정보 -->
        <div class="card-body basis-2/3">
          <h2 class="card-title">{{ c.channelName }}</h2>
          <a :href="c.channelLink" target="_blank" class="link link-primary">
            채널 바로가기
          </a>

          <!-- 최신 영상 -->
          <div class="mt-2">
            <h3 class="font-semibold mb-1">최신 영상</h3>
            <div v-if="!c.recentVideos?.length" class="text-sm opacity-60">
              없음
            </div>
            <div v-else class="grid grid-cols-2 md:grid-cols-3 gap-2">
              <a v-for="v in c.recentVideos" :key="v.videoId"
                 :href="`https://youtu.be/${v.videoId}`" target="_blank">
                <img :src="v.thumbnailUrl || placeholder"
                     class="img-fixed rounded">
                <p class="text-xs mt-1 truncate">{{ v.videoTitle }}</p>
              </a>
            </div>
          </div>
        </div>
      </div>

      <!-- 페이지네이션 -->
      <div class="flex justify-center items-center gap-4 my-8">
        <button class="btn" @click="prevPage" :disabled="page<=0">이전</button>
        <span>{{ page+1 }} / {{ maxPage }}</span>
        <button class="btn" @click="nextPage" :disabled="page>=maxPage-1">다음</button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue';
import axios from 'axios';

const props = defineProps({ selectedTab: String });

/* ─────────────── data ─────────────── */
const allChannels = ref([]);
const loading     = ref(true);
const page        = ref(0);
const size        = 7;
const totalCount  = ref(0);
const sortBy      = ref('latest');
const placeholder = require('@/assets/doksame3.gif');

/* ─────────── reactive watch ─────────── */
watch(
   () => props.selectedTab,
   async () => {
     page.value = 0;          // 탭 바뀌면 1페이지부터
     await fetchTotalCount(); // 총 개수 다시
     await fetchChannels();   // **목록 다시 로드**
   }
);
watch(sortBy,          () => { allChannels.value = [...allChannels.value]; });

/* ───────────── lifecycle ───────────── */
onMounted(async () => {
  await fetchTotalCount();
  await fetchChannels();
});

/* ────────────── computed ───────────── */
const sortedChannels = computed(() => [...allChannels.value].sort((a,b) => {
  if (sortBy.value === 'latest') {
    return new Date(b.recentVideos?.[0]?.publishedAt || 0) -
           new Date(a.recentVideos?.[0]?.publishedAt || 0);
  }
  return a.channelName.localeCompare(b.channelName);
}));
const maxPage = computed(() => Math.ceil(totalCount.value / size));

/* ─────────────── methods ───────────── */
async function fetchTotalCount() {
  try {
    /* 배포용: Netlify 프록시(또는 nginx) → 같은 도메인 */
    // const { data } = await axios.get(`/api/channels/${props.selectedTab}/totalCount`);
    // 로컬 테스트용: 주석 해제 후 사용
    const { data } = await axios.get(`http://localhost:8080/api/channels/${props.selectedTab}/totalCount`);
    
    totalCount.value = data;
  } catch (e) { console.error(e); }
}

async function fetchChannels() {
  loading.value = true;
  try {
    /* 배포용 */
    // const { data } = await axios.get(`/api/channels/${props.selectedTab}`,
    //                                  { params: { page: page.value, size }});
    // 로컬 테스트용
    const { data } = await axios.get(`http://localhost:8080/api/channels/${props.selectedTab}`,{ params: { page: page.value, size }});
    
    allChannels.value = data;
  } catch (e) { console.error(e); }
  finally      { loading.value = false; }
}

async function prevPage(){ if(page.value>0){ page.value--; await fetchChannels(); scrollTop(); } }
async function nextPage(){ if(page.value<maxPage.value-1){ page.value++; await fetchChannels(); scrollTop(); } }
function scrollTop(){ window.scrollTo({ top:0, behavior:'smooth' }); }
</script>