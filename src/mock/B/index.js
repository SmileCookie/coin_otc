/**
 * @description mock B 范例
 * @author luchao.ding
 */
import confs from 'conf';

// 获取mock的默认数据
const { defaultData } = confs;

/**
 * @description 这个只是做为maper的单体，如果此实例作用于集合中如果某一条挂了直接使用单体实例。
 * 单体的结构体
 * @property {String}  id   用户id
 * @property {String}  name 用户名称
 */
const MockB = {
        adType: 0,
        buyNationalCode:defaultData,
        //buyUserFee: defaultData,
        buyUserId: defaultData,
        buyUserMoblie: defaultData,
        //buyUserName: defaultData,
        cancelTime: defaultData,
        //coinNumber: defaultData,
        //coinPrice: defaultData,
        //coinTime: defaultData,
        coinTypeId: defaultData,
        id: defaultData,
        legalTypeId: defaultData,
        //market: "--/--",
        marketType: defaultData,
        //orderId: defaultData,
        //recordNo: defaultData,
        sellNationalCode: defaultData,
        //sellUserFee: defaultData,
        sellUserId: defaultData,
        sellUserMoblie: defaultData,
        //sellUserName: defaultData,
        status: defaultData,
        //statusName: defaultData,
        timeDiff: defaultData,
        type: defaultData,
        userCancelNum: defaultData,
        userId: defaultData
};

export default MockB;
