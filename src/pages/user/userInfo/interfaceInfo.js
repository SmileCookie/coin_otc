/*提供一个接口上来就请求一次 存在 session 里面

    {
        authStatus,
        emailStatu,
        mobileStatu,
        hasSafePwd,
        googleAuth

    }
    以下接口，还有以上字段的无需再返回
*/
/**
 * page http://www.common.com/bw/manage/user
 * interface name getUserInfo
 * @param ???
 *
 * @description
 *     1.除非明确标识状态码，否则全部遵循原来的JSP页面逻辑，因为基本上都是拷贝原来的逻辑。
 *     2.关于修改建议采用axios统一拦截请求LOGING,否则每个页面加Load逻辑
 *     3.issue no
 * 
 *      
 * @return
 *       userInfo = {}
 */
const userInfo = {
    userName: '13406809119', // 用户UID 
    authStatus: '6', // 6 was finished verification, 身份认证
    userSafeLevel: '3', // 1 low 2 in 3 high，安全等级
    previousLogin: this.props.intl.formatDate(+new Date(), this.dateFormat).replace(',', '').replace(/\//g, this.sp), // 返回毫秒级时间戳 last login 上一次登录时间
    loginIp: '127.0.0.199', // 登录IP地址
    emailStatu: '3', //2 show eamil 邮箱状态
    email: '116710782@qq.com',// 邮箱
    mobileStatu: '2', // 2 show mobile // 手机状态
    mobile: mobileFormat('13863946940'), // 手机号
    mobilec:'+86', // 手机前缀CODE
    hasSafe: 0, // 资金密码的状态
    googleAuth: '3' // 谷歌状态的验证
}
// ========================================================================== end getUserInfo =====================================================

/**
 * page http://www.common.com/bw/manage/auth/authentication
 * interface name getAuthInfo
 * @param ???
 * @description
 * 
 * @return
 *     authInfo = {}
 */

const authInfo = {
    authStatus: 4, // 身份验证码，验证成功与否以及验证的一些消息。
    reason: 'test error', // 失败的原因，疑问如果做国家化服务端？个人理解每次切换我需要重新带着语言码重新询问，也或者我内置客户端服务端返回语言ID。个人推荐后者。
    isBlack: '', // 同原来JSP
    isLock: 0  // 同原来JSP
}
// ========================================================================== end getAuthInfo =====================================================

/**
 * http://www.common.com/bw/manage/auth/authtype
 * interface name getAuthTypeInfo
 * @param ???
 * @description
 * 
 * @return
 *     authTypeInfo = {}
 * 
 */
const authTypeInfo = {
    selectedCode: -1, // 默认选中那个哪一项
    redirect: '/' // 选择重定向到哪来自对应JSP页面
}
// ========================================================================== end authTypeInfo =====================================================

/**
 * http://www.common.com/bw/manage/auth/idcardauth
 * interface name saveIdCardAuth
 * @param
 *     POST,
 *      countryCode
 *      countName
 *      lastName
 *      firstName
 *      cardId
 *      startDate 日历对象非时间戳
 *      endDate
 *      frontalImg
 *      backImg
 *      loadImg
 *      cardType
 * 
 *      axios.post(DOMAIN_VIP+"/manage/auth/uploadToken") 保持不变，获取上传前需要的token,如果没有获取到直接返回上一步
 *  @description
 *     1.图片上传需要设置反向代理，反向到七牛上去。 上线需要注意这一点。
 * 
 * 
 * @return ??? 需要根据返回状态判断下一步逻辑。后台定包格式。
 */
// ========================================================================== end saveIdCardAuth =====================================================

 /**
 * http://www.common.com/bw/manage/auth/passportauth
 * interface name savePassPortAuth
 * @param
 *     POST,
 *      countryCode
 *      countName
 *      lastName
 *      firstName
 *      passportNumber
 *      startDate 日历对象非时间戳
 *      endDate
 *      passport
 *      handheldPassport
 *      cardType -- 传什么数后台定
 * 
 *      axios.post(DOMAIN_VIP+"/manage/auth/uploadToken") 保持不变，获取上传前需要的token,如果没有获取到直接返回上一步
 *  @description
 *     1.图片上传需要设置反向代理，反向到七牛上去。 上线需要注意这一点。
 * 
 * 
 * @return ??? 需要根据返回状态判断下一步逻辑。后台定包格式。
 */
// ========================================================================== end saveIdCardAuth =====================================================

// 以下这些逻辑套了一半，在原有基础上修改部分逻辑和国际化，原来套的那些业务并没有审查。

/**
 * http://www.common.com/bw/manage/user/email
 * interface emailInfo
 * 
 * @param ???
 * 
 * @description
 *     1.69-97 国际化没做 now is done
 *     2.<a className="c0" target="_blank" href="http://mail.${fn:split(source, '@')[1]}"> 这块是想要发邮件？ source ? now is done
 *     3. <Link className="mbr15 c0"><FormattedMessage id="user.text115" /></Link>
 *     4.<Link href="" className="ml10" target="_blank"> <FormattedMessage id="user.text21" /></Link>
 * 
 * @return
 *     safeAuth,emailStatus,step,email,mobileStatu,source
 */
// ========================================================================== end emailInfo =====================================================


/**
 * http://www.common.com/bw/manage/user/mobile
 * interface 
 *     fetchMobile
 *     return dispatch(receiveMobile({
 *       mobileStatu:1,
 *       googleAuth:1,
 *       phonenum:"13789858217",
 *       mobileCode,
 *       verifyUserInfo:{
 *           status:1,
 *           addTimeShow 时间戳,
 *       }
 *   }))
 * @param ???
 * 
 * @return 如上结构体    
 */
// ========================================================================== end fetchMobile =====================================================

// loginpassword
// <Link to="" className="ml10" target="_blank"><FormattedMessage id="user.text29" /></Link>


/**
 * http://www.common.com/bw/manage/user/safePwd
 * interface 
 *     receiveSafePwd
 *           hasSafePwd:true,
 *           googleAuth:0,
 *           mobileStatu:2
 * @description
 *     1.<Link to="/ac/safepwd_find" className="ml10"><FormattedMessage id="user.text115" /></Link>
 * @param ???
 * @return 如上结构体
 */
// ========================================================================== end receiveSafePwd =====================================================

/**
 * http://www.common.com/bw/manage/user/google
 * interface
 *     receiveGoogle
 *         {
 *           googleAuth:2,
 *           method:2,
 *           mobileStatu:1,
 *           secret:"KZ7QS345GDZOCW7D",
 *           verifyUserInfo:{
 *               addTimeShow:"2017-09-15",
 *               status:4
 *           }
 *       }
 * @description 
 *  1.帮助中心去哪？
 *  2.<a className={this.state.googleAuthInfo.mobileStatu!=2?'hide':'' } href="/manage/auth/closeGoogleAuth"><FormattedMessage id="user.text103" /></a>
 *  3.<dt className="img-google"><img src="/manage/getGoogleAuthQr?secret={this.state.googleAuthInfo.secret}" className=""/></dt>
 */
// ========================================================================== end receiveGoogle ===================================================== 