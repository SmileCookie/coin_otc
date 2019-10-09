import React from 'react'
import { navWid,DOMAIN_VIP } from '../conf' 

export default class Loading extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            height:document.body.clientHeight
        }
    }

    render(){
        return <div className="page-loading" style={{height:this.state.height-navWid}}></div>
    }

}



























