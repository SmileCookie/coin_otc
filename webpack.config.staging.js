const webpack = require('webpack');
const path = require('path');
const merge = require('webpack-merge');
const common = require('./webpack.common.js');
const CleanPlugin = require("clean-webpack-plugin");

module.exports = merge(common,{
    output: {
        path: path.resolve(__dirname, 'dist/bw'),
        // path: path.resolve(__dirname, 'build/bw'), //本地测试用
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        publicPath: '/bw/'
    },
    plugins: [
        new CleanPlugin(['dist','build']),
        new webpack.optimize.UglifyJsPlugin({
            output: {
                comments: false,
            },
            compress: {
                warnings: false
            }
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('production')
        })
    ]
});
