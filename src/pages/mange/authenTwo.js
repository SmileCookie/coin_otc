import React from "react";
import { Link } from 'react-router';
import { formatURL } from "../../utils"

import Form from '../../decorator/form';
import GetCode from '../../components/phonecode/getCountryCode';
import list from '../../components/phonecode/country';
import A from '../../assets/img/passCardSmall.png';
import B from '../../assets/img/shenfenzheng.png';

@Form
class AuthenTwo extends React.Component{
    constructor(props){
        super(props);

        this.state = {
            selectedCode: '+86',
            countName: '中国',
            isRotate:false
        };

        this.getCurrentSelectedCode = this.getCurrentSelectedCode.bind(this);
        this.isRotate = this.isRotate.bind(this)
        this.getChildMethod = this.getChildMethod.bind(this);
    }

    componentDidMount(){
        const { selectedCode, countName } = this.state;
        sessionStorage.setItem("countryCode", selectedCode);
        sessionStorage.setItem("countName", countName);
    }

    getCurrentSelectedCode(code = "", name = ""){
        sessionStorage.setItem("countryCode", code);
        sessionStorage.setItem("countName", name);
    }
    isRotate(isRotate){
        console.log(isRotate)
        this.setState({
            isRotate,
        })
    }
    getChildMethod(){
        this.refs.giveMethon.setDropDownState()
    }

    render(){
        const { formatMessage } = this.intl;

        return(
            <div className="mfwp" style={{paddingBottom: '140px'}}>
                <div className="uauth_wp">
                    <div className="pgoback">
                        <Link className="ptext-btn" to={formatURL('authenOne')}>
                            <i className="iconfont icon-fanhui-moren"></i>
                            {formatMessage({id: "返回上一步"})}
                        </Link>
                    </div>
                    <div className="pinformation clearfix">
                        <div className="pinforma-title">{formatMessage({id: "选择发证国家"})}</div>
                        <div className="pinfoma-con clearfix">
                            <div className="con-input">
                                <div className="input-lable">{formatMessage({id: "国家"})}</div>
                                <div className="input-con input-w100 k plv  border_none">
                                    {/* <em onClick={this.getChildMethod} className={`iconfont iconfont_dguojia ${this.state.isRotate && 'is_rotate'}`} style={{cursor:'pointer'}}>&#xe681;</em> */}
                                    <GetCode ref="giveMethon" startMove={ false } style={{}} selectedCode={this.state.selectedCode} list={list.country} getCurrentSelectedCode={this.getCurrentSelectedCode} isRotate={this.isRotate}></GetCode>
                                </div>
                            </div>
                        </div>                       
                    </div>
                    <div className="pinformation clearfix">
                        <div className="pinforma-title">{formatMessage({id: "选择证件类型"})}</div>
                        <div className="cordtype ">
                            <div className="w284">
                                <Link to={formatURL('authenThree?go=1')}>
                                    <p className="cordimg imgw166">
                                        <img src={A} />
                                    </p>
                                    <p className="cordna">{formatMessage({id: "护照"})}</p>
                                </Link>
                            </div>
                            <div className="w284 let-top">
                                <Link to={formatURL('authenFour?go=1')}>
                                    <p className="cordimg imgw217">
                                        <img src={B} />
                                    </p>
                                    <p className="cordna">{formatMessage({id: "身份证"})}</p>
                                </Link>
                            </div>
                        </div>
                    </div>
                    {
                    false
                    &&
                    <p className="mpt">{formatMessage({id: "温馨提示：为了保证个人信息不被盗用，同一证件只能一个账户使用，请确保您上传的证件真实有效。"})}</p>
                    }
                </div>
            </div>
        )
    }
}
export default AuthenTwo