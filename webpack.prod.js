const webpack = require('webpack');
const merge = require('webpack-merge');
const UglifyJSPlugin = require('uglifyjs-webpack-plugin');
const common = require('./webpack.common.js');

module.exports = merge(common,{
    plugins: [
        new UglifyJSPlugin({
               uglifyOptions:{
                    output: {
                        comments: false,
                    },
                    compress: {
                        warnings: false
                    }
               }
        }),
        new webpack.DefinePlugin({
            'process.env.NODE_ENV': "'production'"
        })
    ],
});










































