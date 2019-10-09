import { Select, message } from 'antd'
import axios from '../../utils/fetch'
import { SELECTWIDTH, DOMAIN_VIP } from 'Conf'
import { isMap, isArray, isObj, getType } from 'Utils'
const { Option } = Select

export class AsyncSelect extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            arrList: this.props.paymod ? [] : [<Option key='0' value=''>请选择</Option>],
        }
    }
    componentDidMount() {
        const { arrList } = this.state
        axios.post(DOMAIN_VIP + this.props.url).then(res => {
            const result = res.data;
            if (result.code == 0) {
                let list = result.data;
                list.forEach((v, i) => {
                    arrList.push(<Option key={v.key || i} value={v.key}>{v.value}</Option>)
                })
                this.setState({
                    arrList
                })
            } else {
                message.warning(result.msg)
            }
        })
    }
    render() {
        let col = this.props.col || 3;
        return (
            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                <div className="form-group">
                    <label className={'col-sm-' + col + ' control-label'}>{this.props.title || ''}</label>
                    <div className={'col-sm-' + (11 - col)}>
                        <Select value={this.props.value} style={{ width: SELECTWIDTH }} onChange={this.props.onSelectChoose}>
                            {this.state.arrList}
                        </Select>
                    </div>
                </div>
            </div>
        )
    }
}

/**
 * @function 对象转换成数组, {index:1} => [{key:index,value:1}]
 *  
 * @param {Object} o 
 */

const objToArr = o => Object.keys(o).map(v => ({ key: v, value: o[v] }))

const mapToArr = map => [...map.keys()].map(v => ({ key: v, value: map.get(v) }))


/**
 * @author oliver
 * 
 * @param {String} title
 * @param {any} value
 * @param {function} onSelectChoose 回调函数
 * @param {Object} ops
 * @param {any} pCK 请选择的key、value
 * @param {Boolean} colarge true label 宽度为大的   false 宽度为小
 * @param {Number || String} col 设置label占介个栅格
 * @param {Boolean} required 是否是必填项
 * @param {Boolean} colmg 下拉框的宽度 true：宽度为大的   false 宽度为小
 * @param {Boolean} pleaseC 下拉框内是否有  ‘请选择’
 * @param 
 * 
 */
export const MSeOp = ({ value, onSelectChoose, ops, pleaseC = true, defaultValue = '', disabled = false, pCK = '', width = SELECTWIDTH }) => {
    const ckp = {
        Array: () => ops,
        Object: () => objToArr(ops),
        Map: () => mapToArr(ops)
    }
    const _ops = ckp[getType(ops)]()
    return <Select disabled={disabled} defaultValue={defaultValue} value={value} style={{ width }} onChange={onSelectChoose} >
        {
            pleaseC ?
                [<Option key={pCK || ''} value={pCK || ''}>请选择</Option>, ..._ops.map(op => <Option key={op.key} value={op.key}>{op.value}</Option>)]
                :
                _ops.map(op => <Option key={op.key} value={op.key}>{op.value}</Option>)
        }
    </Select>
}

export const SeOp = ({ title, value, onSelectChoose, ops, required = false, pleaseC = true, col = 3, colmg = false, colarge = false, pCK = '', defaultValue = '', clName = 'col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4' }) => (<div className={colmg ? "col-md-12 col-sm-12 col-xs-12" : clName}>
    <div className="form-group">
        <label className={`col-sm-${col} ${colarge ? ' control-label-large text-right' : ' control-label'}`} >{title}：{required && <i>*</i>}</label>
        <div className={'col-sm-' + (11 - col)}>
            {
                MSeOp({ value, onSelectChoose, ops, pleaseC, defaultValue, disabled: false, pCK })
            }
        </div>
    </div>
</div>
)


/**
 * 
 * @param {*} param0 
 */
export const SingleInput = ({ title = '标题', required = false, fuzzy = false, handleInputChange, value = '', name, type = 'text', placeholder = '' }) => (
    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
        <div className="form-group">
            <label className="col-sm-3 control-label">{title}{required && <i>*</i>}：</label>
            <div className="col-sm-8 ">
                <input type={type} className="form-control" placeholder={placeholder} name={name} value={value} onChange={handleInputChange} />
                {fuzzy && <b className="icon-fuzzy">%</b>}
            </div>
        </div>
    </div>
)


/**
 * 
 * @param {*} param0 
 */
export const DoubleInput = ({ title = '标题', required = false, fuzzy = false, handleInputChange, valueMin = '', valueMax = '', nameMin = '', nameMax = '', type = 'text' }) => (
    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
        <div className="form-group">
            <label className="col-sm-3 control-label">{title}{required && <i>*</i>}：</label>
            <div className="col-sm-8 ">
                <div className="left col-sm-5 sm-box"><input type={type} placeholder='最小值' className="form-control" name={nameMin} value={valueMin} onChange={handleInputChange} /></div>
                <div className="left line34">-</div>
                <div className="left col-sm-5 sm-box"><input type={type} placeholder='最大值' className="form-control" name={nameMax} value={valueMax} onChange={handleInputChange} /></div>
            </div>
        </div>
    </div>
)




