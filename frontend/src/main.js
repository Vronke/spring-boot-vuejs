import Vue from 'vue'
import App from './App.vue'
import router from './router'
import BootstrapVue from 'bootstrap-vue'
import 'bootstrap/dist/css/bootstrap.css'
import 'bootstrap-vue/dist/bootstrap-vue.css'
import store from './store'
import vuetify from './plugins/vuetify';
import VueColumnsResizable from './plugins/vue-columns-resizable';
import './assets/styles.scss'

Vue.config.productionTip = false;
var infiniteScroll =  require('vue-infinite-scroll');


// Bootstrap
Vue.use(BootstrapVue);
Vue.use(VueColumnsResizable);
Vue.use(infiniteScroll);

Vue.config.keyCodes = {
    "end": 2
}


var app = new Vue({
    router,
    store,
    vuetify,
    render: h => h(App)
}).$mount('#app');

