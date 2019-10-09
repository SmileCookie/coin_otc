import Decorator from 'DTPath'
import CommonTable from 'CTable'
import { DOMAIN_VIP,PAGEINDEX, PAGRSIZE_OPTIONS20, PAGESIZE, SELECTWIDTH, TIMEFORMAT, TIMEFORMAT_ss } from 'Conf'
import { Button, Modal, Tabs, message, Input } from 'antd'
import { SeOp } from '../../../../components/select/asyncSelect'
import axios from '../../../../utils/fetch';



@Decorator()
export default class ModalTip extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            deNumber: '',
            memo: '',
            item: null,
            content: {
                9: '请买家补充真实有效的付款证明图片',
                10: '请买家补充清晰的付款信息的视频',
                11: '请买家补充相关聊天记录的截图或视频',
                12: '请卖家补充相关聊天记录的截图或视频',
                13: '请卖家补充清晰的未收到款项的视频',
                14: '请卖家补充已释放货币的截图'
            },
            contentId: '9',
            loading: false


        }


    }
    componentDidMount() {
        this.setProp(this.props.item)
    }
    componentWillReceiveProps(nextProps) {
        this.setProp(nextProps.item)
    }
    setProp = (item) => {

        this.setState({ item })
        //  console.log(item);
    }
    // onSubmit = async () => {
    //     const { deNumber, memo, item } = this.state
    //     if (!item) {
    //         message.warning('错误')
    //         return
    //     }

    //     const { cointypeid, recordNo,buyUserId,sellUserId,storefreez, newbalance } = item
    //     await this.request({ url: '/otcCapitalCount/subtract', type: 'post',isP:true }, {
    //         amount: deNumber, memo,
    //         coinTypeId: cointypeid, recordNo:recordNo, buyUserId:buyUserId,sellUserId:sellUserId,storefreezBalance: storefreez || '',
    //         balance: newbalance || ''
    //     })
    //     this.props.requestTable()
    //     this.props.handleCancel()
    // }




    onSubmit = async () => {
        const { item } = this.state
        if (!item) {
            message.warning('错误')
            return
        }
        this.setState({
            loading:true
        })
        const { recordNo, buyUserId, sellUserId, recordId, id } = item

        axios.post(DOMAIN_VIP+'/otcComplain/remind',qs.stringify({
            recordNo: recordNo,
            buyUserId: buyUserId,
            sellUserId: sellUserId,
            contentId: this.state.contentId,
            recordId, id,
        })).then(res=>{
            const result=res.data;
            if(result.code==0){
                
                this.props.handleCancel()
                message.success(result.msg)
                this.setState({
                    loading:false
                })
            }else{
                
                message.error(result.msg)
                this.setState({
                    loading:false
                })
            }
        })
       
        // await this.request({ url: '/otcComplain/remind', type: 'post', isP: true }, {

        //     recordNo: recordNo,
        //     buyUserId: buyUserId,
        //     sellUserId: sellUserId,
        //     contentId: this.state.contentId,
        //     recordId, id,


        // })
        
        // // this.props.requestTable()
        // this.props.handleCancel()

    }



    render() {
        const {recordNo, buyUserId, sellUserId, storefreez, coinTypeName } = this.props.item
        // const { deNumber, memo } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">

                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">订单编号:</label>
                        <div className="col-sm-9">
                            {recordNo}
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">买方ID:</label>
                        <div className="col-sm-9">
                            {buyUserId}
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label-large text-right">卖方ID:</label>
                        <div className="col-sm-9">
                            {sellUserId}
                        </div>
                    </div>
                </div>


                <div className="selectData">
                    <SeOp colmg={true} title='提醒内容' value={this.state.contentId} onSelectChoose={v => this.onSelectChoose(v, 'contentId')} ops={this.state.content} colarge={true} pleaseC={false} />
                </div>

                <div className='col-md-12 col-sm-12 col-xs-12 marbot10' style={{ borderBottom: '1px solid #888888', marginTop: '20px' }}></div>
                <div className="col-md-4 col-sm-4 col-xs-4 right">
                    <div className="right">
                        <Button key="back" onClick={this.props.handleCancel}>取消</Button>
                        <Button key="submit" type="more" loading={this.state.loading} onClick={this.onSubmit} >提交</Button>
                    </div>
                </div>
            </div>
        )


    }
}