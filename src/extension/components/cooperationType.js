import React from 'react';
import {FormattedMessage} from 'react-intl'
class CooperationType extends React.Component{
    constructor(props){
        super(props);
        this.state= {
            
        }
        
    }
    
    render(){
        // console.log(this.props.data)
        let {data,isChooseCoop} = this.props;
        console.log(data)
        return (
            <div className="cooper-list">
                <ul className="bo-center list-bg">
                    {
                        
                        data.map((item,index) =>{
                            return(
                                <li className={`${item.isChecked?'selected':''}`} key={item.type}>
                                    <div className={`list-img ` + item.imgStyle}>
                                        <img  />
                                    </div>
                                    <div className="line-bg"></div>
                                    <div className="list-title"><FormattedMessage id={item.title}/></div>
                                    <div className="list-info">
                                        {/* <p><FormattedMessage id={item.contentTitle}/></p>
                                        <p className="m36"><FormattedMessage id={item.contentText}/></p> */}
                                        {item.isChecked?
                                        <a className="sureLabel" href="javascript:void(0)" onClick={() => {this.props.chooseCooper(item.type)}}></a>
                                        :<a className="choose-btn" href="javascript:void(0)" onClick={() =>{this.props.chooseCooper(item.type)}}><FormattedMessage id="选择"/></a>}
                                    </div>                                
                                </li>
                            )
                        })
                    }
                    {/* <li className={`${AD && 'selected'}`}>
                        <div className="list-img list-AD ">
                            <img  />
                        </div>
                        <div className="line-bg"></div>
                        <div className="list-title">首页广告</div>
                        <div className="list-info">
                            <p>获得交易所上币权</p>
                            <p className="m36">我是文案我是文案我是文案</p>
                            {AD?<a className="choose-btn" href="javascript:void(0)" onClick={()=>this.chooseCooper('AD')}>已选择</a>:<a className="choose-btn" href="javascript:void(0)" onClick={()=>this.chooseCooper('AD')}>未选择</a>}
                        </div>                                
                    </li> */}
            </ul>
                <div className={!isChooseCoop?'noChoose':'noChoose show'}>
                    <FormattedMessage id= "请选择合作类型"/>  
                </div>
            </div>
        );
    }
}

export default CooperationType;