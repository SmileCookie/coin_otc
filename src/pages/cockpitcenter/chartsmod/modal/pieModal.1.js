import React, { Component } from 'react';
import echarts from 'echarts'
import 'echarts/map/js/china';
import geoJson from 'echarts/map/json/china.json';
import { Icon, Statistic, } from 'antd'
// import { toThousands } from '../../../../utils'
import {convertData,mapChina,geoCoordMapChina} from '../../static/static'

export default class PieModal extends Component {
    constructor(props) {
        super(props)
        this.state = {
            showHide: false
        }

    }
    componentDidMount() {
        // let myChart = echarts.init(this.refs.PieModal)
        // echarts.registerMap('zhongguo', geoJson);
        // let option = {
        //     // backgroundColor: '#404a59',
        //     title: {
        //         text: '全国主要城市空气质量',
        //         // subtext: 'data from PM25.in',
        //         // sublink: 'http://www.pm25.in',
        //         left: 'center',
        //         textStyle: {
        //             color: '#fff'
        //         }
        //     },
        //     tooltip : {
        //         trigger: 'item'
        //     },
        //     // legend: {
        //     //     orient: 'vertical',
        //     //     y: 'bottom',
        //     //     x:'right',
        //     //     data:['pm2.5'],
        //     //     textStyle: {
        //     //         color: '#fff'
        //     //     }
        //     // },
        //     geo: {
        //         map: 'world',
        //         label: {
        //             emphasis: {
        //                 show: false
        //             }
        //         },
        //         roam: true,
        //         itemStyle: {
        //             normal: {
        //                 areaColor: '#323c48',
        //                 borderColor: '#111'
        //             },
        //             emphasis: {
        //                 areaColor: '#2a333d'
        //             }
        //         }
        //     },
        //     series : [
        //         {
        //             name: 'Top 5',
        //             type: 'effectScatter',
        //             coordinateSystem: 'geo',
        //             data: convertData(mapChina.sort(function (a, b) {
        //                 return b.value - a.value;
        //             })),
        //             encode: {
        //                 value: 2
        //             },
        //             symbolSize: function (val) {
        //                 return val[2] / 10;
        //             },
        //             showEffectOn: 'render',
        //             rippleEffect: {
        //                 brushType: 'stroke'
        //             },
        //             hoverAnimation: true,
        //             label: {
        //                 normal: {
        //                     // formatter: '{b}',
        //                     position: 'right',
        //                     // show: true
        //                 }
        //             },
        //             itemStyle: {
        //                 normal: {
        //                     color: '#f4e925',
        //                     shadowBlur: 10,
        //                     shadowColor: '#333'
        //                 }
        //             },
        //             zlevel: 1
        //         }
        //     ]
        // }
        // myChart.setOption(option)
        this.initalECharts()
    }
    componentWillReceiveProps() {

    }
    initalECharts = () => {
        const data = [
            {name: '上海', area: '华东大区', type: 'areaCenterCity'},
            {name: '深圳', area: '华南大区', type: 'areaCenterCity'},
            {name: '成都', area: '华西大区', type: 'areaCenterCity'},
            {name: '北京', area: '华北大区', type: 'areaCenterCity'},
            {name: '武汉', area: '华中大区', type: 'areaCenterCity'},
            {name: '沈阳', area: '东北大区', type: 'areaCenterCity'},
        ];
        // echarts.registerMap('zhongguo', geoJson);
        // for(let item of provienceData){
        //     if(item.area === '东北大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#3CA2FC",
        //             },
        //             emphasis: {
        //                 areaColor: "#3CA2FC",
        //             }
        //         }
        //     }else if(item.area === '华北大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#6CAFBE",
        //             },
        //             emphasis: {
        //                 areaColor: "#6CAFBE",
        //             }
        //         }
        //     }else if(item.area === '华中大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#ADD03C",
        //             },
        //             emphasis: {
        //                 areaColor: "#ADD03C",
        //             }
        //         }
        //     }else if(item.area === '华东大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#A13614",
        //             },
        //             emphasis: {
        //                 areaColor: "#A13614",
        //             }
        //         }
        //     }else if(item.area === '华西大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#FFBA00",
        //             },
        //             emphasis: {
        //                 areaColor: "#FFBA00",
        //             }
        //         }
        //     }else if(item.area === '华南大区'){
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#FFD300",
        //             },
        //             emphasis: {
        //                 areaColor: "#FFD300",
        //             }
        //         }
        //         }else if(item.area === '南海诸岛'){
        //           item.itemStyle = {
        //             normal: {
        //               borderColor: '#fff',//区域边框颜色
        //               areaColor:"#fff",//区域颜色
        //             },
        //             emphasis: {
        //               show: false,
        //               // borderColor: '#fff',
        //               // areaColor:"#fff",
        //             }
        //           }
        //     }else{
        //         item.itemStyle = {
        //             normal: {
        //                 areaColor: "#D9D9D9",
        //             },
        //             emphasis: {
        //                 areaColor: "#D9D9D9",
        //             }
        //         }
        //     }
        // }
        const myChart = echarts.init(document.getElementById('PieModal'));
        myChart.setOption({
                tooltip: {
                    show: false,       //不显示提示标签
                    formatter: '{b}',      //提示标签格式
                    backgroundColor:"#ff7f50",//提示标签背景颜色
                    textStyle:{color:"#fff"} //提示标签字体颜色
                },
                grid: {
                    left: '10%',
                    right: '10%',
                    top: '10%',
                    bottom: '10%',
                    containLabel: true
                },
                geo: {
                    map: 'china',
                    roam: false,
                    zoom: 1.2,
                    tooltip: {
                        show: false,       //不显示提示标签
                    },
                    label: {
                        normal: {
                            show: false,//显示省份标签
                            textStyle:{color:"#c71585"}//省份标签字体颜色
                        },
                        emphasis: {//对应的鼠标悬浮效果
                            show: false,
                            textStyle:{color:"#800080"}
                        }
                    },
                    itemStyle: {
                        normal: {
                            borderWidth: .5,//区域边框宽度
                            borderColor: '#fff',//区域边框颜色
                            areaColor:"#ffefd5",//区域颜色
                            label:{show:false}
                        },
                        emphasis: {
                            show: false,
                            borderWidth: .5,
                            borderColor: '#4b0082',
                            areaColor: "#ffdead",
                        }
                    },
                },
                series: [
                    {
                        type: 'scatter',
                        coordinateSystem: 'geo',
                        data: this.convertData(data),
                        symbolSize: 20,
                        symbolRotate: 35,
                        label: {
                            normal: {
                                formatter: '{b}',
                                position: 'right',
                                show: true
                            },
                            emphasis: {
                                show: false
                            }
                        },
                        tooltip: {
                            show: false,       //不显示提示标签
                            formatter: '{c}',      //提示标签格式
                            backgroundColor: "#fff",//提示标签背景颜色
                            borderColor: '#ccc',
                            borderWidth: .5,
                            textStyle:{color:"#000"} //提示标签字体颜色
                        },
                        itemStyle: {
                            normal: {
                                color: '#57c610'
                            }
                        }
                    },
                    {
                        type: 'map',
                        mapType: 'china',
                        roam: false,
                        zoom: 1.2,
                        tooltip: {
                            show: false,       //不显示提示标签
                        },
                        label: {
                            normal: {
                                show: false    //显示省份标签
                            },
                            emphasis: {
                                show: false,
                            }
                        },
                        itemStyle: {
                            normal: {
                                borderWidth: .5,      //区域边框宽度
                                borderColor: '#fff',  //区域边框颜色
                                label:{show:false}
                            },
                            emphasis: {
                                show: false,
                            }
                        },
                        // geoIndex: 0,
                        // tooltip: {show: false},
                        data: mapChina
                    }
                ],
        })
    }
    convertData = (data) => {
        var res = [];
        for (var i = 0; i < data.length; i++) {
            var geoCoord = geoCoordMapChina[data[i].name];
            if (geoCoord) {
                res.push({
                    name: data[i].name,
                    value: geoCoord.concat(data[i].area),
                    area: data[i].area,
                    type: data[i].type,
                });
            }
        }
        console.log(res);
        return res;
    }
    render() {

        const { showHide, } = this.state
        const styleObj = {
            border: '1px solid #333',
            borderRadius: '10px',
            marginLeft: '10px',
            height: '100px'
        }
        return (


            <div className="col-mg-12 col-lg-12 col-md-12 col-sm-6 col-xs-6" style={{ border: '1px solid gray',height:'350px' }}>
                <div style={{ backgroundColor: 'gray', lineHeight: '40px',}}>{this.props.title}</div>
                <div id='PieModal' style={{ height: '100%',width:'100%' }}></div>
            </div>



        )
    }
}