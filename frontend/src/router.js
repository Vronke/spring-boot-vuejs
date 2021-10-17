import Vue from 'vue'
import Router from 'vue-router'
import Service from '@/components/MainPageComponents/Service'
import Bootstrap from '@/components/MainPageComponents/Bootstrap'
import Protected from '@/components/MainPageComponents/Protected'
import LoadAddress from '@/components/MainPageComponents/LoadAddress'
import Product from "@/components/ProductPageComponets/Product";

import Registration from "@/components/Registration";
import store from './store'
import MainPage from '@/components/MainPage';
import Login from "./components/Login";

Vue.use(Router);

const router = new Router({
    routes: [
        { path: '/', component: Login },
        { path: '/registration', component: Registration},
        {
            path: '/main',
            component: MainPage,            children: [
                {
                    path: '/callservice',
                    component: Service
                },
                {
                    path: '/protected',
                    component: Protected
                },
                {
                    path: '/bootstrap',
                    component: Bootstrap
                },
                {
                    path: '/loadaddress',
                    component: LoadAddress
                },
                {
                    path: '/product',
                    component: Product
                }
            ],
            meta: {
                requiresAuth: true
            },
        },

        // otherwise redirect to home
        { path: '*', redirect: '/' }
    ]
});

router.beforeEach((to, from, next) => {
    if (to.matched.some(record => record.meta.requiresAuth)) {
        // this route requires auth, check if logged in
        // if not, redirect to login page.
        if (!store.getters.isLoggedIn) {
            next({
                path: '/'
            })
        } else {
            next();
        }
    } else {
        next(); // make sure to always call next()!
    }
});

export default router;