
import { SHOW_TIME_DEFAULT, TIMEFORMAT_ss, TIME_PLACEHOLDER } from '../../conf'
import { DatePicker } from 'antd'
const { RangePicker, } = DatePicker;

/**
 * 
 * 
 * 
 */
class RPicker extends React.Component {
    constructor(props) {
        super(props)
    }
    render() {

        const { time = [], title = '时间组件', } = this.props
        return (
            <div className="col-mg-4 col-lg-6 col-md-4 col-sm-4 col-xs-4">
                <div className="form-group">
                    <label className="col-sm-3 control-label">{title}:</label>
                    <div className="col-sm-8">
                        <RangePicker
                            showTime={{
                                defaultValue: SHOW_TIME_DEFAULT
                            }}
                            format={TIMEFORMAT_ss}
                            placeholder={TIME_PLACEHOLDER}
                            onChange={(date, dateString) => this.props.onChangeCheckTime && this.props.onChangeCheckTime(date, dateString)}
                            value={time}
                        />
                    </div>
                </div>
            </div>
        )
    }
}

export {
    RPicker
}