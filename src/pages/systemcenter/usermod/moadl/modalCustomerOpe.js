import React from 'react'
import { Checkbox, Row, Col, Input } from 'antd';
const { TextArea } = Input

export default class ModalCustomerOpe extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            limitCause: '',
            defaultVal: [],
            memo: ''
        };
        this.onChange = this.onChange.bind(this);
    }
    componentDidMount() {
        const { limitCause, customerOperation,operationMark } = this.props.item;
        let defaultVal = customerOperation ? customerOperation.split(',') : [];
        this.setState({
            limitCause,
            defaultVal,
            memo:operationMark
        })
    }

    componentWillReceiveProps(nextProps) {
        const { limitCause, customerOperation, memo } = nextProps.item
        let defaultVal = customerOperation ? customerOperation.split(',') : [];
        this.setState({
            limitCause,
            defaultVal,
            memo
        })
    }

    onChange(checkedValues) {
        this.setState({
            defaultVal: checkedValues
        });
        this.props.onChange(checkedValues)
    }
    handleInputChange = e => {
        this.setState({
            memo: e.target.value
        });
        this.props.onChange(e);
    };
    render() {
        const { limitCause, defaultVal, memo } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="x_panel">
                    {this.props.onlySee == false &&
                        <div><div className="x_title">
                            <h4>用户操作类型</h4>
                        </div>
                            <div className="x_content">
                                <div className="form-group">
                                    <label className="col-sm-3 control-label">用户操作类型：</label>
                                    <div className="col-sm-8">
                                        <Checkbox.Group style={{ width: '100%' }} value={defaultVal} onChange={this.onChange}>
                                            <Row>
                                                <Col span={12}><Checkbox value="02" >提现受限</Checkbox></Col>
                                                <Col span={12}><Checkbox value="01" >币币交易受限</Checkbox></Col>
                                                <Col span={12}><Checkbox value="04" >法币交易受限</Checkbox></Col>
                                                {/* <Col span={12}><Checkbox value="05" >期货交易受限</Checkbox></Col> */}
                                            </Row>
                                        </Checkbox.Group>
                                    </div>
                                </div>

                            </div></div>}
                    <div className="x_title">
                        <h4>用户受限展示</h4>
                    </div>
                    <div className="x_content">
                        <div className="table-responsive">
                            <table className="table table-striped jambo_table bulk_action table-linehei table-border">
                                <thead>
                                    <tr className="headings">
                                        <th className="column-title">序号</th>
                                        <th className="column-title">受限规则</th>
                                        <th className="column-title">受限描述</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <tr>
                                        <td>1</td>
                                        <td>运营维护</td>
                                        <td>{!limitCause || limitCause == '03' ? '正常' : '异常'}</td>
                                    </tr>
                                    <tr>
                                        <td>2</td>
                                        <td>资金异常</td>
                                        <td>{!limitCause || limitCause == '03' || limitCause == '01' ? '正常' : '异常'}</td>
                                    </tr>
                                    <tr>
                                        <td>3</td>
                                        <td>系统充值</td>
                                        <td>{!limitCause || limitCause == '03' || limitCause == '01' ? '正常' : '异常'}</td>
                                    </tr>
                                    <tr>
                                        <td>4</td>
                                        <td>风险用户</td>
                                        <td>{!limitCause || limitCause == '03' || limitCause == '01' ? '正常' : '系统风控命中'}</td>
                                    </tr>
                                </tbody>
                            </table>
                        </div>
                        {/* <div className="col-md-12 col-sm-12 col-xs-12"> */}
                            <div className="form-group">
                                <label className="col-sm-1 control-label-sm">备注：</label>
                                <div className="col-sm-11" style={{width:'82%'}}>
                                    <TextArea name="modifyTextArea" rows={4} value={memo} onChange={this.handleInputChange} />
                                </div>
                            </div>
                        {/* </div> */}
                    </div>
                </div>
            </div>
        )
    }
}
























