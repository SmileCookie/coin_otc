/**
 * @description mock A 范例
 * @author luchao.ding
 */
import confs from 'conf';

// 获取mock的默认数据
const { defaultData } = confs;

/**
 * @description 这个只是做为maper的单体，如果此实例作用于集合中如果某一条挂了直接使用单体实例。
 * 单体的结构体
 * @property {String} id    用户id
 * @property {String} name  用户名称
 */
const MockA = {
    id: defaultData,
    name: defaultData,
    fundsType:defaultData
};

export default MockA;
