import React from "react";

import Form from '../../decorator/form';

@Form
class Authening extends React.Component{
    constructor(props){
        super(props)
    }

    render(){
        const { formatMessage } = this.intl;

        return(
            <div className="content">
                <div className="authen">
                    <div className="authen-suc" style={{textAlign:'center'}}><svg className="icon icon24" aria-hidden="true"><use xlinkHref="#icon-tijiaorenzheng"></use></svg>{formatMessage({id: "您的认证文件已提交，请耐心等待工作人员审核bh"})}</div>
                </div> 
            </div>            
        )
    }
}
export default Authening