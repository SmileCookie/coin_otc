/**查看身份认证信息 弹窗*/
import Decorator from '../../../decorator'
import { Button, Select, message, DatePicker, Divider, Modal } from 'antd'
import { arrayTimeToStr, isArray, ckd, TE, dateToFormat, } from '../../../../utils';
const ButtonGroup = Button.Group;
import { Player } from 'video-react';

@Decorator()
export default class ModalInfo extends React.Component {
    constructor(props) {
        super(props)
        this.state = {
            item: {},
            userData: {},
            visible: false,
            imgWidth: '',
            previewImage: '',
            showHtml: ''
        }

    }
    componentDidMount() {
        this.setState({
            item: this.props.item
        },()=>{
            this.requestTable()
        })
        
    }
    componentWillReceiveProps(nextprops) {
        this.setState({
            item: nextprops.item
        },()=>{
            this.requestTable()
        })
        
    }


    requestTable = async () => {
        const { userId, id } = this.state.item
        const result = await this.request({ url: '/authenLog/queryMaterial', type: 'post' }, {
            userId,
            authenId: id,
        })
        this.setState({
            dataSource: result.list || [],
            userData: result.user
        })

    }

    //图片放大缩小
    imgSize(type) {
        const { imgWidth } = this.state
        if (type == 1) {
            if (imgWidth == 1600) {
                message.warning('已放至最大')
            } else {
                this.setState({
                    imgWidth: imgWidth + 100
                })
            }
        } else {
            if (imgWidth == 300) {
                message.warning('已缩放至最小')

            } else {
                this.setState({
                    imgWidth: imgWidth - 100
                })
            }
        }
    }
    //图片放大
    onImgEnlarge(type, src) {

        let changeWidth = type == 'pic' ? 600 : 500
        this.setState({
            imgWidth: changeWidth,
            visible: true,
            previewImage: src,

        }, () => {
            let showHtml = type == 'pic' ? <img alt="example" style={{ width: '100%' }} src={this.state.previewImage} /> : <Player
                playsInline
                poster={this.state.previewImage + '?vframe/jpg/offset/0'}
                src={this.state.previewImage} />


            this.setState({
                showHtml
            })

        }
        )
    }
    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    render() {
        const { dataSource, visible, userData, imgWidth, showHtml } = this.state
        const { mobile, userName, userId, email } = userData
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="x_content">
                    <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        <div className="col-sm-3">用户编号：{userId}</div>
                        <div className="col-sm-3">用户名：{userName} </div>
                        <div className="col-sm-3">邮箱：{email}</div>
                        <div className="col-sm-3">手机号：{mobile}</div>
                    </div>
                    <Divider style={{ float: 'left', marginTop: 15, marginBottom: 15 }} />
                    <div className="col-mg-12 col-lg-12 col-md-12 col-sm-12 col-xs-12">
                        {
                            dataSource.map((data, index) => {
                                return (
                                    <div className="col-mg-4 col-lg-4 col-md-4 col-sm-4 col-xs-4" style={{paddingBottom:25,paddingTop:25}}>
                                        <label className="col-sm-12 control-label">{data.imgType ? '身份证照片' : '认证视频'}</label>
                                        <div className="col-sm-12">
                                            {
                                                data.imgType ?
                                                    <img alt="example" style={{ width: '100%', height:200}} src={data.url} onClick={() => this.onImgEnlarge('pic', data.url)} />
                                                    :
                                                    <img alt="example" style={{ width: '100%', height:200}} src={data.url + '?vframe/jpg/offset/0'} onClick={() => this.onImgEnlarge('video', data.url)} />
                                            }
                                        </div>
                                    </div>
                                )
                            })
                        }
                    </div>
                </div>

                <Modal
                    visible={visible}
                    footer={
                        <ButtonGroup>
                            <Button icon="plus" onClick={() => this.imgSize(1)}></Button>
                            <Button icon="minus" onClick={() => this.imgSize(2)}></Button>
                        </ButtonGroup>
                    }
                    wrapClassName="img-box"
                    width={imgWidth + 'px'} onCancel={this.handleCancel} maskClosable={false}>
                    {showHtml}
                </Modal>
            </div>
        )
    }
}