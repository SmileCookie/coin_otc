/**vip&新人分配记录 修改功能弹窗 */
import React from 'react'
import Decorator from '../../../decorator'
import { Button, Select, message, DatePicker ,Divider} from 'antd'
import { DOMAIN_VIP, SELECTWIDTH, TIMEFORMAT_ss } from '../../../../conf'
import moment from 'moment'
const { Option } = Select;

@Decorator()
export default class ModalDividend extends React.Component {

    constructor(props) {
        super(props)
        this.defaultstate = {
            distflag: '',  // 是否可执行
            distnum: '',//  分配人数
            diststatus: '',// 分配状态
            disttype: '', // 分配类型
            supernodeprofitcount: '',  //分配笔数
            distbal: "", //分配金额
            distbaloriginal: '',// 分配原始金额
            seqno: "", // 分配批次
            usdtprice:'',//分配价格
            distendtime: null,//分配结束时间
            diststarttime: null, // 分配开始时间
            disttime: null,  // 分配时间
            updatetime: null// 结算时间
        }

        this.state = {
            id: '',
            ...this.defaultstate,
        }



    }

    componentDidMount() {
        this.getData(this.props.item)

    }


    componentWillReceiveProps(props) {
        this.getData(props.item)

    }




    getData = (item) => {
        const { id, distflag, distnum, diststatus, disttype, supernodeprofitcount, distbal, distbaloriginal,
            seqno,usdtprice, distendtime, diststarttime, disttime, updatetime } = item


        this.setState({
            id, distflag, distnum, diststatus, seqno, usdtprice:usdtprice ||'', disttype, distbaloriginal:distbaloriginal||'', distbal, supernodeprofitcount:supernodeprofitcount || '',
            diststarttime:  diststarttime? moment(diststarttime): '',
            distendtime: distendtime? moment(distendtime):'' ,
            updatetime: updatetime? moment(updatetime) : '' ,
            disttime: disttime ? moment(disttime) : '',
        })

    }
    // 修改数据
    updateData = () => {
        const { id, distflag, distnum, diststatus, disttype, supernodeprofitcount, distbal, distbaloriginal,
            seqno, usdtprice, distendtime, diststarttime, disttime, updatetime } = this.state
        let params = {
            id, distflag, distnum, diststatus, disttype, seqno, usdtprice,  distbaloriginal, distbal, supernodeprofitcount,
            disttime: moment(disttime).format(TIMEFORMAT_ss) || '',
            updatetime:moment(updatetime).format(TIMEFORMAT_ss) || '',
            diststarttime:moment(diststarttime).format(TIMEFORMAT_ss) || '',
            distendtime:moment(distendtime).format(TIMEFORMAT_ss) || '',
        }
        let msg = '修改成功';
        this.request({ url: '/finUserRewardStatus/update', type: 'post', msg, isP: true }, params)
        this.props.handleCancel()
        this.props.requestTable()


    }


    render() {
        const { distflag, distnum, diststatus, disttype, supernodeprofitcount, distbal, distbaloriginal,
            seqno, usdtprice, distendtime, diststarttime, disttime, updatetime } = this.state


        return (

            <div>
                <div className="x_content">

                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配类型：</label>
                            <div className="col-sm-8 ">
                                <Select value={disttype} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'disttype')}>
                                    <Option value=''>请选择</Option>
                                    <Option value={7}>新人加成</Option>
                                    <Option value={5}>VIP分红</Option>
                                </Select>
                            </div>
                        </div>
                    </div>



                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配批次：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="seqno" value={seqno} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>



                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配笔数：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="supernodeprofitcount" value={supernodeprofitcount} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>


                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配金额：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="distbal" value={distbal} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>


                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配原始金额：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="distbaloriginal" value={distbaloriginal} onChange={this.handleInputChange} />
                            </div>
                        </div>
                    </div>

                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配价格：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="usdtprice" value={usdtprice} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>

                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配状态：</label>
                            <div className="col-sm-8 ">
                                <Select value={diststatus} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'diststatus')} >
                                    <Option value={0}>未分配</Option>
                                    <Option value={1}>已分配</Option>
                                </Select>
                            </div>
                        </div>
                    </div>

                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-4 control-label">是否可执行：</label>
                            <div className="col-sm-8">
                                <Select value={distflag} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'distflag')} >
                                    <Option value={0}>不可执行</Option>
                                    <Option value={1}>可执行</Option>
                                </Select>
                            </div>
                        </div>
                    </div>




                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配人数：</label>
                            <div className="col-sm-8 ">
                                <input type="text" className="form-control" name="distnum" value={distnum} onChange={this.handleInputChange} />

                            </div>
                        </div>
                    </div>


                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'disttime')}
                                    placeholder='选择时间'
                                    value={disttime}
                                />
                            </div>
                        </div>
                    </div>



                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">结算时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'updatetime')}
                                    placeholder='选择时间'
                                    value={updatetime}
                                />
                            </div>
                        </div>
                    </div>

                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配开始时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'diststarttime')}
                                    placeholder='选择时间'
                                    value={diststarttime}
                                />
                            </div>
                        </div>
                    </div>
                    <div className="col-mg-4 col-lg-6 col-md-6 col-sm-6 col-xs-6">
                        <div className="form-group">
                            <label className="col-sm-3 control-label">分配结束时间：</label>
                            <div className="col-sm-8 ">
                                <DatePicker
                                    showTime={{ defaultValue: moment('00:00:00', 'HH:mm:ss') }}
                                    format={TIMEFORMAT_ss}
                                    onChange={(v, d) => this.onChangeCheckTime(v, d, 'distendtime')}
                                    placeholder='选择时间'
                                    value={distendtime}
                                />
                            </div>
                        </div>
                    </div>


                </div>
                <Divider />
                <div className="addFooter">
                    <div className="col-md-6 col-sm-6 col-xs-6 right">
                        <div className="right">
                            <Button type="primary" onClick={() => this.props.handleCancel()}>取消</Button>
                            <Button type="primary" onClick={this.updateData}>提交</Button>
                        </div>
                    </div>
                </div> 
            </div>
        )
    }

}


