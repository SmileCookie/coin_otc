import React from 'react'
import { findDOMNode } from 'react-dom'
import { Card } from 'antd'
import { DragSource, DropTarget } from 'react-dnd'
import { Rnd } from 'react-rnd';
import EchartsModal from '../chartsmod/modal/echartsModal';



const Types = {    // 设定类型，只有DragSource和DropTarget的类型相同时，才能完成拖拽和放置
    CARD: 'CARD'
}

//DragSource相关设定
const CardSource = {//设定DragSource的拖拽事件方法
    beginDrag(props, monitor, component) {//拖拽开始时触发的事件，必须，返回props相关对象
        console.log('beginDrag')
        return {
            index: props.index
        }
    },
    endDrag(props, monitor, component) {
        //拖拽结束时的事件，可选  
    },

    canDrag(props, monitor) {
        //是否可以拖拽的事件。可选  
        console.log(props, monitor)
        return props.isCanDrag
    },
    isDragging(props, monitor) {
        // 拖拽时触发的事件，可选  
    }


}

const collect = (connect, monitor) => {//通过这个函数可以通过this.props获取这个函数所返回的所有属性
    return {
        isDragging: monitor.isDragging(),
        connectDragSource: connect.dragSource()
    }
}

//DropTarget相关设定

const CardTarget = {
    drop(props, monitor, component) { //组件放下时触发的事件
        //...
    },
    canDrop(props, monitor) { //组件可以被放置时触发的事件，可选
        //...
        return false
    },
    hover(props, monitor, component) { //组件在target上方时触发的事件，可选
        // console.log(component)
        if (!component) return null; //异常处理判断

        const dragIndex = monitor.getItem().index;//拖拽目标的Index

        const hoverIndex = props.index; //放置目标Index

        if (dragIndex === hoverIndex) return null;// 如果拖拽目标和放置目标相同的话，停止执行



        //如果不做以下处理，则卡片移动到另一个卡片上就会进行交换，下方处理使得卡片能够在跨过中心线后进行交换.

        const hoverBoundingRect = (findDOMNode(component)).getBoundingClientRect();//获取卡片的边框矩形

        const hoverMiddleX = (hoverBoundingRect.right - hoverBoundingRect.left) / 2;//获取X轴中点
        const hoverMiddleY = (hoverBoundingRect.bottom - hoverBoundingRect.top) / 2;//获取Y轴中点

        const clientOffset = monitor.getClientOffset();//获取拖拽目标偏移量

        const hoverClientX = (clientOffset).x - hoverBoundingRect.left;
        const hoverClientY = (clientOffset).y - hoverBoundingRect.top;

        if (dragIndex < hoverIndex && hoverClientX < hoverMiddleX) { // 从前往后放置

            return null
        }
        if (dragIndex > hoverIndex && hoverClientX > hoverMiddleX) { // 从后往前放置
            return null
        }

        // if (dragIndex < hoverIndex && hoverClientY < hoverMiddleY) { // 从上往下放置

        //     return null
        // }
        // if (dragIndex > hoverIndex && hoverClientY > hoverMiddleY) { // 从下往上放置
        //     return null
        // }
        props.onDND(dragIndex, hoverIndex); //调用App.js中方法完成交换
        monitor.getItem().index = hoverIndex; //重新赋值index，否则会出现无限交换情况
    },
}
const collect1 = (connect, monitor) => {//同DragSource的collect函数
    return {
        connectDropTarget: connect.dropTarget(),
        isOver: monitor.isOver(), //source是否在Target上方
        isOverCurrent: monitor.isOver({ shallow: true }),
        canDrop: monitor.canDrop(),//能否被放置
        itemType: monitor.getItemType(),//获取拖拽组件type
    }
}

@DragSource(Types.CARD, CardSource, collect)
@DropTarget(Types.CARD, CardTarget, collect1)
class Box extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            width: 500,
            height: 190,
            x: 10,
            y: 10,
        }
    }

    render() {
        const { isDragging, connectDragSource, connectDropTarget,Comp } = this.props
        let opacity = isDragging ? 0.1 : 1; //当被拖拽时呈现透明效果
        return connectDragSource(//使用DragSource 和 DropTarget
            connectDropTarget(

                <div className="col-mg-4 col-lg-4 col-md-6 col-sm-6 col-xs-6" >
                    {/* <Card
                        title={this.props.title}
                        style={{ opacity }}
                    >
                        <p>{this.props.content}</p>
                    </Card> */}
                    {/* <EchartsModal
                        title={this.props.title}
                    /> */}
                    <Comp title={this.props.title}/>
                </div>))
    }
}



export default Box




























































































































































































































































































