import {isEmail} from '../../utils'
//import validator from './index'
export const formTest2 = {
    name: {
        validator(rule, value, callback, source, options) {
            /* callback必须执行一次,带参数为错误信息,不带参数为正确 */
            if (!value) {
                callback({
                    errMsg: "请输入邮箱",
                    value,
                    errStatus: true
                });
            }
            if(!isEmail(value)){
                callback({
                    errMsg: "请输入正确的邮箱地址",
                    value,
                    errStatus: true
                });
            }else{
                callback({
                    errMsg: "",
                    value,
                    errStatus: false
                });
            }
            
        }
    },
    age: {
        validator(rule, value, callback, source, options) {
            /* callback必须执行一次,带参数为错误信息,不带参数为正确 */
            if (!value) {
                callback({
                    errMsg: "dasdadddd",
                    value,
                    errStatus: true
                });
            }
            if(!isEmail(value)){
                callback({
                    errMsg: "dsadsadsd",
                    value,
                    errStatus: true
                });
            }else{
                callback({
                    errMsg: "",
                    value,
                    errStatus: false
                });
            }
            
        }
    }
};

