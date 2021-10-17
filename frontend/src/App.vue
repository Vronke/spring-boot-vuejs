<template>
  <div id="app">
    <router-view/>
  </div>
</template>

<script>
import {addHandler} from "@/newProductWS";

export default {
  name: 'login',
  data () {
    return {
      loginError: false,
      user: '',
      password: '',
      error: false,
      errors: []
    }
  },
  methods: {
    callLogin() {
      this.errors = [];
      this.$store.dispatch("login", { user: this.user, password: this.password})
          .then(() => {
            this.$router.push('/main')
          })
          .catch(error => {
            this.loginError = true;
            this.errors.push(error);
            this.error = true
          })
    }
  }

}
</script>

<style lang="scss">
#app {
  text-align: center;
  color: #2c3e50;
  background-color: WhiteSmoke;
  padding: 5px;
  height: 100vh;
}
#nav {
  a {
    font-weight: bold;
    color: #2c3e50;
    &.router-link-exact-active {
      color: #42b983;
    }
  }
}
</style>