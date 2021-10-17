<template>
    <div class="registration">
        <router-link to="/">Login</router-link>
        <h1>Create User</h1>

        <input class="reglogInput" type="text" v-model="user.username" placeholder="Логин">
      <input class="reglogInput" type="password" v-model="user.password" placeholder="Пароль">
      <input class="reglogInput" type="text" v-model="user.assignee" placeholder="Пользователь">

        <button @click="createNewUser()">Create User</button>

        <div v-if="showResponse"><h6>User created with Id: {{ response }}</h6></div>

        <button v-if="showResponse" @click="retrieveUser()">Retrieve user {{user.id}} data from database</button>

        <h4 v-if="showRetrievedUser">Retrieved User {{retrievedUser.username}} {{retrievedUser.password}}</h4>

    </div>
</template>

<script>
    import api from "./backend-api";

    export default {
      name: 'user',

      data() {
        return {
          response: [],
          errors: [],
          user: {
            username: '',
            password: '',
            assignee: '',
            id: 0
          },
          showResponse: false,
          retrievedUser: {},
          showRetrievedUser: false
        }
      },
      methods: {
        // Fetches posts when the component is created.
        createNewUser() {
          let formData = new FormData();
          formData.append('password', this.user.password);
          formData.append('username', this.user.username);
          formData.append('assignee', this.user.assignee)

          api.createUser(formData).then(response => {
            // JSON responses are automatically parsed.
            this.response = response.data;
            this.user.id = response.data;
            console.log('Created new User with Id ' + response.data);
            this.showResponse = true
          })
              .catch(e => {
                this.errors.push(e)
              })
        },
        retrieveUser() {
          api.getUser(this.user.id).then(response => {
            // JSON responses are automatically parsed.
            this.retrievedUser = response.data;
            this.showRetrievedUser = true
          })
              .catch(e => {
                this.errors.push(e)
              })
        }
      }
    }

</script>


<!-- Add "scoped" attribute to limit CSS to this component only -->
<style>
    .reglogInput {
      border: 1px solid black;
      border-radius: 3px;
      margin: 10px;
    }
    h1, h2 {
        font-weight: normal;
    }

    ul {
        list-style-type: none;
    }

    a {
        color: #42b983;
    }
</style>
