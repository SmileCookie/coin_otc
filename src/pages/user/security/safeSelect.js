import React from 'react';
import axios from 'axios'
import qs from 'qs'
import { DOMAIN_VIP } from '../../../conf'
import { FormattedMessage, injectIntl ,FormattedDate} from 'react-intl';

class safeSelect extends React.Component{
    constructor(props){
        super(props);
        this.topClick = this.topClick.bind(this);
        this.chooseWay = this.chooseWay.bind(this);
        this.state = {
            showSwitch : false,
        }
        this.securityType = ["loginAuthenType","tradeAuthenType","withdrawAuthenType","withdrawAddressAuthenType"]
        this.openSafePwd = this.openSafePwd.bind(this)
    }
    componentDidMount(){

    }
    componentWillReceiveProps(nextProps){
        
    }

    openSafePwd(){
        axios.post(DOMAIN_VIP+"/manage/auth/changeAuth",qs.stringify({
            category: 2,
            type: 3
        })).then(res => {
            const result = res.data
            if(result.isSuc){
                this.props.notifSend(result.des)
                this.props.fetchSecurityInfo()
            }else{
                this.props.notifSend(result.des)
            }
        })
    }

    topClick(id){
        this.setState({
            showSwitch : !this.state.showSwitch
        },() => this.props.addSpandList(id,this.state.showSwitch))
    }
    chooseWay(id){
        // this.props.item.choose(ide);
        this.props.chooseOne(id);
    }
    render(){
        let addressName = ""
        const {selectInfo,item,selectId,spandArr} = this.props;
        const showSwitch = spandArr.includes(selectId);
        let waysLength = item.Ways.length;
        if(!showSwitch){
            waysLength = 0;
        }
        const itemAuthenType = this.securityType[selectId]
        if(itemAuthenType == "withdrawAddressAuthenType"&&selectInfo){
            addressName = (selectInfo[itemAuthenType]== 0||selectInfo[itemAuthenType]== 1)?"security.text19":"安全模式"
        }else{
            addressName = selectInfo?selectInfo[`${itemAuthenType}Name`]:""
        }
        return(
            <li>
                <div  className="listtop clearfix" onClick={() => this.topClick(selectId)}>
                    <div className="ld"><b className="text-deepgray">{item.Title}</b><span>{item.Info}</span></div>
                    <div className={`md ${this.props.lang}`}>
                    {selectInfo&&(selectInfo[itemAuthenType]!=0||itemAuthenType=="withdrawAddressAuthenType" ?
                        <span className="red">"<FormattedMessage id={`${addressName}`} />"</span>
                        :
                        <span><FormattedMessage id='security.text32' /></span>)
                    }
                    </div>
                    {showSwitch ?
                        <div className="rd rotate-45"><i></i></div>
                        :
                        <div className="rd rotate45"><i></i></div>
                    }
                </div>
                <div className="listbody" style={{height:waysLength*58}} >  
                    <dl>
                        {selectInfo&&item.Ways.map((tem,index)=>{
                                let boolType,btnType;
                                if(itemAuthenType == "withdrawAddressAuthenType"&&index==0){
                                    btnType = (selectInfo[itemAuthenType] != 0 && selectInfo[itemAuthenType] != 1)
                                    boolType = (selectInfo[itemAuthenType] == 0 || selectInfo[itemAuthenType] == 1)?"color-green":""
                                }else{
                                    btnType = selectInfo[itemAuthenType]!=tem.type
                                    boolType = selectInfo[itemAuthenType]==tem.type?"color-green":""
                                }
                                return (
                                    <dd key={tem.type}>
                                        <span className={boolType}>{tem.name}<b>{tem.needGoogle&&selectInfo&&!selectInfo.hasGoogleAtuh&&<FormattedMessage id='security.text33' />}</b></span>

                                        {(!tem.needGoogle&&btnType)?<a onClick={(selectId==1&&index==2)?
                                            ()=>this.openSafePwd():selectId==3?()=>this.props.openAddressAuthen(tem.type):
                                            ()=>{this.chooseWay(tem.id)}}><FormattedMessage id="SecuritySelect"/></a>:
                                            selectInfo&&selectInfo.hasGoogleAtuh&&btnType?
                                                <a onClick={(selectId==1&&index==2)?
                                                    ()=>this.openSafePwd()
                                                    :selectId==3?()=>this.props.openAddressAuthen(tem.type)
                                                    :()=>{this.chooseWay(tem.id)}}>
                                                    <FormattedMessage id="SecuritySelect"/>
                                                </a>:null
                                        }
                                    </dd>
                                )
                            }
                        )}
                    </dl>
                </div>
            </li>
        )
    }
}

export default injectIntl(safeSelect)