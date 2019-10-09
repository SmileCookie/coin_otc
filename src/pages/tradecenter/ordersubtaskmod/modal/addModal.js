import React from 'react'
import MarketList from '../../../common/select/marketrequests'
import { Select } from 'antd'
const { Option } = Select;

export default class AddModal extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            market: '',
            priceType: '',
            startPrice:''
        }
        this.changeTodoName = this.changeTodoName.bind(this)
        this.handleInputChange = this.handleInputChange.bind(this)
    }
    componentDidMount() {
        const { market, priceType,startPrice } = this.props
        this.setState({
            market: market || '',
            priceType: priceType || '',
            startPrice:startPrice ||''
        })
    }



    changeTodoName=(priceType)=> {
        this.setState({
            priceType
        })
        this.props.changeTodoName && this.props.changeTodoName(priceType)
    }

    handleInputChange = (event) => {
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.changestartPrice && this.props.changestartPrice(value)
    }



    handleSelectMarket = (market) => {
        this.setState({ 
            market
         })
        this.props.handleSelectMarket && this.props.handleSelectMarket(market)
    }





    componentWillReceiveProps(nextProps) {
        const { market,priceType,startPrice } = nextProps
        this.setState({
            market: market || '',
            priceType: priceType || '',
            startPrice:startPrice || ''
        })
    }
    handleSelectMarket = (market) => {
        this.setState({ market })
        this.props.handleSelectMarket && this.props.handleSelectMarket(market)
    }
    render() {
        const { market, priceType ,startPrice} = this.state
        return (
            <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                <MarketList market={market} underLine={true} col='3' handleChange={this.handleSelectMarket} />
                <div className="form-group">
                    <label className="col-sm-3 control-label">启动价格：</label>
                    <div className="col-sm-4" style={{width:122,paddingRight:0}}>
                        <Select value={priceType} style={{ width: 100}} onChange={this.changeTodoName}>
                            <Option value="">请选择</Option>
                            <Option value="0">{'<='}</Option>
                            <Option value="1">{'>='}</Option>
                        </Select>
                    </div>
                    <div className="col-sm-4" style={{width:110,padding:0,borderRadius:4}} >
                       <input type="text" style={{width:100,borderRadius:4}} className="form-control" name="startPrice" value={startPrice} onChange={this.handleInputChange} />
                    </div>
                </div>
            </div>


        )
    }
}