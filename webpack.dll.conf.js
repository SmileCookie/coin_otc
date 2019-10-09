/**
 * 生成dll文件
 */
const path = require('path');
const wp   = require('webpack');

module.exports = {
    entry: {
        // 引入需要打包的dll lodash
        antd: ['antd'], // 如果有多个写多个key 生成多个dll , 也可以多个库放一起['a', 'b' ...]
        react: ['react'],
        reactdom: ['react-dom'],
        reactbootstrap: ['react-bootstrap'],
        wangeditor: ['wangeditor'],
        reactrouter: ['react-router'],
        reactslidedown:['react-slidedown'],
        reactrouterdom:['react-router-dom'],
        babelpresetenv:['babel-preset-env'],
        qs:['qs'],
        jscookie:['js-cookie'],
        bigjs:['big.js'],
        reactGridLayout:['react-grid-layout'],
        echarts:['echarts'],
        echartsForReact:['echarts-for-react'],
        reactSortableHoc:['react-sortable-hoc'],
        // dragact:['dragact'],
        reactDnd:['react-dnd'],
        reactDndHtml5Backend:['react-dnd-html5-backend'],
        reactLoadable:['react-loadable'],
        reactResizable:['react-resizable'],
        reactRnd:['react-rnd'],
        arrayMove:['array-move'],
        xlsxOc:['xlsx-oc']
    },
    // 下面所有的[name]对应的都是vendor
    output: {
        path: path.resolve(__dirname, 'src/dll/'), // 生成的dll文件扔到src/dll目录
        filename: '[name].dll.js',
        library: '[name]' // 给使用的人一个引用名字
    },
    plugins:[
        new wp.DllPlugin({
            path: path.resolve(__dirname, 'src/dll/[name].manifest.json'), // 打包出来的map放哪？
            name: '[name]'
        }),
        new wp.optimize.UglifyJsPlugin(

        )
    ]
}