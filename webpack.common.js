const path = require('path');
const webpack = require('webpack');
const HtmlWebpackPlugin = require('html-webpack-plugin');
const ExtractTextPlugin = require("extract-text-webpack-plugin");
const AddAssetHtmlPlugin = require('add-asset-html-webpack-plugin');
const CleanPlugin = require("clean-webpack-plugin");
const UglifyJsPlugin = require('uglifyjs-webpack-plugin')

const HappyPack = require('happypack');
const os = require('os');
const happyThreadPool = HappyPack.ThreadPool({ size: os.cpus().length });
module.exports = {
    entry: {
        polyfill:['babel-polyfill'],
        vendor: ['react', 'react-dom', 'react-router'],
        app: './src/index'
    },
    output: {
        path: path.resolve(__dirname, 'dist/admin'),
        filename: '[name].[hash].bundle.js',
        chunkFilename: '[chunkhash].chunk.js',
        publicPath: '/'
    },
    resolve: {
        extensions: ['.js','.ts','.tsx', '.css', '.less'],
        alias: {
            Utils: path.resolve(__dirname, 'src/utils'),
            Conf:path.resolve(__dirname,'src/conf'),
            CTable:path.resolve(__dirname,'src/pages/common/table/commonTable'),
            DTPath:path.resolve(__dirname,'src/pages/decorator'),
            GCPath:path.resolve(__dirname,'src/pages/common/modal/googleCode')
        }
    },
    module: {
        rules: [
            {
                test: /\.(jsx?|tsx?|ts?)$/,
                exclude: /node_modules/,
                include:/src/,
                use: {
                    loader: 'happypack/loader?id=babel',
                }
            },
            {
                test: /\.css$/,
                use: ExtractTextPlugin.extract({
                    fallback: 'style-loader',
                    use: ['happypack/loader?id=css']
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
        new CleanPlugin(['dist']),
        new webpack.optimize.CommonsChunkPlugin({
            name: ['polyfill', 'vendor']
        }),
        new UglifyJsPlugin({
            parallel: true,
            cache:true
        }),
        new HtmlWebpackPlugin({
            template: path.resolve(__dirname, './src/index.html'),
            filename: 'index.html',
            inject: 'body'
        }),
        new AddAssetHtmlPlugin({ filepath: path.resolve('./src/**/*.dll.js'),includeSourcemap:false,outputPath:'./dll',publicPath:'/dll'}),
        new ExtractTextPlugin({
            filename: '[contenthash].css',
            allChunks: true
        }),
        new webpack.ProvidePlugin({
            'React':'react',
            'qs':'qs',
            'moment':'moment'
        }),
        new HappyPack({
            //用id来标识 happypack处理那里类文件
          id: 'babel',
          //如何处理  用法和loader 的配置一样
          loaders:['babel-loader?cacheDirectory'],
          //共享进程池
          threadPool: happyThreadPool,
          //允许 HappyPack 输出日志
          verbose: true,
          cache: true,
        }),
        new HappyPack({
            //用id来标识 happypack处理那里类文件
          id: 'css',
          //如何处理  用法和loader 的配置一样
          loaders:['css-loader'],
          //共享进程池
          threadPool: happyThreadPool,
          //允许 HappyPack 输出日志
          verbose: true,
          cache: true,
        }),
        new HappyPack({
            //用id来标识 happypack处理那里类文件
          id: 'style',
          //如何处理  用法和loader 的配置一样
          loaders:['style-loader'],
          //共享进程池
          threadPool: happyThreadPool,
          //允许 HappyPack 输出日志
          verbose: true,
          cache: true,
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/antd.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/react.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactdom.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactbootstrap.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/wangeditor.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactrouter.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactslidedown.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactrouterdom.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/babelpresetenv.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/qs.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/jscookie.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/bigjs.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactGridLayout.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/echarts.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/echartsForReact.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactSortableHoc.manifest.json') //代表我们的map的地址
        }),
        // new webpack.DllReferencePlugin({
        //     manifest: path.resolve(__dirname, 'src/dll/reactDnd.manifest.json') //代表我们的map的地址
        // }),
        // new webpack.DllReferencePlugin({
        //     manifest: path.resolve(__dirname, 'src/dll/reactDndHtml5Backend.manifest.json') //代表我们的map的地址
        // }),
        // new webpack.DllReferencePlugin({
        //     manifest: path.resolve(__dirname, 'src/dll/reactLoadable.manifest.json') //代表我们的map的地址
        // }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/reactResizable.manifest.json') //代表我们的map的地址
        }),
        // new webpack.DllReferencePlugin({
        //     manifest: path.resolve(__dirname, 'src/dll/reactRnd.manifest.json') //代表我们的map的地址
        // }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/arrayMove.manifest.json') //代表我们的map的地址
        }),
        new webpack.DllReferencePlugin({
            manifest: path.resolve(__dirname, 'src/dll/xlsxOc.manifest.json') //代表我们的map的地址
        }),
    ]
};


































