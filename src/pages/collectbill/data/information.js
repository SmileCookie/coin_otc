export const rechargeUrls = [{port:'/coinChangeCxception',content:'提现异常预警'},                       
                            {port:'/coinChangeDaywithdrawal',content:'单日累计提现额度报警'},
                            {port:'/coinChangeLargewithdrawal',content:'单笔大额提现报警',unreadcount:true},
                            {port:'/coinChangeDayrecharge',content:'单币种日累计充值报警'},
                            {port:'/coinChangeLargerecharge',content:'单笔大额充值报警',unreadcount:true},
                            // {port:'',content:'提现总金额报警'},
                            {port:'',content:'小额打币功能报警'},
                            {port:'/coinChangeBalance',content:'提现后账户余额为X报警',unreadcount:true},
                        ]

export const platformUrls = [{port:'/checkreconciliation',content:'交易平台对账'},
                            {port:'/generalledger',content:'交易平台钱包对账'},
                            // {port:'',content:'区块钱包对账'},
                            {port:'',content:'区块钱包vs交易平台对账'},
                            // {port:'',content:'期货账户对账'},
                            {port:'/billReconciliation',content:'币币账户对账'},
                            // {port:'',content:'OTC账户对账'},
                            {port:'/coinChangeInvented',content:'虚拟资金异常',unreadcount:true}, ]

export const walletUrls = [{port:'',content:'热提钱包异常'},
                            {port:'',content:'热充钱包异常'},
                            {port:'',content:'冷钱包流水异常'},
                            ]

export const paymentUrls = [{port:'/rechargeBalanceWindcontrol',content:'充值对账'},
                            {port:'/withdrawBalanceWindcontrol',content:'提现对账'},
                            // {port:'',content:'钱包每日对账'},
                            {port:'',content:'X钱包对账'},
                            ]
                            
