
### 一、运行
```
npm install
npm run start
```
### 二、目录结构
```
.
├── _src
|   ├── assets//公共目录，存放公共css,字体，图片
|   |  ├──css
|   |  |  ├──app.css
|   |  |  ├──bootstrap.css
|   |  |  ├──bootstrap.min.css
|   |  |  ├──bootstrap-theme.css
|   |  |  ├──bootstrap-theme.min.css
|   |  |  ├──custom.css
|   |  |  └──custom.min.css
|   |  ├──fonts
|   |  └──images
|   ├──components
|   |  └──overlays
|   |  |  └──index.js
|   ├──conf
|   |  └──index.js
|   ├──pages
|   |  ├──balancecenter
|   |  ├──common
|   |  ├──deskcenter
|   |  ├──financialcenter
|   |  ├──home
|   |  ├──login
|   |  ├──monitorcenter
|   |  ├──reportcenter
|   |  ├──systemcenter
|   |  ├──tradecenter
|   |  ├──app.js
|   |  └──component.js
|   ├──utils
|   |  ├──upload
|   |  |  ├──moxie.js
|   |  |  ├──Moxie.swf
|   |  |  ├──Moxie.xap
|   |  |  ├──plupload.full.min.js
|   |  |  └──qiniu.js
|   |  ├──fetch.js
|   |  ├──history.js
|   |  └──index.js
|   ├──index.html
|   ├──index.js
|   └──routes.js
├── npm-debug.log
├──package.json
├──package-lock.json
├──server.js
└── webpack.config.js

```
### 三、webpack配置
```
const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const CleanPlugin = require("clean-webpack-plugin");
module.exports = {
    entry: {
        polyfill:['babel-polyfill'],
        vendor: ['react', 'react-dom', 'react-router'],
        app: './src/index'
    },
    output: {
        path: path.resolve(__dirname, 'build'),
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        publicPath: '/'
    },
    resolve: {
        extensions: ['.js', '.css', '.less']
    },
    devServer: {
        inline: true,
        historyApiFallback: true,
        proxy: [//服务代理
            {
                context: ["/capitalCount", "/withdraw","/recharge","/authenLog", "/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news","/brush","/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/common"],
                target:"http://192.168.3.20:8081",
                secure: false,
                changeOrigin: true
            }
            {
                context: [],
                target:"http://www.bitadmin.com/",
                secure: false,
                changeOrigin: true
            }
        ] 
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['es2015', 'react'],
                        plugins: [
                            ["import", { "libraryName": "antd", "libraryDirectory": "es", "style": "css" }] // `style: true` 会加载 less 文件
                          ]
                    }
                }
            },
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader']
                })
            },
            {
                test: /\.less$/,
                exclude: /node_modules/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader', 'less-loader']
                })
            },
            {
                test: /\.(jpg|png|gif)$/,
                exclude: /node_modules/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 8192
                    }
                }
            },
            {
                test: /\.(woff2|woff|eot|svg|ttf|otf)(\?.*)?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 80000
                    }
                }
            }
        ]
    },
    plugins: [
        new CleanPlugin(['build']),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['polyfill', 'vendor']
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './src/index.html'),
            filename: 'index.html',
            inject: 'body'
        }),
        new ExtractTextPlugin({
            filename: '[contenthash].css',
            allChunks: true
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('dev')
        })
    ],
    devtool: 'cheap-module-eval-source-map'
};
```
### 四、依赖模块
#### 1、开发环境
| 名称    | 版本|
|:--------|---------:|
| babel-core| 6.26.0 |
| babel-loader| 7.1.2|
| babel-preset-es2015|6.24.1|
| babel-preset-react| 6.24.1|
| css-loader| 0.28.7|
| file-loader| 1.1.6|
| html-webpack-plugin|2.30.1|
| style-loader| 0.19.1|
|url-loader| 0.6.2|
| webpack|3.10.0|
| webpack-dev-middleware|2.0.4|
| webpack-dev-server|2.10.1|
#### 2、线上依赖
| 名称    | 版本|
|:--------|---------:|
| antd| 3.1.0 |
| axios| 6.26.0 |
|js-cookie| 2.2.0|
|react|6.24.1|
|react-dom| 6.24.1|
| react-router| 0.28.7|
|qs|6.5.0|
|react-bootstrap|0.32.0|