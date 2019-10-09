const path = require("path");

const webpack = require("webpack");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");
const HtmlWebpackPlugin = require("html-webpack-plugin");
const MiniCssExtractPlugin = require("mini-css-extract-plugin");
const HappyPack = require('happypack');
const os = require('os');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });

module.exports = {
    entry: ["./src/index.js"],
    output: {
        // 输出目录
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        path: path.resolve(__dirname, "../dist")
    },
    resolve: {
        extensions: [".js", ".css", '.less'],
        alias: {
            "src": path.resolve(__dirname, "../src"),
            pages: path.resolve(__dirname, "../src/pages"),
            router: path.resolve(__dirname, "../src/router"),
            reduxs: path.resolve(__dirname, "../src/redux/module"),
            conf: path.resolve(__dirname, "../src/conf"),
            nets: path.resolve(__dirname, "../src/net"),
            components: path.resolve(__dirname, "../src/components"),
            utils: path.resolve(__dirname, "../src/utils"),
            mock: path.resolve(__dirname, "../src/mock"),
        }
    },
    module: {
        rules: [
            {
                // cnpm i babel-loader @babel/core @babel/preset-env -D
                test: /\.(js|jsx)?$/,
                exclude: /node_modules/,
                use: [
                    {
                        loader: "happypack/loader?id=happyBabel"
                    }
                ]
            },
            {
                test: /\.(c|le)ss$/,
                use: [
                    MiniCssExtractPlugin.loader,
                    "css-loader", // 编译css
                    "postcss-loader", // 使用 postcss 为 css 加上浏览器前缀
                    //"sass-loader", // 编译scss
                    "less-loader" // 编译 less
                ]
            },
            {
                test: /\.(png|jpg|jpeg|gif|svg)/,
                use: {
                    loader: "url-loader",
                    options: {
                        outputPath: "images/", // 图片输出的路径
                        limit: 10 * 1024
                    }
                }
            },
            {
                test: /\.(eot|woff2?|ttf|svg)$/,
                use: [
                    {
                        loader: 'url-loader',
                        options: {
                            name: '[name]-[hash:5].min.[ext]',
                            limit: 5000, // fonts file size <= 5KB, use 'base64'; else, output svg file
                            publicPath: 'fonts/',
                            outputPath: 'fonts/'
                        }
                    }
                ]
            }
        ]
    },
    plugins: [
        new CleanWebpackPlugin(),
        new HtmlWebpackPlugin({
            filename: "index.html", // 最终创建的文件名
            template: path.resolve(__dirname, '..', "src/template.html"), // 指定模板路径
            minify: {
                collapseWhitespace: true, // 去除空白
            },
            favicon: path.resolve(__dirname, '..', "src/assets/image/base/favicon.ico")
        }),
        new webpack.ProvidePlugin({ $: 'jquery' }),
        // happypack
        new HappyPack({
            //用id来标识 happypack处理那里类文件
            id: 'happyBabel',
            //如何处理  用法和loader 的配置一样
            loaders: [{
                loader: 'babel-loader?cacheDirectory=true',
            }],
            //共享进程池threadPool: HappyThreadPool 代表共享进程池，即多个 HappyPack 实例都使用同一个共享进程池中的子进程去处理任务，以防止资源占用过多。
            threadPool: happyThreadPool,
            //允许 HappyPack 输出日志
            verbose: true,
        }),
        // css单独提取
        new MiniCssExtractPlugin({
            filename: "[name].[chunkhash].css",
            chunkFilename: "[id].[chunkhash].css"
        })
    ],
    performance: false // 关闭性能提示
};
