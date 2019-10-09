import React from 'react'
import { Button, Col, Input } from 'antd'
import { SELECTWIDTH, PAGEINDEX, PAGESIZE, DEFAULTVALUE, TIMEFORMAT_ss, TIMEFORMAT, PAGRSIZE_OPTIONS20, DAYFORMAT } from '../../../conf'

export const ButtonInit = (porps) =>
    [<Button key='1' type="primary" onClick={() => { porps.requestTable() }}>查询</Button>,
    <Button key='2' type="primary" onClick={() => { porps.resetState() }}>重置</Button>]


export const HeaderContent = (props) => {
    return (
        <div className="page-title">
            当前位置：{props.headTitle}
            <i className={props.showHide ? "iconfont cur_poi icon-shouqi right" : "iconfont cur_poi icon-zhankai right"} onClick={() => { props.clickHide(props.showHide) }}></i>
        </div>
    )
};
export const InformationInput = (props) => {
    return (
        <div>
            <label className=" control-label">{props.content}</label>
            <div width={SELECTWIDTH}>
                <Input type="text" name={props.name} value={props.value} onChange={(e) => { props.handleInputChange(e) }} />
            </div>
        </div>
    )
};
// 市场配置页面 input 
export const MarketConfigInput = (props) => {
    return (
        <div className="col-md-6 col-sm-6 col-xs-6">
            <div className="form-group">
                <label className="col-sm-3 control-label" dangerouslySetInnerHTML={{ __html: props.content }}></label>
                <div className="col-sm-8">
                    <input type={props.inputType} className="form-control" name={props.name} value={props.value} onChange={(e) => { props.handleInputChange(e) }} disabled={props.disabled || false} />
                </div>
            </div>
        </div>
    )
};