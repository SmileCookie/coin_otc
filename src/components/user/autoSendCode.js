/**
 * @desc: 验证码录入完成后自动 提交
 * @author: Eric Ye
 * @Date: 2019-08-20
 */

import React,{Component} from 'react';
import Form from '../../decorator/form';

@Form
class AutoSendCode extends Component {
    constructor(props){
        super(props);
        this.state ={
            len:1,
            inputList:[],
            currKeyCode:'',
            func:'',
            ...this.props
        }
    }
    componentWillMount() {

    }
    componentDidMount() {
        let {len,inputList} = this.state;
        inputList = new Array(len).fill({value:''});
        this.setState({
            inputList
        })
    }
    // 下一个焦点
    nextFocus = (code,index) => {
        let dom = document.querySelectorAll(".autocode-input"),
            currInput = dom[index],
            nextInput = dom[index + 1],
            prevInput = dom[index - 1];

        if ((code >= 47 && code <= 58) || (code >=96 && code <=105)) {
            if (index < (this.state.inputList.length - 1)) {
                nextInput.focus();
            } else {
                currInput.blur();
            }
        }
        this.autoSubmit();
    }
    // 上一个焦点
    prevFocus = (code,index) =>{
        let dom = document.querySelectorAll(".autocode-input"),
            currInput = dom[index],
            nextInput = dom[index + 1],
            prevInput = dom[index - 1];

        if(code == 8){
            if (index !=0) {
                console.log(currInput);
                if (currInput.value){
                    let {inputList} = this.state;
                    inputList[index] = '';
                    this.setState({
                        inputList: [...inputList]
                    },() =>{
                        currInput.focus();
                    })
                }else {
                    prevInput.focus();
                }
            }
        }
    }


    changeValue = (e,i) => {
        let {inputList} = this.state;
        let obj = {
            value : e.currentTarget.value
        }
        inputList[i] = obj;
        this.setState({
            inputList: [...inputList]
        },() =>{
            console.log(this.state.inputList);
            let {currKeyCode} = this.state;
            this.nextFocus(currKeyCode,i);
        })

    }

    // 校验输入 只能为数字
    ckCode = (i) =>{
        if ((event.keyCode >= 47 && event.keyCode<=58) || (event.keyCode>=96 && event.keyCode<=105 )) {
            this.setState({
                currKeyCode: event.keyCode
            })
            event.returnValue = true;
        }else if(event.keyCode == 8){
            this.setState({
                currKeyCode: event.keyCode
            })
            this.prevFocus(event.keyCode,i);
            console.log(event)
            event.returnValue = true;
        }else{
            event.returnValue=false;
        }
    }
    // 自动提交
    autoSubmit = () =>{
        let {inputList} = this.state;
        let tempList = [];
        for(let l of inputList){
            if (!l.value){
                return false;
            }
            tempList.push(l.value);
        }
        let codes = tempList.join('');
        console.log('codes===>' + codes);
        this.props.func(codes)
    }
    // 清除父组件 Error
    clearError = () =>{
        // todo
        this.props.clearError()
    }

    render() {
        const {inputList} = this.state;
        const {PK} = this.props;
        let {fIn} = this;
        return (
            <div className="second-check-code">
                {
                    inputList.map((v,i) =>{
                        return (
                            <input className={`autocode-input ${v.value  && 'active'}`} autoComplete="off" name={PK}
                                   key={i} type="text"  value={v.value} maxLength={1} onFocus={() =>{this.clearError()}}
                                   onChange={(e) =>{ this.changeValue(e,i)}} onKeyDown={() =>{this.ckCode(i)}}/>
                        )
                    })
                }
            </div>
        )
    }
}

export default AutoSendCode;
