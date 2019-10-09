import React from 'react'
import decorator from '../../../decorator'
import { SELECTWIDTH, DOMAIN_VIP } from '../../../../conf'
import { Upload, Icon, message, Select, Modal, Input, Button, Divider } from 'antd'
const { Option } = Select;

@decorator()
export default class ModalEdite extends React.Component {

    constructor(props) {
        super(props)
        this.defaultState = {
            id: '',
            name: '',
            image: '',
            address: '',
            fileList: [],
            preUserImg: '',
            userImg: {},
        }
        this.state = {
            ...this.defaultState,
            loading: false,
            visible: false,

        }

    }

    componentDidMount() {
        if (this.props.item.id) {
            this.getData(this.props.item)
        } else {
            this.setState({
                ...this.defaultState
            })
        }
    }

    handleCancel = () => {
        this.setState({
            visible: false
        })
    }
    componentWillReceiveProps(nextProps) {
        if (nextProps.item.id) {
            this.getData(nextProps.item)
        } else {
            this.setState({
                ...this.defaultState
            })
        }
    }



    getData = (item) => {
        const { id, name, image, address, } = item
        this.setState({
            name,
            fileList: [{
                uid: 1,
                status: 'done',
                name: image,
                url: image,
            }],
            address, id,
            userImg: {
                fileList: image
            }
        })



    }

    edit = () => {
        if (this.state.id) {
            this.updateData()
        } else {
            this.addData()
        }

    }

    // 新增数据
    addData = async () => {
        // this.setState({
        //     ...this.defaultState,
        // })
        const { name, address, userImg } = this.state
        let state = this.isContent(name, address, userImg)
        if (state) {
            const params = { name, address, image: userImg.fileList, }
            await this.request({ url: '/webBottom/insert', type: 'post', isP: true }, params)
            this.props.handleCancel()
            this.props.requestTable()
        }

    }

    // 修改数据
    updateData = async () => {
        const { name, address, userImg, fileList, id } = this.state
        // console.log(fileList)
        const state = this.isContent(name, address, userImg)
        //console.log(state)
        if (state) {
            const params = { name, address, image: userImg.fileList || '', id }
            await this.request({ url: '/webBottom/update', type: 'post', isP: true }, params)
            this.props.handleCancel()
            this.props.requestTable()
        }
    }
    getBase64 = (file) => {
        return new Promise((resolve, reject) => {
            const reader = new FileReader();
            reader.readAsDataURL(file);
            reader.onload = () => resolve(reader.result);
            reader.onerror = error => reject(error);
        });
    }

    uploadImageCoscb = ({ _key, url }) => {
        this.setState({
            userImg: Object.assign({}, this.state.userImg, { [_key]: url })
        })
    }

    // 判断名称是否重复//判断是否为空
    isContent = (name, address, userImg) => {
        if (!name) {
            message.warning('名称项为必填，请重新输入!');
            return false;
        }
        if (!address) {
            message.warning('地址项为必填，请重新输入!');
            return false;
        }
        if (!userImg.fileList) {
            message.warning('上传图片项为必填，请重新输入!');
            return false;
        }
        return true

    }


    // 图片上传状态 
    handleChange = (file, _key) => {
        const { fileList } = file;
        const size = fileList.length
        // let _limit = _key == 'video' ? _limitVideo : void 0;
        //console.log (fileList[size-1])
        if (size) {
            if (fileList[size - 1].size / 1024 > 300) {
                return false
            }
            // if (!this.limitUpSize(fileList[size - 1].size, _limit)) return false;
            fileList[size - 1].status = 'done'
            //console.log(fileList[size - 1])
            this.setState({
                [_key]: fileList,
            })
        }

    }
    onRemove = (info, _key) => {
        this.setState({
            [_key]: [],
            userImg: {}
        })
    }



    //图片上传预览
    handlePreview = async (file, _key) => {
        this.setState({
            preUserImg: file.url || file.thumbUrl,
            // preUserImg: file.url || file.preview,
            visible: true,
        })
    }
    uploadButton = p => (
        <div>
            <Icon type="plus" />
            <div className="ant-upload-text">{p}</div>
        </div>
    );


    render() {
        const { name, address, fileList, visible, preUserImg } = this.state
        return (
            <div className="col-md-12 col-sm-12 col-xs-12">
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">名称：</label>
                        <div className="col-sm-8">
                            <Select name="name" value={name} style={{ width: SELECTWIDTH }} onChange={(v) => this.onSelectChoose(v, 'name')}>
                                <Option value=''>请选择</Option>
                                <Option value='facebook'>facebook</Option>
                                <Option value='t.me'>t.me</Option>
                                <Option value='twitter'>twitter</Option>
                            </Select>
                        </div>
                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">地址：</label>
                        <div className="col-sm-8">
                            <input type="text" className="form-control" name="address" value={address} onChange={this.handleInputChange} />
                        </div>

                    </div>
                </div>
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group">
                        <label className="col-sm-4 control-label-lg">上传图片：</label>
                        <div className="col-sm-8">
                            <Upload
                                name="image"
                                fileList={fileList.slice(-1)}
                                listType="picture-card"
                                onRemove={(v) => this.onRemove(v, 'fileList')}
                                onPreview={(v) => this.handlePreview(v, 'fileList')}
                                onChange={(v) => this.handleChange(v, 'fileList')}
                                customRequest={(e) => {
                                    this.uploadImageCos(e.file, this.uploadImageCoscb, 'fileList')
                                }}
                            >
                                {fileList.length >= 1 ? null : this.uploadButton('上传图片')}
                            </Upload>
                        </div>
                    </div>
                </div>
                <Divider style={{ marginTop: 6, marginBottom: 6 }} />
                <div className="col-md-12 col-sm-12 col-xs-12">
                    <div className="form-group right">
                        <Button key="back" onClick={this.props.handleCancel}>取消</Button>
                        <Button key="submit" type="more" onClick={() => this.edit()}>确认</Button>
                    </div>
                </div>

                <Modal visible={visible} footer={null} onCancel={this.handleCancel}>
                    <img alt="example" style={{ width: '100%' }} src={preUserImg} />
                </Modal>


            </div>
        )
    }


}

