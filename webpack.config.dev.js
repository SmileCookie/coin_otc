const merge = require('webpack-merge')
const common = require('./webpack.common.js')
const path = require('path');
const webpack = require('webpack');
const CleanPlugin = require("clean-webpack-plugin");


module.exports = merge(common,{
    devtool: 'cheap-module-eval-source-map',
    plugins: [
        new CleanPlugin(['build']),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': JSON.stringify('dev')
        })
    ]
    
});