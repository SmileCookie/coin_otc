
import 'babel-polyfill'
import React from 'react'
import axios from 'axios'
import qs from 'qs'

import ReactDOM from 'react-dom'
import { DOMAIN_VIP } from '../src/conf'
import { Router ,browserHistory} from 'react-router'
import createRoutes from './routes'

class LoginDenger extends React.Component {
    constructor(props){
        super(props)
        this.state = {
            isDenger:false 
        }
        this.loginBefore = this.loginBefore.bind(this)
    }
    componentWillMount(){
        this.loginBefore()
    }
    loginBefore(){
        axios.get(DOMAIN_VIP+"/common/sys").then(res => {
            const result = res.data;
            if(result.code == 500){
                window.location.href=result.url
                this.setState({
                   isDenger:true 
                })
            }
        })
    }
    render() {
        const { isDenger} = this.state;
        return isDenger?<div></div>:createRoutes()
    }
}

ReactDOM.render(
    // <Provider store = {store}>
        <LoginDenger/>,
    //{/* </Provider>, */}
    
    document.getElementById("root")
)


