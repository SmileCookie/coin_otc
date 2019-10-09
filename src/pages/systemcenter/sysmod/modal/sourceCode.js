import React, { Component } from 'react'
import { Button, Tabs, Input} from 'antd'
const TabPane = Tabs.TabPane;
const { TextArea } = Input;

export default class SourceCode extends Component {
    constructor(props) {
        super(props)
        this.state = {
        	contentArr: props.contentArr,
        	activeKey: props.activitykey || 'cn',
        }
        this.handleInputChange = this.handleInputChange.bind(this)
    }

    componentWillReceiveProps(nextProps) {
    	this.setState({
    		activeKey: nextProps.activitykey || 'cn',
    		contentArr: nextProps.contentArr
    	})
    }


    onChange = activeKey => {

	    this.setState({ activeKey });
  	};
  	//输入时 input 设置到 state
    handleInputChange(event) {
    	return;
    	console.log(event)
    	const key = this.state.activeKey
        const target = event.target;
        const value =  target.value;
        const newContentArr = this.state.contentArr;
        newContentArr.map((item) => {
			if (item.key === key) {
				item.con['content_' + key] = value
			}
        })
        this.setState({
            contentArr: newContentArr
        });
    }

    render() {
        const { contentArr, activeKey } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12' >
            	<Tabs onChange={this.onChange} activeKey={this.state.activeKey}>
					{
						contentArr.map(pane => (
				          	<TabPane tab={pane.title} key={pane.key} >
					          	<TextArea rows={24} value={pane.con['content']} onChange={this.handleInputChange}></TextArea>
				          	</TabPane>
				        ))
	            	}
            	</Tabs>
            </div>
        )
    }
}