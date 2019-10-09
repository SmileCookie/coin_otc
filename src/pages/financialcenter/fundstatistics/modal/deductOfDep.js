import Decorator from 'DTPath'
import CommonTable from 'CTable'
import GoogleCode from 'GCPath'
import { PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIMEFORMAT, TIMEFORMAT_ss } from 'Conf'
import { Button, Modal, Tabs, message, Input } from 'antd'
const { TextArea } = Input

@Decorator()
export default class DeductOfDep extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            deNumber: '',
            memo: '',
            item: null
        }
        this.goofn = () => new Map([
            ['default', v => this.onSubmit(v)],
        ])
    }
    componentDidMount() {
        this.setProp(this.props.item)
    }
    componentWillReceiveProps(nextProps) {
        this.setProp(nextProps.item)
    }
    setProp = (item) => {

        this.setState({
            item,
            deNumber: '',
            memo: '',
        })
    }
    onSubmit = async () => {
        const { deNumber, memo, item } = this.state
        
        if (!deNumber) {
            message.warning('扣减数量不能为空！')
            return
        }
        if (deNumber <= 0) {
            message.warning('扣减数量不能小于等于0！')
            return
        }
        if (!memo) {
            message.warning('扣减备注不能为空！')
            return
        }
        if (!item) {
            message.warning('错误')
            return
        }

        const { cointypeid, userid, storefreez, newbalance } = item
        await this.request({ url: '/otcCapitalCount/subtract', type: 'post', isP: true }, {
            amount: deNumber, memo,
            coinTypeId: cointypeid, userId: userid, storefreezBalance: storefreez || '',
            balance: newbalance || ''
        })
        this.props.requestTable()
        this.props.handleCancel()
    }
    render() {
        const { userid, storefreez, coinTypeName } = this.props.item
        const { deNumber, memo } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">用户编号:</label>
                        <div className="col-sm-9">
                            {userid}
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">用户资质:</label>
                        <div className="col-sm-9">
                            取不到暂时不加
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">保证金余额:</label>
                        <div className="col-sm-8">
                            {storefreez} &nbsp;&nbsp;&nbsp;<span>{coinTypeName}</span>
                        </div>
                        
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">扣减数量：<i>*</i></label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control" name="deNumber" value={deNumber} onChange={this.handleInputChange} />
                            <span className="line34 marl10">USDT</span>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">扣减说明：<i>*</i></label>
                        <div className="col-sm-9">
                            <TextArea name='memo' rows={4} value={memo} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                <div className='col-md-12 col-sm-12 col-xs-12 line marbot10' style={{ marginTop: '20px' }}></div>
                <div className="col-md-4 col-sm-4 col-xs-4 right">
                    <div className="right">
                        <Button key="back" onClick={this.props.handleCancel}>取消</Button>
                        <Button key="submit" type="more" onClick={() => this.modalGoogleCode({}, void 0, 'check')} >提交</Button>
                    </div>
                </div>
                <GoogleCode
                    wrappedComponentRef={this.saveFormRef}
                    check={this.state.googleCheck}
                    handleInputChange={this.handleInputChange}
                    mid={new Date()}
                    visible={this.state.googVisibal}
                    onCancel={this.handleGoogleCancel}
                    onCreate={this.handleCreate}
                />
            </div>
        )


    }
}