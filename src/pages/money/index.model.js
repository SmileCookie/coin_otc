import axios from 'axios';
import {DFData, DOMAIN_VIP} from '../../conf';
import qs from 'qs';
import {Types, TH} from '../../utils/index'

/**
 * 已经和接口约定好不在使用mock
 */

/**
 * productSuperNode 产品&超级节点信息
 */
const productSuperNode = async () => {
    const moc = {
        proTotalAmount: DFData,
        proTotalUser: DFData,
        profitWeight: DFData,
        sNodeNum: DFData,
        sNodeTotalProfit: DFData,
        currentBlock: DFData,
        profitBlock: DFData,
        profitWeightTotal: DFData,
        surplusHour: DFData,
        bonusSurplusTime: DFData,
        vdsEcologyBack: DFData,
    }

    // 获取数据
    let result = await axios.get(DOMAIN_VIP+'/productSuperNode');
    result = result.data.datas;
    // jpa 验证接口
    return baseGpa(moc, result);
}

const baseGpa = (moc, result) => {
    const dic = Object.keys(moc);
    const rs = dic.reduce((res, key)=>{
        void 0 !== result[key] && (res[key] = result[key]);
        return res;
    }, {});

    return Object.keys(rs).length === dic.length ? result : moc;
}

/**
 *  maock 数据和请求返回数据做解构赋值
 *  moc 本地mock数据  data 返回数据
 */
const transferData = (mock, data) => {
    let result = [];
    if (Array.isArray(mock) && Array.isArray(data)) {
        for (let d of data) {
            let tempMock = Object.assign({},mock[0]); // clone Object
            for (let key in tempMock) {
                ({[key]: tempMock[key] = DFData} = d);
            }
            result.push(tempMock);
        }
    } else {
        let tempMock = Object.assign({},mock);
        for (let key in tempMock) {
            ({[key]: tempMock[key] = DFData} = data);
        }
        result = tempMock;
    }
    // console.log(result)
    return result
}

/**
 * userFinancialInfo 用户认证支付信息
 */
const userFinancialInfo = async () => {
    const moc = {
        userVID: DFData,
        pInvitationCode: DFData,
        userFinVdsAmount: DFData,
        authPayFlag: 0,
        matrixLevel: 0,
    }

    // 获取数据
    let result = await axios.get(DOMAIN_VIP+'/manage/financial/userFinancialInfo');
    result = result.data.datas;

    return baseGpa(moc, result);
}


/**
 * userFinCenInfo 理财中心用户信息 数据结构体
 */
const centerInfo = {
    investAvergPrice: -1,
    sumInvestUsdtAmount: DFData,
    userProfit: DFData,
    userProfitUsdt: DFData,
    investAmount: DFData,
    invitationCode: DFData,
    pInvitationCode: DFData,
    invitationTotalNum: DFData,
    invitationLinks: DFData,
    userVID: DFData,
    userName: DFData,
    
    authPayFlag: 0,
    investUsdtAmount: DFData,
    expectProfitUsdt: DFData,
    modifyTime: DFData,
    profitTime: DFData,
    directInvitationNum: DFData,
    physicsSupName: DFData,
    directInvitationSucNum: DFData,
    pInvitationUserName: DFData,
    proTotalUser: DFData,

    hierarchyBuildAmount: DFData,
    pushGuidanceAmount: DFData,
    levelPromotionAmount: DFData,
    leaderBonusAmount: DFData,
    leaderBonusWeight: DFData,
    superNodeAmount: DFData,
    superNodeWeight: DFData,
    ecologySystemAmount: DFData,
    newVipUserAmount: DFData,
    hierarchyBuildFloor: DFData,
    pushGuidanceRatio: DFData,
    levelPromotionRatio: DFData,
    platSuperNodePayAmount: DFData,
    platSuperNodeNotPayAmount: DFData,
    proTotalAmount: DFData,
    platNewVipWeekAmount: DFData,
    platEcologySystemAmount: DFData,
    platLeaderBonusAmount: DFData,
    userFinCenRewardInfo: DFData,
    leaderBonusRatio: DFData,
    platNewVipWeekNotPayAmount: DFData,
    platReleaseNotPayAmount: DFData,
    //冻结新增
    hierarchyBuildInsureAmount:DFData, //层级建点奖励保险冻结
    pushGuidanceInsureAmount:DFData,//	直推执导奖励保险冻结
    levelPromotionInsureAmount:DFData,//级别晋升奖励保险冻结
    leaderBonusInsureAmount:DFData//全球领袖分红奖励保险冻结


}
// 获取理财中心 用户信息
const userFinCenInfo = async () => {
    const moc = Object.assign({},centerInfo);

    // 获取数据
    let result = await axios.get(DOMAIN_VIP+'/manage/financial/userFinCenInfo');
    result = result.data.datas;
    const leaderBonusWeight = result.leaderBonusWeight;
    const superNodeWeight = result.superNodeWeight;
    // 用户理财奖励信息
    let mif = await axios.get(DOMAIN_VIP + '/manage/financial/userFinCenRewardInfo');
    result = {
        ...result,
        ...mif.data.datas,
        //leaderBonusWeight,
        superNodeWeight,
    }

    return transferData(moc, result);
}


/**
 * centerDetailsList 理财中心收益详情 数据结构体
 */
const tableList = [{
    createTime: DFData, typeName: DFData, fundsTypeName: DFData, amount: DFData, remark: DFData
}]
// 获取理财中心 收益详情
const centerDetailsList = async () => {
    const moc = tableList
return [];
    // 获取数据
    let result = await axios.get(DOMAIN_VIP+'/manage/financial/userFinProfitBill');
    result = result.data.datas;
    // console.log(result);
    if (result.length == 0){
        return []
    } else {
        return transferData(moc,result);
    }
}


/**
 * 组合消息体
 */
const init = async () => {
    const productSuperNoded = await productSuperNode();
    const userFinancialInfod = await userFinancialInfo();

    return {
        ...productSuperNoded,
        ...userFinancialInfod,
    }
}

/**
 * save
 */
const doSave = async (cd = {}) => {

    // 保存接口
    let result = await axios.post(DOMAIN_VIP+'/manage/financial/userProductInfoSave', qs.stringify(cd));
    result = result.data;


    //console.log(qs.stringify(cd));
    return Promise.resolve({
        suc: result.isSuc,
        des: result.des,
        errors: result.datas,
        ekeys: Object.keys(result.datas),
    })
}
/**
 * pay
 */
const doPay = async (type = 0) => {
    let result = await axios.post('/manage/financial/userProductInfoPay', qs.stringify({matrixLevel: type}));
    result = result.data;

    return Promise.resolve({
        suc: result.isSuc,
        des: result.des,
    })
}

export { init,userFinancialInfo,doSave, doPay,userFinCenInfo,centerDetailsList,tableList,centerInfo, productSuperNode};