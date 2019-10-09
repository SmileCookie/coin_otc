import React, { Component } from 'react'
import { Button, Tabs} from 'antd'
const TabPane = Tabs.TabPane;

export default class Prevs extends Component {
    constructor(props) {
        super(props)
        this.state = {
        	contentArr: props.contentArr,
        	activeKey: props.activitykey || 'cn',
            isColor: false
        }
    }

    componentWillReceiveProps(nextProps) {
    	this.setState({
    		activeKey: nextProps.activitykey || 'cn',
    		contentArr: nextProps.contentArr
    	})
    }

    switchColor = () => {
        this.setState({
            isColor: !this.state.isColor
        })
    }

    onChange = activeKey => {

	    this.setState({ activeKey });
  	};

    render() {
        const { contentArr, activeKey } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12' >
                <div style={{ marginBottom: '10px' }}><Button type='primary' size='small' onClick={this.switchColor}>背景颜色切换</Button></div>
            	<Tabs onChange={this.onChange} activeKey={this.state.activeKey}>
					{
						contentArr.map(pane => (

				          	<TabPane tab={pane.title} key={pane.key} >
					          	<div className='col-md-12 col-sm-12 col-xs-12' style={{ maxHeight: '780px', minHeight: '200px', overflow: 'auto',color:`${this.state.isColor ? '#9199AF' : '#2F343F'}`, backgroundColor: `${this.state.isColor ? '#2F343F' : '#ffffff'}` }}>
				                    <div dangerouslySetInnerHTML={{ __html: pane.con['content'] }} ></div>
				                </div>
				          	</TabPane>
				        ))
	            	}
            	</Tabs>
            </div>
        )
    }
}