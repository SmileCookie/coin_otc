import React from 'react';
import '../../assets/css/gcoin.less';
import { injectIntl } from 'react-intl';
import CheckBox from '../../components/form/checkbox';
import { Link } from 'react-router';
import { formatURL } from '../../utils';

class Apply extends React.Component{
    constructor(props){
        super(props);
    }

    render(){
        const { formatMessage } = this.props.intl;
        return (<div className="gcoin_wp">
            <div className="gcm">
                <h2 className="gctith">{formatMessage({id: 'bbyh上币项目审核须知'})}</h2>
                <div className="gcwz">
                    <ul>
                        <li><h3>{formatMessage({id: 'bbyh一．上币申请条件'})}</h3></li>
                        <li>{formatMessage({id: 'bbyh为保护投资者的利益，我们会对上币申请作出如下说明，所有上线的币种要通过我们的审核，需要满足但不限于以下条件。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 无政策风险，达到专业及合规要求，不存在违法违规的可能。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 项目提交资料完整且准确。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 具备强劲的技术架构或有落地应用。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 能真实及时披露项目信息，包含项目白皮书、定期发展及进度报告。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 强有力的团队且社区活跃度高。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 交易平台关于上线币种交易的其他要求。'})}</li>
                    </ul>
                </div>

                <div className="gcwz">
                    <ul>
                        <li><h3>{formatMessage({id: 'bbyh二. 流程说明'})}</h3></li>
                        <li>{formatMessage({id: 'bbyh1.申请：完整填写项目申请资料。'})}</li>
                        <li>{formatMessage({id: 'bbyh2.审核：我们会对项目进行审核，初审结果会邮件通知项目方，审核时间：7-10个工作日。'})}</li>
                        <li>{formatMessage({id: 'bbyh3.商谈：通过审核的项目，双方就上币事项进行商谈。'})}</li>
                        <li>{formatMessage({id: 'bbyh4.上币：商谈成功的项目会排期进行上币。'})}</li>
                    </ul>
                </div>

                <div className="gcwz">
                    <ul>
                        <li><h3>{formatMessage({id: 'bbyh三、币种下线说明'})}</h3></li>
                        <li>{formatMessage({id: 'bbyh为保护投资者利益，平台保留项目下线的权利，项目方如果触犯如下条件，我们会公告通知交易下线，包含但不限于：'})}</li>
                        <li>{formatMessage({id: 'bbyh* 突然出现分叉。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 信息披露出现虚假谎报、重大偏差。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 项目方面临重大法律问题。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 严重的技术或安全问题没有及时得到解决。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 连续10个交易日出现零成交。'})}</li>
                        <li>{formatMessage({id: 'bbyh* 不满足继续交易的其他事项。'})}</li>
                        <li>{formatMessage({id: 'bbyh我们会对决定下线的项目提前5天发出下线公告 ，用户将有30天的期限移出资产。'})}</li>
                    </ul>
                </div>

                <div className="gcwz">
                    <div className="gcft"></div>
                    <div className="gcftp">
                        <CheckBox /><span>{formatMessage({id: 'bbyh我承诺已了解以上项目审核须知，并同意接受此方案。'})}</span>
                    </div>
                </div>

                <div className="tc">
                    <Link to={formatURL('/ca/stepOne')} className="sb">{formatMessage({id: "nuser48"})}</Link>
                </div>

            </div>
        </div>)
    }
}

export default injectIntl(Apply);