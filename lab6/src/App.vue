<script setup lang="ts">
import { computed } from "vue";
import { useRoute } from "vue-router";
import MainLayout from "@/layouts/main-layout.vue";

const route = useRoute();
const layout = computed(() => (
  route.meta.layout || MainLayout
));
</script>

<template>
  <component :is="layout" >
    <RouterView/>
  </component>
</template>

