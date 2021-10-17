import axios from 'axios'

const AXIOS = axios.create({
  baseURL: `/api`,
  timeout: 60000
});

export default {
    hello() {
        return AXIOS.get(`/hello`);
    },
    getUser(userId) {
        return AXIOS.get(`/user/` + userId);
    },
    createUser(formData) {
        return AXIOS.post('/registration', formData)
    },
    getAssignee(userId){
        return AXIOS.get(`/assignee/` + userId);
    },
    getSecured(user, password) {
        return AXIOS.get(`/main`,{
            auth: {
                username: user,
                password: password
            }});
    },
    sendFile(formData) {
        return AXIOS.post('/service', formData, {
            headers: {
                'Content-Type': 'multipart/form-data'
            },
            onUploadProgress: function( progressEvent ) {
                this.uploadPercentage = Math.round( ( progressEvent.loaded / progressEvent.total ) * 100 )
            }.bind(this)
        })
    },
    loadAddresses(jsonData){
        return AXIOS.post('/loadaddresses', jsonData)
    },
    getAuthority(username){
        return AXIOS.get('/authority/' + username)
    }
}


