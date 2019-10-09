import React from 'react'
import ReactDOM from 'react-dom'
import {USERID} from 'conf';

/**
 * 
 */

class Empty extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            hidden: false
        }
    }

    componentDidMount(){
        if (USERID) {
            this.setState({
                hidden: true
            })
        }
    }

    componentWillUnmount(){
       
    }

    componentDidUpdate(prevProps, prevState){
        
    }

   
    render(){
        const {hidden} = this.state
        return(
            <div style={{width: '100vw',
                        height: '100vh',
                        position: 'fixed',
                        zIndex: '1000',
                        background: '#17191F',
                        top: '0',
                        left: '0'}} >
            </div>
        )
    }
}

export default Empty
