<template>
  <div class="alert" :class="liveStatus.liveOn ? 'alert-success' : 'alert-error'">
    <span aria-hidden="true">{{ liveStatus.liveOn ? '📡' : '●' }}</span>
    <span>{{ liveStatus.liveOn ? '방송 중 (On Air)' : '지금은 오프라인입니다' }}</span>
  </div>
</template>

<script setup>
import { liveStatus } from '@/state/liveStatus'
</script>
