import React from 'react'
import { Input } from 'antd'
import axios from "../../../../utils/fetch";
import {DOMAIN_VIP} from "../../../../conf";
const { TextArea } = Input
export default class ModalAddAndDeleteTag extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            tagMemo: '',
            type: false,
            checkbox: []
        }

    }
    componentDidMount() {
        this.initData(this.props);
    }

    componentWillReceiveProps(nextProps) {
        this.setProps(nextProps)
    }
    initData(props){
        axios.post(DOMAIN_VIP+'/common/getAttrTypeDesc').then(res => {
            const result = res.data;
            let list = result.data;
            let arr = list.map(item=>{
                let obj = {};
                obj.name = item;
                obj.isTrue = false;
                return obj;
            });
            this.setState({
                checkbox: arr
            },()=> {
                this.setProps(props);
            });
        })
    }
    setProps = props => {
        const { type, memo,selected } = props
        let { checkbox } = this.state
        let newcheckbox = checkbox.map((item) => {
            if(selected.includes(item.name)){
                item.isTrue = true;
            } else {
                item.isTrue = false;
            }
            return item
        })
        this.setState({
            type,
            tagMemo: memo || '',
            selected,
            checkbox:newcheckbox
        })
    }
    //输入时 input 设置到 satte
    handleInputChange = (event) =>{
        const target = event.target;
        const value = target.type === 'checkbox' ? target.checked : target.value;
        const name = target.name;
        this.setState({
            [name]: value
        });
        this.props.onChange(event)
    }
    onCheckBoxChange = values => {
        let { checkbox } = this.state;
        checkbox.forEach(element => {
            if (values.name == element.name) {
                element.isTrue = !element.isTrue;
            }
        });
        let selected = checkbox.filter((item) => item.isTrue == true).map((item) => item.name);
        this.setState({
            checkbox,
            selected
        })
        this.props.onCheckBoxChange&&this.props.onCheckBoxChange(selected)
    }
    createCheckBox = (arr) => {
        return arr.map((item) => {
            return <a key={item.name} className={`${item.isTrue ? 'userinfo-checked' : ''} userinfo-check mar10`} href='javascript:void(0);' onClick={() => this.onCheckBoxChange(item)}>{item.name}</a>
        })
    }
    render() {
        const { tagMemo, type } = this.state
        return (
            type
                ?
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-3 control-label">备注：</label>
                        <div className="col-sm-9 text-box">
                            <TextArea name="tagMemo" rows={4} value={tagMemo} onChange={this.handleInputChange} />
                        </div>
                    </div>
                </div>
                :
                <div className="col-md-12 col-sm-12 col-xs-12">
                    {this.createCheckBox(this.state.checkbox)}
                </div>
        )
    }
}