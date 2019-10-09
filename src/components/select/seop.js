import { Select } from 'antd'
import { SELECTWIDTH } from 'Conf'
const { Option } = Select

const objToArr = o => Object.keys(o).map(v => ({ k: v, c: o[v] }))

export const SeOp = ({ title, value, onSelectChoose, ops, required, pleaseC, col } = { required: false, pleaseC: true, col: 3 }) => (
    <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
        <div className="form-group">
            <label className={'col-sm-' + col + ' control-label'} >{title}：{required && <i>*</i>}</label>
            <div className={'col-sm-' + (11 - col)}>
                <Select defaultValue='' value={value} style={{ width: SELECTWIDTH }} onChange={onSelectChoose} >
                    {
                        pleaseC ?
                            [<Option key='' value=''>请选择</Option>, ...objToArr(ops).map(op => <Option key={op.k} value={op.k}>{op.c}</Option>)]
                            :
                            ops.map(op => <Option key={op.k} value={op.k}>{op.c}</Option>)
                    }
                </Select>
            </div>
        </div>
    </div>
)
