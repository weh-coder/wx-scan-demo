<template>
  <div>
    <h1>微信公众号扫码登录页面</h1>
    <hr />
    <el-button type="primary" @click="userLogin" v-if="!isShow">登录按钮</el-button>
    <div v-if="isShow">
      <div>
        <p>扫码👉关注【weh程序猿】公众号👉输入"验证码"三个字</p>
      </div>
      <div class="qrCode" v-loading="loadCode" element-loading-text="拼命加载中">
        <img :src="this.qrurl" class="img-qr" alt="">
        <!-- <iframe class="wh100 box" id="frame" src="./qrCode.html" scrolling="no"></iframe> -->
      </div>
      <el-form :model="numberValidateForm" ref="numberValidateForm" label-width="30%" class="demo-ruleForm">
        <el-form-item style="width:30%;margin: auto;" label="验证码：" prop="code" :rules="[
      { required: true, message: '验证码不能为空' },
    ]">
          <el-input v-model.trim="numberValidateForm.code" autocomplete="off" style="width:70%;"></el-input>
          <el-button size="small" type="primary" @click="submitForm('numberValidateForm')"
            style="width:25%;">确定</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script>

// https://blog.csdn.net/helloxiaoliang/article/details/126865344

export default {
  data() {
    return {
      qrurl: "",
      isShow: false,
      loadCode: true,
      numberValidateForm: {
        myid: '',
        code: ''
      }
    }
  },
  created() {
    this.isShow = false;
    this.uuid = "";
    this.qrurl = "";
    sessionStorage.setItem("uuid", "");
  },
  methods: {
    userLogin() {
      this.isShow = true
      this.getQrCode();
    },
    async getQrCode() {
      let url = `${window.baseApi}/conn`
      let res = await this.$axios.post(url);
      if (res) {
        sessionStorage.setItem("uuid", res.data.uuid);
        this.qrurl = res.data.qrurl;
        let timer = setTimeout(() => {
          this.loadCode = false;
          clearTimeout(timer);
        }, 2000)
        // 轮询后台，看用户是否扫码成功
        this.loopFun()
      }
    },
    sleep(dealy) {
      return new Promise((res, rej) => {
        let tm = setTimeout(() => {
          clearTimeout(tm)
          res()
        }, dealy)
      })
    },
    async loopFun() {
      console.log(sessionStorage.getItem("uuid"));
      // 根据本地存储的myid区分用户,在生成二维码的时候存储的，也传给后台了
      let res = await this.$axios.get(`${window.baseApi}/login?myid=${sessionStorage.getItem("uuid")}`)
      console.log(res.data);
      // 如果还没登录，休眠3s再问一次
      if (!res.data.isexit) {
        this.$message({
          message: '二维码已失效',
          type: "error"
        })
        return;
      }
      // 未登录
      if (!res.data.login) {
        // 休眠3s
        await this.sleep(3000)
        this.loopFun()
      } else {
        this.$message({
          message: '登录成功',
          type: "success"
        })
        this.$router.push('/home')
      }
    }
    ,
    async submitForm(formName) {
      let flag = false;
      let myid = sessionStorage.getItem("uuid") || 'a';
      this.numberValidateForm.myid = myid;
      this.$refs[formName].validate((valid) => {
        if (valid) {
          console.log(sessionStorage.getItem("uuid"), this.numberValidateForm.code);
          flag = true;
        }
      });
      if (flag) {
        let res = await this.$axios.post(`${window.baseApi}/checkCode?myid=${this.numberValidateForm.myid}&code=${this.numberValidateForm.code}`);
        if (!res.data.statu) {
          this.$message({
          message: '该验证码已失效！！！',
          type: "error"
        })
        } else {
          sessionStorage.setItem("username", res.data.username);
          console, console.log(res.data.username);

        }
      }
    }
  }
}
</script>
<style>
/* 
    该样式能调整二维码大小，太神奇了
    网友的建议 https://m.656463.com/wenda/rhsyymjxiframesf_600
  */
#frame {
  width: 450px;
  height: 450px;
  position: relative;
  left: -8px;
  top: -8px;
  -webkit-transform-origin: 0 0;
  -webkit-transform: scale(0.5);
}

.img-qr {
  width: 100%;
  height: 100%;
}
</style>

<style scoped>
.wh100 {
  width: 100%;
  height: 100%;
}

.box {
  border: none;
  overflow: hidden;
}

.qrCode {
  border: 1px solid #ddd;
  width: 200px;
  height: 200px;
  margin: 20px auto;
  overflow: hidden;
}
</style>