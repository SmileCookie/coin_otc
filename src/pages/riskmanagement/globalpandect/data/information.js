export const keepWarning = [{port:'/coinReverseaccount',content:'频繁对倒账户'},
                          
                            {port:'/frequent',content:'频繁挂撤单账户'},
                            {port:'/coinInfoupdate',content:'关键信息修改'},
                            {port:'/linkedaccount',content:'关联账户'},
                            {port:'/coinLargeAccount',content:'大额账户监控'},
                            {port:'/coinLargeOrder',content:'大额挂单账户'}]

export const valueToWarn = [{port:'/coinQtHedgingabnormal',content:'保值异常'},
                            {port:'/coinQtRecordabnormal',content:'保值记录异常'},
                            {port:'/coinQtBelowwarning',content:'保值记录异常'},
                            {port:'/coinQtHedgingnumbers',content:'保值下单数量异常'},]

export const marketValue = [{port:'/coinQtForfailure',content:'外网行情获取失败预警'},
                            {port:'/coinQtMarketdeparture',content:'行情偏离'},
                            {port:'/coinQtAmountlowwarning',content:'刷量账号资金低于预警'},
                            {port:'/coinQtStopwarning',content:'盘口深度低于预警值预警'},
                            {port:'/coinQtDishlowwarning',content:'量化程序停止报警'},
                            {port:'/coinQtAccounted',content:'用户成交占数量资金比'},
                            ]
export const personalBlowUrls = [{port:'',content:'频繁对倒账户熔断'}, //个人熔断                      
                            {port:'',content:'频繁挂撤单账户熔断'},
                            {port:'',content:'关联账户熔断'},
                        ]

export const platformBlowUrls = [{port:'',content:'行情偏离熔断'},//平台熔断接口
                            {port:'',content:'热充钱包熔断'},
                            {port:'',content:'热提钱包熔断'},
                            {port:'',content:'冷钱包熔断'},
]
                            
