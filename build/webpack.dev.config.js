const path = require("path");
const merge = require('webpack-merge')
const commonConfig = require('./webpack.base.config.js')
const webpack = require("webpack");

module.exports = merge(commonConfig, {
    mode: "development",
    entry: ["react-hot-loader/patch"],
    devtool: 'cheap-module-eval-source-map',
    output: {
        // 输出目录
        path: path.resolve(__dirname, "../dist"),
        // 文件名称
        filename: "bundle.js",
        chunkFilename: '[name].js',
        publicPath: '/',
    },
    plugins: [
        //开启HMR(热替换功能,替换更新部分,不重载页面！) 相当于在命令行加 --hot
        new webpack.HotModuleReplacementPlugin(),
        new webpack.DefinePlugin({
            'process.env': {
                NODE_ENV: '"development"'
            }
        })
    ],
    devServer: {
        hot: true,
        contentBase: path.resolve(__dirname, "../dist"),
        host: "www.common.com", // 可以使用手机访问
        port: 3000,
        historyApiFallback: true, //  该选项的作用所有的404都连接到index.html
        proxy: {
            // 代理到后端的服务地址
            "/api": {
                changeOrigin: true,            
                target: "http://192.168.3.184:8091",//http://192.168.7.245:8091 //海洋 http://192.168.3.184:8091 //魏群  http://192.168.4.42:8091 //192.168.3.7:8091 //
                pathRewrite: {'^/api' : ''},
                logLevel: 'debug',
            },
            "/bbApi": {
                changeOrigin: true,            
                target: "http://192.168.7.242:8081",//http://192.168.7.242:8081; //海洋 http://192.168.3.184:8091 //魏群  http://192.168.4.42:8091 //192.168.3.7:8091 //
                pathRewrite: {'^/bbApi' : ''},
                logLevel: 'debug',
            },
            "/transApi": {
                changeOrigin: true,            
                target: "http://192.168.7.242:8080",//http://192.168.2.35:8011; //海洋 http://192.168.3.184:8091 //魏群  http://192.168.4.42:8091 //192.168.3.7:8091 //
                pathRewrite: {'^/transApi' : ''},
                logLevel: 'debug',
            }
        }
    }
});
