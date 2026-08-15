<template>
  <div class="tabs justify-center my-4">
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
