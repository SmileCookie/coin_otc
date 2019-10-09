import axios from '../utils/fetch'
import { DOMAIN_VIP, PAGEINDEX, PAGESIZE } from 'Conf'
import { message, Modal, } from 'antd'
import { pageLimit, isArray, kpEventLisInquiry } from './../utils'
import GoogleCode from 'GCPath'
const Big = require('big.js')

/**
 * @author oliver
 * 
 * @function 装饰器
 * 
 * @param {Object} param
 * @param {Array || String} lb 
 * 
 * @returns @function @param {Object} Cp 类 @returns [Component] 类
 * 
 * @description 用此装饰器，this.requestTable函数只能用于请求列表数据，不得作为其他用途，方便下拉查询数据、Enter查询数据。
 */
const Decorator = ({ lb } = { lb: '' }) => {
    return Cp => {
        class Comp extends Cp {
            constructor(props) {
                super(props)
                this.state = {
                    showHide: true,
                    pageTotal: 0,
                    pageIndex: PAGEINDEX,
                    pageSize: PAGESIZE,
                    dataSource: [],
                    googVisibal: false,
                    googleItem: null,
                    googleType: null,
                    googleCheck: '',
                    loading: false,
                    cpMT: '',//modal title
                    cpMV: false, // modal visible
                    cpMW: '',//modal width
                    cpMlh: '', //modal html
                    limitBtn: lb ? Comp.getlb(lb, this.props.permissList) : [],//判断按钮权限
                    ckp: new Map(),
                    ...this.state,
                    ...this.props,
                    ...this.defaultState
                }
            }
            /**
             * @function 获取按钮权限
             * 
             * @param {String} lb 
             * @param {Array} lbs
             * 
             * @returns [Array]
             */
            static getlb = (lb, lbs = []) => isArray(lb) ? lb.map(i => pageLimit(i, lbs)).flat(Infinity) : pageLimit(lb, lbs)

            componentDidMount() {
                super.componentDidMount && super.componentDidMount()

                //Enter 触发查询列表事件，即this.requestTable
                this.props.appActiveKey && kpEventLisInquiry(this.props.appActiveKey, this.state.appActiveKey, this.keyDown)

                // setTimeout(() => {
                //     this._inputChangeType()
                // },3000)
            }
            componentWillUnmount() {
                super.componentWillUnmount && super.componentWillUnmount();
                //重写组件的setState方法，直接返回空
                this.setState = (state, callback) => {
                    return;
                };

                //卸载 Enter 查询列表数据事件
                this.state.appActiveKey && kpEventLisInquiry(this.keyDown)
            }
            componentWillReceiveProps(nextProps) {
                super.componentWillReceiveProps && super.componentWillReceiveProps(nextProps)

                //Enter 触发查询列表事件，即this.requestTable
                this.state.appActiveKey && kpEventLisInquiry(nextProps.appActiveKey, this.state.appActiveKey, this.keyDown)
            }
            /**
             * @function  keypress-event
             */
            keyDown = e => {
                e.keyCode == 13 && this.requestTable && this.requestTable(PAGEINDEX)
            }
            /**
             * @function 请求API
             * @param {Object}
             * @description  [String] url 请求的接口, [String] type:post || get 请求的方式，
             * [String] msg 提示信息， [Boolean] isP 是否弹出提示信息
             * 
             * @param {Object} data 传的参数
             */
            request = ({ url, type, msg, isP } = { url: '', msg: '小米辣', isP: false }, data = {}) => {
                if (type) {
                    return new Promise((resolve, reject) => {
                        axios[type](DOMAIN_VIP + url, qs.stringify(data)).then(res => {
                            const result = res.data;
                            if (result.code == 0) {
                                isP && message.success(msg || result.msg)
                                resolve(result.data || result.status);
                            } else {
                                message.error(result.msg)
                                reject(result.msg)
                            }
                        })
                    })
                }
                return new Promise((resolve, reject) => {
                    axios.get(DOMAIN_VIP + url, { params: data }).then(res => {
                        const result = res.data;
                        if (result.code == 0) {
                            isP && message.success(msg || result.msg)
                            resolve(result.data || result);
                        } else {
                            message.error(result.msg)
                            reject(result.msg)
                        }
                    })
                })
            }
            //点击收起
            clickHide = () => {
                this.setState({
                    showHide: !this.state.showHide,
                })
            }
            //input-OnChange
            handleInputChange = event => {
                const target = event.target;
                const value = target.type === 'checkbox' ? target.checked : target.value;
                const name = target.name;
                // const dpe = event.target.getAttribute('data-type')
                // console.log(dpe)
                // const ckp = {
                //     number: () => {
                //         if(!/(^[\-0-9][0-9]*(.[0-9]+)?)$/.test(value)){
                //             message.error('只能是数字')
                //             return
                //         }
                //         return true
                //     }
                // }
                // if(dpe && !ckp[dpe]()){
                //      return 
                // }
                this.setState({
                    [name]: value,
                })
            }
            //重置按钮
            resetState = () => {
                this.setState({ ...this.defaultState })
            }
            handleCreate = () => {
                const form = this.formRef.props.form;
                form.validateFields((err, values) => {
                    if (err) {
                        return;
                    }
                    form.resetFields();
                    this.modalGoogleCodeBtn(values)
                });
            }
            saveFormRef = formRef => {
                this.formRef = formRef;
            }
            //谷歌弹窗关闭
            handleGoogleCancel = () => {
                this.setState({
                    googVisibal: false
                })
            }
            /**
             * @function google 验证弹窗
             * 
             * @param {Object} googleItem 传的参数
             * @param {String} googleType goofn根据此判断调用那个回调
             * @param {any} googleCheck 是否需要监察员Google 
             */
            modalGoogleCode = (googleItem, googleType, googleCheck = '') => {
                this.setState({
                    googVisibal: true,
                    googleItem,
                    googleType,
                    googleCheck,
                })
            }
            //google 按钮
            modalGoogleCodeBtn = async (value) => {
                let { googleItem, googleType = 'default' } = this.state
                const { googleCode, checkGoogle } = value

                let url = checkGoogle ? "/common/checkTwoGoogleCode" : "/common/checkGoogleCode"
                let params = checkGoogle ? { googleCode, checkGoogle } : { googleCode }

                await this.setState({ loading: true })
                await this.request({ url, type: 'post', isP: true }, params)
                await this.setState({ googVisibal: false, loading: false })

                this.goofn().has(googleType) && this.goofn().get(googleType)(googleItem)
            }
            /**
             * @description 时间控件
             * @param {Array} date @requires
             * @param {Array} dateString
             * @param {String} key @requires
             */
            onChangeCheckTime = (date, dateString, key) => {
                this.setState({
                    [key]: date
                })
            };
            /**
             * @description 下拉选择框
             * @param {any} v @requires
             * @param {String} key @requires
             */
            onSelectChoose = (v, key) => {
                this.setState({
                    [key]: v
                }, () => {
                    //下拉选择请求数据 
                    this.requestTable && this.requestTable()    //this.requestTable 只能用于请求列表
                })
            }

            //获取可视屏幕的宽度，为true时，使table  x轴产生滚动条
            islessBodyWidth = () => document.body.clientWidth < 1540

            //上传 腾讯云
            getAuthorization = async (callback) => {
                let result = await this.request({ url: "/news/getTencentToken", type: 'post' })
                callback({
                    url: result.host,
                    key: result.key,
                    XCosSecurityToken: result.token
                });
            }
            /**
             * @function 限制上传文件的大小，默认300k
             * 
             * @param {Number} size 文件的字节数
             * @param {Number} max 限制的大小
             * @returns [Boolean] less: true,
             */
            limitUpSize = (size, max = 300) => {

                if (size / 1024 < max) {
                    return true
                } else {
                    message.warning(`上传图片大小不能超过${max}k！`)
                    return false
                }
            }
            /**
             * @description 自定义上传函数
             * 
             * @param {Object} file 上传的文件
             * @param {Function} callback 上传成功后的回调函数
             * @param {String} _key 上传有不同类型时，用此参数判断，例 有中英繁三种图片，_key:zh || en || hk
             * @param {Number} limitSize 限制上传文件的大小，默认 300k
             */
            uploadImageCos = (file, callback = () => { }, _key = '', limitSize) => {
                if (!this.limitUpSize(file.size, limitSize)) {
                    return false
                }
                this.getAuthorization((info) => {
                    file.status = 'done'
                    let fd = new FormData();
                    fd.append('key', info.key);
                    fd.append('Signature', info.XCosSecurityToken);
                    fd.append('Content-Type', '');
                    fd.append('file', file);
                    const xmlhttp = new XMLHttpRequest();
                    xmlhttp.open('post', info.url, true);
                    xmlhttp.send(fd);
                    xmlhttp.onreadystatechange = () => {
                        if (xmlhttp.readyState == 4) {
                            message.success('上传成功')
                            callback({
                                _key,
                                url: info.url + info.key
                            })
                        }
                    }
                })
            }
            /**
             * @function confirm确认框
             * @param {Object} 
             * @description [String] method ,[String] title,其他属性随意加
             * 
             * @param {Function} cb 点击确定后的回调函数
             */
            md = ({ method = 'confirm', title = '', okType = 'more' }, cb = () => { }) => {
                Modal[method]({
                    title,
                    okText: '确定',
                    okType,
                    cancelText: '取消',
                    confirmLoading: this.state.loading,
                    maskClosable: true,
                    onOk: async () => {
                        await this.setState({ loading: true })
                        this.setState({ loading: false })
                        cb && cb()
                    },
                    onCancel() {
                        console.log('Cancel')
                    }

                })

            }
            //modal cancel
            cpMCancel = () => {
                this.setState({ cpMV: false })
            }
            /**
             * @function 验证按钮权限
             * @returns {Boolean}
             */
            ckAuth = (key = '') => {
                const { limitBtn } = this.state
                return limitBtn.includes(key)
            }
            // render
            render() {
                const { cpMV, cpMT, cpMW, cpMlh, googVisibal, googleCheck, loading } = this.state
                const { cpMCancel, saveFormRef, handleInputChange, handleCreate, handleGoogleCancel } = this

                return (
                    <div ref={this.state.appActiveKey}>
                        {super.render && super.render()}

                        {/*     普通弹框  */}
                        <Modal
                            visible={cpMV}
                            title={cpMT}
                            width={cpMW}
                            style={{ top: 50 }}
                            onCancel={cpMCancel}
                            footer={this.footer || null}
                        >
                            {cpMlh}
                        </Modal>

                        {/*       google 弹框     */}
                        <GoogleCode
                            wrappedComponentRef={saveFormRef}
                            check={googleCheck}
                            handleInputChange={handleInputChange}
                            mid={new Date()}
                            visible={googVisibal}
                            googleLoading={loading}
                            onCancel={handleGoogleCancel}
                            onCreate={handleCreate}
                        />
                    </div>
                )
            }
        }
        return Comp
    }
}

export default Decorator