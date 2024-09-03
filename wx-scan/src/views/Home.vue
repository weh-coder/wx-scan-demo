<template>
  <div class="home">
    
    <h1>【{{ username }}】登录成功，欢迎您！<el-button @click="logout">登出</el-button></h1>
    <hr />
  </div>
</template>

<script>

export default {
  data(){
    return{
      username: sessionStorage.getItem("username")
    }
  },
  methods:{
    async logout(){
      let myid = sessionStorage.getItem('myid') || 'a';
      let rel = await this.$axios.get(`${window.baseApi}/Logout?myid=${myid}`)
      console.log('登出：',rel);
      if(rel && rel.data){
        this.$router.push('/')
      } else {
        // 如果扫码了，跳转页面
        this.$message({
          message: '服务器异常',
          type: "error"
        })
      }
    }
  }
}
</script>
