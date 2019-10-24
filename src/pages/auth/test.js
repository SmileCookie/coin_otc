import React from 'react';
import { injectIntl } from 'react-intl';

import Opt from '../../components/msg/opt';
import Confirm from '../../components/msg/confirm';
import Select from '../../components/form/select';
import Radio from '../../components/form/radio';

class Test extends React.Component{
    constructor(props){
        super(props);

        this.cb = this.cb.bind(this);
    }
    cb(flg){
        console.log(flg);
    }
    render(){
        const { formatMessage } = this.props.intl;
        
        return (
            <div>
                {
                    false &&
                    <Opt msg={`${formatMessage({id: '为了您的账号安全，我们强烈建议您开启安全验证。'})}`} msg2={formatMessage({id:"请选择您的安全验证方式。"})} ft={formatMessage({id: '暂不设置'})} />
                }
                {
                    false && 
                    <Confirm msg={formatMessage({id: '您正在切换高级模式，开启后，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？'})} />
                }
                {
                    false &&
                    <Confirm msg={formatMessage({id: '您正在切换初级模式，开启后，添加新地址时将不会进行安全验证。并且您的账户将被锁定24小时，在此期间不支持提现操作，可正常交易。您是否要继续？'})} />
                }
                {
                    false &&<Confirm msg={formatMessage({id: '您正在切换高级模式，开启后，您的账户将锁定24小时，在此期间不支持提现操作，添加新地址时将进行安全验证，并锁定该地址24小时。您是否要继续？'})} />
                }
                {
                    false && 
                
                <div className="plv theme sp0">
                    <Select list={[{name:"11111",code:1},{name:"111112",code:2}]} currentCode={1}></Select>
                </div>
                }
                { true &&
                <div className="plv theme sp1">
                    <Select list={[{name:"不限",code:1},{name:"买入",code:2}]} currentCode={1}></Select>
                </div>
                }
                {
                    false &&
                    <Radio cb={this.cb} isCk={false}></Radio>
                }
                
            </div>
        );
    }
}

export default injectIntl(Test);