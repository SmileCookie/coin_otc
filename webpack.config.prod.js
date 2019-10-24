const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const CleanPlugin = require("clean-webpack-plugin");
const CopyWebpackPlugin = require('copy-webpack-plugin');

module.exports = {
    entry: {
        polyfill:['babel-polyfill', 'intl'],
        vendor: ['react', 'react-dom', 'react-router'],
        app: './src/index'
    },
    output: {
        path: path.resolve(__dirname, 'dist/bw'),
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        publicPath: 'https://s.bitglobal.com/bitglobal/bw/'
    },
    resolve: {
        extensions: ['.js', '.css', '.less']
    },
    module: {
        rules: [
            {
                test: /\.jsx?$/,
                exclude: /node_modules/,
                use: {
                    loader: 'babel-loader',
                    options: {
                        presets: ['es2015', 'react','stage-0'],
                        plugins: ["transform-decorators-legacy", "transform-async-to-generator"]
                    }
                }
            },
            {
                test: /\.css$/,
                exclude: /node_modules/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader?minimize']
                })
            },
            {
                test: /\.less$/,
                exclude: /node_modules/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['css-loader?minimize', 'less-loader']
                })
            },
            {
                test: /\.(jpg|png|gif|eot|woff|woff2|ttf|svg)$/,
                exclude: /node_modules/,
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
        new CleanPlugin(['dist']),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['polyfill', 'vendor']
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './src/index.html'),
            filename: 'index.html',
            inject: 'body',
            favicon: './src/assets/img/favicon.ico'
        }),
        new ExtractTextPlugin({
            filename: '[contenthash].css',
            allChunks: true
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        }),
        new webpack.optimize.UglifyJsPlugin({
            output: {
                comments: false,
            },
            compress: {
                warnings: false
            }
        }),
        new CopyWebpackPlugin([
            {from:'src/charting_library',to:'src/charting_library'}
        ])
    ]
};