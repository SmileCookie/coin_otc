import React from 'react';
import { connect } from 'react-redux';

import Form from '../../decorator/form';
import { doGCkCode } from '../../redux/modules/usercenter'; 

@connect(
    state => ({usercenter: null}),
    {
        doGCkCode,   
    },
)
@Form
class SetGCkCode extends React.Component{
    constructor(props){
        super(props);

        this.base = {
            paycode: '',
        };

        this.state = {
            ...this.base,
        };

        this.dictionaries = [...Object.keys(this.base)];

        this.cm = this.cm.bind(this);
    }
    cm(){
        if(!this.hasError(this.dictionaries)){
            this.props.doGCkCode(this.state, this.callError, 'paycode');
        }
    }
    render(){
        const { formatMessage } = this.intl;
        const { paycode, errors } = this.state;
        const { setPaycode, cm, fIn, bOut } = this;
        const { paycode:epaycode = [] } = errors;

        return (
            <div className="mfwp" style={{paddingBottom: '270px'}}>
                <form className="uauth_wp">
                    <ul className="list">
                        <li className={`lst3x ${epaycode[0] && 'err'}`}>
                            <h3>{formatMessage({id: '设置谷歌验证码'})}</h3>
                            <input autoComplete="off" onPaste={setPaycode} onChange={setPaycode} value={paycode} name="paycode" type="text" className="i1" placeholder={formatMessage({id: "请输入谷歌验证码（水印）"})} onFocus={fIn} onBlur={bOut} />
                            <span className="ew">{epaycode[0]}</span>
                        </li>
                    </ul>
                    <div className="subs">
                        <input onClick={cm} type="button" value={formatMessage({id: "验证并开启"})} className="i3 v" />
                     </div>
                </form>
            </div>
        );
    }
}

export default SetGCkCode;