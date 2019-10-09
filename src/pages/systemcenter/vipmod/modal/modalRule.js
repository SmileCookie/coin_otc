import React from 'react'
import { Input,Radio,Switch,Select } from 'antd'
import {SELECTWIDTH} from "../../../../conf";
import RuleTypeList from '../../../common/select/ruleTypeList';
const { TextArea  } = Input;
const RadioGroup = Radio.Group;
const Option = Select.Option;

export default class ModalRule extends React.Component{

    constructor(props){
        super(props);
        this.state = {
            seqNo:0, // 序号名称的code码
            typeCode: "1", //是什么类型的积分
            score: "", // 积分
            integType: 1, //积分类型
            rule: "", //积分规则
            memo: "",// 备注
            status: 1, // 状态
            unit: '', //单位
            daylimit: '',//积分上限
            ruleSelect: 1, //活动积分的规则选择
            activityRadio: '',// 活动积分的规则的radio
            activityMoney:'', //活动里的规则的输入框
            activityIntegral:'',//活动里的规则的积分
            activityCount:'',//活动里的规则按多少递增递减
            punishType: 1,// 惩罚积分的规则的的下拉框
            id: ''
        };
    }

    componentDidMount(){
        let item = this.props.item;
        this.setState({
            id: item.id?item.id: '',
            seqNo: item.seqNo != undefined ? item.seqNo.toString(): 0,
            typeCode: item.typeCode ? item.typeCode: "1",
            score: item.score != undefined ? item.score: '',
            integType: item.integType ? item.integType: 1,
            rule: item.rule?item.rule: '',
            memo: item ? item.memo: '',
            status: item.status != undefined ?item.status: 1, // 状态
            unit: item.unit?item.unit: '',
            daylimit: item.daylimit?item.daylimit: '',
            ruleSelect: 1, //活动积分的规则选择
            activityRadio: '',// 活动积分的规则的radio
            activityMoney:'', //活动里的规则的输入框
            activityIntegral:'',//活动里的规则的积分
            activityCount:'',//活动里的规则按多少递增递减
            punishType: item.punishType?item.punishType:1// 惩罚积分的规则的的下拉框
        })
    }
    componentWillReceiveProps(nextProps){
        let item = nextProps.item;
        this.setState({
            id: item.id?item.id: '',
            seqNo: item.seqNo != undefined ? item.seqNo.toString(): 0,
            typeCode: item.typeCode ? item.typeCode: "1",
            score: item.score != undefined ? item.score: '',
            integType: item.integType ? item.integType: 1,
            rule: item.rule?item.rule: '',
            memo: item ? item.memo: '',
            status: item.status != undefined ?item.status: 1, // 状态
            unit: item.unit?item.unit: '',
            daylimit: item.daylimit?item.daylimit: '',
            ruleSelect: 1, //活动积分的规则选择
            activityRadio: '',// 活动积分的规则的radio
            activityMoney:'', //活动里的规则的输入框
            activityIntegral:'',//活动里的规则的积分
            activityCount:'',//活动里的规则按多少递增递减
            punishType: item.punishType?item.punishType:1// 惩罚积分的规则的的下拉框
        })
    }

    //输入时 input 设置到 satte
    handleInputChange = event => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.handleInputChange(event);
    };
    // radio按钮的选择
    onChange = (e, type) => {
        this.setState({
            [type]: e.target.value,
        });
        this.props.onChooseChange(e.target.value, type);
    };

    onSelectChoose =(val, type) =>{
        this.setState({
            [type]: val
        });
        this.props.onChooseChange(val, type);
    };
    onTypeChoose = (val, type) =>{
        this.setState({
            [type]: val
        });
        this.setState({
            score: "", // 积分
            integType: 1, //积分类型
            rule: "", //积分规则
            memo: "",// 备注
            status: 1, // 状态
            unit: '', //单位
            daylimit: '',//积分上限
            ruleSelect: 1, //活动积分的规则选择
            activityRadio: '',// 活动积分的规则的radio
            activityMoney:'', //活动里的规则的输入框
            activityIntegral:'',//活动里的规则的积分
            activityCount:'',//活动里的规则按多少递增递减
            punishType: 1,// 惩罚积分的规则的的下拉框
        })
        this.props.resetItem();
        this.props.onChooseChange(val, type);
    };
    render(){
        const { seqNo,id, status,typeCode,score,integType,rule,memo,daylimit,unit,ruleSelect,activityRadio,punishType,activityMoney,activityIntegral,activityCount } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                {/*<div className="form-group">*/}
                    {/*<label className="col-sm-3 control-label">序号：<i>*</i></label>*/}
                    {/*<div className="col-sm-9">*/}
                        {/*<input type="text" className="form-control" name="seqNo" value={seqNo} onChange={this.handleInputChange} />*/}
                    {/*</div>*/}
                {/*</div>*/}
                <div className="form-group">
                    <RuleTypeList col="3" scoreName={seqNo} handleChange={ val =>this.onSelectChoose(val, 'seqNo')} />
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">类型：<i>*</i></label>
                    <div className="col-sm-9">
                        <Select disabled={id != '' && id != null && id != undefined} value={typeCode} style={{ width: SELECTWIDTH }} onChange={val=>this.onTypeChoose(val, 'typeCode')}>
                            <Option value="1">普通积分</Option>
                            <Option value="2">累加积分</Option>
                            {/*<Option value="3>活动积分</Option>*/}
                            <Option value="4">惩罚积分</Option>
                        </Select>
                    </div>
                </div>
                {typeCode == '1' && (
                    <div>
                        <div className="form-group">
                            <label className="col-sm-3 control-label">规则：<i>*</i></label>
                            <div className="col-sm-9">
                                <input type="text" className="form-control" name="rule" value={rule} onChange={this.handleInputChange} />
                            </div>
                        </div>
                        <div className="form-group">
                            <label className="col-sm-3 control-label">积分：<i>*</i></label>
                            <div className="col-sm-9">
                                <input type="text" className="form-control" name="score" value={score} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>
                )}
                {typeCode == '2' && (
                    <div className="form-group score-rules">
                        <label className="col-sm-3 control-label">规则：<i>*</i></label>
                        <div className="col-sm-9">
                            <div className="add-integral-rule">
                                用户单次充值/交易金额（折算成USDT）
                            </div>
                            <div className="add-integral-rule">
                                <span>每满足</span>
                                <input style={{width: '50px'}} type="text" placeholder="100" className="form-control" name="unit" value={unit} onChange={this.handleInputChange} />
                                <span>获得</span>
                                <input style={{width: '50px'}} type="text" placeholder="25" className="form-control" name="score" value={score} onChange={this.handleInputChange} />
                                <span>积分</span>
                            </div>
                            <div className="add-integral-rule">
                                <span>单日积分获取上限</span>
                                <input style={{width: '70px'}} type="text" placeholder="50000" className="form-control" name="daylimit" value={daylimit} onChange={this.handleInputChange} />
                                <span>总计</span>
                            </div>

                        </div>
                    </div>
                )}
                {typeCode == '3' && (
                    <div className="form-group score-rules">
                        <label className="col-sm-3 control-label">规则：<i>*</i></label>
                        <div className="col-sm-9">
                            <div className="add-integral-rule">
                                <span>满足</span>
                                <input style={{width: '50px'}} type="text" className="form-control" name="activityMoney" value={activityMoney} onChange={this.handleInputChange} />
                                <RadioGroup size="small" onChange={e=>this.onChange(e, 'activityRadio')} value={activityRadio}>
                                    <Radio style={{marginRight: '0px'}} value="1">减少</Radio>
                                    <Radio style={{marginRight: '0px'}} value="2">增加</Radio>
                                </RadioGroup>
                                <input style={{width: '50px'}} type="text" className="form-control" name="activityIntegral" value={activityIntegral} onChange={this.handleInputChange} />
                                <span>积分</span>
                            </div>
                            <div className="add-integral-rule">
                                <span>按</span>
                                <input style={{width: '70px'}} type="text" className="form-control" name="activityCount" value={activityCount} onChange={this.handleInputChange} />
                                <span>递增/递减（无需则填写0）</span>
                            </div>
                            <div className="add-integral-rule">
                                <Select value={ruleSelect} style={{ width: '100px' }} onChange={val=>this.onSelectChoose(val, 'ruleSelect')}>
                                    <Option value={1}>投票</Option>
                                    <Option value={2}>抽奖</Option>
                                </Select>
                            </div>
                        </div>
                    </div>
                )}
                {typeCode == '4' && (
                    <div className="form-group score-rules">
                        <label className="col-sm-3 control-label">规则：<i>*</i></label>
                        <div className="col-sm-9">
                            <div className="add-integral-rule">
                                <span>用户连续</span>
                                <input style={{width: '50px'}} type="text" className="form-control" name="unit" value={unit} onChange={this.handleInputChange} />
                                <span>小时未</span>
                                <Select value={punishType} style={{ width: '80px',marginLeft: '4px' }} onChange={val=>this.onSelectChoose(val, 'punishType')}>
                                    <Option value={1}>登录</Option>
                                    <Option value={2}>交易</Option>
                                    <Option value={3}>充值</Option>
                                </Select>
                            </div>
                            <div className="add-integral-rule">
                                <span>惩罚</span>
                                <input style={{width: '70px'}} type="text" className="form-control" name="score" value={score} onChange={this.handleInputChange} />
                                <span>积分</span>
                            </div>
                        </div>
                    </div>
                )}
                <div className="form-group">
                    <label className="col-sm-3 control-label">积分类型：</label>
                    <div className="col-sm-9">
                        <RadioGroup onChange={e=>this.onChange(e,'integType')} value={integType}>
                            <Radio value={1}>一次性</Radio>
                            <Radio value={2}>周期性</Radio>
                            <Radio value={3}>重复性</Radio>
                        </RadioGroup>
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">状态：<i>*</i></label>
                    <div className="col-sm-9">
                        <Switch checked={status == 1} onChange={val=>this.onSelectChoose(val, 'status')} />
                    </div>
                </div>
                <div className="form-group">
                    <label className="col-sm-3 control-label">备注：</label>
                    <div className="col-sm-9 text-box">
                        <TextArea rows={4} name="memo" maxLength="50" value={memo} onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>
        )
    }
}
