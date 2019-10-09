import React from 'react'
import moment from 'moment'
import { Modal,Radio,Select ,Button,message,Card} from 'antd'
const ButtonGroup = Button.Group;
const RadioGroup = Radio.Group;
const Option = Select.Option;

export default class ModalCloseSecondVerify extends React.Component{
    constructor(props){
        super(props)
        this.state = {
            visible:false,
            loadImg:'',
            frontalImg:'',
            backImg:'',
            width:'',
            previewImage:'',
            imgWidth:500,
            url:'',
            restatus:1,

        }
        this.onImgEnlarge = this.onImgEnlarge.bind(this)
        this.onChangeRadio = this.onChangeRadio.bind(this)
        this.imgSize = this.imgSize.bind(this)
        this.handleCancel = this.handleCancel.bind(this)

    }
    componentDidMount(){
        const { loadImg,frontalImg,backImg,url } = this.props.item
        this.setState({
            loadImg,
            backImg,
            frontalImg,
            url
        })
    }
    componentWillReceiveProps(nextProps){
        const { loadImg,frontalImg,backImg,url } = nextProps.item
        this.setState({
            loadImg,
            backImg,
            frontalImg,
            url
        })
    }
    //图片放大
    onImgEnlarge(src){
        this.setState({
            imgWidth:500,
            visible:true,
            previewImage:src
        })
    }
    //图片放大缩小
    imgSize(type){
        const {imgWidth} = this.state;
        if(type==1){
            if(imgWidth==1000){
                message.warning('已放至最大')
            }else{
                this.setState({
                    imgWidth:imgWidth+100
                })
            }
        }else{
            if(imgWidth==300){
                message.warning('已缩放至最小')
            }else{
                this.setState({
                    imgWidth:imgWidth-100
                })
            }
        }

    }
    //关闭弹窗
    handleCancel(){
        this.setState({
            visible:false
        })
    }
    //按钮切换
    onChangeRadio(e){
        this.setState({
            restatus:e.target.value
        })
        this.props.onChangeRadio(e.target.value)
    }
    render(){
        const { loadImg,frontalImg,backImg,url,restatus,width,visible,previewImage,imgWidth, } = this.state

        return (
            <div className="col-md-12 col-sm-12 col-xs-12">    
                <div className="col-md-12 col-sm-12 col-xs-12 ">
                    <div className="form-group">
                    <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>二次审核图片：</label>
                        <div className="col-md-4 col-sm-4 col-xs-4">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={url} onClick={() => this.onImgEnlarge(url)} />
                        </div>
                    </div>   

                    <div className="form-group">
                        <label className="col-md-12 col-sm-12 col-xs-12 control-label" style={{ width: '100%' }}>实名认证图片：</label>
                        <div className="col-md-4 col-sm-4 col-xs-4">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={frontalImg} onClick={() => this.onImgEnlarge(frontalImg)} />
                        </div>
                        <div className="col-md-4 col-sm-4 col-xs-4">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={backImg} onClick={() => this.onImgEnlarge(backImg)} />
                        </div>                       
                        <div className="col-md-4 col-sm-4 col-xs-4">
                            <img alt="example" style={{ width: '100%',height:'150px' }} src={loadImg} onClick={() => this.onImgEnlarge(loadImg)} />
                        </div>
                    </div>
                    {
                    this.props.item.status==0?
                    <div className="form-group">
                        <label className="col-sm-3 control-label">审核：</label>
                        <div className="col-sm-9">
                            <RadioGroup onChange={this.onChangeRadio} value={restatus}>
                                {/* <Radio className='purple' value={0}>待审核</Radio> */}
                                <Radio className='green' value={1}>通过</Radio>
                                <Radio className='red' value={2}>不通过</Radio>
                            </RadioGroup>
                        </div>
                    </div>
                    :
                    <div className="form-group">
                        <label className="col-sm-3 control-label">审核结果：</label>
                        <div className="col-sm-9 control-label">
                            {this.props.item.status==1?<span className='green'>通过</span>:<span className='red'>不通过</span>}
                        </div>
                    </div>
                    }
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12 ">
                    <Modal 
                        visible={visible}
                        footer={
                            <ButtonGroup>
                                <Button icon='plus' onClick={()=>this.imgSize(1)}></Button>
                                <Button icon='minus' onClick={()=>this.imgSize(2)}></Button>
                            </ButtonGroup>
                        }
                        wrapClassName='img-box'
                        width={imgWidth+'px'}                    
                        onCancel={this.handleCancel}
                        maskClosable={false}
                    >
                        <img alt='example' style={{width:'100%'}} src={previewImage} />
                    </Modal>
                </div>
            </div>
        )
    }
}