<template>
  <div class="my-6">
    <div v-if="loading" class="card bg-base-100 shadow-md">
      <div class="card-body">
        <div class="flex items-center gap-3">
          <span class="loading loading-spinner loading-md"></span>
          <span class="text-sm opacity-70">AI 채널 활동 요약을 불러오는 중입니다...</span>
        </div>
      </div>
    </div>

    <div v-else-if="error" class="card bg-base-100 shadow-md border border-error/30">
      <div class="card-body">
        <h2 class="card-title text-lg">AI 채널 활동 요약</h2>
        <p class="text-sm opacity-70">
          AI 요약을 불러오지 못했습니다. 잠시 후 다시 시도해 주세요.
        </p>
      </div>
    </div>

    <div v-else-if="summaryData" class="card bg-base-100 shadow-md">
      <div class="card-body">
        <div class="flex items-center justify-between gap-3 flex-wrap">
          <h2 class="card-title text-lg">
            AI 채널 활동 요약
          </h2>
          <span class="badge badge-outline">
            최근 {{ summaryData.periodDays }}일
          </span>
        </div>

        <p class="text-sm leading-7 whitespace-pre-line mt-2">
          {{ summaryData.overallSummary }}
        </p>

        <div
          v-if="summaryData.stats"
          class="mt-4 flex flex-wrap gap-2 text-xs opacity-80"
        >
          <span class="badge badge-ghost">
            총 채널 {{ summaryData.stats.totalChannels }}개
          </span>
          <span class="badge badge-ghost">
            활동 중 {{ summaryData.stats.activeChannels }}개
          </span>
          <span class="badge badge-ghost">
            비활성 {{ summaryData.stats.inactiveChannels }}개
          </span>
          <span class="badge badge-ghost">
            영상 {{ summaryData.stats.totalVideos }}개
          </span>
        </div>

        <div
          v-if="summaryData.topActiveChannels && summaryData.topActiveChannels.length"
          class="mt-5"
        >
          <h3 class="font-semibold text-sm mb-2">가장 활발한 채널</h3>
          <div class="flex flex-col gap-2">
            <div
              v-for="(item, index) in summaryData.topActiveChannels"
              :key="`${item.channelName}-${index}`"
              class="rounded-lg border border-base-300 px-3 py-2 text-sm"
            >
              <div class="font-medium">{{ item.channelName }}</div>
              <div class="text-xs opacity-70 mt-1">
                최근 {{ summaryData.periodDays }}일 업로드 {{ item.videoCount }}개
                <span v-if="item.daysSinceLastUpload >= 0">
                  · 마지막 업로드 {{ item.daysSinceLastUpload }}일 전
                </span>
              </div>
            </div>
          </div>
        </div>

        <div
          v-if="summaryData.recentlyInactiveChannels && summaryData.recentlyInactiveChannels.length"
          class="mt-5"
        >
          <h3 class="font-semibold text-sm mb-2">최근 조용한 채널</h3>
          <div class="flex flex-col gap-2">
            <div
              v-for="(item, index) in summaryData.recentlyInactiveChannels"
              :key="`${item.channelName}-${index}`"
              class="rounded-lg border border-base-300 px-3 py-2 text-sm"
            >
              <div class="font-medium">{{ item.channelName }}</div>
              <div class="text-xs opacity-70 mt-1">
                최근 {{ summaryData.periodDays }}일 업로드 {{ item.videoCount }}개
                <span v-if="item.daysSinceLastUpload >= 0">
                  · 마지막 업로드 {{ item.daysSinceLastUpload }}일 전
                </span>
              </div>
            </div>
          </div>
        </div>

        <div class="mt-4 text-[11px] opacity-50">
          현재는 통계 기반 요약 1차 버전이며, 이후 AI 추천 에이전트의 기초 데이터로 확장될 예정입니다.
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue'
import api from '@/api'

const props = defineProps({
  category: {
    type: String,
    default: 'clip'
  }
})

const loading = ref(true)
const error = ref(false)
const summaryData = ref(null)

async function fetchSummary() {
  loading.value = true
  error.value = false

  try {
    const url = '/api/ai/channel/summary'
    const { data } = await api.get(url, {
      params: {
        category: props.category,
        periodDays: 14
      }
    })
    summaryData.value = data
  } catch (e) {
    console.error('fetchSummary error:', e)
    error.value = true
    summaryData.value = null
  } finally {
    loading.value = false
  }
}

onMounted(fetchSummary)
watch(() => props.category, fetchSummary)
</script>
