/**VDS行情 修改弹窗*/
import Decorator from '../../../decorator'
import axios from '../../../../utils/fetch'
import { Button, message, Select } from 'antd'
import { DOMAIN_VIP, SELECTWIDTH } from '../../../../conf'
const { Option } = Select;

@Decorator()
export default class ModalMarket extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            id:'',
            isQuant:'',
            isHedge:''
        }

    }

    componentDidMount() {
        this.setState({
            id: this.props.record.id,
            isQuant: this.props.record.isQuant,
            isHedge: this.props.record.isHedge
        })
    }
    componentWillReceiveProps(props){
        this.setState({
            id: props.record.id,
            isQuant: props.record.isQuant,
            isHedge: props.record.isHedge
        })
    }

    //修改数据
    putData = () => {
        const {id,isQuant,isHedge}=this.state;
        axios.post(DOMAIN_VIP + '/brush/config/updateConfigMarketPlatform', qs.stringify({
            id,isQuant,isHedge,
        })).then(res => {
            const result = res.data;
            if (result.code == 0) {
                this.props.handleCancel();
                this.props.requestTable();
                message.success(result.msg)
            } else {
                message.error(result.msg)
            }
        })
    }






    render() {
        const {isQuant, isHedge } = this.state
        return (
            <div>
                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-6 " style={{lineHeight:2.2}}>是否是刷量参考平台:</label>
                        <div className="col-sm-4">
                            <Select value={isQuant=='0'? '是':'否'} style={{ width: 71 }} onChange={(v) => this.onSelectChoose(v, 'isQuant')} >
                                <Option value=''>请选择</Option>
                                <Option value='0'>是</Option>
                                <Option value='1'>否</Option>
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-6 " style={{lineHeight:2.2}}>是否是保值参考平台:</label>
                        <div className="col-sm-4">
                            <Select value={isHedge=='0'? '是':'否'} style={{ width: 71 }} onChange={(v) => this.onSelectChoose(v, 'isHedge')} >
                                <Option value=''>请选择</Option>
                                <Option value='0'>是</Option>
                                <Option value='1'>否</Option>
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12" style={{borderTop:'1px solid #f0f0f0',paddingTop:'20px'}}>
                    <div className="col-mg-6 col-lg-6 col-md-6 col-sm-6 col-xs-6"  style={{float:'right'}}>
                     <Button key="submit" type="more" style={{float:'right'}} onClick={this.putData}>保存修改</Button>
                     <Button key="back" style={{float:'right'}} onClick={this.props.handleCancel}>取消</Button>
                    </div>
                    
                </div>
            </div>
        )
    }

}


