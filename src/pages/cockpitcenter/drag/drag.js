import React from 'react'
import { Rnd } from 'react-rnd';
import { DragDropContext } from 'react-dnd'
import HTML5Backend from 'react-dnd-html5-backend'
import Box from './box'
import EchartsModal from '../chartsmod/modal/echartsModal';


const CardList = [
    {
        title: "钱包资金分布",
        id: 1,
        content: "this is first Card",
        Component:EchartsModal

    },
    {

        title: "保值资金分布",
        id: 2,
        content: "this is second Card",
        Component:EchartsModal
    },
    {

        title: "交易所金额分布",
        id: 3,
        content: "this is Third Card",
        Component:EchartsModal
    }
]


class ResizeDemo extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            width: 500,
            height: 190,
            x: 10,
            y: 10,
            CardList,
        }
    }
    componentDidMount() {

    }

    handleDND = (dragIndex, hoverIndex) => {
        let CardList = this.state.CardList;
        let tmp = CardList[dragIndex] //临时储存文件
        CardList.splice(dragIndex, 1) //移除拖拽项
        CardList.splice(hoverIndex, 0, tmp) //插入放置项
        this.setState({
            CardList
        })

    };
    render() {
        const { CardList } = this.state
        return (
            <div className="row">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="x_panel">
                        <div className="x_content" >
                            <div className="col-md-12 col-sm-12 col-xs-12" style={{ display: 'flex', justifyContent: 'space-around' }}>
                                {/* <Rnd
                    size={{ width: this.state.width, height: this.state.height }}
                    // position={{ x: this.state.x, y: this.state.y }}
                    // onDragStop={(e, d) => { 
                    //     console.log('d:::',d)
                    //     this.setState({ x: d.x, y: d.y }) 
                    // }}
                    disableDragging={true}
                    onResize={(e, direction, ref, delta, position) => {
                        console.log(ref,position)
                        this.setState({
                            width: ref.style.width,
                            height: ref.style.height,
                            // ...position,
                        });
                    }}
                    minWidth={500}
                    maxWidth={700}
                    minHeight={190}
                    maxHeight={300}
                    bounds="parent" 

                >
                    <Box />
                    <ComTwo />
                </Rnd> */}
                                {
                                    CardList.map((item, index) => {
                                        return (
                                            <Box
                                                key={item.id}
                                                title={item.title}
                                                content={item.content}
                                                index={index}
                                                onDND={this.handleDND}
                                                isCanDrag={this.props.isCanDrag || false}
                                                Comp={item.Component}
                                            />
                                        )
                                    })
                                }

                            </div>
                        </div>
                    </div>
                </div>
            </div>
        )
    }
}

export default DragDropContext(HTML5Backend)(ResizeDemo)