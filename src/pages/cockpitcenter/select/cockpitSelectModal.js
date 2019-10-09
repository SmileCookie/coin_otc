import React, { Component } from 'react'
import { Select, Menu, Dropdown, Icon,Button } from 'antd'
import { SELECTWIDTH } from '../../../conf'
// const {MenuItem} = Menu

const Option = Select.Option



class CockpitSelectModal extends Component {
    constructor(props) {
        super(props)
    }
    componentDidMount() {

    }
    componentWillReceiveProps() {

    }
    onClick = (visible) => {
        console.log(visible)
    }
    render() {
        const menu = (
            <Menu onClick={this.props.handleChange && this.props.handleChange}>
                {this.props.isShow && <Menu.Item key="1">设置面板内容</Menu.Item>}
                <Menu.Item key="2">自定义布局</Menu.Item>
                <Menu.Item key="3">重置布局</Menu.Item>
                <Menu.Item key="4">统计设置</Menu.Item>
            </Menu>
        );
        return (
            <div className="right icons-list" style={{ marginRight: '22px',position:'absolute',right:'25px',top:'50px' }} >
                {this.props.isDraggable ?
                    <div className="cock_set float-left">
                        <Button key="submit" type="more" size='small' onClick={this.props.saveLayout&&this.props.saveLayout}>确认</Button>
                        <Button key="back" size='small'  onClick={this.props.handleCancel&&this.props.handleCancel}>取消</Button>
                    </div>
                    : <div>
                        <Dropdown overlay={menu} placement='bottomRight' overlayStyle={{ cursor: "pointer", }}>
                            <Icon type="setting" style={{ fontSize: '18px', color: '#0a477e', marginRight: '10px', cursor: "pointer", }} />
                        </Dropdown>
                        <Icon type="sync" spin={this.props.iconIsSpin || false} style={{ fontSize: '18px', color: '#0a477e', marginRight: '10px' }} onClick={this.props.update && this.props.update} />
                    </div>
                }

            </div>
        )
    }

}

export default CockpitSelectModal