<template>
  <div class="tabs flex-nowrap justify-start sm:justify-center overflow-x-auto my-2 sm:my-4 pb-1 mobile-tabs" aria-label="영상 분류">
    <button type="button" class="tab" :class="active('clip')"     @click="$emit('update:tab','clip')">클립</button>
    <button type="button" class="tab" :class="active('stclip')"   @click="$emit('update:tab','stclip')">클립(중단)</button>
    <button type="button" class="tab" :class="active('replay')"   @click="$emit('update:tab','replay')">리플레이</button>
    <button type="button" class="tab" :class="active('song')"     @click="$emit('update:tab','song')">노래</button>
    <button type="button" class="tab" :class="active('main')"     @click="$emit('update:tab','main')">본채널</button>
  </div>
</template>

<script setup>
const props = defineProps({ selectedTab:String });
const active = t => props.selectedTab===t ? 'tab-active' : '';
</script>
<style scoped>
@media (max-width: 639px) {
  .mobile-tabs .tab {
    flex: none;
    padding: 0 0.65rem;
    font-size: 0.82rem;
    white-space: nowrap;
  }
}
</style>
