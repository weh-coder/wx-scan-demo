<template>
  <div class="home">
    <img alt="Vue logo" src="../assets/logo.png">
    <Login></Login>
    
  </div>
</template>

<script>
import Login from '@/components/Login.vue'

export default {
  name: 'HomeView',
  components: {
    Login
  }
}
</script>
<style scoped>
img{
  width: 20%;
}
</style>

