import React from 'react'
import { Checkbox, Row, Col } from 'antd';

export default class ModalCustomerOpe extends React.Component{

    constructor(props){
        super(props)
        this.state ={
            limitCause:'',
            defaultVal:[],
        }
        this.onChange = this.onChange.bind(this)
    }
    componentDidMount(){
        console.log(this.props.item)
        const { limitCause,customerOperation } = this.props.item

        let defaultVal = customerOperation?customerOperation.split(','):[];
        this.setState({
            limitCause,
            defaultVal
        },()=>{console.log(this.state.limitCause)})
    }

    componentWillReceiveProps(nextProps){
        const { limitCause,customerOperation } = nextProps.item
        let defaultVal = customerOperation?customerOperation.split(','):[];
        this.setState({
            limitCause,
            defaultVal
        },()=>{console.log(this.state.limitCause)})
    }

    onChange(checkedValues){
        console.log('checked = ', checkedValues);
        this.setState({
            defaultVal:checkedValues
        })
        this.props.onChange(checkedValues)
    }

    render(){
        const {limitCause,defaultVal} = this.state
        return(
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="x_panel">
                    {this.props.onlySee==false&&
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
                                        {/* <Col span={12}><Checkbox value="04" >法币交易受限</Checkbox></Col> */}
                                        {/* <Col span={12}><Checkbox value="05" >期货交易受限</Checkbox></Col> */}
                                    </Row>
                                </Checkbox.Group>
                            </div>
                        </div>
                        
                    </div></div>}

                </div>
            </div>
        )
    }
}
























