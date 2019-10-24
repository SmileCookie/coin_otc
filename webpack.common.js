const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: {
        polyfill:['babel-polyfill', 'intl'],
        vendor: ['react', 'react-dom', 'react-router'],
        app: './src/index'
    },
    output: {
        path: path.resolve(__dirname, 'build/bw'),
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        publicPath: '/bw/'
    },
    resolve: {
        extensions: ['.js', '.css', '.less'],
        alias: {
            // 'wm':path.resolve(__dirname, 'src'),
            'extension': path.resolve(__dirname, 'src/extension'),
            '@': path.resolve('./node_modules/oasis-client-dep/src'),
            Utils: path.resolve(__dirname, 'src/utils'),
            Components: path.resolve(__dirname, 'src/components'),
            Assets: path.resolve(__dirname, 'src/assets'),
            Conf: path.resolve(__dirname, 'src/conf'),
            Redux: path.resolve(__dirname, 'src/redux')
        }
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: [
                    /node_modules(?!.*_*oasis-client-dep.*)/,
                    path.resolve(__dirname, "src/utils/charting_library")
                ],
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['es2015', 'react', 'stage-0'],
                        plugins: ["transform-decorators-legacy",
                                  "transform-async-to-generator",
                                  "transform-do-expressions"]
                    }
                }
            },
            {
                test: /\.css$/,
                exclude: [
                    /node_modules(?!.*_*oasis-client-dep.*)/,
                    path.resolve(__dirname, "src/utils/charting_library")
                ],
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader',{
                        loader: 'postcss-loader',
                        options: {
                            ident: 'postcss',
                            plugins: [
                                require('postcss-cssnext')()
                            ]
                        }
                    }]
                })
            },
            {
                test: /\.less$/,
                exclude: /node_modules(?!.*_*oasis-client-dep.*)/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader',{
                        loader: 'postcss-loader',
                        options: {
                            ident: 'postcss',
                            plugins: [
                                require('postcss-cssnext')()
                            ]
                        }
                    }, 'less-loader']
                })
            },
            // {
            //     test: path.resolve("src/utils/charting_library/kline.min"),
            //     use: "imports-loader?$=jquery"
            // },
            {
                test: /\.(jpg|png|gif|eot|woff|woff2|ttf|svg)$/,
                exclude: [
                    /node_modules(?!.*_*oasis-client-dep.*)/,
                    path.resolve(__dirname, "src/utils/charting_library")
                ],
                use: {
                    loader: 'url-loader',
                    options: {
                        limit: 8192
                    }
                }
            }
        ]
    },
    plugins: [
        new webpack.optimize.CommonsChunkPlugin({
            name: ['vendor', 'polyfill']
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, 'src/index.html'),
            filename: 'index.html',
            inject: 'body',
            favicon: './src/assets/img/favicon.ico'
        }),
        new ExtractTextPlugin({
            filename: '[contenthash].css',
            allChunks: true
        }),
        new CopyWebpackPlugin([
            {from:'src/utils/charting_library',to:'src/charting_library'}
        ])
    ]
};



































