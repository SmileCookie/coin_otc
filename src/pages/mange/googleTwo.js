import React from "react";
import { Link } from "react-router"
import { formatURL } from "../../utils"

class GoogleTwo extends React.Component{
    constructor(){
        super();
        
    }
    render(){
        return(
                <div className="content">
                    <div className="google">
                        <div className="google-verCode">
                            <div className="vercode-lable">设置谷歌验证码</div>
                            <div className="vercode-input">
                                <input placeholder="请输入验证码" />
                            </div>
                        </div>
                        <Link className="next-btn" to={formatURL('')}>验证并开启</Link>
                    </div>
                </div>
        )
    }
}
export default GoogleTwo