/**
 * 保险弹窗首页
 * @description 对外暴露开关是否打开。
 * @author luchao.ding
 * @since 2019-08-28
 */
import React from 'react';
import ReactModal from '../../components/popBox';
import PropTypes from 'prop-types';
import Fm from './form';
import Prop from './prop';
import './ins.less';
import { optPop } from '../../utils/index';

class InsuranceForm extends React.Component{
    static PropTypes = {
        open:PropTypes.number.isRequired,
    }
    static defaultProps = {
        open: 1, // 1 打开 0 关闭
    }
    constructor(props){
        super(props);

        this.state = {
            dialogtos:null,
        }

        this.modaltos = null;

        this.cb = this.cb.bind(this);

        this.sDialog = this.sDialog.bind(this);
    }
    sDialog(props){
        props.open && this.modaltos.openModal();
        this.setState({
            dialogtos: <Fm close={this.modaltos} cb={this.cb} />
        })
    }
    cb(dt = {}){
        this.modaltos.closeModal();

        // 如果服务端错误直接构建错误消息，反之构建成功对话框。
        if(dt.isSuc || dt.dealFlg){
            this.setState({
                dialogtos: <Prop close={this.modaltos.closeModal} {...dt} />
            }, () => {
                this.modaltos.openModal();
            })
        } else {
            optPop(()=>{}, dt.msg, undefined, true);
        }
    }
    componentDidMount(){
        this.sDialog(this.props);
    }
    componentWillReceiveProps(np){
        if(np.open != this.props.open){
            this.sDialog(np);
        }
    }
    render(){
        
        return (
            <ReactModal ref={modal => this.modaltos = modal}>
                {this.state.dialogtos}
            </ReactModal>
        );
    }
}

export default InsuranceForm;

