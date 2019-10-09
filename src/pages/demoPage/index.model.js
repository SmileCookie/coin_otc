/**
 * model
 * @description 所有的核心数据放在此处处理，控制层只拿处理好的数据。
 * 如果在运算中出现了任何异常直接使用mock数据，视图在渲染如果没有此字段直接用--。
 */
import axios from 'nets';

// 获取模型对应的mock
import MockA from 'mock/A';

// 工具类 不采用路径简写的形式，因为需要工具看定义
import {containsObj} from '../../utils';

async function DemoPageModel() {
    // 获取数据
    let result = await axios.get('/manage/getAssetsDetail');
    result = result.data;
// result.id = 1;
// result.name = '';
    // 组装自己需要的mock 或者 mapper 如果在验证或者在计算过程中出现任何异常直接使用前端自己的数据。本例采用简单的下个mockb采用嵌套的。
    let rtData = MockA;

    // 数据深加工
    // 数据分深层嵌套以及单层嵌套
    try{
        // 验证mock中对应的实例是否都存在。
        // 如果存在多层下个实例讲明
        containsObj(MockA, result);

        // 这里可能是各种的计算

        // 如果没有抛出异常代表没问题
        rtData = result;
    } catch(e){

    }

    // 无论如何都将返回供体数据。
    return Promise.resolve(rtData);
}

export { DemoPageModel };