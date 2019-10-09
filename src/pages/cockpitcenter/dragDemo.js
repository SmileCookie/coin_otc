import React, { Component } from 'react'
import { Dragact } from 'dragact'
import ReactEcharts from 'echarts-for-react'
import { Row, Col, Icon } from 'antd'
import { SortableContainer, SortableElement, sortableHandle, } from 'react-sortable-hoc';
import arrayMove from 'array-move'

// const fakeData = [
//     { GridX: 0, GridY: 0, w: 4, h: 2, key: '0' },
//     { GridX: 0, GridY: 0, w: 4, h: 2, key: '1' },
//     { GridX: 0, GridY: 0, w: 4, h: 2, key: '2' }
// ]

// const getblockStyle = isDragging => {
//     return {
//         background: isDragging ? '#1890ff' : 'white'
//     }
// }

const DragHandle = sortableHandle(() => <div>&nbsp;</div>);

const SortableItem = SortableElement(({ value }) => (
    <li style={{ padding: "10px", border: "1px solid #ccc", margin: "5px 0", }}>
        <DragHandle />
        {value}
    </li>
));
console.log(this)
const SortableList = SortableContainer(({ items }) => {
    return (
        <ul>
            {items.map((value, index) => (
                <SortableItem key={`item-${index}`} index={index} value={value} />
            ))}
        </ul>
    );
});

class DragDemo extends Component {
    constructor(props) {
        super(props)
        this.state = {
            fakeData: [
                {
                    GridX: 0, GridY: 0, w: 3, h: 4, key: '1-0', option: {
                        title: { text: 'ECharts 示例' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                },
                {
                    GridX: 3, GridY: 0, w: 3, h: 4, key: '1-1', option: {
                        title: { text: 'ECharts 示例222' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                },
                {
                    GridX: 6, GridY: 0, w: 3, h: 4, key: '1-2', option: {
                        title: { text: 'ECharts 示例333' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                }
            ],
            fakeData2: [
                {
                    GridX: 0, GridY: 0, w: 3, h: 4, key: '2-0', option: {
                        title: { text: 'ECharts 222' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                },
                {
                    GridX: 3, GridY: 0, w: 3, h: 4, key: '2-1', option: {
                        title: { text: 'ECharts 示例2-1' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                },
                {
                    GridX: 6, GridY: 0, w: 3, h: 4, key: '2-2', option: {
                        title: { text: 'ECharts 示例2-1' },
                        tooltip: {},
                        xAxis: {
                            data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                        },
                        yAxis: {},
                        series: [{
                            name: '销量',
                            type: 'bar',
                            data: [5, 20, 36, 10, 10, 20]
                        }]
                    }
                }
            ],
            dragactNode: Dragact,
            items: []
        }
    }

    componentDidMount() {
        // this.createItems(3)
        const lastLayout = localStorage.getItem('layout');
        if (lastLayout) {
            // storeLayout = JSON.parse(lastLayout);
            this.setState({
                fakeData: JSON.parse(lastLayout)
            })
        }
    }
    // saveLayout = () => {
    //     const newLayout = this.state.dragactNode.getLayout();
    //     const parseLayout = JSON.stringify(newLayout)
    //     console.log(JSON.parse(parseLayout))
    // }

    // createItems = (num) => {
    //     let arr = []
    //     for (let i = 0; i < num; i++) {
    //         arr.push(<div >
    //             <Dragact
    //                 layout={this.state.fakeData[i]}//必填项
    //                 col={12}//必填项
    //                 width={1000}//必填项
    //                 rowHeight={40}//必填项
    //                 margin={[5, 5]}//必填项
    //                 className='plant-layout'//必填项
    //                 style={{ height: '300px' }}//非必填项
    //                 placeholder={true}//非必填项
    //                 ref={node => node ? this.state.dragactNode = node : null}
    //                 onDragEnd={this.handleOnDragEnd}
    //                 onResizeEnd={this.handleResizeEnd}
    //             >
    //                 {(item, provided) => {
    //                     return (
    //                         <div
    //                             {...provided.props}
    //                             {...provided.dragHandle}
    //                             style={{ ...provided.props.style, border: '1px solid #ccc', color: '#fff' }}>
    //                             <div style={{ position: 'absolute', width: 10, height: 10, right: 15, top: 5, cursor: 'pointer', color: '#333', zIndex: '1000' }} onClick={() => this.onDelete(item.key)}>❌</div>
    //                             <ReactEcharts
    //                                 ref={(e) => { this.echarts_react = e; }}
    //                                 option={item.option}
    //                                 notMerge={true}
    //                                 style={{ width: '100%', height: '100%' }}
    //                             />
    //                             <span {...provided.resizeHandle}
    //                                 style={{
    //                                     position: 'absolute',
    //                                     width: 10, height: 10, right: 2, bottom: 2, cursor: 'se-resize',
    //                                     borderRight: '2px solid red',
    //                                     borderBottom: '2px solid red'
    //                                 }} />
    //                         </div>
    //                     )
    //                 }}
    //             </Dragact>
    //         </div>)
    //     }
    //     // return arr
    //     this.setState({
    //         items: arr
    //     })
    // }
    onSortEnd = ({ oldIndex, newIndex }) => {
        this.setState(({ items }) => ({
            items: arrayMove(items, oldIndex, newIndex),
        }));
    }
    //新增拖拽DIV
    addDragact() {
        this.state.fakeData.push({
            GridX: 0, GridY: 1, w: 3, h: 2, key: '3', option: {
                title: { text: 'ECharts 示例444' },
                tooltip: {},
                xAxis: {
                    data: ["衬衫", "羊毛衫", "雪纺衫", "裤子", "高跟鞋", "袜子"]
                },
                yAxis: {},
                series: [{
                    name: '销量',
                    type: 'bar',
                    data: [5, 20, 36, 10, 10, 20]
                }]
            }
        });
        this.setState({
            fakeData: this.state.fakeData
        });
    }
    //删除DIV
    onDelete = (key) => {
        let newFakeData = this.state.fakeData.filter((item) => {
            return item.key != key;
        });
        this.setState({
            fakeData: newFakeData
        });
    }
    //保存拖拽信息
    saveLayOut = () => {
        const newLayout = this.state.dragactNode.getLayout();
        const parsedLayout = JSON.stringify(newLayout);
        localStorage.setItem('layout', parsedLayout)
        console.log(parsedLayout);
    }
    //拖拽完成的回调函数
    handleOnDragEnd = () => {
        // let echarts_instance = this.echarts_react.getEchartsInstance();
        // echarts_instance.resize({opts: {width: 'auto', height: 'auto'}})

    }
    render() {
        return (
            <div>
                <Row>
                    <Col span={2} onClick={this.addDragact}>新增</Col>
                    <Col span={2} onClick={this.saveLayOut}>保存</Col>
                    <Col span={2}>回退</Col>
                </Row>
                {/* <SortableList useDragHandle items={this.state.items} onSortEnd={this.onSortEnd} > */}
                    <div style={{ border: '1px solid #ccc' }}>
                        <Dragact
                            layout={this.state.fakeData}//必填项
                            col={12}//必填项
                            width={1000}//必填项
                            rowHeight={40}//必填项
                            margin={[5, 5]}//必填项
                            className='plant-layout'//必填项
                            style={{ height: '300px' }}//非必填项
                            placeholder={true}//非必填项
                            ref={node => node ? this.state.dragactNode = node : null}
                            onDragEnd={this.handleOnDragEnd}
                        >
                            {(item, provided) => {
                                return (
                                    <div
                                        {...provided.props}
                                        {...provided.dragHandle}
                                        style={{ ...provided.props.style, border: '1px solid #ccc', color: '#fff' }}>
                                        <div style={{ position: 'absolute', width: 10, height: 10, right: 15, top: 5, cursor: 'pointer', color: '#333', zIndex: '1000' }} onClick={() => this.onDelete(item.key)}>❌</div>
                                        <ReactEcharts
                                            ref={(e) => { this.echarts_react = e; }}
                                            option={item.option}
                                            notMerge={true}
                                            style={{ width: '100%', height: '100%' }}
                                        />
                                        <span {...provided.resizeHandle}
                                            style={{
                                                position: 'absolute',
                                                width: 10, height: 10, right: 2, bottom: 2, cursor: 'se-resize',
                                                borderRight: '2px solid red',
                                                borderBottom: '2px solid red'
                                            }} />
                                    </div>
                                )
                            }}
                        </Dragact>
                    </div>

                {/* </SortableList> */}
            </div>

        )
    }
}

export default DragDemo