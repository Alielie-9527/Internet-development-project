<template>
  <div id="app">
    <router-view />
  </div>
</template>

<script setup>
// Store已经在创建时自动从localStorage初始化，不需要额外调用
</script>

<style>
#app {
  width: 100%;
  height: 100%;
  background-color: #f7f8fa;
}
</style>

