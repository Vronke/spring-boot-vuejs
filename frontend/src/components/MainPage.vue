<template>
    <div id="main">
        <div id="nav">
          <router-link to="/callservice" v-show="authority === 'SERVER_ADMIN'">Service</router-link><a v-show="authority === 'SERVER_ADMIN'"> | </a>
          <router-link to="/bootstrap" v-show="authority === 'SERVER_ADMIN'">Bootstrap</router-link><a v-show="authority === 'SERVER_ADMIN'"> | </a>
          <router-link to="/protected" v-show="authority === 'SERVER_ADMIN'">Protected</router-link><a v-show="authority === 'SERVER_ADMIN'"> | </a>
          <router-link to="/loadaddress" v-show="authority === 'SERVER_ADMIN' || authority === 'ANALYST'">Load</router-link><a v-show="authority === 'SERVER_ADMIN' || authority === 'ANALYST'"> | </a>
          <router-link to="/product" v-show="authority === 'USER' || authority === 'SERVER_ADMIN' || authority === 'ANALYST'">Products</router-link>
        </div>
        <router-view></router-view>
    </div>
</template>

<script>
import api from '../components/backend-api'
    export default {
      data() {
        return {
          authority: ''
        }
      },
      name: "MainPage",
      beforeCreate() {
        api.getAuthority(this.$store.getters.getUserName).then(response => {
          this.authority = response.data;
        })
        api.getAssignee(this.$store.getters.getUserName).then(response => {
          this.$store.commit("set_assignee", {assignee: response.data});
        })
      }
    }
</script>

<style scoped>
#nav {
  padding: 3px;
  margin: 0;
}
</style>