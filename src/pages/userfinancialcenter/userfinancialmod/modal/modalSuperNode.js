/**超级节点管理 弹框 */
import Decorator from 'DTPath'

import { Button, Divider } from 'antd'
import { NODE_TYPE, IS_SHOW, NODE_STATE } from 'Conf'
import { toThousands, TE, ckd, dateToFormat } from 'Utils'
import { SeOp } from '../../../../components/select/asyncSelect'
import { node } from 'prop-types';

const Big = require('big.js')

@Decorator()
export default class ModalSuperNode extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            item: {}
        }
    }
    componentDidMount(){
        this.setDefault(this.props.item)
    }
    componentWillReceiveProps(nextProps){
        this.setDefault(nextProps.item)
    }
    setDefault = item => {
        this.setState({item})
    }
    onSelect = (v, k) => {
        this.setState({
            item: Object.assign({}, this.state.item, { [k]: v })
        })
    }
    onSave = async () => {
        await this.request({url:'/supernode/update',type:'post',isP:true},this.state.item)
        this.props.requestTable()
        this.props.cpMCancel()
    }
    render() {
        const {onSelect} = this
        const {loading,item} = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">节点名称：</label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control" name="market" value={item.snodename || ''} disabled />
                        </div>
                    </div>
                </div>
                <div className="col-md-6 col-sm-6 col-xs-6">
                    <div className="form-group">
                        <label className="col-sm-3 control-label ">节点地址：</label>
                        <div className="col-sm-9">
                            <input type="text" className="form-control" name="market" value={item.snodeaddr ||''} disabled />
                        </div>
                    </div>
                </div>
                <SeOp clName="col-md-6 col-sm-6 col-xs-6" title='节点类型' value={item.snodetype || ''} ops={NODE_TYPE} onSelectChoose={v => onSelect(v, 'snodetype')} pleaseC />
                <SeOp clName="col-md-6 col-sm-6 col-xs-6" title='归属类型' value={item.snodebeltype || ''} ops={NODE_TYPE} onSelectChoose={v => onSelect(v, 'snodebeltype')} pleaseC />
                <SeOp clName="col-md-6 col-sm-6 col-xs-6" title='显示标志' value={item.snodeshowflag || ''} ops={IS_SHOW} onSelectChoose={v => onSelect(v, 'snodeshowflag')} pleaseC />
                <SeOp clName="col-md-6 col-sm-6 col-xs-6" title='节点状态' value={item.snodestate} ops={NODE_STATE} onSelectChoose={v => onSelect(v, 'snodestate')} pleaseC />
                <Divider />
                <div className="col-md-12 col-sm-12 col-xs-12 right">
                    <div className="right">
                        <Button key="back" onClick={this.props.cpMCancel}>取消</Button>
                        <Button key="submit" type="more" loading={loading} onClick={this.onSave} >保存修改</Button>
                    </div>
                </div>
            </div>
        )
    }
}