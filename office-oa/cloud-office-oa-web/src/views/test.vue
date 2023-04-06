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
      window.localStorage.setItem('token', 'ad');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjIysTCz1FEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQB1aOicQgAAAA.oupIj6DtxUko1csH1YYMh0VBvjhNpBEBYZLXSQOVVXEPk3XHyKthWXeAAF6Kr8biHNi3OzKjRFpAx2IWS2MBcQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zhangsan() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjKyMDI00FEqLU4t8kxRsjKHMPMSc1OBWqoyEvPSixPzlGoBLe2w9UUAAAA.qBRbCi_DQVwhhfZ8IQsJWyq_kpFjxYEyIN9K4MpXClSCa1RpmcC8Zn7Dfse0888S1mOsTI1yKhSk-13cBTKLTQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjIyNze01FEqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagGMuAcTQQAAAA.QoPJp5FTneh-J_7EpwXWhNOS370Rg-km6OfoRJ77xnva3_8foFthRV0hZjmh8DtRzPV_1grEhu8MVWzD6OUVIg'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAA_6tWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjIyNzcx01EqLU4t8kxRsjKBMPMSc1OBWnIyizOVagHflq1wQQAAAA.RUIcwL0CX6qlBwTemdiABL-dgGBJthgOKOC1DQOpsv6iVXBo_xAHYABqdtAzncnHgXP4RHRBXNVq01iHldn1yw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
