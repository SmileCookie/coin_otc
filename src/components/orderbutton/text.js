import React from 'react';
import { connect } from 'react-redux';
import { FormattedMessage, injectIntl } from 'react-intl';
import validate from '../checkoutInput/index';
//import {formTest2} from '../checkoutInput/checkForm'
import './orderButton.less'
import {creatErrorMsg,clearRrroMsg,isEmail} from '../../utils'
const checkArr = ['name','age']
const formTest2 = {
    name: {
        validator(rule, value, callback, source, options) {
            /* callback必须执行一次,带参数为错误信息,不带参数为正确 */
            if (!value) {
                callback({
                    errMsg: "请输入邮箱",
                    value,
                    errStatus: true
                });
            }
            if(!isEmail(value)){
                callback({
                    errMsg: "请输入正确的邮箱地址",
                    value,
                    errStatus: true
                });
            }else{
                callback({
                    errMsg: "",
                    value,
                    errStatus: false
                });
            }
            
        }
    },
    age: {
        validator(rule, value, callback, source, options) {
            /* callback必须执行一次,带参数为错误信息,不带参数为正确 */
            if (!value) {
                callback({
                    errMsg: "dasdadddd",
                    value,
                    errStatus: true
                });
            }
            if(!isEmail(value)){
                callback({
                    errMsg: "dsadsadsd",
                    value,
                    errStatus: true
                });
            }else{
                callback({
                    errMsg: "",
                    value,
                    errStatus: false
                });
            }
            
        }
    }
};

// ---------------------------
class OrderButton extends React.Component{
    constructor(props){
        super(props);
        this.state = {
               
            }     
        }
       
    componentWillMount(){
        creatErrorMsg(checkArr,this);
    }

    componentDidMount(){
        
        
        
    }
    componentWillReceiveProps(nextProps){
       
    }
    handleChange = (e) =>{
        let name = e.target.name
        this.setState({
            [name]:Object.assign(this.state[name], {value: e.target.value}) 
        })
    }
    checkout = (e) =>{
        let name = e.target.name;
        console.log(name);
        
        let errInfo = validate({
            descriptor: formTest2,
            source: {
                [name]: this.state[name].value,
            }
        });
        this.setState({ [name]: errInfo.errors[0].message});
         console.log(errInfo);
        
    }
    checkFocus = (e) => {
        let name = e.target.name;

    }
    
    render(){
       let {name,age} = this.state;
       console.log(this.state);
        return(
            <div className="orderButton">
                <input type="text" name="name" value={name.value} onChange={(e) =>this.handleChange(e)} onBlur={(e) =>this.checkout(e)}/>
                <p style={{color:'red'}}>{name.errMsg}</p>
                <input type="text" name="age" value={age.value} onChange={(e) =>this.handleChange(e)} onBlur={(e) =>this.checkout(e)}/>
                <p style={{color:'red'}}>{age.errMsg}</p>
            </div> 
        )
    }
}

export default injectIntl(OrderButton);