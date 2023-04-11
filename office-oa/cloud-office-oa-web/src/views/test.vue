<template>
  <div>
    <div>账号切换</div>
    <button @click="zhangsan()" type="default" size="mini">张三</button>
    <button @click="lisi()" type="default" size="mini">李四</button>
    <button @click="zzjl()" type="default" size="mini">张总经理</button>
    <button @click="admin()" type="default" size="mini">admin</button>
    <div>当前token：{{ token }}</div>
  </div>
</template>

<script>
export default {
  name: "Test",

  data() {
    return {
      token: ''
    };
  },

  created(){
    this.token = window.localStorage.getItem('token')
  },

  methods: {
    admin() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNjcztjQ10VEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQDsKgvYQgAAAA.ov3NQKuz2Sbd7NklKaSJtY2GHrhcS8RhNl5whbINdWTLv4xUwrAtuTb20G3q_HQPdiGjTrGZCD3VuVrRbA524A'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      // let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNDA0tjQx0lEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSixPzlGoBh6m51UUAAAA.3_JzjboU2EejGDpSmzhmV-0dU7wNn4MyFF9iLN2Ngy1mfYod-hjio1hb2t74JAUKfdAD-kGxNmNLU89-bZjkyA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      // let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNLC0NDCz1FEqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagGSPkblQQAAAA.uhtvIoIYOea6EQzXnmA5QlQEi3x8xJmq9LQ3mgr0ArVOnBxq9LQDj1BS1mwWQxsB47kaqELzIbMBVFkOen5lmw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwNjczMTM20VEqLU4t8kxRsoIy8xJzU4FacjKLM5VqAf7-yStBAAAA.ZfhExF0f-0CM2ANPV95sjmavgJeftUU0MMxfxJW0wOz8dHTzeE4AcDgERIThw_Ax1r9TJagbBGzvohdbzGEGqw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
