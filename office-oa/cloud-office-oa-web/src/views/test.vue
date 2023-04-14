<template>
  <div>
    <div>账号切换</div>
    <button @click="dabiao()" type="default" size="mini">大彪</button>
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
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMTAGQgMdpdLi1CLPFKAYhJmXmJsK1JKYkpuZp1QLAIzoR_ZCAAAA.Ld4M0eT8VI9iL-PsHEOIes4zBfQsGkzfdA78cO37USGQfYESQxdEE2FJkFc-k6FKzA0i-P6C5CTETO-CI2E58Q'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    dabiao() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMTA2sLA00FEqLU4t8kwBihlD2HmJualAPSmJSZmJ-Uq1AG68LJdEAAAA.kU_5_px7oYGuswBlvaDVKuc_MXEtCFxrdfW6kEqeuK53Aj6n_Uh_LS0tuA7QL3SzmO7BOvKWhv8_t6tNYL5yhg'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwtjSzNDW31FEqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagF9aekPQQAAAA.8DJTOCDJg6RcbZgXR0dfTlmyajHKQSpNIOBtAuRMtKK3hFYjwx4aJ_ZroMvYDhmlUOXN0Lahv4uLYDQJcguMQw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwtjQ3NjG30FEqLU4t8kxRsjKBMPMSc1OBWnIyizOVagFmxnfyQQAAAA.IBvO_xMFNA3RaqrEfPcqxGJV4dU6OePt_FhyPQEWmTpw0I1PyXm9n0sg07C5gRppfHZ7qGuMG2k1w9l45q3ynw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
