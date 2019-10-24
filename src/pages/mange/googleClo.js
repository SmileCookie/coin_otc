import React from "react";
import { Link } from "react-router"
import { formatURL } from "../../utils"

class GoogleClo extends React.Component{
    constructor(){
        super();
        
    }
    render(){
        return(
            <div className="content">
                <div className="google">
                    <div className="google-verCode">
                        <div className="vercode-lable">邮箱验证码</div>
                        <div className="vercode-input">
                            <input className="input-border" placeholder="请输入验证码" /><a className="text-btn" href="javascript:void(0)">获取</a>
                        </div>
                    </div>
                    <div className="google-verCode">
                        <div className="vercode-lable">谷歌验证码</div>
                        <div className="vercode-input">
                            <input placeholder="" />
                        </div>
                    </div>
                    <Link className="next-btn" to={formatURL('')}>关闭谷歌验证码</Link>
                </div>
            </div>        
        )
    }
}
export default GoogleClo