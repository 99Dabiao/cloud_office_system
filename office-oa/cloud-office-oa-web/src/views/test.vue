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
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjI1Nzcw0lEqLU4t8kwBikGYeYm5qUAtiSm5mXlKtQC3E0PXQgAAAA.fwAw9yPArjlDvC9lU8t-hgUXKBjlmjkejvc8k5xWl4QekpcsLOZ4Gtc3EDX7O9TsWqW0jt_CqfZiF-YcZMnIyA'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    dabiao() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjIzMbE01VEqLU4t8kwBihlD2HmJualAPS6JSZmJ-Uq1ADsCIeBEAAAA.uRKSBNk91HMKxE5MmnkiwoviFH1Yu4FQLtFQ3zPu8vqAjXbPdnRktshxd3Xd-YDJQIDqtEauEWUzq857kIBVfQ'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    zzjl() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjIxNzCx0FEqLU4t8kxRsjKFMPMSc1OBWqqqsnKUagF3x7rRQQAAAA.KyxH7K4WEPRoPB9TOTEECxzCLD36P30qVNlmvhmXu3k81ojtQbVeNZwWGy3uclwhDPYVDO8CdCYogbfoWJj-ig'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    },

    lisi() {
      window.localStorage.setItem('token', '');
      let token = 'eyJhbGciOiJIUzUxMiIsInppcCI6IkdaSVAifQ.H4sIAAAAAAAAAKtWKi5NUrJScgwN8dANDXYNUtJRSq0oULIyNLMwMjI1t7Q00FEqLU4t8kxRsjKBMPMSc1OBWnIyizOVagGlQTrsQQAAAA.bdciciJChEeYzaMZtdW5a8xfY0bi7bl3nCNFEX4rxfM7a_gEB_SexFY7teYqusAmfPSSDOA1VYYjPBbEqnNtfw'
      window.localStorage.setItem('token', token);
      this.token = window.localStorage.getItem('token')
    }
  }
}
</script>
