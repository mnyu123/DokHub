<template>
  <div>
    <!-- 로딩 -->
    <div v-if="loading" class="flex flex-col items-center my-10">
      <span class="loading loading-spinner loading-lg"></span>
      <p class="mt-4">채널 목록을 불러오는 중입니다…</p>
    </div>

    <div v-else>
      <!-- 정렬 버튼 -->
      <div class="flex justify-between items-center mb-4">
        <p class="font-bold">총 채널 개수 : {{ totalCount }}</p>
        <div class="join">
          <button class="btn join-item"
                  :class="sortBy==='latest' && 'btn-primary'"
                  @click="sortBy='latest'">최신순</button>
          <button class="btn join-item"
                  :class="sortBy==='name' && 'btn-primary'"
                  @click="sortBy='name'">이름순</button>
          <button class="btn join-item"
                  :class="sortBy==='random' && 'btn-primary'"
                  @click="sortBy='random'">랜덤</button>
        </div>
      </div>

      <!-- 채널 카드 -->
      <div v-for="c in displayedChannels" :key="c.channelId"
           class="card lg:card-side bg-base-100 shadow-xl mb-6 animate-fadeIn">
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
              <a v-for="v in c.recentVideos" :key="v.videoId"
                 :href="`https://youtu.be/${v.videoId}`" target="_blank">
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

const allChannels = ref([]);
const totalCount  = ref(0);
const loading     = ref(true);
const page        = ref(0);
const size        = 7;
const sortBy      = ref('latest');

const displayedChannels = computed(() => {
  const list = [...allChannels.value];
  if (sortBy.value === 'latest') {
    return list.sort((a, b) =>
      new Date(b.recentVideos[0]?.publishedAt || 0) -
      new Date(a.recentVideos[0]?.publishedAt || 0)
    );
  }
  if (sortBy.value === 'name') {
    return list.sort((a, b) =>
      a.channelName.localeCompare(b.channelName)
    );
  }
  if (sortBy.value === 'random') {
    return list.sort(() => Math.random() - 0.5);
  }
  return list;
});
const maxPage = computed(() => Math.ceil(totalCount.value / size));

async function fetchTotalCount() {
  try {
    const { data } = await axios.get(
      `/api/channels/${props.selectedTab}/totalCount`
    );
    totalCount.value = data;
  } catch (e) {
    console.error('fetchTotalCount error:', e);
  }
}

async function fetchChannels() {
  loading.value = true;
  try {
    const { data } = await axios.get(
      `/api/channels/${props.selectedTab}`,
      { params: { page: page.value, size } }
    );
    allChannels.value = data;
  } catch (e) {
    console.error('fetchChannels error:', e);
  } finally {
    loading.value = false;
  }
}

function getHighRes(url) {
  return url.replace(/default\.jpg$/, 'maxresdefault.jpg');
}

async function loadData() {
  // 두 호출을 병렬로 실행
  await Promise.all([fetchTotalCount(), fetchChannels()]);
}

// 탭 변경 시
watch(
  () => props.selectedTab,
  async () => {
    page.value = 0;
    await loadData();
  }
);

// 마운트 시
onMounted(loadData);

function prevPage() {
  if (page.value > 0) {
    page.value--;
    fetchChannels();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}

function nextPage() {
  if (page.value < maxPage.value - 1) {
    page.value++;
    fetchChannels();
    window.scrollTo({ top: 0, behavior: 'smooth' });
  }
}
</script>