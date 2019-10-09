# bitglobla项目的mongoDb使用

> mongoDB版本 2.4.9
> java驱动版本(mongo-java-driver) 2.11.3
      
---

## 使用注意

mongodb在项目中的使用亮点,需要注意的地方以及后来可能预见的问题评估
      
- mongo在bitglobla里面其实有很大量的使用,比如用户信息全部存在mongo中,所以这块一定要做高可用和容灾
- 项目中有一套约定俗成的规则:
    - collection名称用的是classname(用classname的原因就目前来看是为了id生成器而设计的)
    - ids:id生成器,充分利用了mongodb findAndUpdate的原子性保证了生成的id不会重复
    - 通过使用ids直接取代了mongo的_id哈希码而变为可读的自增id
- 问题:mongodb 3.X以上版本对mongodb 2.6以下版本兼容性不好ß

## 项目中用到mongodb的业务

此条目慢慢补充,发现即可补充进来

- 短信发送: 先存放到mongo中,在通过程序扫描mongo进行发送(不知道发送成功了没有)
- 用户信息保存
- ~国际化资源保存(已废除)~
- 