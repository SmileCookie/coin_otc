import React, { Component } from 'react'
import { Button } from 'antd'

export default class Prev extends Component {
    constructor(props) {
        super(props)
        this.state = {
            isColor: true
        }
    }
    switchColor = () => {
        this.setState({
            isColor: !this.state.isColor
        })
    }
    render() {
        // const { html } = this.state
        return (
            <div className='col-md-12 col-sm-12 col-xs-12' >
                <div style={{ marginBottom: '10px' }}><Button type='primary' size='small' onClick={this.switchColor}>背景颜色切换</Button></div>
                <div className='col-md-12 col-sm-12 col-xs-12' style={{ maxHeight: '780px', overflow: 'auto',color:`${this.state.isColor ? '#9199AF' : '#2F343F'}`, backgroundColor: `${this.state.isColor ? '#2F343F' : '#ffffff'}` }}>
                    <div dangerouslySetInnerHTML={{ __html: this.props.content }} ></div>
                </div>
            </div>
        )
    }
}