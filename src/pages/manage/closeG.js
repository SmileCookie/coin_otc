import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import { doCloseG } from '../../redux/modules/usercenter'; 
import Sms from '../../components/user/sms';
const CODETYPE = 37;

@connect(
    state => ({usercenter: null}),
    {
        doCloseG,   
    },
)
@Form
class CloseG extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            gcode: '',
            emailcode: '',
            codeType: CODETYPE,
        };

        this.state = {
            ...this.base,
        };

        this.dictionaries = [...Object.keys(this.base)];

        this.cm = this.cm.bind(this);
    }
    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doCloseG(this.state, this.callError, 'gcode');
        }
    }
    componentDidMount(){
        // init get code msg queue
        this.initGetCodes(['emailcode']);
    }
    render(){
        const { formatMessage } = this.intl;
        const { gcode, errors, emailcode, codeType } = this.state;
        const { setGCode, cm, fIn, bOut, setEmailcode, setGetCodes } = this;
        const { gcode:egcode = [], emailcode:eemailcode = [] } = errors;

        return (
            <div className="mfwp" style={{paddingBottom: '180px'}}>
                <form className="uauth_wp">
                    <ul className="list">
                        <li className={eemailcode[0] && 'err'}>
                            <h3>{formatMessage({id: "邮箱验证码"})}</h3>
                            <div className="plv">
                                <input autoComplete="off" type="text" className="i1" placeholder={formatMessage({id: "请输入邮箱验证码（水印）"})} onPaste={setEmailcode} onChange={setEmailcode} value={emailcode} name="emailcode" onFocus={fIn} />
                                <Sms {...{codeType}} fn={(k, v)=>{setGetCodes('emailcode', 1);this.callError(k, v)}} sendUrl="/userSendCode" errorKey="emailcode" otherData={{type:2}} codeType={CODETYPE} />
                            </div>
                            <span className="ew">{eemailcode[0]}</span>
                        </li>
                        <li className={`lst3x ${egcode[0] && 'err'}`}>
                            <h3>{formatMessage({id: '谷歌验证码'})}</h3>
                            <input onPaste={setGCode} onChange={setGCode} value={gcode} name="gcode" type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} onFocus={fIn} onBlur={bOut} />
                            <span className="ew">{egcode[0]}</span>
                        </li>
                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "关闭谷歌验证"})} className="i3 v" style={{width:'auto',height:'auto',padding:'5px 42px'}}/>
                     </div>
                </form>
            </div>
        );
    }
}

export default CloseG;