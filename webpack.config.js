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
        proxy: [
            {
                context: [],
                target:"http://192.168.3.4:8080",
                secure: false,
                changeOrigin: true
            },
            {//管凯礼
                context:[],
                target:"http://192.168.3.18:8099",
                secure: false,
                changeOrigin: true
            },
            {//王浩
                context: [],
                target:"http://192.168.3.28:8080",
                secure: false,
                changeOrigin: true
            },
            {//张祥
                context: [],
                target:"http://192.168.3.3:8089",
                secure: false,
                changeOrigin: true
            },
            {//卜宪冠
                context: [],
                target:"http://192.168.3.19:8080",
                secure: false,
                changeOrigin: true
            },
            // {//建芳
            //     context: ["/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/common","/usermod",'/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcConfig','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/walletUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking','/otcCointype','/billFutures','/leverageRecord','/transReset','/positionDetails',"/common","/generalledger","/checkreconciliation",'/transactionfutures',"/returnsSummary","/transactionAll","/moneyChange","/futures","/fundUserCapitalDetail","/entrustmentDetails","/transactionRecord","/billReconciliation","/futuresreconciliation","/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/usermod",'/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcConfig','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/walletUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking','/otcCointype',"/walletaccount","/walletRecon","/withdraw","/otc","/userFutures","/walletUserCapital",'/tradingRecord','/positionChangeRecord','/handicapTrading','/fundsRate','/feeAccountDetails','/feeAccountCheck','/insuranceFund','/coinLargeOrder','/coinLargeAccount','/coinReverseaccount','/coinInfoupdate',"/frequent","/qttransrecord","/linkedaccount",],
            //     target:"http://192.168.3.23:8099",
            //     secure: false,
            //     changeOrigin: true
            // },
            {//建芳
                context: ['/schedule','/scheduleLog'],
                target:"http://192.168.3.23:8999",
                secure: false,
                changeOrigin: true
            },
            {//刘冰
                context: [],
                target:"http://192.168.3.53:8080",
                secure: false,
                changeOrigin: true
            },
            {//文图
                context: [],
                target:"http://192.168.3.121:8099",
                secure: false,
                changeOrigin: true
            },
            {//庆先
                context: [],
                target:"http://192.168.3.182:8099",
                secure: false,
                changeOrigin: true
            },
            {//树国
                context: [],
                target:"http://192.168.3.226:8099",
                secure: false,
                changeOrigin: true
            },
            {
                context: ["/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/common","/usermod",'/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcConfig','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/walletUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking','/otcCointype','/billFutures','/leverageRecord','/transReset','/positionDetails',"/common","/generalledger","/checkreconciliation",'/transactionfutures',"/returnsSummary","/transactionAll","/moneyChange","/futures","/fundUserCapitalDetail","/entrustmentDetails","/transactionRecord","/billReconciliation","/futuresreconciliation","/gbcDividendCount","/capitalCount", "/withdraw","/recharge","/authenLog","/centerCapitalExp","/withdrawRecord", "/withdrawRecord", "/settlement", "/marketTrade","/marketMergeTrade","/entrustRecord","/planEntrustRecord","/centerCapitalDaily","/centerCapitalSum","/userPaymentTradeCount","/userBaseOperCount","/userPositionCount","/userPositionCount","/mars","/smallAutoPayRecord","/smallPayTask","/feeProfit","/capitalUse", "/userCapital","/accountManage",'/blacklist',"/batchEntrust", "/news",'/brush',"/capitalaccount","/authentication","/billDetail","/SysDictionary","/app","/gbcreportmod","/backcapital",'/walletBill','/walletBillDetail','/walletBalance',"/msg","/integralBill","/integralRule","/integralVipRule","/googleVerify","/phoneVerify","/messageSend","/msgTemplateRule","/operInfo","/agencyTask",'/rechargeAddress','/withdrawAddress',"/dealRecord","/capitalMonitor","/SendEmailAccount","/voteManage","/drawManage","/userInfo","/sys","/loginInfo","/usermod",'/walletRecon','/otcConfig','/otcBank','/otcPaymentType','/otcConfig','/otcBank','/otcPaymentType','/advertisement','/otcComplain','/orderform','/otcBill','/otcUserCapital','/walletUserCapital','/otcSms','/coin','/otcCapitalCount','/walletCapitalCount','/friendUrl','/bannerPhoto','/bannerGroup','/sysmod','/doubleCheck','/fundTransferLog','/otcRePush','/otcBannerPhoto','/deblocking',
                    '/otcCointype',"/walletaccount","/walletRecon","/withdraw","/otc","/userFutures","/walletUserCapital",'/tradingRecord','/positionChangeRecord','/handicapTrading','/fundsRate','/insuranceFund','/feeAccountDetails','/feeAccountCheck','/coinLargeOrder','/coinLargeAccount','/coinReverseaccount','/coinInfoupdate',"/frequent","/qttransrecord","/linkedaccount","/coinQtStopwarning",'/coinQtBelowwarning',' /coinQtAccounted','/coinQtStopwarning','/coinQtBelowwarning','/coinQtAccounted','/coinQtMarketdeparture','/coinQtDishlowwarning','/coinQtForfailure','/coinQtHedgingabnormal','/coinQtHedgingnumbers','/coinQtRecordabnormal','/coinQtAmountlowwarning'],
                target:"http://admin.bitstaging.com/",
                secure: false,
                changeOrigin: true
            }
        ] 
    },
    module: {
        rules: [
            {
                test: /\.(jsx?|tsx?|ts?)$/,
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
            'process.env.NODE_ENV': JSON.stringify('development')
        })
    ],
    devtool: 'cheap-module-eval-source-map'
};










































