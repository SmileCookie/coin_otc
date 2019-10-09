import React, { Component } from 'react'
import { navWid, DOMAIN_VIP } from '../conf'

export default class ErrorBoundary extends Component {
    constructor(props) {
        super(props)
        this.state = {
            hasError: false,
            errInfo: ''
        }
    }
    // static getDerivedStateFromError(error){
    //     console.log(error)
    //     return {
    //         hasError:true
    //     }
    // }
    componentDidCatch(error, info) {
        this.setState({ hasError: true, errInfo: error })
        console.log('error:', error, 'info:>>>>>>>', info)

    }
    render() {
        if (this.state.hasError) {
            return <div style={{ display: 'flex', height: document.body.clientHeight - navWid }}>
                <div style={{ margin: 'auto', display: 'block',textAlign:'center' }}>
                    <h1>404</h1>
                    <h3 style={{color:'red'}}>{this.state.errInfo.message}</h3>
                </div>
            </div>;
        }
        return this.props.children;
    }
}