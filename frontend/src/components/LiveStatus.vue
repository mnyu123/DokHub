<template>
  <div class="alert flex min-h-0 flex-row items-center gap-2 rounded-xl px-4 py-3 text-sm sm:text-base" :class="liveStatus.liveOn ? 'alert-success' : 'alert-error'">
    <span class="shrink-0" aria-hidden="true">{{ liveStatus.liveOn ? '📡' : '●' }}</span>
    <span>{{ liveStatus.liveOn ? '방송 중 (On Air)' : '지금은 오프라인입니다' }}</span>
  </div>
</template>

<script setup>
import { liveStatus } from '@/state/liveStatus'
</script>
