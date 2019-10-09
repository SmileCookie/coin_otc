try {
    require(['module_swiper', 'module_method', 'module_market'], function (Swiper, method, bill) {
        var lanInt = $.cookie('zlan')
        bannerLoad();

        function bannerLoad() {
            $.ajax({
                url: '/getPhotoUrl',
                type: 'GET',
                dataType: 'json',
                data: {bannerGroup: 'spotWeb'},
                success: function (res) {
                    var result = res.datas;
                    var bannerHtml_pc = ''
                    var bannerHtml_mo = ''
                    for (var i = 0; i < result.length; i++) {
                        var jsonLink = JSON.parse(result[i].linkUrl)
                        var pic = JSON.parse(result[i].bannerUrl)
                        var linkType = result[i].linkType == 0? '_self':'_blank';
                        //console.log(linkType)
                        //bannerHtml += ' <a class="swiper-slide" href="' + jsonLink[lanInt] + '" style="background:url(' + pic[lanInt] + ') center center no-repeat;background-size:cover;><img alt="' + result[i].bannerName + '" src="' + pic[lanInt] + '" /></a>'
                        bannerHtml_pc += '<div  class="swiper-slide" ><a href="' + jsonLink[lanInt] + '" target="' + linkType + '" class="pc_hover" style="width: 320px;height: 140px;display:inline-block;background-image: url(' + pic[lanInt] + ');background-size:cover;background-repeat: no-repeat;background-position: center;border: 1px solid #3E6DA2;border-radius: 4px;"></a></div>'
                        bannerHtml_mo += '<a  class="swiper-slide" href="javascript:void(0)"><div style="width: 89%;height: 81%;margin:0 auto;background-image: url(' + pic[lanInt] + ');background-size:cover;background-repeat: no-repeat;background-position: center;border: 1px solid #3E6DA2;border-radius: 4px;"></div></a>'
                    }
                    // for(var i=0;i < 4 ; i++){
                    //     bannerHtml += '<a  class="swiper-slide" href="' + jsonLink[lanInt] + '"><div style="width: 320px;height: 140px;background-image: url(' + pic[lanInt] + ');background-size:cover;background-repeat: no-repeat;background-position: center;border: 1px solid #3E6DA2;border-radius: 4px;"></div></a>'
                    // }
                    $('.swiper-container-pc .swiper-wrapper').html(bannerHtml_pc)
                    $('.swiper-container-mo .swiper-wrapper').html(bannerHtml_mo)
                    var _length = $('.swiper-container-pc').length;
                    if(_length){
                        var mySwiper_pc = new Swiper('.swiper-container-pc', {
                            autoplay: {
                                disableOnInteraction: false,
                            },
                            slidesPerView : 4,
                            slidesPerGroup : 4,
                            spaceBetween : '1%',
                            loop: result.length > 4 ? true : false,
                            // 如果需要分页器
                            pagination: {
                                el: result.length > 4?  '.swiper-pagination' : null,
                                clickable: true,
                            },
                        })
                        result.length > 4 ? mySwiper_pc.pagination.$el.addClass('MyClass') : null; //为分页器增加样式
                    }else{
                        //mobile
                        var mySwiper_mo = new Swiper('.swiper-container-mo', {
                            autoplay: {
                                disableOnInteraction: false,
                            },

                            loop: result.length > 1 ? true : false,
                            // 如果需要分页器
                            pagination: {
                                el: result.length > 1?  '.swiper-pagination' : null,
                                clickable: true,
                            },
                        })
                        result.length > 1 ? mySwiper_mo.pagination.$el.addClass('MyClass') : null; //为分页器增加样式

                    }


                }
            })
        }

        coinNum();

        function coinNum() {
            $.ajax({
                url: '/coinAll',
                type: 'GET',
                dataType: 'json',
                success: function (res) {
                    var num = res.datas;
                    $('#coin_num').html(num)
                }
            })
        }


        //获取公告
        noticeShow();
        // 铃铛提示
        function noticeShow() {
            $.ajax({
                url: "/msg/newsOrAnnList?type=1&pageIndex=1&pageSize=3&noticeType=0",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    var html = '';
                    var notice_data = data.datas;
                    var notice_data_list = notice_data.datalist || [];
                    for (var i = 0; i < notice_data_list.length; i++) {
                        notice_data_list[i].pubTime = method.formatDate(notice_data_list[i].pubTime, lanInt === 'en' ? "MM-dd-yyyy hh:mm" : "yyyy-MM-dd hh:mm");
                        html += '<a class="notice_href" href = "javascript:volid(0);" data-id=' + notice_data_list[i].myId + '><span class="iconfont icon-gonggao-yiru"></span><b class="notice_tit" data-id = ' + notice_data_list[i].myId + '>' + notice_data_list[i].title + '</b><i class="notice_time"></i></a>';
                    }
                    var browser = {
                        versions:function() {
                            var u = navigator.userAgent, app = navigator.appVersion;
                            return {//移动终端浏览器版本信息
                                trident : u.indexOf('Trident') > -1, //IE内核
                                presto : u.indexOf('Presto') > -1, //opera内核
                                webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                                gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                                mobile : navigator.userAgent.match(/(phone|pad|pod|iPhone|iPod|ios|iPad|Android|Mobile|BlackBerry|IEMobile|MQQBrowser|JUC|Fennec|wOSBrowser|BrowserNG|WebOS|Symbian|Windows Phone)/i), //是否为移动终端
                                ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                                android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                                iPhone : u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
                                iPad: u.indexOf('iPad') > -1, //是否iPad
                                webApp : u.indexOf('Safari') == -1,//是否web应该程序，没有头部与底部
                                google:u.indexOf('Chrome')>-1
                            };
                        }(),
                        language : (navigator.browserLanguage || navigator.language).toLowerCase()
                    };
                    $(".sys_nobox").html(html);

                    $(".notice_tit").on("click", function () {
                        //console.log(2332)
                        if(browser.versions.mobile){
                            if(browser.versions.android){
                                window.location.href = '/downApp_And'
                            }
                            if(browser.versions.iPhone){
                                window.location.href = '/downApp_ios'
                            }
                        }else{
                            var ids = $(this).attr("data-id")
                            $.cookie("notice_id", ids, {path: "/"});
                            self.location.href = '/bw/announcements/announcementsdetail?id=' + ids;
                        }

                    });
                    // console.log($(".notice_tit")[0].textContent)
                    // sliceEnglish($(".notice_tit").textContent,10)

                },
                error: function (e) {
                    console.log(e);
                }
            });
        };
//     //英文标题按单词截取(参数说明 text:要截取的英文 len：要截取的长度)
//     function sliceEnglish(text,len) {
//         text = String(text)
//         console.log(text.length)
//         //如果要截取文本的长度小于或者等于要截取的长度，则不进行截取，直接返回文本
//         if(text.length < len) {
//             return text;
//         }
//         //文本的长度大于要截取的长度，进行截取
//         else {
//             text = text.substr(0,len);
//             console.log(text)
// //       以空格切分字符串
//             var textArr = text.split(" ");
// //           最后一个字符长度
//             var lastLen = textArr.pop().length;
//             if(lastLen > 3) {
//                 return text.substr(0,text.length-lastLen-1);
//             } else if(lastLen === 3 ){
//                 return text;
//             }else{
//                 console.log(textArr)
//                 var lastTwoLen =  textArr[textArr.length - 1].length;
//                 return text.substr(0,text.length-lastLen-lastTwoLen-2);
//             }
//         }
//     }
        news_func();
        // 首页右边栏快讯
        function news_func() {
            $.ajax({
                url: "/msg/newsListHome?type=2",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    var html = "";
                    var html1 = "";
                    var row = data.datas.datalist;
                    //超出父级的高度
                    var _maxHeight = 465;
                    var browser = {
                        versions:function() {
                            var u = navigator.userAgent, app = navigator.appVersion;
                            return {//移动终端浏览器版本信息
                                trident : u.indexOf('Trident') > -1, //IE内核
                                presto : u.indexOf('Presto') > -1, //opera内核
                                webKit : u.indexOf('AppleWebKit') > -1, //苹果、谷歌内核
                                gecko : u.indexOf('Gecko') > -1 && u.indexOf('KHTML') == -1, //火狐内核
                                mobile : !! u.match(/AppleWebKit.*Mobile.*/) || !! u.match(/AppleWebKit/) && u.indexOf('QIHU') && u.indexOf('QIHU') > -1 && u.indexOf('Chrome') < 0, //是否为移动终端
                                ios : !!u.match(/\(i[^;]+;( U;)? CPU.+Mac OS X/),
                                android : u.indexOf('Android') > -1 || u.indexOf('Linux') > -1, //android终端或者uc浏览器
                                iPhone : u.indexOf('iPhone') > -1 || u.indexOf('Mac') > -1, //是否为iPhone或者QQHD浏览器
                                iPad: u.indexOf('iPad') > -1, //是否iPad
                                webApp : u.indexOf('Safari') == -1,//是否web应该程序，没有头部与底部
                                google:u.indexOf('Chrome')>-1
                            };
                        }(),
                        language : (navigator.browserLanguage || navigator.language).toLowerCase()
                    };
                    for (var i = 0; i < row.length; i++) {
                        !row[i].pubTime && (row[i].pubTime = '');
                        row[i].pubTime && (row[i].pubTime = lanInt == 'en' ? method.formatDate(row[i].pubTime, "MM-dd-yyyy") : method.formatDate(row[i].pubTime, "yyyy-MM-dd"));
                        // html += '<li class="work">' +
                        //     '<div class="relative">' +
                        //     '<span class="date">' + (row[i].top ? "" : row[i].pubTime) + '</span>' +
                        //     '<a class="wooke_tit ' + (row[i].top ? "bbyh-istop" : "") + (!row[i].top && !row[i].pubTime ? " bbyh-wooke_tit-child" : "") + '" href="/v2/news/newsdetail?id=' + row[i].id + '"><i>' + row[i].topInfo + '</i>' + row[i].title + '</a>' +
                        //     '<span class="' + (row[i].pubTime ? "circle" : "") + '"></span>' +
                        //     '</div>' +
                        //     '<div class="content" style="width: 100%;overflow: hidden;">' +
                        //     '<a class="content_a" href="/v2/news/newsdetail?id=' + row[i].id + '"><p  class="limitLine">' + row[i].digest + '</p></a>'
                        // '</div></li>'
                        if(browser.versions.mobile){
                            html += '<li class="work">' +
                                '<div class="relative">' +
                                '<span class="date">' + (row[i].top ? "" : row[i].pubTime) + '</span>' +
                                '<a class="wooke_tit ' + (row[i].top ? "bbyh-istop" : "") + (!row[i].top && !row[i].pubTime ? " bbyh-wooke_tit-child" : "") + '" href="javascript:void(0)"><i>' + row[i].topInfo + '</i>' + row[i].title + '</a>' +
                                '<span class="' + (row[i].pubTime ? "circle" : "") + '"></span>' +
                                '</div>' +
                                '<div class="content" style="width: 100%;overflow: hidden;">' +
                                '<a class="content_a" href="javascript:void(0)"><p  class="limitLine">' + row[i].digest + '</p></a>'
                            '</div></li>'
                        }else{
                            html += '<li class="work">' +
                                '<div class="relative">' +
                                '<span class="date">' + (row[i].top ? "" : row[i].pubTime) + '</span>' +
                                '<a class="wooke_tit ' + (row[i].top ? "bbyh-istop" : "") + (!row[i].top && !row[i].pubTime ? " bbyh-wooke_tit-child" : "") + '" href="/bw/news/newsdetail?id=' + row[i].id + '"><i>' + row[i].topInfo + '</i>' + row[i].title + '</a>' +
                                '<span class="' + (row[i].pubTime ? "circle" : "") + '"></span>' +
                                '</div>' +
                                '<div class="content" style="width: 100%;overflow: hidden;">' +
                                '<a class="content_a" href="/bw/news/newsdetail?id=' + row[i].id + '"><p  class="limitLine">' + row[i].digest + '</p></a>'
                            '</div></li>'
                        }
                    }
                    $("#timeline").html(html)

                    var _limitContent = document.getElementsByClassName('limitLine');
                    var _overHeight = $('#timeline').height();
                    // console.log(_overHeight)
                    if (_overHeight < _maxHeight) {
                        $('#timeline').css({
                            'paddingRight': '20px'
                        })
                    }

                    for (var i = 0; i < _limitContent.length; i++) {
                        //var _content = limitNum('maturity, their returns are more stable. Fourth, we should pay attention to current earnings. Bond funds mainly pursue fixed current receivers, which lack the potential of value-added compared with stock funds, and are more suitable for investors who are unwilling to take too many risks and seek stable current returns.the risk is small. Compared with stock funds, although the risk is low, the return is much worse. Second, the cost is lower. Because bond fund management is not as complex as the stock base, management fees are relatively lower. Third, income is stable.');
                        //var _content = limitNum('大萨达大多数 打算的的阿萨德 的暗示打算的 的阿达阿萨德撒的阿萨德 的阿萨德啊打算打打打 的阿达阿达打的啊的啊的啊 的大大阿达  打算的的阿达  打的的，大大的阿达的');
                        var _content = limitNum(_limitContent[i].innerHTML);
                        _limitContent[i].innerHTML = _content
                    }


                }

            })
        }

        function limitNum(word) {
            var _lan = $.cookie('zlan');
            var _word = word.toString()
            var _str;
            if (_lan == 'en') {
                if (_word.length >= 95) {
                    _str = _word.slice(0, 95);
                    _str = _str.slice(0, _str.lastIndexOf(' ')) + '...'
                    return _str
                }
            } else {

                if (_word.length >= 46) {
                    _str = _word.slice(0, 46) + '...'
                    return _str
                }

            }
            return _word
        }

        friend_link();

        function friend_link() {
            $.ajax({
                url: "/getFriendUrl",
                type: "GET",
                dataType: "json",
                success: function (data) {
                    var html = "";
                    var html1 = "";
                    var row = data.datas;
                    if(row.length == 0){
                        $(".friend_link").css('display','none')
                    }else{
                        for (var i = 0; i < row.length; i++) {
                            html += '<a href="' + row[i].url + '" target=_blank>' + row[i].name + '</a>'
                        }
                        $(".friend_box").html(html)
                    }
                },
                error:function () {
                    $(".friend_link").css('display','none')
                }
            })
        }

        //图表
// 基于准备好的dom，初始化echarts实例
//==========================  暂时隐藏
// var myChart = echarts.init(document.getElementById('main'));
// var axisChart = echarts.init(document.getElementById('axisChart'));
// var catChart = echarts.init(document.getElementById('catChart'));
// var hereyChart = echarts.init(document.getElementById('hereyChart'));
// ==========================
        var mychartadd = {}
        var axisadd = {}
        var catadd = {}
        var hereyadd = {}
// 指定图表的配置项和数据
        option = ({
            title: {
                text: bitbank.L('用户占比'),
                textStyle: {
                    color: '#737A8D',
                    fontSize: 16,
                    fontWeight: 'normal',
                },
                left: !window.dps ? '65%' : '40%',
            },

            tooltip: {
                trigger: 'item',
                padding: 10,
                formatter: "{a} <br/>{b}: {c} ({d}%)"
            },
            legend: {
                selectedMode: false,
                orient: 'vertical',
                x: !window.dps ? 'right' : '75%',
                y: !window.dps ? '70' : '28%',
                textStyle: {
                    color: ' #737A8D'
                },
                align: 'left',
                width: 45,
                itemGap: 16,
                itemWidth: 12,
                itemHeight: 12,
                data: [],
            },
            series: [

                {
                    name: bitbank.L('用户占比'),
                    type: 'pie',
                    clockwise: true,
                    // selectedMode: 'single',
                    radius: !window.dps ? ['40%', '70%'] : ['40%', '70%'],
                    center: ['50%', '50%'],
                    avoidLabelOverlap: false,
                    selectedOffset: 16,
                    animationDuration: 2000,
                    // hoverAnimation: false,
                    // animationType:'scale',
                    animationEasing: 'linear',
                    label: {
                        normal: {
                            show: false,
                            position: 'center'
                        },
                    },
                    labelLine: {
                        normal: {
                            show: false
                        }
                    },
                    data: []
                }
            ],
            calculable: true,

            color: ['#6AABC9', '#5FBFCC', '#5EA8A0', '#78C795', '#A2BF8E', '#C0C998'],
        });
// myChart.showLoading({
//     color: '#6687D3',
//     textColor: '#B6C1DA',
//     maskColor:'#2F343F'
// });
//==========================  暂时隐藏
//mychartLoad();
        function mychartLoad() {

            $.ajax({
                url: '/report/queryUserDistribution/',
                type: "GET",
                dataType: "json",
                data: {'internationalization': lanInt.toUpperCase()},
                success: function (data) {
                    myChart.hideLoading();
                    var legendData = [];
                    var seriesData = [];
                    data.datas.forEach(function (item) {
                        legendData.push(item.attribute);
                    var seriesJson = {};
                    seriesJson.value = item.initial;
                    seriesJson.name = item.attribute;
                    seriesData.push(seriesJson)
                })
                    console.log(legendData);
                    option['legend'].data = legendData
                    option['series'][0].data = seriesData
                    if (window.dps) {
                        option['legend'].textStyle.fontSize = window.dps
                        option['title'].textStyle.fontSize = window.dps
                        option['series'][0].center = ['38%', '60%']
                        option['legend'].itemGap = window.dps * 1.5
                        option['tooltip'].padding = 2
                        option['tooltip'].textStyle = {
                            fontSize: window.dps
                        }
                    }
                }
            })
        }

//交易量分布
        axisoption = {
            title: {
                text: bitbank.L('交易量分布'),
                textStyle: {
                    color: '#737A8D',
                    fontSize: 16,
                    fontWeight: 'normal',
                },
                left: 'center'
            },
            tooltip: {
                formatter: "{a} <br/>{b}: {c} "
            },
            grid: {
                left: '15%',
                right: '5%',
            },
            xAxis: [
                {
                    type: 'category',
                    data: [],
                    axisPointer: {
                        type: 'shadow'
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#5F6575',
                        },
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: window.dps ? ((document.documentElement || document.body).offsetWidth < 568 ? '-60' : 0) : 0,
                    }
                }
            ],
            yAxis: [
                {
                    type: 'value',
                    // interval: 10,
                    splitLine: {show: false},
                    axisLine: {
                        lineStyle: {
                            color: '#5F6575'
                        }
                    },
                    interval: 0,
                    max: 0,
                    min: 0,
                    splitNumber: 6,
                    axisLabel: {
                        formatter: function (dayVolume_1) {
                            Big.RM = 0;
                            var priceBtc = "";
                            var dayVolume = new Big(dayVolume_1);
                            var k_1 = new Big(1000);
                            var wan = new Big(10000);
                            var m_1 = new Big(1000000);
                            if (parseFloat(dayVolume_1) < 10000) {
                                if (parseFloat(dayVolume_1) < 100) {
                                    priceBtc = parseInt(dayVolume);
                                } else {
                                    priceBtc = parseInt(dayVolume / 100) * 100;
                                }
                            } else {
                                if (LANG == 'cn') {
                                    priceBtc = parseInt(dayVolume.div(wan)) + "万";
                                    // this.fixNumber(parseFloat(dayVolume) / 10000, 2) + "万";
                                } else {
                                    if (parseFloat(dayVolume_1) >= 10000000) {
                                        priceBtc = parseInt(dayVolume.div(m_1)) + "M";
                                        // this.fixNumber(parseFloat(dayVolume) / 1000000, 2) + "M";
                                    } else {
                                        priceBtc = parseInt(dayVolume.div(k_1)) + "K";
                                        // this.fixNumber(parseFloat(dayVolume) / 1000, 2) + "K";
                                    }
                                }
                            }
                            return priceBtc;
                        }
                    },
                    axisTick: {
                        show: false
                    },
                }
            ],
            series: [
                {
                    name: bitbank.L('交易量分布'),
                    type: 'bar',
                    barWidth: 31,
                    animationDuration: 2000,
                    itemStyle: {
                        normal: {
                            color: '#5FBFCC',
                        }
                    },
                    data: []
                },
            ]
        };
// axisChart.showLoading({
//     color: '#6687D3',
//     textColor: '#B6C1DA',
//     maskColor:'#2F343F'
// });
//==========================  暂时隐藏
//axischartLoad();
        function axischartLoad() {

            $.ajax({
                url: '/report/queryTransactionVolume/',
                type: "GET",
                dataType: "json",
                success: function (data) {
                    axisChart.hideLoading();
                    var legendData = [];
                    var seriesData = [];
                    var max = 0;
                    data.datas.forEach(function (item){
                        if(item) {
                            legendData.push(item.marketName);
                            if (item.volumeCache > max) {
                                max = item.volumeCache
                            }
                            seriesData.push(item.volumeCache);
                        }
                    }
                )
                    var range = max / 6
                    axisoption['yAxis'][0].max = max;
                    axisoption['yAxis'][0].interval = range;
                    axisoption['xAxis'][0].data = legendData;
                    axisoption['series'][0].data = seriesData;

                    if (window.dps) {

                        axisoption['title'].textStyle.fontSize = window.dps;
                        axisoption['series'][0].barWidth = '44%';
                        axisoption['xAxis'][0].axisLabel.textStyle = {
                            fontSize: window.dps
                        };
                        axisoption['yAxis'][0].axisLabel.textStyle = {
                            fontSize: window.dps
                        }
                    }

                }

            })
        }

        catoption = {
            title: {
                text: bitbank.L("平台资金"),
                textStyle: {
                    color: '#737A8D',
                    fontSize: 16,
                    fontWeight: 'normal',
                },
                left: !window.dps ? '230' : '45%'
            },
            tooltip: {
                // trigger: 'axis'
                formatter: "{a} <br/>{b}: {c} "
            },
            legend: {
                selectedMode: false,
                data: [bitbank.L('流入'), bitbank.L('流出')],
                // x: 'right',
                right: 10,
                top: !window.dps ? 30 : '15%',
                textStyle: {
                    color: '#737A8D',
                },
                itemGap: 15,
                itemWidth: 30,
                itemHeight: 4,
            },
            grid: {
                left: '15%',
                right: '5%',
            },
            xAxis: [
                {
                    type: 'category',
                    data: [],
                    axisPointer: {
                        type: 'shadow'
                    },
                    axisLine: {
                        lineStyle: {
                            color: '#5F6575'
                        },
                    },
                    axisLabel: {
                        interval: 0,
                        rotate: window.dps ? ((document.documentElement || document.body).offsetWidth < 568 ? '-60' : 0) : 0,
                    },
                },

            ],
            yAxis: [
                {
                    type: 'value',
                    axisLine: {
                        lineStyle: {
                            color: '#5F6575'
                        },
                    },
                    interval: 0,
                    max: 0,
                    min: 0,
                    data: [],
                    axisLabel: {
                        formatter: function (dayVolume_1) {
                            Big.RM = 0;
                            var priceBtc = "";
                            var dayVolume = new Big(dayVolume_1);
                            var k_1 = new Big(1000);
                            var wan = new Big(10000);
                            var m_1 = new Big(1000000);
                            if (parseFloat(dayVolume_1) < 10000) {
                                if (parseFloat(dayVolume_1) < 100) {
                                    priceBtc = parseInt(dayVolume);
                                } else {
                                    priceBtc = parseInt(dayVolume / 100) * 100;
                                }

                            } else {
                                if (LANG == 'cn') {
                                    priceBtc = parseInt(dayVolume.div(wan)) + "万";
                                    // this.fixNumber(parseFloat(dayVolume) / 10000, 2) + "万";
                                } else {
                                    if (parseFloat(dayVolume_1) >= 10000000) {
                                        priceBtc = parseInt(dayVolume.div(m_1)) + "M";
                                        // this.fixNumber(parseFloat(dayVolume) / 1000000, 2) + "M";
                                    } else {
                                        priceBtc = parseInt(dayVolume.div(k_1)) + "K";
                                        // this.fixNumber(parseFloat(dayVolume) / 1000, 2) + "K";
                                    }
                                }
                            }
                            return priceBtc;
                        }
                    },
                    splitLine: {show: false},
                    axisTick: {
                        show: false
                    },
                }
            ],
            series: [
                {
                    name: bitbank.L('流入'),
                    type: 'bar',
                    data: [],
                    barGap: !window.dps ? '80%' : '50%',
                    barWidth: 15,
                    itemStyle: {
                        normal: {
                            color: '#A2BF8E',
                        }
                    },

                },
                {
                    name: bitbank.L('流出'),
                    type: 'bar',
                    barWidth: 15,
                    data: [],
                    itemStyle: {
                        normal: {
                            color: '#5FBFCC',
                        }
                    },
                }
            ]
        };
// catChart.showLoading({
//     color: '#6687D3',
//     textColor: '#B6C1DA',
//     maskColor:'#2F343F'
// });
//==========================  暂时隐藏
//catchartLoad();
        function catchartLoad() {

            $.ajax({
                url: '/report/queryPlatformFunds',
                type: "GET",
                dataType: "json",
                success: function (data) {
                    catChart.hideLoading();
                    var legendData = [];
                    var buyIn = [];
                    var sellOut = [];
                    data.datas.forEach(function (item){
                        legendData.push(item.fundsName);
                })
                    var yAxisData = [];
                    var maxData = 0;
                    var yData = Array.from(new Set(legendData))

                    yData.forEach(function (conf) {
                        data.datas.forEach(function (item){
                        if(item.fundsName == conf
                )
                    {
                        if (item.dealType == 1) {
                            buyIn.push(item.txAmount);
                        } else {
                            sellOut.push(item.txAmount)
                        }
                    }
                    if (item.txAmount > maxData) {
                        maxData = item.txAmount
                    }
                })
                })

                    var range = maxData / 6
                    catoption['yAxis'][0].interval = range;
                    catoption['yAxis'][0].max = maxData
                    catoption['xAxis'][0].data = yData;
                    catoption['series'][0].data = buyIn;
                    catoption['series'][1].data = sellOut;
                    if (window.dps) {

                        catoption['title'].textStyle.fontSize = window.dps;
                        catoption['xAxis'][0].axisLabel.textStyle = {fontSize: window.dps};
                        catoption['yAxis'][0].axisLabel.textStyle = {fontSize: window.dps};
                        catoption['legend'].textStyle.fontSize = window.dps - 2;
                        catoption['grid'].top = '30%';

                    }

                }
            })
        }

        var baifenbi = [0.111, 0.333, 0.444, 0.555, 0.777, 0.888, 0.922];
        var grayBar = [1, 1, 1, 1, 1, 1, 1];
        var zongjine = [91230000, 20000000, 30000000, 40000000, 50000000, 60000000, 70000000];

        hereyoption = {
            title: {
                text: bitbank.L('委托分布'),
                left: !window.dps ? '230' : '45%',
                textStyle: {
                    color: '#737A8D',
                    fontSize: 16,
                    fontWeight: 'normal',
                },
                subtext: ''
            },
            color: ['#33B8C9', '#A2BF8E'], //进度条颜色
            tooltip: {
                formatter: "{a} <br/>{b}:{c}"
            },
            legend: {
                selectedMode: false,
                data: [bitbank.L('买入'), bitbank.L('卖出')],
                textStyle: {
                    color: '#737A8D',
                },
                right: 10,
                top: 30,
                itemGap: 15,
                itemWidth: 30,
                itemHeight: 4,
            },
            yAxis: [{
                type: 'category',
                axisLine: {
                    lineStyle: {
                        color: '#5F6575'
                    },
                },
                axisTick: {
                    show: false
                },
                data: []
            }],
            xAxis: [{
                type: 'value',
                splitLine: {show: false},
                axisLine: {
                    lineStyle: {
                        color: '#5F6575'
                    },
                },
                axisLabel: {
                    formatter: function (value) {
                        var str = Number(value * 100);
                        str += "%";
                        return str;
                    },
                },
                axisTick: {
                    show: false
                },
            }],
            grid: {
                left: '15%',
                right: '5%',
            },
            series: [{
                name: bitbank.L('买入'),
                stack: '分布',
                type: 'bar',
                barWidth: 14,
                animationDuration: 2000,
                data: [],
            },
                {
                    name: bitbank.L('卖出'),
                    stack: '分布',
                    type: 'bar',
                    animationDuration: 2000,
                    barWidth: 14,
                    data: [],
                }
            ]
        };
// hereyChart.showLoading({
//     color: '#6687D3',
//     textColor: '#B6C1DA',
//     maskColor:'#2F343F'
// });
//==========================  暂时隐藏

//hereychartLoad();
        function hereychartLoad() {

            $.ajax({
                url: '/report/queryEntrustmentDisstribution',
                type: "GET",
                dataType: "json",
                success: function (data) {
                    hereyChart.hideLoading();
                    var legendData = [];
                    var buyIn = [];
                    var sellOut = [];
                    data.datas.forEach(function (item){
                        legendData.push(item.coinTypeName);
                })
                    var yData = Array.from(new Set(legendData))

                    yData.forEach(function (conf) {
                        data.datas.forEach(function (item){
                        if(item.coinTypeName == conf
                )
                    {
                        if (item.type == 0) {
                            buyIn.push(item.buyPercentage);
                        } else {
                            sellOut.push(item.salePercentage)
                        }
                    }
                })
                })
                    hereyoption['yAxis'][0].data = yData;
                    hereyoption['series'][0].data = buyIn;
                    hereyoption['series'][1].data = sellOut;
                    if (window.dps) {

                        hereyoption['title'].textStyle.fontSize = window.dps;
                        hereyoption['xAxis'][0].axisLabel.textStyle = {fontSize: window.dps};
                        hereyoption['yAxis'][0].axisLabel = {textStyle: {fontSize: window.dps}};
                        hereyoption['legend'].textStyle.fontSize = window.dps - 2;
                        hereyoption['grid'].top = '30%';

                    }
                }
            })
        };

        function percentageData(value) {
            var str = Number(value * 100).toFixed(1);
            str += "%";
            return str;
        }

        var myChartNum = 1;
        var axisChartNum = 1;
        var catChartNum = 1;
        var hereyChartNum = 1;
        // 使用刚指定的配置项和数据显示图表。
        //==========================  暂时隐藏

// $(window).scroll(function(){
//     if($(this).scrollTop()>600&&myChartNum ==1){
//         setTimeout(
//             function(){
//                 myChart.setOption(option)
//                 myChart.setOption(mychartadd)
//             }
//
//             ,500)
//          myChartNum++
//     }
//
//     if($(this).scrollTop()>1000&&axisChartNum==1){
//         setTimeout(
//             function(){
//                 axisChart.setOption(axisoption)
//             }
//
//             ,500)
//         axisChartNum++
//     }
//     if($(this).scrollTop()>1400&&catChartNum == 1){
//         setTimeout(
//             function(){
//                 catChart.setOption(catoption)
//             }
//             ,500)
//         catChartNum++
//     }
//     if($(this).scrollTop()>1800&&hereyChartNum ==1){
//         hereyChart.setOption(hereyoption)
//         setTimeout(
//             function(){
//                 hereyChart.setOption(hereyoption)
//             }
//             ,500)
//             hereyChartNum++
//     }
// });
        $('#key_word').bind('blur keyup', function () {
            if ($(this).val() == '') {
                $('.icon-shanchu-yiru').hide()
                $('.icon-search-bizhong').show()
            }
        });
        $('#key_word').on('input propertychange', function () {
            bill.searchName = $('#key_word').val()
            bill.getMarket();
            $('.icon-search-bizhong').hide();
            $('.icon-shanchu-yiru').show();
            $('.icon-shanchu-yiru').click(function () {
                $('#key_word').val("")
                $('.icon-shanchu-yiru').hide()
                $('.icon-search-bizhong').show()
                bill.searchName = '';
                bill.getMarket();
            })

        });

        var marketState = 0;
// 行情鼠标点击事件
        $(".markt_top > li").on("click", function () {
            var sobj = $(".slider"),
                w = sobj.width();

            // var htmla = '<svg class="icon" aria-hidden="true"><use xlink:href="#icon-Shape"></use><span>自选区</span></svg>';
            // var htmlb = '<svg class="icon" aria-hidden="true"><use xlink:href="#icon-favorit_hover"></use><span>自选区</span></svg>';
            // $(".favorit_bth").html(htmla);
            $(".markt_top > li").removeClass("markttop_on");
            var whatTab = $(this).index();
            var _this = $(this);
            var howFar = w * whatTab;
            var sliderWidth = w;
            if (_this.index() == 2) {
                sliderWidth = w;
                marketState = 1;
                // $(".favorit_bth").html(htmlb);
            } else {
                sliderWidth = w;
                marketState = 0;
            }
            _this.addClass("markttop_on");
            sobj.stop().animate({
                left: howFar + "px",
                width: sliderWidth + "px"
            }, 300, "easeInOutQuart");
            $(".markettable").css("display", "none")
            $(".markettable").eq($(this).index()).css("display", "block");
        });

//点击排序事件
// $(".market_title > li > i").hide();
        var preIndex = 0;
        var html1 = '<svg class="icon" aria-hidden="true">' +
            '<use xlink:href="#icon-paixujiantou-zhengxu"></use>' +
            '</svg>';
        var html2 = '<svg class="icon" aria-hidden="true">' +
            '<use xlink:href="#icon-paixujiantou-daoxu"></use>' +
            '</svg>';
        var html3 = '<svg class="icon arrow show_on" aria-hidden="true">' +
            '<use xlink:href="#icon-paixujiantou-moren"></use></svg>';
// var html4 = '<svg class="icon arrow" aria-hidden="true">' +
// 	'<use xlink:href="#icon-jiantou_down"></use></svg>';
        $(".market_title > .sort_btn span").on('click', function () {
            if (preIndex == $(this).parent().index()) {
                ++bill.clickNum;
                bill.clickNum = bill.clickNum == 3 ? 0 : bill.clickNum;
            } else {
                $(this).parent().siblings().find("i").html(html3);
                bill.clickNum = 1
            }
            if (bill.clickNum == 1) {
                // $(this).find("i").show()
                $(this).find("i").html(html1)

            } else if (bill.clickNum == 2) {
                $(this).find("i").html(html2)

            } else {
                $(this).find("i").html(html3)
            }
            bill.thIndex = $(this).parent().index();
            if ($(this).index() == 5 && marketState == 1) {
                bill.sortName = "volume"
            } else {
                bill.sortName = $(this).parent().data("sortname");
            }
            if (bill.sortName) {
                bill.getMarket();
                ;
            }
            preIndex = $(this).parent().index()
        })
// myChart.setOption(option,true);
// axisChart.setOption(axisoption)
// catChart.setOption(catoption)
// hereyChart.setOption(hereyoption)


    })
}catch(e) {

}