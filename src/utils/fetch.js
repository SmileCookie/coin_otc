import React from 'react'
import axios from 'axios'
import cookie from 'js-cookie'
import { Redirect } from 'react-router-dom'
import history from './history'
import { message } from 'antd'
// axios 配置
axios.defaults.timeout = 60000;
// http request 拦截器
axios.interceptors.request.use(
    config => {
        if (cookie.get("token")) {  // 判断是否存在token，如果存在的话，则每个http header都加上token
            config.headers.token = cookie.get("token");
        }
        return config;
    },
    err => {
        message.error('请求超时！');
        return Promise.reject(err);
    });

// http response 拦截器
axios.interceptors.response.use(
    response => {

        if (response.data) {
            switch (response.data.code) {
                case 401:
                    cookie.remove("token")
                    // window.location.href="#/login"
                    history.push('/login')
            }
        }
        let res = JSON.parse(JSON.stringify(response).replace(/"0E-9"/g, () => "0"));
        return res;
    },
    error => {
        if (error.response) {
            switch (error.response.status) {
                case 401:
                    // 返回 401 清除token信息并跳转到登录页面
                    //     store.commit(types.LOGOUT);
                    //     router.replace({
                    //         path: 'login',
                    //         query: {redirect: router.currentRoute.fullPath}
                    // })
                    cookie.remove("token")
                    window.location.href = '/';
                    break;
                case 404:
                    const err = error.response.data
                    message.error('接口：' + err.path + '，状态码：' + err.status + '，结果：' + err.error)
                    break;
                default:
                    break;
            }
        }
        return Promise.reject(error)   // 返回接口返回的错误信息
    });


export default axios;


