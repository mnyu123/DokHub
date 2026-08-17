<template>
  <section class="click-insight" aria-labelledby="click-insight-title">
    <div class="click-insight__header">
      <div>
        <p class="click-insight__eyebrow">VIEWER SIGNAL</p>
        <h2 id="click-insight-title">클릭 인사이트</h2>
      </div>
      <span class="click-insight__status">30분마다 갱신</span>
    </div>

    <div v-if="loading && !analytics" class="click-insight__loading">
      <span class="loading loading-spinner loading-sm"></span>
      <span>시청 선택 데이터를 정리하는 중입니다.</span>
    </div>

    <div v-else-if="error && !analytics" class="click-insight__empty">
      클릭 통계를 불러오지 못했습니다. 잠시 후 자동으로 다시 시도합니다.
    </div>

    <template v-else-if="analytics">
      <p class="click-insight__summary">{{ analytics.summary }}</p>

      <div class="click-insight__metrics">
        <div>
          <strong>{{ formatNumber(analytics.totalClicks) }}</strong>
          <span>전체 클릭</span>
        </div>
        <div>
          <strong>{{ formatNumber(analytics.uniqueChannels) }}</strong>
          <span>선택된 채널</span>
        </div>
        <div>
          <strong>{{ formatNumber(analytics.uniqueVideos) }}</strong>
          <span>선택된 영상</span>
        </div>
      </div>

      <div v-if="hasRankings" class="click-insight__rankings">
        <RankingBars title="많이 선택한 채널" :items="analytics.topChannels" />
        <RankingBars title="많이 선택한 영상" :items="analytics.topVideos" />
      </div>

      <div v-if="hasDailyClicks" class="click-insight__trend">
        <div class="click-insight__trend-head">
          <h3>최근 클릭 흐름</h3>
          <span>최근 {{ visibleDailyClicks.length }}일</span>
        </div>
        <div class="click-insight__columns" role="img" aria-label="날짜별 영상 클릭 수 막대 그래프">
          <div
            v-for="item in visibleDailyClicks"
            :key="item.date"
            class="click-insight__column-wrap"
            :title="`${formatDate(item.date)} · ${item.clickCount}회`"
          >
            <span class="click-insight__column-count">{{ item.clickCount || '' }}</span>
            <div
              class="click-insight__column"
              :class="{ 'is-empty': item.clickCount === 0 }"
              :style="{ height: `${dailyHeight(item.clickCount)}%` }"
            ></div>
            <span class="click-insight__column-label">{{ shortDate(item.date) }}</span>
          </div>
        </div>
      </div>

      <p class="click-insight__updated">
        {{ formatUpdatedAt(analytics.generatedAt) }} 기준 · 최근 {{ analytics.periodDays }}일 집계
      </p>
    </template>
  </section>
</template>

<script setup>
import { computed, defineComponent, h, onBeforeUnmount, onMounted, ref } from 'vue'
import api from '@/api'

const REFRESH_INTERVAL_MS = 30 * 60 * 1000
const analytics = ref(null)
const loading = ref(true)
const error = ref(false)
let refreshTimer

const hasRankings = computed(() =>
  Boolean(analytics.value?.topChannels?.length || analytics.value?.topVideos?.length)
)
const visibleDailyClicks = computed(() => analytics.value?.dailyClicks?.slice(-14) || [])
const hasDailyClicks = computed(() => visibleDailyClicks.value.some(item => item.clickCount > 0))
const maxDailyClicks = computed(() => Math.max(1, ...visibleDailyClicks.value.map(item => item.clickCount)))

const RankingBars = defineComponent({
  name: 'RankingBars',
  props: {
    title: { type: String, required: true },
    items: { type: Array, default: () => [] }
  },
  setup(props) {
    const max = computed(() => Math.max(1, ...props.items.map(item => item.clickCount)))
    return () => h('div', { class: 'click-insight__ranking' }, [
      h('h3', props.title),
      props.items.length
        ? h('div', { class: 'click-insight__bar-list' }, props.items.map((item, index) =>
            h('div', { class: 'click-insight__bar-item', key: item.id || `${item.label}-${index}` }, [
              h('div', { class: 'click-insight__bar-meta' }, [
                h('span', { title: item.label }, item.label),
                h('strong', `${item.clickCount}회`)
              ]),
              h('div', { class: 'click-insight__bar-track' }, [
                h('div', {
                  class: 'click-insight__bar-fill',
                  style: { width: `${Math.max(4, (item.clickCount / max.value) * 100)}%` }
                })
              ])
            ])
          ))
        : h('p', { class: 'click-insight__ranking-empty' }, '아직 표시할 클릭이 없습니다.')
    ])
  }
})

async function fetchAnalytics() {
  error.value = false
  try {
    const { data } = await api.get('/api/analytics/clicks', {
      params: { periodDays: 30, limit: 5 }
    })
    analytics.value = data
  } catch (requestError) {
    console.error('fetchClickAnalytics error:', requestError)
    error.value = true
  } finally {
    loading.value = false
  }
}

function dailyHeight(clickCount) {
  if (!clickCount) return 4
  return Math.max(12, (clickCount / maxDailyClicks.value) * 100)
}

function formatNumber(value) {
  return new Intl.NumberFormat('ko-KR').format(Number(value) || 0)
}

function shortDate(date) {
  const [, month, day] = date.split('-')
  return `${Number(month)}/${Number(day)}`
}

function formatDate(date) {
  return new Date(`${date}T00:00:00`).toLocaleDateString('ko-KR')
}

function formatUpdatedAt(value) {
  if (!value) return '갱신 시각 없음'
  return new Date(value).toLocaleString('ko-KR', {
    month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'
  })
}

onMounted(() => {
  fetchAnalytics()
  refreshTimer = window.setInterval(fetchAnalytics, REFRESH_INTERVAL_MS)
})

onBeforeUnmount(() => window.clearInterval(refreshTimer))
</script>

<style>
.click-insight {
  position: relative;
  overflow: hidden;
  padding: 1.5rem;
  border: 1px solid rgba(148, 163, 184, 0.18);
  border-radius: 1.25rem;
  background:
    linear-gradient(140deg, rgba(18, 24, 38, 0.96), rgba(13, 17, 28, 0.92)),
    radial-gradient(circle at top right, rgba(185, 28, 28, 0.18), transparent 38%);
  box-shadow: 0 20px 55px rgba(0, 0, 0, 0.2);
}

.click-insight::after {
  content: '';
  position: absolute;
  inset: 0 0 auto;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(248, 113, 113, 0.7), transparent);
}

.click-insight__header,
.click-insight__trend-head,
.click-insight__bar-meta {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 1rem;
}

.click-insight__eyebrow {
  margin-bottom: 0.25rem;
  color: #f87171;
  font-size: 0.68rem;
  font-weight: 700;
  letter-spacing: 0.2em;
}

.click-insight h2 {
  font-size: 1.4rem;
  font-weight: 720;
  letter-spacing: -0.03em;
}

.click-insight__status {
  padding: 0.35rem 0.7rem;
  border: 1px solid rgba(248, 113, 113, 0.28);
  border-radius: 999px;
  color: #fca5a5;
  background: rgba(127, 29, 29, 0.16);
  font-size: 0.72rem;
}

.click-insight__loading,
.click-insight__empty {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  min-height: 7rem;
  color: rgba(255, 255, 255, 0.58);
  font-size: 0.9rem;
}

.click-insight__summary {
  max-width: 58rem;
  margin-top: 1rem;
  color: rgba(255, 255, 255, 0.72);
  font-size: 0.9rem;
  line-height: 1.8;
}

.click-insight__metrics {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 0.75rem;
  margin-top: 1.25rem;
}

.click-insight__metrics > div {
  display: flex;
  flex-direction: column;
  gap: 0.2rem;
  padding: 0.9rem 1rem;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 0.85rem;
  background: rgba(255, 255, 255, 0.035);
}

.click-insight__metrics strong {
  font-size: 1.45rem;
  font-variant-numeric: tabular-nums;
}

.click-insight__metrics span,
.click-insight__updated,
.click-insight__trend-head span {
  color: rgba(255, 255, 255, 0.45);
  font-size: 0.72rem;
}

.click-insight__rankings {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 1.25rem;
  margin-top: 1.5rem;
}

.click-insight__ranking,
.click-insight__trend {
  padding: 1rem;
  border: 1px solid rgba(148, 163, 184, 0.14);
  border-radius: 1rem;
  background: rgba(2, 6, 23, 0.28);
}

.click-insight h3 {
  margin-bottom: 0.9rem;
  color: rgba(255, 255, 255, 0.88);
  font-size: 0.86rem;
  font-weight: 650;
}

.click-insight__bar-list {
  display: flex;
  flex-direction: column;
  gap: 0.8rem;
}

.click-insight__bar-meta {
  margin-bottom: 0.35rem;
  font-size: 0.75rem;
}

.click-insight__bar-meta span {
  overflow: hidden;
  color: rgba(255, 255, 255, 0.67);
  text-overflow: ellipsis;
  white-space: nowrap;
}

.click-insight__bar-meta strong {
  flex: none;
  color: rgba(255, 255, 255, 0.82);
  font-variant-numeric: tabular-nums;
}

.click-insight__bar-track {
  overflow: hidden;
  height: 0.35rem;
  border-radius: 999px;
  background: rgba(148, 163, 184, 0.12);
}

.click-insight__bar-fill {
  height: 100%;
  border-radius: inherit;
  background: linear-gradient(90deg, #991b1b, #f87171);
}

.click-insight__trend {
  margin-top: 1.25rem;
}

.click-insight__trend-head h3 {
  margin-bottom: 0;
}

.click-insight__columns {
  display: grid;
  grid-template-columns: repeat(14, minmax(0, 1fr));
  align-items: end;
  gap: 0.35rem;
  height: 9rem;
  margin-top: 1rem;
}

.click-insight__column-wrap {
  display: grid;
  grid-template-rows: 1rem 1fr 1rem;
  align-items: end;
  height: 100%;
  min-width: 0;
  text-align: center;
}

.click-insight__column-count,
.click-insight__column-label {
  color: rgba(255, 255, 255, 0.42);
  font-size: 0.6rem;
  font-variant-numeric: tabular-nums;
}

.click-insight__column {
  width: min(1rem, 70%);
  min-height: 3px;
  margin: 0 auto;
  border-radius: 0.3rem 0.3rem 0 0;
  background: linear-gradient(180deg, #f87171, #7f1d1d);
}

.click-insight__column.is-empty {
  background: rgba(148, 163, 184, 0.14);
}

.click-insight__updated {
  margin-top: 0.9rem;
  text-align: right;
}

.click-insight__ranking-empty {
  color: rgba(255, 255, 255, 0.42);
  font-size: 0.78rem;
}

@media (max-width: 767px) {
  .click-insight {
    padding: 1.1rem;
    border-radius: 1rem;
  }

  .click-insight__rankings {
    grid-template-columns: 1fr;
  }

  .click-insight__columns {
    gap: 0.2rem;
  }

  .click-insight__column-label {
    transform: rotate(-50deg);
    transform-origin: center;
    white-space: nowrap;
  }
}
</style>
