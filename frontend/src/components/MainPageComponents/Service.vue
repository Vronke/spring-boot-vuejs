<template>
  <div class="service">
    <div class="large-12 medium-12 small-12 cell">
      <label>File
        <input type="file" id="file" ref="file" v-on:change="handleFileUpload()"/>
      </label>
      <b-progress :value="uploadPercentage" :max=100 show-progress animated/>
      <button v-on:click="submitFile()">Submit</button>
    </div>
  </div>
</template>

<script>
  import api from "../backend-api";
  import axios from 'axios'
  import router from "../../router";

  export default {
    name: 'service',

    data(){
      return {
        file: '',
        uploadPercentage: 0
      }
    },
    methods: {
      submitFile(){
        /*
          Initialize the form data
        */
        let formData = new FormData();

        /*
          Add the form data we need to submit
        */
        formData.append('file', this.file);
        formData.append('name', this.$store.getters.getUserName)

        /*
          Make the request to the POST /single-file URL
        */
        axios.post('/api/service',
                formData,
                {
                  headers: {
                    'Content-Type': 'multipart/form-data'
                  },
                  onUploadProgress: function( progressEvent ) {
                    this.uploadPercentage = Math.round( ( progressEvent.loaded / progressEvent.total ) * 100 )
                  }.bind(this)
                }
        ).then(res => console.log(res.data));
      },
      handleFileUpload(){
        this.file = this.$refs.file.files[0];
      }
    }
  }

</script>


<!-- Add "scoped" attribute to limit CSS to this component only -->
<style scoped>
  h1, h2 {
    font-weight: normal;
  }

  ul {
    list-style-type: none;
    padding: 0;
  }

  li {
    display: inline-block;
    margin: 0 10px;
  }

  a {
    color: #42b983;
  }
</style>
