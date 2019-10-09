define(function(require, exports, module) {
    "require:nomunge,exports:nomunge,module:nomunge";
    var M = require("module_method");
    var markets = require("module_market");
    var asset = require("module_asset");
    var tmpl_s = require("module_tmpl");
    var range_s = require("module_range");
    var Highcharts = require("module_highCharts");
    var wheel = require("module_wheeler");

    var trans = {};

    var socket;
    var oldChannel;

    var market;
    var numberBi;
    var numberBiEn;
    var numberBiNote;
    var numberBixNormal;
    var numberBixShow;
    var numberBixDian;
    var exchangeBi;
    var exchangeBiEn;
    var exchangeBiNote;
    var exchangeBixNormal;
    var exchangeBixShow;
    var exchangeBixDian;
    var entrustUrlBase;
    trans.last_trade_tid = 0;
    trans.last_repo_tid = 0;
    trans.tradesLimit = 25;
    //盘口配置
    var marketLength = 5;
    var marketDepth = $("#bkDepth dd[class='active']").data("depth") || $("#bkDepth dd:eq(0)").data("depth");
    var marketPrice = [];
    marketPrice[0] = 0;
    marketPrice[1] = [0];
    marketPrice[2] = [0];
    marketPrice[3] = "up";
    var maxPrice = 0,
        minPrice = 0,
        dayVolume = 0;
    var loanProtect = {
        "BTC": 0.01,
        "ETH": 0.01,
        "ETC": 0.01
    }
    var marketProtectCur,
        amountDecialCur;
    var lastTime = new Date().getTime();
    var lastTimeTrans = 0;
    var lastTimeRecord = 0;
    var lockEntrust = false,
        lockRepeatEntrust = false;
    var type = -1,
        entrustType = 1,
        status = 0,
        timeFrom = 0,
        timeTo = 0,
        numberFrom = 0,
        numberTo = 0,
        priceFrom = 0,
        priceTo = 0,
        dateTo = 0,
        pageSize = 20,
        pageIndex = 1;
    var needSafeWord = false;
    var ajaxIng = false;
    var doEntrustStatus = false;
    var buyRangeMp;
    var sellRangeMp;
    var buyPlanRangeMp;
    var sellPlanRangeMp;
    var buyStrategy = 0;
    var sellStrategy = 0;
    var leverFlagBuy = false;
    var leverFlagSell = false;
    var canLoanInMoney, canLoanInCoin;
    var userRecord = null;
    var userRecordLastTime = 0;
    var initDishFlag = true;
    var initRecordFlag = true;
    var ordersTransposalItemP = 0;
    var legalTender = "BTC";
    var bkEntrustTabClose = true;
    
    function getParameterByName(name) {
        name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
        var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
            results = regex.exec(location.search);
        return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
    }
    trans.pageIndexInit = function(type, t) {
        var $this = this;
        $this.hasSafePwd();
        $this.getUnreadNotice();
        // console.log(type);
        if(type!="gbc_usdt"){
            $("#gbc-repo").hide();
            if (!user.isLogin()) {
                // $("#gbc-repo").hide();
            }
        }
        $this.getMarket(type, function() {
            $this.getTrans();
            if (user.isLogin()) {
                var userId = $.cookie(UID);
                // var readyFun = setInterval(function() {
                //     if (webSocket) {
                //         webSocket.init(function() {
                //             webSocket.sendMessage("{'event':'addChannel','channel':'push_user_record','userId':'" + userId + "','market':'" + market + "','isZip':'true'}");
                //         });
                //         clearInterval(readyFun);
                //     }
                // }, 50);
            }else{
                var $thisI = $('#bkEntrustTab h4').find('i'),
                        $parentB = $("#bkEntrustTab");
                    $('#bkEntrustTab h4').css('padding-bottom', '15px');
                    $parentB.css('padding-bottom', '0');
                    $parentB.find('.trade-content').hide();
                    $parentB.find('.trade-title').css('border-bottom', '0');
                    $parentB.find('.trade-title-right').hide();
                    $thisI.removeClass('open');
            }
            var scrollFlag = false;
            var sideBarTop = $('.menu-left').offset().top;
            var footerHeight = $('.bk-footer').height();
            //var windowHeight = $(window).height();
            // //var documentHeight = $(document).height();
            // var sideBarHeight = $('.menu-left').height();
            // var fsHeight = footerHeight+sideBarHeight;
            $(document).scroll(function() {
                var documentHeight = $(document).height();
                var sideBarHeight = $('.menu-left').height();
                var fsHeight = footerHeight + sideBarHeight;
                if ($(document).scrollTop() > sideBarTop) {
                    var bodyScroll = $(document).scrollLeft();
                    var bodyScrollTop = $(document).scrollTop();
                    if (documentHeight < fsHeight + bodyScrollTop) {
                        bodyScrollTop = documentHeight - bodyScrollTop - fsHeight - 14;
                    } else {
                        bodyScrollTop = 0;
                    };

                    scrollFlag = true;
                    $('.menu-left').css({
                        'position': 'fixed',
                        'top': bodyScrollTop,
                        'left': '-' + bodyScroll + 'px',
                        'margin-left': '10px',
                        'box-shadow': 'rgba(0,0,0,.06) 0 2px 3px',
                        'z-index': '100'
                    })
                } else {
                    scrollFlag = false;
                    $('.menu-left').css({
                        'position': 'relative',
                        'box-shadow': 'none',
                        'margin-left': '0',
                        'left': '0px'
                    })
                }
            });
            $this.getEntrustRecord({
                listDiv: "#entrustRecord",
                lastTimeRecord: lastTime,
                status: 3,
                iHide: function() {
                    var $thisI = $('#bkEntrustTab h4').find('i'),
                        $parentB = $("#bkEntrustTab");
                    $('#bkEntrustTab h4').css('padding-bottom', '15px');
                    $parentB.css('padding-bottom', '0');
                    $parentB.find('.trade-content').hide();
                    $parentB.find('.trade-title').css('border-bottom', '0');
                    $parentB.find('.trade-title-right').hide();
                    $thisI.removeClass('open');

                },
            });
            $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, status: 2 });

            $this.getNewTradesRecord({ symbol: type, initRecord: true, last_trade_tid: "0" });
            if(type=="gbc_usdt"){
                var repoBody = $(".repo_body");
                var repoListBox = repoBody.find(".repo_list_box");
                var relatedListBox = repoBody.find(".related_list_box");
                var $related = $("#related");
                repoListBox.show();
                relatedListBox.hide();
                $related.removeClass("active");
                if (user.isLogin()) {
                    // $("#gbc-repo").hide();
                    $related.click(function(){
                        var b = repoListBox.css("display")=="block"?true:false;
                        if(b){
                            repoListBox.hide();
                            relatedListBox.show();
                            $related.addClass("active");
                        }else{
                            repoListBox.show();
                            relatedListBox.hide();
                            $related.removeClass("active");
                        }
                    });
                }else{
                    $related.click(function(){
                        JuaBox.showRight(bitbank.L("请登录后再尝试"));
                    })
                }
                $this.getRepo();
                $(document).on("click", ".repo_body tbody tr", function() {
                    // console.log($(this).attr("id"));
                    $this.getRepoDetail(
                        $(this).attr("id"),
                        function(data) {
                            $this.showRepoDetail(data);
                        }
                    );
                });
            }

            asset.getUserAsset(function() {
                $this.upAsset();
            });
            setInterval(function(t) {
                $this.upAsset();
                $this.getEntrustRecord({ listDiv: "#entrustRecord", status: 3 });
                $this.getEntrustRecord({ listDiv: "#historyRecord", status: 2 });
            }, 3000);

            setInterval(function() {
                $this.getNewTradesRecord({ symbol: type, initRecord: false, last_trade_tid: $this.last_trade_tid });
                $this.getUserTradesRecord();
            }, 3000);
            $this.depthChart(type);
            setInterval(function() {
                $this.depthChart(type);
            }, 10000);
        });
        if (user.isLogin()) {
            setInterval(function() {
                $this.updateSummary(type)
            }, 5000);
        }
        $("#summaryRecount").on('click', function(event) {
            event.preventDefault();
            /* Act on the event */
            $.getJSON(DOMAIN_TRANS + "/entrust/transactionSummaryResum?callback=?&currency=" + type, function(result) {
                JuaBox.showWrong(bitbank.L("清零成功！"));
                $this.updateSummary(type)
            })
        });
        $("#sellMarket").on("click", "tr", function() {
            $("#buyUnitPrice").val($(this).find("td").eq(1).text());
            $("#sellUnitPrice").val($(this).find("td").eq(1).text());
            var depthNumber = 0;
            if ($(this).parents("#sellMarket").length > 0) {
                for (var i = 0; i <= $(this).index(); i++) {
                    depthNumber = M.add(parseFloat(depthNumber), parseFloat(marketPrice[2][i][1]));
                }
            }
            $("#buyNumber").val(M.floorNumber(depthNumber, numberBixDian));
            $this.upAmount("buy");
            $this.upAmount("sell");
            if ($(document).scrollTop() > $('.bk-trans-form').offset().top) {
                goScrollTo('.bk-trans-form', 0);
            }
        });
        $("#buyMarket").on("click", "tr", function() {
            $("#buyUnitPrice").val($(this).find("td").eq(1).text());
            $("#sellUnitPrice").val($(this).find("td").eq(1).text());
            var depthNumber = 0;
            
            if ($(this).parents("#buyMarket").length > 0) {
                for (var i = 0; i <= $(this).index(); i++) {
                    depthNumber = M.add(parseFloat(depthNumber), parseFloat(marketPrice[1][i][1]));
                }
            }
            console.log(depthNumber)
            $("#sellNumber").val(M.floorNumber(depthNumber, numberBixDian));
            $this.upAmount("buy");
            $this.upAmount("sell");
            if ($(document).scrollTop() > $('.bk-trans-form').offset().top) {
                goScrollTo('.bk-trans-form', 0);
            }
        });

        $("#newTradesRecord").on("click", "li:not('.tit')", function() {
            $("#sellUnitPrice, #buyUnitPrice").val($(this).find("p").eq(2).text());
            $this.upAccount("buy");
            $this.upAccount("sell");
        });
        $("#leverSwitchBuy").on("click", function() {
            leverFlagBuy = this.checked;
            $this.upAccount("buy");
            $("#leverAccountBuyLabel").removeClass("open");
            if (leverFlagBuy) {
                $("#leverAccountBuyLabel").addClass("open");
                JuaBox.showRight(bitbank.L("您已开启买入交易的一键杠杆服务，请注意平仓风险！"))
            }
        });
        $("#leverSwitchSell").on("click", function() {
            leverFlagSell = this.checked;
            $this.upAccount("sell");
            $("#leverAccountSellLabel").removeClass("open");
            if (leverFlagSell) {
                $("#leverAccountSellLabel").addClass("open");
                JuaBox.showRight(bitbank.L("您已开启卖出交易的一键杠杆服务，请注意平仓风险！"))
            }
        });
        $("#buyBtn").on("click", function(event) {
            if($(this).hasClass("btn-disabled")) return false;
            if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
            if (event.originalEvent) {
                $this.doEntrust(1);
            } else {
                return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            }
        });
        $("#sellBtn").on("click", function(event) {
            if($(this).hasClass("btn-disabled")) return false;
            if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
            if (event.originalEvent) {
                $this.doEntrust(0);
            } else {
                return JuaBox.showWrong(bitbank.L("请勿使用非法脚本"));
            }
        });
        $(".bk-entrust").on("click", ".cancelEntrust", function() {
            $this.cancelEntrust($(this).data("id"), $(this).data("type"), $(this).data("plantype"));
        });
        $("[id='batchCancel']").on("click", function() {
            $this.batchCancelEntrust($(this).data("plantype"),
                function() {
                    setTimeout(function() {
                        $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                    }, 1000);
                });
        });
        $("#batchCancelPlan").on("click", function() {
            $this.batchCancelPlanEntrust(function() {
                setTimeout(function() {
                    $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                }, 1000);
            });
        });
        $(".bk-entrust").on("click", ".detailEntrust", function() {
            $this.getEntrustDetail(
                $(this).data("id"),
                $(this).data("types"),
                $(this).data("numbers"),
                function() {
                    JuaBox.info($("#tradeList").html(), { title: bitbank.L("委托详情"), width: 600, btnNum: 1 });
                }
            );
        });

        $("#buyType").on("click", "dd", function() {
            if ($(this).hasClass("active")) {
                return false;
            }
            buyStrategy = $(this).index();

            $("#buyType dd").removeClass("active");
            $(this).addClass("active");
            $("#buyType span").text($(this).text());

            if (buyStrategy == 0) {
                $("#buyPlanForm, .buyBatLabel").hide();
                $("#buyDefaultForm, .buyDefaultLabel").show();
            }
            if (buyStrategy == 1) {
                $("#buyDefaultForm").hide();
                $("#buyPlanForm").show();
            }
            if (buyStrategy == 2) {
                $("#buyDefaultForm,  .buyBatLabel").show();
                $("#buyPlanForm, .buyDefaultLabel").hide();
            }
        });
        $("#sellType").on("click", "dd", function() {
            if ($(this).hasClass("active")) {
                return false;
            }
            sellStrategy = $(this).index();
            $("#sellType dd").removeClass("active");
            $(this).addClass("active");
            $("#sellType span").text($(this).text());

            if (sellStrategy == 0) {
                $("#sellPlanForm, .sellBatLabel").hide();
                $("#sellDefaultForm, .sellDefaultLabel").show();
            }
            if (sellStrategy == 1) {
                $("#sellDefaultForm").hide();
                $("#sellPlanForm").show();
            }
            if (sellStrategy == 2) {
                $("#sellDefaultForm,  .sellBatLabel").show();
                $("#sellPlanForm, .sellDefaultLabel").hide();
            }
        });

        $("#bkDepth").on("click", "dd", function() {
            if ($(this).hasClass("active")) {
                return false;
            }
            if ($(this).index() > 0) {
                $("#bkLength").removeClass("active");
                $("#bkDepth").addClass("active");
                $("#bkLength dd:eq(0)").click();
            }
            $("#bkDepth dd").removeClass("active");
            $(this).addClass("active");
            $("#bkDepth span").text($(this).text());
            $this.changeDepth($(this).data("depth"));
        });
        $("#bkLength").on("click", "dd", function() {
            if ($(this).hasClass("active")) {
                return false;
            }
            if ($(this).index() > 0) {
                $("#bkDepth").removeClass("active");
                $("#bkLength").addClass("active");
                $("#bkDepth dd:eq(0)").click();
            }
            $("#bkLength dd").removeClass("active");
            $(this).addClass("active");
            $("#bkLength span").text($(this).text());
            $this.changeLength($(this).data("length"));
        });
        $("#buyUnitPrice, #realBuyAccount, #buyTriggerPrice, #buyPlanPrice, #buyPlanMoney, #sellUnitPrice, #buyMaxPrice, #realSellAccount, #sellTriggerPrice, #sellPlanPrice, #sellPlanMoney, #sellMaxPrice").on({
            "keyup": function() { $this.checkNumber($(this), exchangeBixDian); },
            "blur": function() { $this.checkNumber($(this), exchangeBixDian); }
        });
        $("#buyNumber,#buyPlanNumber, #sellNumber, #sellPlanNumber").on({
            "keyup": function() { $this.checkNumber($(this), numberBixDian); },
            "blur": function() { $this.checkNumber($(this), numberBixDian); }
        });
        $("#buyUnitPrice").on("keyup", function() {
            if (buyStrategy != 0) {
                return false;
            }
            var buyUnitPrice = parseFloat($("#buyUnitPrice").val()) || 0,
                buyNumber = parseFloat($("#buyNumber").val()) || 0;
            var buyMoney = 0;
            if (!buyUnitPrice) {
                return false;
            }
            buyMoney = M.floorNumber(M.multiply(buyUnitPrice, buyNumber), exchangeBixDian);

            $("#realBuyAccount").val(buyMoney);
        });
        $("#buyMaxPrice").on("keyup", function() {
            var buyMaxPrice = parseFloat($("#buyMaxPrice").val()) || 0,
                buyNumber = parseFloat($("#buyNumber").val()) || 0;
            var buyMoney = 0;
            if (!buyMaxPrice) {
                return false;
            }
            buyMoney = M.fixNumber(M.multiply(buyMaxPrice, buyNumber), exchangeBixDian);

            $("#realBuyAccount").val(buyMoney);
        });
        $("#buyNumber").on("keyup", function() {
            var buyUnitPrice = parseFloat($("#buyUnitPrice").val()) || 0,
                buyNumber = parseFloat($("#buyNumber").val()) || 0;
            var buyMoney = 0;
            if (!buyUnitPrice) {
                return false;
            }
            buyMoney = M.floorNumber(M.multiply(buyUnitPrice, buyNumber), exchangeBixDian);
            $("#realBuyAccount").val(buyMoney);
        });
        $("#realBuyAccount").on("keyup", function() {
            var buyUnitPrice = parseFloat($("#buyUnitPrice").val()) || 0,
                buyNumber = parseFloat($("#buyNumber").val()) || 0;
            var buyMoney = parseFloat($(this).val()) || 0;
            if (!buyUnitPrice) {
                return false;
            }
            buyNumber = M.floorNumber(M.divide(buyMoney, buyUnitPrice), numberBixDian);

            $("#buyNumber").val(buyNumber);
        });
        $("#buyPlanPrice").on("keyup", function() {
            var buyPlanPrice = parseFloat($("#buyPlanPrice").val()) || 0,
                buyPlanNumber = parseFloat($("#buyPlanNumber").val()) || 0;
            var buyPlanMoney = 0;
            if (!buyPlanPrice) {
                return false;
            }
            buyPlanMoney = M.floorNumber(M.multiply(buyPlanPrice, buyPlanNumber), exchangeBixDian);

            $("#buyPlanMoney").val(buyPlanMoney);
        });
        $("#buyPlanNumber").on("keyup", function() {
            var buyPlanPrice = parseFloat($("#buyPlanPrice").val()) || 0,
                buyPlanNumber = parseFloat($("#buyPlanNumber").val()) || 0;
            var buyPlanMoney = 0;
            if (!buyPlanPrice || !buyPlanNumber) {
                return false;
            }
            buyPlanMoney = M.floorNumber(M.multiply(buyPlanPrice, buyPlanNumber), exchangeBixDian);
            $("#buyPlanMoney").val(buyPlanMoney);
        });
        $("#buyPlanMoney").on("keyup", function() {
            var buyPlanPrice = parseFloat($("#buyPlanPrice").val()) || 0,
                buyPlanNumber = parseFloat($("#buyPlanNumber").val()) || 0;
            var buyPlanMoney = parseFloat($(this).val()) || 0;
            if (!buyPlanPrice) {
                return false;
            }
            buyPlanNumber = M.floorNumber(M.divide(buyPlanMoney, buyPlanPrice), numberBixDian);

            $("#buyPlanNumber").val(buyPlanNumber);
        });

        $("#sellUnitPrice").on("keyup", function() {
            var sellUnitPrice = parseFloat($("#sellUnitPrice").val()) || 0,
                sellNumber = parseFloat($("#sellNumber").val()) || 0;

            var sellMoney = 0;
            if (!sellUnitPrice) {
                return false;
            }
            sellMoney = M.fixNumber(M.multiply(sellUnitPrice, sellNumber), exchangeBixDian);
            $("#realSellAccount").val(sellMoney);
        });
        $("#sellNumber").on("keyup", function() {
            var sellUnitPrice = parseFloat($("#sellUnitPrice").val()) || 0,
                sellNumber = parseFloat($("#sellNumber").val()) || 0;
            var sellMoney = 0;
            sellMoney = M.floorNumber(M.multiply(sellUnitPrice, sellNumber), exchangeBixDian);
            $("#realSellAccount").val(sellMoney);
        });
        $("#realSellAccount").on("keyup", function() {
            var sellUnitPrice = parseFloat($("#sellUnitPrice").val()) || 0,
                sellNumber = parseFloat($("#sellNumber").val()) || 0;
            var sellMoney = parseFloat($(this).val()) || 0;

            sellNumber = M.floorNumber(M.divide(sellMoney, sellUnitPrice), numberBixDian);
            $("#sellNumber").val(sellNumber);
        });
        $("#sellPlanPrice").on("keyup", function() {
            var sellPlanPrice = parseFloat($("#sellPlanPrice").val()) || 0,
                sellPlanNumber = parseFloat($("#sellPlanNumber").val()) || 0;

            var sellPlanMoney = 0;
            if (!sellPlanPrice) {
                return false;
            }
            sellPlanMoney = M.floorNumber(M.multiply(sellPlanPrice, sellPlanNumber), exchangeBixDian);

            $("#sellPlanMoney").val(sellPlanMoney);
        });

        $("#sellPlanNumber").on("keyup", function() {
            var sellPlanPrice = parseFloat($("#sellPlanPrice").val()) || 0,
                sellPlanNumber = parseFloat($("#sellPlanNumber").val()) || 0;
            var sellMoney = 0;
            sellMoney = M.floorNumber(M.multiply(sellPlanPrice, sellPlanNumber), exchangeBixDian);
            $("#sellPlanMoney").val(sellMoney);
        });
        $("#sellPlanMoney").on("keyup", function() {
            var sellPlanPrice = parseFloat($("#sellPlanPrice").val()) || 0,
                sellPlanNumber = parseFloat($("#sellPlanNumber").val()) || 0;
            var sellMoney = parseFloat($(this).val()) || 0;

            sellPlanNumber = M.floorNumber(M.divide(sellMoney, sellPlanPrice), numberBixDian);
            if (isNaN(sellPlanNumber)) return;
            $("#sellPlanNumber").val(sellPlanNumber);
        });

        $("#canUseMoney").on("click", function(){
            var money = $(this).text();
            if(money){
                if(buyStrategy==0){
                    $("#realBuyAccount").val(money);
                    $("#realBuyAccount").trigger('keyup');
                }else if(buyStrategy==1){
                    $("#buyPlanMoney").val(money);
                    $("#buyPlanMoney").trigger('keyup');
                }
            }
        });
        $("#canUseCoin").on("click", function(){
            var coin = $(this).text();
            if(coin){
                if(sellStrategy==0){
                    $("#sellNumber").val(coin);
                    $("#sellNumber").trigger('keyup');
                }else if(sellStrategy==1){
                    $("#sellPlanNumber").val(coin);
                    $("#sellPlanNumber").trigger('keyup');
                }else{
                    $("#sellNumber").val(coin);
                    $("#sellNumber").trigger('keyup');
                }
            }
        });
        var buyBtnText = function(text) {
            return bitbank.L(text);
        };
        var sellBtnText = function(text) {
            return bitbank.L(text);
        };
        $("#bkEntrustTab").slide({
            titCell: ".trade-title-right .btn",
            mainCell: ".trade-content",
            titOnClassName: "active",
            trigger: "click"
        });
        $(".trade-history").slide({
            titCell: ".trade-title-right span",
            mainCell: ".trade-content",
            titOnClassName: "active",
            trigger: "click"
        });
        $this.ordersTransposalChange();
        $("#ordersTransposal").on('click', function(event) {
            event.preventDefault();
            /* Act on the event */
            var self = $(this);
            var ordersTIP_status = 1;
            var sellItem = self.parent().find('.sell-item');
            var now = sellItem.css('left');
            if (now == "0px") {
                ordersTIP_status = 0;
            } else {
                $ordersTIP_status = 1;
            }
            if (window.localStorage) {
                localStorage["ordersTIP"] = ordersTIP_status;
                $.cookie('ordersTIP', null);
            } else {
                $.cookie('ordersTIP', ordersTIP_status, { expires: 7, path: '/', domain: DOMAIN_BASE });
            }
            $this.ordersTransposalChange();
        });

        $("#bkEntrustTab").on('click', '.trade-title h4', function(event) {
            event.preventDefault();

            var $this = $(this),
                $parent = $("#bkEntrustTab")
            var status = $this.find('i').attr('class');
            bkEntrustTabClose = false;
            if (status == "open") {
                $this.css('padding-bottom', '15px');
                $parent.find('.trade-content').hide();
                $parent.css('padding-bottom', '0');
                $parent.find('.trade-title').css('border-bottom', '0');
                $parent.find('.trade-title-right').hide();
                $(this).find('i').removeClass('open');
            } else {
                $this.css('padding-bottom', '10px');
                $parent.find('.trade-content').show();
                $parent.css('padding-bottom', '15px');
                $parent.find('.trade-title').css('border-bottom', '1px solid #E4E4E4');
                $parent.find('.trade-title-right').show();
                $(this).find('i').addClass('open');
            };

        });
        legalTender = $.trim($(".price-type").find('h5 span').text()).substr(0,3);
        // var htmlPriceType = "<li>BTC</li><li>USD</li><li>CNY</li><li>EUR</li><li>GBP</li><li>AUD</li>";
        // if(legalTender=="USD"){
        //     htmlPriceType = "<li>USD</li><li>BTC</li><li>CNY</li><li>EUR</li><li>GBP</li><li>AUD</li>"
        // }
        //$("#basePriceType").html(htmlPriceType);
        $(".price-type").on('click', 'ul li', function(event) {
            event.preventDefault();
            /* Act on the event */
            legalTender = $(this).text().substr(0,3);
            $this.getTransAjax();
        });
        var MoneyType = $('#hidden_moneytype').val();
        var tagTab = $("#tagTab").find('span');
        for (var tagi = 0; tagi < tagTab.length; tagi ++ ) {
            var tagTabThis = tagTab.eq(tagi);
            if ( tagTabThis.text() == MoneyType ) {
                tagTabThis.addClass('active').siblings().removeClass('active');
            };
        };
        $("#tagTab").on('click', 'span', function(event) {
            event.preventDefault();
            /* Act on the event */
            $(this).addClass('active').siblings().removeClass('active');
            markets.getMarket();
        });
    };
    trans.pageRecordInit = function(market) {
        var $this = this;
        $this.getMarket(market, function() {
            $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, isPage: true, status: 3, entrustType: 1 });
            $this.getEntrustRecord({ listDiv: "#readyRecord", lastTimeRecord: lastTime, isPage: true, status: 0, entrustType: 2 });
            $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, status: 2, entrustType: 1 });
            $this.getEntrustRecord({ listDiv: "#oldHistoryRecord", lastTimeRecord: lastTime, isPage: true, status: 2, entrustType: 1 });
        });
        $(".bk-entrust").on("click", ".cancelEntrust", function() {
            $this.doCancelEntrust(
                $(this).data("id"),
                $(this).data("type"),
                $(this).data("plantype"),
                function() {
                    setTimeout(function() {
                        $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 3, entrustType: 1, opEntrust: true });
                        $this.getEntrustRecord({ listDiv: "#readyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 0, entrustType: 2, opEntrust: true });
                        $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                        $this.getEntrustRecord({ listDiv: "#oldHistoryRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                    }, 2000)
                }
            );
        });
        $(".bk-entrust").on("click", ".detailEntrust", function() {
            $this.getEntrustDetail(
                $(this).data("id"),
                $(this).data("types"),
                $(this).data("numbers"),
                function() {
                    JuaBox.info($("#tradeList").html(), { title: bitbank.L("成交明细"), width: 600, btnNum: 1 });
                }
            );
        });
        $(".bk-entrust").on("click", "#batchCancel", function() {
            $this.batchCancelEntrust("ALL", function() {
                setTimeout(function() {
                    $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 3, entrustType: 1, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#readyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 0, entrustType: 2, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#oldHistoryRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                }, 2000)
            });
        });
        $(".bk-entrust").on("click", "#batchCancelPlan", function() {
            $this.batchCancelPlanEntrust(function() {
                setTimeout(function() {
                    $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 3, entrustType: 1, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#readyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 0, entrustType: 2, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                    $this.getEntrustRecord({ listDiv: "#oldHistoryRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, status: 2, entrustType: 1, opEntrust: true });
                }, 2000)
            });
        });
        $("#entrustTypeDrop .dropdown-menu").on("click", "li", function() {
            $("#entrustTypeDrop .text-g").text($(this).text());
            $("#entrustType").val($(this).data("value"));
        });
        $("#transTypeDrop .dropdown-menu").on("click", "li", function() {
            $("#transTypeDrop .text-g").text($(this).text());
            $("#transType").val($(this).data("value"));
        });
        $("#transStatusDrop .dropdown-menu").on("click", "li", function() {
            $("#transStatusDrop .text-g").text($(this).text());
            $("#transStatus").val($(this).data("value"));
        });
        $("#transRangeDrop .dropdown-menu").on("click", "li", function() {
            $("#transRangeDrop .text-g").text($(this).text());
            $("#transRange").val($(this).data("value"));
        });
        $("#reSetBtn").on("click", function() {
            $("#transTypeDrop .text-g, #transStatusDrop .text-g").text(bitbank.L("不限"));
            $("#entrustTypeDrop .text-g").text(bitbank.L("限价委托"));
            $("#transRangeDrop .text-g").text(bitbank.L("最近委托"));
            $("#transRange").val("0");
            $("#transType").val("-1");
            $("#transStatus").val("0");
            type = -1;
            status = 0;
            entrustType = 1;
            timeFrom = 0;
            timeTo = 0;
            numberFrom = 0;
            numberTo = 0;
            priceFrom = 0;
            priceTo = 0;
            dateTo = 0;
            pageSize = 10;
            pageIndex = 1;
            lockRepeatEntrust = false;
            $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, opEntrust: true });
        });
        $("#doSearchBtn").on("click", function() {
            type = $("#transType").val() == null ? type : $("#transType").val();
            entrustType = $("#entrustType").val() == null ? entrustType : $("#entrustType").val();
            status = $("#transStatus").val() == null ? status : $("#transStatus").val();
            timeFrom = $("#startDate").val() == "" ? timeFrom : Date.parse($("#startDate").val());
            timeTo = $("#endDate").val() == "" ? timeTo : Date.parse($("#endDate").val());
            numberFrom = $("#startNumber").val() == "" ? numberFrom : $this.formatNumberUse($("#startNumber").val());
            numberTo = $("#endNumber").val() == "" ? numberTo : $this.formatNumberUse($("#endNumber").val());
            priceFrom = $("#startPrice").val() == "" ? priceFrom : $this.formatMoneyUse($("#startPrice").val());
            priceTo = $("#endPrice").val() == "" ? priceTo : $this.formatMoneyUse($("#endPrice").val());
            dateTo = $("#transRange").val() == null ? dateTo : $("#transRange").val();
            pageSize = 10;
            pageIndex = 1;
            lockRepeatEntrust = false;
            $this.getEntrustRecord({ listDiv: "#historyRecord", lastTimeRecord: lastTime, isPage: true, pageIndex: pageIndex, opEntrust: true });
        });

    };
    trans.depthChart = function(typese) {
        var buyData = [],
            sellData = [];
        var url = DOMAIN_TRANS + '/Line/getMarketDepth-' + typese + '?jsoncallback=?';
        $.getJSON(url, function(result) {
            // var result = ([{"listDown":[[2.099995,0.199,0.417899005],[2.0999926,0.764,1.6043948240],[2.0999881,0.915,1.9214930271],[2.0999821,1.221,2.5640875497],[2.0999814,1.796,3.7715768547],[2.0999534,1.983,4.1642681405],[2.0999524,2.255,4.7354551933]],"listUp":[[2.0999986,0.086,0.1805998796],[2.0999988,1.086,2.2805986796],[2.1,1.1000,20157.8999986796],[2.10001,1.2000,20157.8999986796]]}]);
            buyData = result[0].listDown;
            sellData = result[0].listUp;
            $("#graphbox_depth").highcharts({
                title: {
                    text: ""
                },
                credits: {
                    enabled: false
                },
                chart: {
                    type: "area",
                    height: 220
                },
                legend: {
                    enabled: false,
                    verticalAlign: "top",
                    backgroundColor: "rgba(0,0,0,.25)",
                    floating: true
                },
                xAxis: {
                    title: {
                        text: ''
                    },
                    gridLineWidth: 0
                },
                yAxis: {
                    title: {
                        text: ''
                    },
                    gridLineWidth: 1
                },
                tooltip: {
                    crosshairs: [true, true],
                    useHTML: true,
                    formatter: function() {
                        var thisZ = "";
                        for(var i in buyData){
                            if(this.x == buyData[i][0]){
                                thisZ =  buyData[i][2];
                                break;
                            }
                        }
                        if(thisZ == ""){
                            for(var m in sellData){
                                if(this.x == sellData[m][0]){
                                    thisZ =  sellData[m][2];
                                    break;
                                }
                            }
                        }
                        return "<div class='tooltip_box' style='border-left-color:"+this.series.name+"'>"+
                        "<h5>"+
                       // bitbank.L("价格") + ": " +
                        this.x.toFixed(exchangeBixDian) +" "+ market.split("_")[1].toUpperCase()+" </h5>"+
                        "<div class='clearfix'><p>" + bitbank.L("数量") + ":<br/>" + this.y.toFixed(numberBixDian) + " "+market.split("_")[0].toUpperCase()+"</p>"+
                        "<p>"+ bitbank.L("总额") + ":<br/>" + thisZ.toFixed(exchangeBixDian) +" "+ market.split("_")[1].toUpperCase()+" </p>"+
                        "</div></div>";
                    },
                    backgroundColor: "rgba(0,0,0,0.6)",
                    shadow:null,
                    borderColor: null,
                    borderWidth: 0,
                    borderRadius: 0,
                    style: {
                        color: "#fff"
                    }
                },
                series: [{
                    animation: false,
                    lineColor: "#2BB38A",
                    lineWidth: 1,
                    marker: {
                        symbol: 'circle',
                        enabled: false,
                        lineColor:"#FFF",
                    },
                    name: "#2BB38A",//bitbank.L("买单")
                    data: buyData,
                    color: "#82d8be"
                }, {
                    animation: false,
                    lineColor: "#E34B51",
                    lineWidth: 1,
                    marker: {
                        symbol: 'circle',
                        enabled: false,
                        lineColor:"#FFF",
                    },
                    name: "#E34B51",//bitbank.L("卖单")
                    data: sellData,
                    color: "#ea9a9e"
                }]
            });
        });

    }
    trans.ordersTransposalChange = function() {
        var $this = this;
        var self = $("#ordersTransposal");
        var ie = window.localStorage ? localStorage["ordersTIP"] : $.cookie("ordersTIP") ;
        var ordersTIP_status = ie || 0;
        var sellItem = self.parent().find('.sell-item');
        var buyItem = self.parent().find('.buy-item');

        if (ordersTIP_status == 0) {
            sellItem.css('left', '50%');
            buyItem.css('left', '0px');
        } else {
            sellItem.css('left', '0px');
            buyItem.css('left', '50%');
        };

    }
    trans.updateSummary = function(type) {
        var summaryDefault = $("#summaryDefault");
        $.getJSON(DOMAIN_TRANS + "/entrust/transactionSummary?callback=?&currency=" + type, function(result) {
            var datar = result.datas;
            summaryDefault.find('.netValue').text(0);
            summaryDefault.find('.costPrice').text(0);
            summaryDefault.find('.marketValue').text(0);

            var summaryR = summaryDefault.find('.buy-sum');
            if (datar.isProfit == 0) {
                summaryR = summaryDefault.find('.sell-sum');
                summaryR.find('.netValue').text(datar.netValue);
                summaryR.find('.costPrice').text(datar.costPrice);
            } else {
                summaryR.find('.netValue').text(datar.netValue);
                summaryR.find('.costPrice').text(datar.costPrice);
            }

            summaryDefault.find('.lastPrice').text(datar.lastPrice);
            summaryR.find('.marketValue').text(datar.marketValue);
            if (datar.profitOrLoss > 0) {
                $("#profitOrLoss").removeClass('red').addClass('green');
            } else if (datar.profitOrLoss < 0) {
                $("#profitOrLoss").removeClass('green').addClass('red');
            }
            $("#profitOrLoss b").text(datar.profitOrLoss)
        })
    }
    trans.checkNumber = function(_this, unit) {
        var value = _this.val();
        if (value != "") {
            if ($.isNumeric(value)) {
                var valueStr = value + "";
                if (valueStr.indexOf(".") != -1) {
                    var newStr,
                        intStr = valueStr.split(".")[0] + "",
                        floatStr = valueStr.split(".")[1] + "";
                    if (floatStr.split("").length > unit) {
                        newStr = intStr + "." + floatStr.substr(0, unit);
                        _this.val(newStr);
                    }
                }
            } else {
                _this.val("");
            }
        }
    };
    trans.upP2Pstatus = function() {
        $("#leverSwitchBuy, #leverSwitchSell").attr("disabled", user.isLogin() ? false : true);
        asset.getLoanAsset(function() {
            $("#canLoanBTC").html(asset.btc.canLoanIn);
            $("#canLoanCNY").html(asset.cny.canLoanIn);
            $("#canLoanLTC").html(asset.ltc.canLoanIn);
            $("#canLoanETH").html(asset.eth.canLoanIn);
            $("#canLoanETC").html(asset.etc.canLoanIn);

            canLoanInMoney = asset[exchangeBi.toLowerCase()].canLoanIn;
            canLoanInCoin = asset[numberBi.toLowerCase()].canLoanIn;
            if (canLoanInMoney < loanProtect[exchangeBi]) {
                $("#leverSwitchBuy").attr("disabled", true);
            }
            if (canLoanInCoin < loanProtect[numberBi]) {
                $("#leverSwitchSell").attr("disabled", true);
            }
        });
    };
    var timeout;
    trans.upAmount = function(buyorsell) {
        
        var $this = this;

        var canUseMoney = asset[exchangeBi.toLowerCase()]?parseFloat(asset[exchangeBi.toLowerCase()]["usable"]):100000000;
        var canUseCoin = asset[numberBi.toLowerCase()]?parseFloat(asset[numberBi.toLowerCase()]["usable"]):100000000;
        canUseMoney = M.floorNumber(canUseMoney, exchangeBixDian);
        canUseCoin = M.floorNumber(canUseCoin, numberBixDian);
        if (buyorsell == "buy") {
            var buyUnitPrice = parseFloat($("#buyUnitPrice").val()) || 0,
            buyNumber = parseFloat($("#buyNumber").val()) || 0;
            var buyMoney = M.multiply(buyUnitPrice, buyNumber);
            if (buyMoney > canUseMoney) {
                buyMoney = canUseMoney;
                buyNumber = M.floorNumber(M.divide(buyMoney, buyUnitPrice), numberBixDian);
                $("#buyNumber").val(buyNumber);
            }
            buyMoney = M.floorNumber(buyMoney, exchangeBixDian);
            $("#realBuyAccount").val(buyMoney);
        } else {
            var sellUnitPrice = parseFloat($("#sellUnitPrice").val()) || 0,
            sellNumber = parseFloat($("#sellNumber").val()) || 0;
            if (sellNumber > canUseCoin) {
                sellNumber = canUseCoin;
                $("#sellNumber").val(sellNumber);
            }
            sellMoney = M.floorNumber(M.multiply(sellUnitPrice, sellNumber), amountDecialCur);
            $("#realSellAccount").val(sellMoney);
        }

    };
    trans.upAccount = function(type) {
        var $this = this;
        var buyUnitPrice = parseFloat($("#buyUnitPrice").val()),
            buyNumber = parseFloat($("#buyNumber").val()),
            sellUnitPrice = parseFloat($("#sellUnitPrice").val()),
            sellNumber = parseFloat($("#sellNumber").val()),
            buyMinPrice = parseFloat($("#buyMinPrice").val());
        sellMaxPrice = parseFloat($("#sellMaxPrice").val());
        var canUseMoney = asset[exchangeBi.toLowerCase()]?parseFloat(asset[exchangeBi.toLowerCase()]["usable"]):100000000;
        var canUseLeverMoney = leverFlagBuy ? (canUseMoney + canLoanInMoney) : canUseMoney;
        var canUseCoin = asset[numberBi.toLowerCase()]?parseFloat(asset[numberBi.toLowerCase()]["usable"]):100000000;
        var canUseLeverCoin = leverFlagSell ? (canUseCoin + canLoanInCoin) : canUseCoin;
        canUseMoney = M.fixFloat(canUseMoney, exchangeBixDian);
        canUseLeverMoney = M.fixFloat(canUseLeverMoney, exchangeBixDian);
        canUseCoin = M.fixFloat(canUseCoin, numberBixDian);
        canUseLeverCoin = M.fixFloat(canUseLeverCoin, numberBixDian);

        if (type == "buy") {
            if (buyUnitPrice > 0 && buyNumber > 0) {
                var countBuyMoney = M.floorNumber(buyUnitPrice * buyNumber, amountDecialCur);
                var canBuyCoin = M.floorNumber(canUseLeverMoney / buyUnitPrice, numberBixDian);

                if (countBuyMoney > canUseLeverMoney) {
                    var leverMoney = M.floorNumber((canUseLeverMoney > canUseMoney ? canUseLeverMoney - canUseMoney : 0), exchangeBixDian);
                    if (leverMoney > 0 && leverMoney < loanProtect[exchangeBi] && canLoanInMoney >= loanProtect[exchangeBi]) {
                        leverMoney = loanProtect[exchangeBi];
                    }
                    if (canLoanInMoney < loanProtect[exchangeBi]) {
                        leverMoney = 0;
                    }
                    $("#buyNumber").val(canBuyCoin);
                    $("#realBuyAccount").val(M.floorNumber(canUseLeverMoney, amountDecialCur));
                    $("#leverAccountBuy").text(leverMoney);
                    $("#buySlider .sliderPercent").html("100%");
                    return false;
                } else {
                    var leverMoney = M.floorNumber((countBuyMoney > canUseMoney ? countBuyMoney - canUseMoney : 0), exchangeBixDian);
                    if (leverMoney > 0 && leverMoney < loanProtect[exchangeBi]) {
                        leverMoney = loanProtect[exchangeBi];
                    }
                    if (canLoanInMoney < loanProtect[exchangeBi]) {
                        leverMoney = 0;
                    }
                    var buysliderPercent = (countBuyMoney / canUseLeverMoney * 100).toFixed(2);
                    $("#realBuyAccount").val(countBuyMoney);
                    $("#leverAccountBuy").text(leverMoney);
                    $("#buySlider .sliderPercent").html(buysliderPercent + "%");
                    return false;
                }
            } else {
                $("#realBuyAccount").val("0.00");
                $("#leverAccountBuy").text("0.00");
                $("#buySlider .sliderPercent").html("0.00%");
            }
        } else {
            if (sellUnitPrice > 0 && sellNumber > 0) {
                var countSellMoney = M.floorNumber(sellUnitPrice * sellNumber, amountDecialCur);
                var canSellMoney = M.floorNumber(sellUnitPrice * canUseLeverCoin, amountDecialCur);
                if (sellNumber > canUseLeverCoin) {
                    var leverCoin = M.floorNumber((canUseLeverCoin > canUseCoin ? canUseLeverCoin - canUseCoin : 0), numberBixDian);
                    if (leverCoin > 0 && leverCoin < loanProtect[numberBi] && canLoanInCoin >= loanProtect[numberBi]) {
                        leverCoin = loanProtect[numberBi];
                    }
                    if (canLoanInCoin < loanProtect[numberBi]) {
                        leverCoin = 0;
                    }
                    $("#leverAccountSell").text(leverCoin);
                    $("#sellNumber").val(M.floorNumber(canUseLeverCoin, numberBixDian));
                    $("#realSellAccount").val(canSellMoney.toFixed(amountDecialCur));
                    $("#sellSlider .sliderPercent").html("100%");
                    return false;
                } else {
                    var leverCoin = M.floorNumber((sellNumber > canUseCoin ? sellNumber - canUseCoin : 0), numberBixDian);
                    if (leverCoin > 0 && leverCoin < loanProtect[numberBi]) {
                        leverCoin = loanProtect[numberBi];
                    }
                    if (canLoanInCoin < loanProtect[numberBi]) {
                        leverCoin = 0;
                    }
                    $("#leverAccountSell").text(leverCoin);
                    var sellsliderPercent = (sellNumber / canUseLeverCoin * 100).toFixed(2);
                    $("#realSellAccount").val(countSellMoney.toFixed(amountDecialCur));
                    $("#sellSlider .sliderPercent").html(sellsliderPercent + "%");
                    return false;
                }
            } else {
                $("#realSellAccount").val("0.00");
                $("#leverAccountSell").text("0.00");
                $("#sellSlider .sliderPercent").html("0.00%");
            }
        }
    };
    trans.getMarket = function(type, callback) {
        var type = type || "etc_btc";
        var json = null;
        if (typeof marketData === "undefined") {
            $.getJSON(DOMAIN_TRANS + "/getMarket?callback=?&type=" + type, function(result) {
                if (result.isSuc && result.datas.length > 0) {
                    json = result.datas[0];
                    market = json.market;
                    numberBi = json.numberBi;
                    numberBiEn = json.numberBiEn;
                    numberBiNote = json.numberBiNote;
                    numberBixNormal = parseFloat(json.numberBixNormal);
                    numberBixShow = parseFloat(json.numberBixShow);
                    numberBixDian = parseInt(json.numberBixDian);
                    exchangeBi = json.exchangeBi;
                    exchangeBiEn = json.exchangeBiEn;
                    exchangeBiNote = json.exchangeBiNote;
                    exchangeBixNormal = parseFloat(json.exchangeBixNormal);
                    exchangeBixShow = parseFloat(json.exchangeBixShow);
                    exchangeBixDian = parseInt(json.exchangeBixDian);
                    entrustUrlBase = json.entrustUrlBase;
                    amountDecialCur = parseInt(json.exchangeBixDian);
                    marketProtectCur = 1.05;
                    if ($.isFunction(callback)) {
                        callback();
                    }
                }
            });
        } else {
            json = marketData[0];
            market = json.market;
            numberBi = json.numberBi;
            numberBiEn = json.numberBiEn;
            numberBiNote = json.numberBiNote;
            numberBixNormal = parseFloat(json.numberBixNormal);
            numberBixShow = parseFloat(json.numberBixShow);
            numberBixDian = parseInt(json.numberBixDian);
            exchangeBi = json.exchangeBi;
            exchangeBiEn = json.exchangeBiEn;
            exchangeBiNote = json.exchangeBiNote;
            exchangeBixNormal = parseFloat(json.exchangeBixNormal);
            exchangeBixShow = parseFloat(json.exchangeBixShow);
            exchangeBixDian = parseInt(json.exchangeBixDian);
            entrustUrlBase = json.entrustUrlBase;
            amountDecialCur = parseInt(json.exchangeBixDian);
            marketProtectCur = 1.05;
            if ($.isFunction(callback)) {
                callback();
            }
        }
    };
    trans.getTrans = function(length, depth) {
        var $this = this;
        if (market.indexOf("btq") != -1) {
            $this.getTransAjax();
            setInterval(function(t) {
                $this.getTransAjax();
            }, 2000);
        } else {
            if (initDishFlag) {
                $this.getTransAjax();
                initDishFlag = false;
            }
            var channel = "";
            var cdepth = marketDepth.toString();
            cdepth = cdepth.replace('.', '');
            channel = "dish_depth_" + cdepth + "_" + market;
            oldChannel = channel;
            // var readyFun = setInterval(function() {
            //     if (webSocket) {
            //         webSocket.init(function() {
            //             webSocket.sendMessage("{'event':'addChannel','channel':'" + channel + "','isZip':'true'}");
            //         });
            //         clearInterval(readyFun);
            //     }
            // }, 50);
            setInterval(function(t) {
                if (ajaxRun == false) {
                    return;
                }
                //if (!webSocket || !webSocket.socket || (webSocket.socket && webSocket.socket.readyState != WebSocket.OPEN)) {
                    $this.getTransAjax();
                //}
            }, 2000);
        }
    };
    trans.getTransAjax = function(length, depth) {
        var $this = this;
        marketLength = length || marketLength;
        marketDepth = depth || marketDepth;
        var jsonUrl;
        if (marketLength > 5) {
            jsonUrl = DOMAIN_TRANS + "" + entrustUrlBase + "Line/GetTrans-" + market + "?";
        } else {
            jsonUrl = DOMAIN_TRANS + "" + entrustUrlBase + "dish/data-" + market + "?";
        }
        jsonUrl += "lastTime=" + lastTimeTrans + "&";
        jsonUrl += "length=" + marketLength + "&";
        jsonUrl += "depth=" + marketDepth + "&";
        jsonUrl += "jsoncallback=?";
        $.getJSON(jsonUrl, function(result) {
            // if (new Number(result[0].exchangeRate[legalTender]) <= 0) {
            //     legalTender = "BTC";
            // }
            var $basePriceText = $('#basePriceType').find("li")
            for(var k=0;k<$basePriceText.length;k++){
                if($basePriceText[k].innerHTML.substr(0,3) == legalTender){
                    $(".price-type").find('h5 span').text($basePriceText[k].innerHTML); 
                    break;
                }
            }
            //$(".price-type").find('ul').hide();
            $this.setDish(result);
        });
    };
    trans.getTransWebsocket = function(length, depth) {
        var $this = this;
        marketLength = length || marketLength;
        marketDepth = depth || marketDepth;

        var channel = "";

        if (marketLength > 5) {
            channel = "dish_length_" + marketLength + "_" + market;
        } else {
            var cdepth = marketDepth.toString();
            cdepth = cdepth.replace('.', '');
            channel = "dish_depth_" + cdepth + "_" + market;
        }
        if (webSocket.socket && webSocket.socket.readyState == WebSocket.OPEN) {
            if (oldChannel != channel && oldChannel != null) {
                webSocket.sendMessage('{"event":"removeChannel","channel":"' + oldChannel + '"}');
                var message = '{"event":"addChannel","channel":"' + channel + '","isZip":"true"}';
                oldChannel = channel;
                webSocket.sendMessage(message);
            } else if (oldChannel != channel && oldChannel == null) {
                var message = '{"event":"addChannel","channel":"' + channel + '","isZip":"true"}';
                oldChannel = channel;
                webSocket.sendMessage(message);
            }
        }
    };
    trans.setDish = function(result) {
        var $this = this;
        var exRate = 1;
        var fabDian = exchangeBixDian;
        if (legalTender != "BTC") {
            fabDian = 2;
        }
        var marknow = market.split("_")[1].toUpperCase() ;
        if(legalTender == marknow){
            exRate = 1;
        }else{
            if(marknow == "BTC"){
                exRate = new Number(result[0].exchangeRateBTC[legalTender]);
            }else if(marknow == "USDT"){
                exRate = new Number(result[0].exchangeRateUSD[legalTender]);
            }
        }
        //if not gbc and data.length < 15 , break
        // if( market && market != "gbc_usdt" && (result[0].listUp.length < 15 || result[0].listDown.length < 15)){
        //     return;
        // }
        //console.log(result)
        if (result[0].listUp.length > 0) {
            
            if (result[0].listUp.length > 15) {
                result[0].listUp = result[0].listUp.slice(0, 15);
            }
            var maxSellNumber = 0;
            for (var i = 0; i < result[0].listUp.length; i++) {
                if (result[0].listUp[i][1] > maxSellNumber) {
                    maxSellNumber = result[0].listUp[i][1];
                }
            };
            for (var i = 0; i < result[0].listUp.length; i++) {
                marketPrice[2][i] = [];
                marketPrice[2][i][0] = result[0].listUp[i][0] = M.fixNumber(new Number(result[0].listUp[i][0]), exchangeBixDian);
                marketPrice[2][i][1] = result[0].listUp[i][1] = M.fixNumber(new Number(result[0].listUp[i][1]), numberBixDian);
                marketPrice[2][i][2] = result[0].listUp[i][2] = M.fixNumber(new Number(result[0].listUp[i][2]), numberBixDian);
                result[0].listUp[i][2] = (result[0].listUp[i][1] / maxSellNumber * 100).toFixed(2);
                result[0].listUp[i][3] = (result[0].listUp[i][1] * result[0].listUp[i][0]).toFixed(exchangeBixDian);
            };
            if ($("#sellMarket").length > 0) {
                $("#sellMarket").html(tmpl("tmpl-sellMarket", result[0].listUp));
            }
            if ($("#buyUnitPrice").val() == "") {
                $("#buyUnitPrice").val(marketPrice[2][0][0]);
            };
        } else {
            $("#sellMarket").html(tmpl("tmpl-sellMarket", []));
        }
        if (result[0].listDown.length > 0) {
            if (result[0].listDown.length > 15) {
                result[0].listDown = result[0].listDown.slice(0, 15);
            }
            var maxBuyNumber = 0;
            for (var i = 0; i < result[0].listDown.length; i++) {
                if (result[0].listDown[i][1] > maxBuyNumber) {
                    maxBuyNumber = result[0].listDown[i][1];
                }
            };
            for (var i = 0; i < result[0].listDown.length; i++) {
                marketPrice[1][i] = [];
                marketPrice[1][i][0] = result[0].listDown[i][0] = M.fixNumber(new Number(result[0].listDown[i][0]), exchangeBixDian);
                marketPrice[1][i][1] = result[0].listDown[i][1] = M.fixNumber(new Number(result[0].listDown[i][1]), numberBixDian);
                marketPrice[1][i][2] = result[0].listDown[i][2] = M.fixNumber(new Number(result[0].listDown[i][2]), numberBixDian);
                result[0].listDown[i][2] = (result[0].listDown[i][1] / maxBuyNumber * 100).toFixed(2);
                result[0].listDown[i][3] = (result[0].listDown[i][1] * result[0].listDown[i][0]).toFixed(exchangeBixDian);
            };
            if ($("#buyMarket").length > 0) {
                $("#buyMarket").html(tmpl("tmpl-buyMarket", result[0].listDown));
            }
            if ($("#sellUnitPrice").val() == "") {
                $("#sellUnitPrice").val(marketPrice[1][0][0]);
            };
        } else {
            $("#buyMarket").html(tmpl("tmpl-buyMarket", []));
        }
        var currentPrice = M.fixNumber(new Number(result[0].currentPrice), exchangeBixDian);
        //console.log(currentPrice+"*"+exRate);
        if (marketPrice[0]) {
            marketPrice[3] = marketPrice[0] > currentPrice ? "down" : "up";
            marketPrice[3] = marketPrice[0] == currentPrice ? "equal" : marketPrice[3];
        } else {
            marketPrice[3] = "equal";
        }

        maxPrice = M.fixNumber(new Number(result[0].high), exchangeBixDian);
        minPrice = M.fixNumber(new Number(result[0].low), exchangeBixDian);
        dayVolume = M.fixNumber(new Number(result[0].dayNumber), numberBixDian);
        if (marketPrice[3] != "equal") {
            var marketClass = marketPrice[3] == "up" ? "text-primary" : "text-third";
        }
        
        $("#curPrice").html(M.fixNumber(currentPrice * exRate, fabDian));
        marketPrice[0] = currentPrice;
        $("#maxPrice").html(M.fixNumber(maxPrice * exRate, fabDian));
        $("#minPrice").html(M.fixNumber(minPrice * exRate, fabDian));
        var changeOfDayDom = $(".symbol-list").children(".active").find('.symbol-range');
        $("#changeOfDay").html(changeOfDayDom.text()).css({ color: changeOfDayDom.css('background') });
        if (parseFloat(dayVolume) < 10000) {
            $("#dayVolume").html(dayVolume + " " + numberBiEn);
            $("#dayVolumeInfo_a").html(dayVolume + " " + numberBiEn);

        } else {
            var dayVolumeStr;
            if (LANG == 'cn') {
                dayVolumeStr = M.fixNumber(parseFloat(dayVolume) / 10000, 2) + "万";
            } else {
                if (parseFloat(dayVolume) >= 10000000) {
                    dayVolumeStr = M.fixNumber(parseFloat(dayVolume) / 1000000, 2) + "M";
                }else{
                    dayVolumeStr = M.fixNumber(parseFloat(dayVolume) / 1000, 2) + "K";
                }
                
                // dayVolumeStr = M.divNumber(parseFloat(dayVolume), 2);
            }
            $("#dayVolume").html(dayVolumeStr + " " + numberBiEn);
            $("#dayVolumeInfo_a").html(dayVolumeStr + " " + numberBiEn);
            $("#dayVolume").attr("title", dayVolume + " " + numberBiEn);
        }
        var totalBtcExRate = M.fixNumber((new Number(result[0].totalBtc) * exRate), 2)
        // var totalBtcExRate = M.fixNumber((new Number(result[0].totalBtc) * exRate), fabDian)
        if (parseFloat(totalBtcExRate) < 10000) {
            $("#dayVolumeInfo_b").html(totalBtcExRate + " " + legalTender)
        } else {
            if (LANG == 'cn') {
                totalBtcExRateStr = M.fixNumber(parseFloat(totalBtcExRate) / 10000, 2) + "万";
            } else {
                if (parseFloat(dayVolume) >= 10000000) {
                    totalBtcExRateStr = M.fixNumber(parseFloat(totalBtcExRate) / 1000000, 2) + "M";
                }else{
                    totalBtcExRateStr = M.fixNumber(parseFloat(totalBtcExRate) / 1000, 2) + "K";
                }
                // totalBtcExRateStr = M.divNumber(parseFloat(totalBtcExRate), 2);
            }
            $("#dayVolumeInfo_b").html(totalBtcExRateStr + " " + legalTender)
        }
        if (marketLength > 5) {
            $(".bk-trans-record").removeClass("col-xs-12").addClass("col-xs-8");
        } else {
            $(".bk-trans-record").removeClass("col-xs-8").addClass("col-xs-12");
        }
    };
    trans.changeDepth = function(depth) {
        var $this = this;
        marketDepth = parseFloat(depth) || 0;
        lastTimeTrans = lastTime;
        $this.getTransWebsocket();
    };
    trans.changeLength = function(length) {
        var $this = this;
        marketLength = parseFloat(length) || 5;
        lastTimeTrans = lastTime;
        $this.getTransWebsocket();
    };
    trans.upAsset = function() {
        if (!user.isLogin()) {
            $('#canUseMoney').parent().html(bitbank.L("登录注册后进行交易")).css("visibility", "visible");
            $('#canUseCoin').parent().html(bitbank.L("登录注册后进行交易")).css("visibility", "visible");
            return false;
        }
        $this = this;
        var canUseMoney = parseFloat(asset[exchangeBi.toLowerCase()]["usable"]);
        var canUseLeverMoney = leverFlagBuy ? (canUseMoney + asset[exchangeBi.toLowerCase()].canLoanIn) : canUseMoney;

        var canUseCoin = parseFloat(asset[numberBi.toLowerCase()]["usable"]);
        var canUseLeverCoin = leverFlagSell ? (canUseCoin + asset[numberBi.toLowerCase()].canLoanIn) : canUseCoin;

        canUseMoney = M.floorNumber(canUseMoney, exchangeBixDian);
        canUseLeverMoney = M.floorNumber(canUseLeverMoney, exchangeBixDian);
        canUseCoin = M.floorNumber(canUseCoin, numberBixDian);
        canUseLeverCoin = M.floorNumber(canUseLeverCoin, numberBixDian);

        var canBuyCoin = 0;
        var canSellMoney = 0;
        try {
            canBuyCoin = canUseLeverMoney / marketPrice[2][0][0];
            canSellMoney = canUseLeverCoin * marketPrice[1][0][0];
            canBuyCoin = M.floorNumber(canBuyCoin, numberBixDian);
            canSellMoney = M.floorNumber(canSellMoney, exchangeBixDian);
        } catch (e) {
            console.log(bitbank.L("获取市场行情信息失败"));
        }
        if ($.isNumeric(canUseMoney)) $("#canUseMoney").html(canUseMoney);
        if ($.isNumeric(canUseCoin)) {
            $("#canUseCoin").html(canUseCoin);
            $("#summaryTotal b").html(canUseCoin);
        }
        if ($.isNumeric(canBuyCoin)) $("#canBuyCoin").html(canBuyCoin);
        if ($.isNumeric(canSellMoney)) $("#canSellMoney").html(canSellMoney);
        $('#canUseMoney').parent().css("visibility", "visible");
        $('#canUseCoin').parent().css("visibility", "visible");
    };
    trans.showPage = function(listDiv, pageIndex, rsCount, pageSize, status, entrustType) {
        var $this = this;
        var $pageDiv = $(listDiv + "_Page");
        if (rsCount < pageSize && pageIndex == 1) {
            $pageDiv.html("");
            return false;
        }
        // var pageCount = rsCount % pageSize == 0 ? parseInt(rsCount / pageSize) : parseInt(rsCount / pageSize) + 1 ;
        $pageDiv.createPage({
            noPage: true,
            pageSize: pageSize,
            rsCount: rsCount,
            current: pageIndex || 1,
            backFn: function(pageNum) {
                $this.getEntrustRecord({ listDiv: listDiv, lastTimeRecord: lastTime, isPage: true, pageIndex: pageNum, status: status, entrustType: entrustType });
            }
        });
    };
    trans.setEntrustRecordSocket = function(result) {
        var $this = this;
        $this.userRecord = result;
        $this.userRecordLastTime = new Date().getTime();
    };
    trans.getEntrustRecord = function(option) {
        var $this = this;
        var userOption = option || {};
        var listDiv = userOption.listDiv;
        var htmlNoLogin = "<tr><td colspan='8' class='botnone'>" + bitbank.L("通用未登录提示") + "</td></tr>";
        var htmlNoRecord = listDiv == "#historyRecord" ? "<tr><td colspan='9' class='botnone'>" + bitbank.L("通用没有任何记录") + "</td></tr>" : "<tr><td colspan='8' class='botnone'>" + bitbank.L("通用没有任何记录") + "</td></tr>";
        if (!user.isLogin()) {
            $(listDiv).html(htmlNoLogin);
            $("#historyRecord").html(htmlNoLogin);
            $("#readyRecord").html(htmlNoLogin);
            $(listDiv + "_Page").html("");
            return false;
        };
        var isPage = userOption.isPage === true ? true : false;
        var type = userOption.type || type,
            status = userOption.status || status,
            entrustType = userOption.entrustType || entrustType,
            timeFrom = userOption.timeFrom || timeFrom,
            timeTo = userOption.timeTo || timeTo,
            dateTo = userOption.dateTo || dateTo,
            pageSize = userOption.pageSize || pageSize,
            pageIndex = userOption.pageIndex || 1,
            lastTimeRecord = userOption.lastTimeRecord || lastTimeRecord,
            opEntrust = userOption.opEntrust || false;
        // if(!isPage){
        //           pageSize = 0 ;
        //       	if(lockEntrust){}
        // }else{
        // pageSize = 30 ;
        // 	if(lockRepeatEntrust){}
        // }
        pageSize = 0;
        lockRepeatEntrust = true;
        var result = $this.userRecord;
        var userRecordLastTime = isNaN($this.userRecordLastTime) ? 0 : $this.userRecordLastTime;
        var nowTime = new Date().getTime();
        if (!result || ajaxRun == true || (opEntrust == true && nowTime - userRecordLastTime >= 2000) || initRecordFlag == true) {
            var jsonUrl = DOMAIN_TRANS + "" + entrustUrlBase + "Record/Get-" + market + "?";
            if(listDiv == "#oldHistoryRecord"){
                jsonUrl = DOMAIN_TRANS + "" + entrustUrlBase + "Record/getHistory-" + market + "?";                
            }
            jsonUrl += "lastTime=" + lastTimeRecord + "&";
            if (type) {
                jsonUrl += "type=" + type + "&";
            }
            if (entrustType) {
                jsonUrl += "entrustType=" + entrustType + "&";
            }
            if (status) {
                jsonUrl += "status=" + status + "&";
            }
            if (timeFrom) {
                jsonUrl += "timeFrom=" + timeFrom + "&";
            }
            if (timeTo) {
                jsonUrl += "timeTo=" + timeTo + "&";
            }
            if (dateTo) {
                jsonUrl += "dateTo=" + dateTo + "&";
            }
            jsonUrl += "pageSize=" + pageSize + "&";
            jsonUrl += "pageIndex=" + pageIndex + "&";
            jsonUrl += "jsoncallback=?";

            $.getJSON(jsonUrl, function(result) {
                var rCount = typeof result[0].count == "undefined" ? 0 : result[0].count;
                var nRecord = typeof result[0].record == "undefined" ? [] : result[0].record;
                var pRecord = typeof result[0].precord == "undefined" ? [] : result[0].precord;
                var hRecord = typeof result[0].record == "undefined" ? [] : result[0].record;
                var rLastTime = typeof result[0].lastTime == "undefined" ? lastTime : result[0].lastTime;
                if (rLastTime == 0) {
                    JuaBox.showWrong(bitbank.L("系统忙碌，请稍候！"));
                    return lockRepeatEntrust = false;
                }
                if (rLastTime == -1) {
                    JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
                    return lockRepeatEntrust = false;
                }
                if (!userOption.lastTimeRecord) {
                    if (lastTimeRecord != rLastTime) {
                        setTimeout(function() {
                            lastTimeRecord = rLastTime;
                        }, 2000);
                    } else {
                        return lockRepeatEntrust = false;
                    }
                }
                if (status == 3) {
                    var pRecordWaters = 0;
                    var pRecordNew = []
                    for (var i = 0; i < pRecord.length; i++) {
                        if (pRecord[i][7] == "-1") {
                            pRecordWaters++;
                            pRecordNew.push(pRecord[i]);
                        }
                    };
                    if (nRecord.length + pRecordWaters > 0 ) {
                        $("#bkEntrustTab").find('.trade-title h4 em').text(" · " + (nRecord.length + pRecordWaters));
                        if(bkEntrustTabClose == true){
                            var $thisI = $('#bkEntrustTab h4').find('i'),
                            $parentB = $("#bkEntrustTab");
                            $('#bkEntrustTab h4').css('padding-bottom', '10px');
                            $parentB.find('.trade-content').show();
                            $parentB.find('.trade-title').css('border-bottom', '1px solid #E4E4E4');
                            $parentB.find('.trade-title-right').show();
                            $thisI.addClass('open');
                        }
                    } else {
                        $("#bkEntrustTab").find('.trade-title h4 em').text('');
                        if (userOption.iHide) {
                            userOption.iHide();
                        };
                    }
                }
                if (!isPage) {
                    $this.userRecord = result;
                    if (status == 3) {
                        if (nRecord.length == 0) {
                            $(listDiv).html("<tr><td colspan='8' class='botnone'>" + bitbank.L("当前没有限价委托") + "</td></tr>");
                        } else {
                            $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                        }
                        if (pRecordNew.length == 0) {
                            $("#readyRecord").html("<tr><td colspan='8' class='botnone'>" + bitbank.L("当前没有计划委托") + "</td></tr>");
                        } else {
                            $("#readyRecord").html(tmpl("tmpl-readyRecord", $this.formatEntrustRecord(pRecordNew)));
                        }
                    }
                    if (status == 0) {
                        //处理计划委托
                        if (pRecordNew.length == 0) {
                            $("#readyRecord").html("<tr><td colspan='8' class='botnone'>" + bitbank.L("当前没有计划委托") + "</td></tr>");
                        } else {
                            $("#readyRecord").html(tmpl("tmpl-readyRecord", $this.formatEntrustRecord(pRecord)));
                        }
                    }
                    if (status == 2) {
                        if (hRecord.length == 0) {
                            $("#historyRecord").html("<tr><td colspan='9' class='botnone'>" + bitbank.L("当前没有委托记录") + "</td></tr>");
                        } else {
                            $("#historyRecord").html(tmpl("tmpl-historyRecord", $this.formatEntrustRecord(hRecord)));
                        }
                    }
                    formatNum();
                }
                if (isPage) {
                    if (nRecord.length == 0) {
                        $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                        $(listDiv).html($(listDiv).html() + htmlNoRecord);
                        if (pageIndex == 1) {
                            $(listDiv + "_Page").html("");
                        } else {
                            $this.showPage(listDiv, pageIndex, 0, 30, status, entrustType);
                        }
                    } else {
                        $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                        formatNum();
                        $this.showPage(listDiv, pageIndex, nRecord.length, 30, status, entrustType);
                    }
                }
                initRecordFlag = false;
                return lockRepeatEntrust = false;
            });
        } else {
            var rCount = typeof result[0].count == "undefined" ? 0 : result[0].count;
            var nRecord = typeof result[0].record == "undefined" ? [] : result[0].record;
            var pRecord = typeof result[0].precord == "undefined" ? [] : result[0].precord;
            var hRecord = typeof result[0].hrecord == "undefined" ? [] : result[0].hrecord;
            var rLastTime = typeof result[0].lastTime == "undefined" ? lastTime : result[0].lastTime;
            if (rLastTime == 0) {
                JuaBox.showWrong(bitbank.L("系统忙碌，请稍候！"));
                return lockRepeatEntrust = false;
            }
            if (rLastTime == -1) {
                JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
                return lockRepeatEntrust = false;
            }
            if (!userOption.lastTimeRecord) {
                if (lastTimeRecord != rLastTime) {
                    setTimeout(function() {
                        lastTimeRecord = rLastTime;
                    }, 2000);
                } else {
                    return lockRepeatEntrust = false;
                }
            }
            if (!isPage) {
                if (nRecord.length == 0 && pRecord.length == 0) {
                    lockEntrust = true;
                }

                if (nRecord.length == 0) {
                    $(listDiv).html(htmlNoRecord);
                } else {
                    $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                }
                if (pRecord.length == 0) {
                    $("#readyRecord").html(htmlNoRecord);
                } else {
                    $("#readyRecord").html(tmpl("tmpl-readyRecord", $this.formatEntrustRecord(pRecord)));
                }
                if (hRecord.length == 0) {
                    $("#historyRecord").html(htmlNoRecord);
                } else {
                    $("#historyRecord").html(tmpl("tmpl-historyRecord", $this.formatEntrustRecord(hRecord)));
                }
                formatNum();
            }
            if (isPage) {
                if (nRecord.length == 0) {
                    $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                    $(listDiv).html($(listDiv).html() + htmlNoRecord);
                    if (pageIndex == 1) {
                        $(listDiv + "_Page").html("");
                    } else {
                        $this.showPage(listDiv, pageIndex, 0, pageSize, status, entrustType);
                    }
                } else {
                    $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), $this.formatEntrustRecord(nRecord)));
                    formatNum();
                    $this.showPage(listDiv, pageIndex, nRecord.length, pageSize, status, entrustType);
                }
            }
            return lockRepeatEntrust = false;
        }
    };
    trans.formatEntrustRecord = function(json) {
        var record = [];
        var nameStatus = {
            "-1": bitbank.L("计划中"),
            "0": bitbank.L("待成交"),
            "1": bitbank.L("已取消"),
            "2": bitbank.L("已完成"),
            "3": bitbank.L("待成交"),
            "4": bitbank.L("部分成交"),
        };
        var entrustSource = {
            "8": bitbank.L("网页"),
            "5": bitbank.L("手机APP"),
            "6": bitbank.L("API")
        };
        var planNameStatus = {
            "-1": bitbank.L("未触发"),
            "1": bitbank.L("已取消"),
            "2": bitbank.L("已委托")
        };
        for (var i = 0; i < json.length; i++) {
            var entrustId = json[i][0];
            var unitPrice = json[i][1];
            var numbers = json[i][2];
            var completeNumber = json[i][3];
            var completeTotalMoney = json[i][4];
            var types = json[i][5];
            var submitTime = json[i][6];
            var status = json[i][7];
            if (status == 3 && completeNumber > 0) {
                status = 4;
            }
            var feeRate = 0;
            if (json[i].length > 8 && json[i][8] != null) {
                feeRate = json[i][8];
            }
            var triggerPrice = 0;
            if (json[i].length >= 10) {
                triggerPrice = json[i][9];
            }
            var webId = 8;
            if (json[i].length >= 11) {
                webId = json[i][10];
                if (webId == 0) {
                    webId = 8;
                }
            }

            var triggerPriceProfit = 0;
            if (json[i].length >= 12) {
                triggerPriceProfit = json[i][11];
            }

            var unitPriceProfit = 0;
            if (json[i].length >= 13) {
                unitPriceProfit = json[i][12];
            }
            var totalMoney = 0;
            if (json[i].length >= 14) {
                totalMoney = json[i][13];
            }

            var formalEntrustId = "";
            if (json[i].length >= 15) {
                formalEntrustId = json[i][14];
            }
            var plantype = "false";
            if (json[i].length >= 16) {
                plantype = json[i][15];
            }
            record[i] = {};
            record[i].submitTime = LANG == 'en' ? new Date(submitTime).format("MM-dd-yyyy hh:mm:ss") : new Date(submitTime).format("yyyy-MM-dd hh:mm:ss");
            record[i].nameClass = types == 0 ? "text-primary" : "text-third";
            record[i].nameType = types == 0 ? bitbank.L("卖S") : bitbank.L("买B");
            record[i].numbers = M.fixNumber(numbers, numberBixDian) == 0 ? "--" : M.fixNumber(numbers, numberBixDian);
            record[i].unitPrice = M.fixNumber(unitPrice, exchangeBixDian) == 0 ? "--" : M.fixNumber(unitPrice, exchangeBixDian);
            record[i].completeNumber = M.fixNumber(completeNumber, numberBixDian);
            record[i].noCompleteNumber = M.fixNumber(numbers - completeNumber, numberBixDian);
            record[i].averagePrice = completeNumber > 0 ? M.fixNumber(completeTotalMoney / completeNumber, exchangeBixDian) : "--";
            record[i].completeTotalMoney = completeTotalMoney > 0 ? parseFloat(M.fixNumber(completeTotalMoney, amountDecialCur)) : "--";
            record[i].nameStatus = plantype == "true" ? planNameStatus[String(status)] : nameStatus[String(status)];
            record[i].operat = status == 0 || status == 3 || status == 4 || status == -1 ? bitbank.L("取消") : "";
            record[i].entrustId = entrustId;
            record[i].types = types;
            record[i].tradeFee = types == 0 ? M.fixDecimal(M.multiply(completeTotalMoney, feeRate), 8) + "(" + exchangeBi + ")" : M.fixDecimal(M.multiply(completeNumber, feeRate), 8) + "(" + numberBi + ")";
            record[i].triggerPrice = M.fixNumber(triggerPrice, exchangeBixDian) == 0 ? "--" : M.fixNumber(triggerPrice, exchangeBixDian);
            record[i].triggerPriceProfit = M.fixNumber(triggerPriceProfit, exchangeBixDian) == 0 ? "--" : M.fixNumber(triggerPriceProfit, exchangeBixDian);
            record[i].unitPriceProfit = M.fixNumber(unitPriceProfit, exchangeBixDian) == 0 ? "--" : M.fixNumber(unitPriceProfit, exchangeBixDian);
            record[i].totalMoney = M.fixNumber(totalMoney, exchangeBixDian);
            record[i].formalEntrustId = formalEntrustId;
            record[i].plantype = plantype;
            if (unitPrice == 0) {
                record[i].stopAmount = M.fixNumber(numbers, numberBixDian) == 0 ? M.fixNumber(totalMoney / unitPriceProfit, numberBixDian) : M.fixNumber(numbers, numberBixDian);
            } else {
                record[i].stopAmount = M.fixNumber(numbers, numberBixDian) == 0 ? M.fixNumber(totalMoney / unitPrice, numberBixDian) : M.fixNumber(numbers, numberBixDian);
            }
            //record[i].stopAmount = M.fixNumber(numbers, numberBixDian)==0?M.fixNumber(totalMoney/unitPriceProfit, exchangeBixDian):M.fixNumber(numbers, numberBixDian);
            record[i].source = entrustSource[String(webId)];
        }
        return record;
    };
    trans.getEntrustDetail = function(id, buyorsell,allNumber, callback) {
        if (!user.isLogin()) return false;
        var $this = this;
        var detailDiv = "#tradeRecordInfo";
        var listDiv = "#tradeRecord";
        var detailDivHead = "#tradeRecordHead";
        var jsonUrl = DOMAIN_TRANS + "/Record/GetDetails-" + market + "-" + id + "?jsoncallback=?";
        $.getJSON(jsonUrl, function(result) {
            if (result[0].record == undefined){
                return false;
            }
            if (result[0].record.length == 0) {
                $(listDiv).html("<tr><td colspan='7'>" + bitbank.L("通用没有任何记录") + "</td><tr>");
                JuaBox.sure($("#tradeList").html(), { width: 600, btnNum: 0 });
                return false;
            };
            var json = result[0].record;
            var record = [];
            var detailInfo = {};
            var allTotalCoin = 0;
            var allTotalMoney = 0;
            var allAvrPrice = 0;
            var moneyType = market.toUpperCase().split("_")[1];
            var coinType = market.toUpperCase().split("_")[0];
            Big.RM = 0;
            for (var i = 0; i < json.length; i++) {
                var tradeId = json[i][0];
                var unitPrice = json[i][1];
                var totalMoney = json[i][2];
                var numbers = json[i][3];
                var types = json[i][4];
                var submitTime = json[i][5];
                var feeRate = 0;
                if (json[i].length > 6 && json[i][6] != null) {
                    feeRate = json[i][6];
                }
                record[i] = {};
                record[i].submitTime = LANG =="en"? new Date(submitTime).format("MM-dd-yyyy hh:mm:ss"):new Date(submitTime).format("yyyy-MM-dd hh:mm:ss");
                record[i].nameClass = types == 0 ? "text-primary" : "text-third";
                record[i].nameType = types == 0 ? bitbank.L("卖S") : bitbank.L("买B");
                record[i].numbers =  new Big(numbers).toFixed(numberBixDian);
                record[i].unitPrice = new Big(unitPrice).toFixed(exchangeBixDian);
                // record[i].totalMoney = M.fixNumber(totalMoney, amountDecialCur);
                record[i].moneyType = moneyType;
                record[i].coinType = coinType;
                record[i].totalMoney = new Big(totalMoney).toFixed(exchangeBixDian);
                record[i].tradeId = tradeId;
                record[i].types = types;
                record[i].tradeFee = types == 0 ? M.fixDecimal(M.multiply(totalMoney, feeRate), 8) + "(" + exchangeBi + ")" : M.fixDecimal(M.multiply(numbers, feeRate), 8) + "(" + numberBi + ")";
                allTotalCoin = M.add(allTotalCoin, record[i].numbers);
                allTotalMoney = M.add(allTotalMoney, record[i].totalMoney);
            }
            detailInfo = {
                types: buyorsell == 0 ? bitbank.L("卖S") : bitbank.L("买B"),
                typesClass: buyorsell == 0 ? 'bk-entrust-info-type-sell' : 'bk-entrust-info-type-buy',
                allTotalMoney: M.fixNumber(allTotalMoney, amountDecialCur),
                allNumber:allNumber,
                allTotalCoin: allTotalCoin,
                allAvrPrice: M.fixNumber(allTotalMoney / allTotalCoin, exchangeBixDian),
                moneyType : moneyType,
                coinType : coinType
            };
            if ($(listDiv).length > 0) {
                $(listDiv).html(tmpl("tmpl-" + (listDiv.replace(/[#,.]/g, "")), record));
                formatNum();
            }
            if ($(detailDiv).length > 0) {
                $(detailDiv).html(tmpl("tmpl-" + (detailDiv.replace(/[#,.]/g, "")), detailInfo));
            }
            if ($(detailDivHead).length > 0) {
                $(detailDivHead).html(tmpl("tmpl-" + (detailDivHead.replace(/[#,.]/g, "")), detailInfo));
            }
            if ($.isFunction(callback)) { callback(); };
        });

    };
    trans.getDealRecord = function(option) {
        var jsonUrl = DOMAIN_TRANS + "" + entrustUrlBase + "Record/traderecord-" + market + "?";
        jsonUrl += "lastTime=" + lastTimeRecord + "&";
        jsonUrl += "type=" + type + "&";
        jsonUrl += "status=" + status + "&";
        jsonUrl += "timeFrom=" + timeFrom + "&";
        jsonUrl += "timeTo=" + timeTo + "&";
        jsonUrl += "numberFrom=" + numberFrom + "&";
        jsonUrl += "numberTo=" + numberTo + "&";
        jsonUrl += "priceFrom=" + priceFrom + "&";
        jsonUrl += "priceTo=" + priceTo + "&";
        jsonUrl += "dateTo=" + dateTo + "&";
        jsonUrl += "pageSize=" + pageSize + "&";
        jsonUrl += "pageIndex=" + pageIndex + "&";
        jsonUrl += "jsoncallback=?";
    };
    trans.getUnreadNotice = function() {
        if (!user.isLogin()) return false;
        var $this = this;
        var jsonUrl = DOMAIN_VIP + '/msg/getUserUnReadNotice';
        $.getJSON(jsonUrl, function(result) {
            if (!result.isSuc) return false;
            if (!result.datas || result.datas.length == 0) return false;
            var notice = result.datas[0];
            var html = [
                '<div class="juabox-notice">',
                '<div class="juabox-notice-title">' + notice['title'] + '</div>',
                '<div class="juabox-notice-time">' + M.formatDate(notice['publishTime'], "yyyy-MM-dd") + '</div>',
                '<div class="juabox-notice-content">' + notice['content'] + '</div>',
                '</div>'
            ].join('');
            JuaBox.sure(html, {
                tipClass: "juabox-notice-dialog",
                btnFun1: function(JuaId) {
                    var url = DOMAIN_VIP + '/msg/readNotice?maxNoticeId=' + notice['id'];
                    $.getJSON(url, function(result) {
                        JuaBox.close(JuaId);
                    });
                },
                btnName1: bitbank.L('确定'),
                maskFun: function() {

                },
                width: 710
            });
        })
    };
    trans.formatBillDetail = function(json) {
        var $this = this;
        var record = [];
        for (var i = 0; i < json.length; i++) {
            var id = json[i].id;
            var showType = json[i].showType;
            var sendTime = json[i].sendTime.time;
            var amount = json[i].amount;
            var balance = json[i].balance;
            var coinName = json[i].coinName;
            var inout = json[i].bt.inout;
            var fees = json[i].fees;
            record[i] = {};
            record[i].id = id;
            record[i].showType = showType;
            record[i].sendTime = M.formatDate(sendTime, "MM-dd hh:mm:ss");
            record[i].showType = showType;
            record[i].amount = M.fixNumber(amount, $this[coinName.toLowerCase()].decimal);
            record[i].balance = M.fixNumber(balance, $this[coinName.toLowerCase()].decimal);
            record[i].coinName = coinName;
            record[i].inout = inout == 1 ? "+" : "-";
            record[i].numFees = fees;
            record[i].fees = M.fixNumber(fees, $this[coinName.toLowerCase()].decimal);;
        }
        return record;
    };
    trans.getBillDetail = function() {
    };
    trans.getUserTradesRecord = function() {
        var $this = this;
        var totalUls = "";
        var htmlNoLogin = "<tr><td colspan='7'>" + bitbank.L("通用未登录提示") + "</td><tr>";
        var htmlNoRecord = "<tr><td colspan='7'>" + bitbank.L("通用没有任何记录") + "</td><tr>";
        $trades = $('#mineTradesRecord');
        if (!user.isLogin()) {
            $trades.html(htmlNoLogin);
            return false;
        };

        var jsonUrl = DOMAIN_TRANS + "/Record/traderecord-" + market + "?jsoncallback=?&pageIndex=1&dateTo=5";
        $.getJSON(jsonUrl, function(result) {
            var result = result[0];
            if (!result.record) {
                $trades.html(htmlNoLogin);
                return false;
            }

            if (result.record.length == 0) {
                $trades.html(htmlNoRecord);
                return false;
            }
            var records = result.record;
            for (var i = 0, len = Math.min(records.length, $this.tradesLimit); i < len; i++) {
                var dateStr = M.formatDate(records[i][4], "hh:mm:ss");
                var formatPrice = M.fixNumber(records[i][0], exchangeBixDian);
                var formatMoney = M.fixNumber(records[i][1], exchangeBixDian);
                var formatAmount = M.fixNumber(records[i][2], numberBixDian);
                var type = records[i][3] == 0 ? 'Sell' : 'Buy';
                //totalUls += "<tr><td>" + dateStr + "</td><td style='color:"+(type=='buy' ? '#2BB38A;':'#E55C62;')+"'>"+type+"</td><td>"+formatPrice+"</td><td>"+formatAmount+"</td><td>"+formatMoney+"</td></tr>"
                totalUls += "<tr><td>" + dateStr + "</td><td style='color:" + (type == 'Buy' ? '#2BB38A;' : '#E55C62;') + "'>" + type + "</td><td>" + formatPrice + "</td><td>" + formatAmount + "</td></tr>"
            }
            $trades.html(totalUls);
        });
    };
    trans.getNewTradesRecord = function(option, callback) {
        var $this = this;
        var userOption = option || {};
        var symbol = userOption.symbol;
        var initRecord = userOption.initRecord;
        var last_trade_tid = userOption.last_trade_tid;
        var totalUls = "";
        var $trades = $("#newTradesRecord");
        var jsonUrl = DOMAIN_TRANS + "/getLastTrades?callback=?&symbol=" + symbol + "&last_trade_tid=" + last_trade_tid;
        $.getJSON(jsonUrl, function(result) {
            var item = result.datas;
            if (initRecord) {
                if (item != null && item.length > 0) {

                    for (var i = item.length - 1; i >= 0; i--) {
                        var dateStr = M.formatDate(item[i].date * 1000, "hh:mm:ss");
                        var formatPrice = M.fixNumber(item[i].price, exchangeBixDian);
                        var formatAmount = M.fixNumber(item[i].amount, numberBixDian);
                        var formatMoney = M.fixNumber(formatPrice * formatAmount, exchangeBixDian);
                        var arr = formatAmount.split(".");
                        //totalUls += "<tr data-tid='"+item[i].tid+"'><td>"+dateStr+"</td><td style='color:"+(item[i].type=='buy' ? '#2BB38A;':'#E55C62;')+"'>"+(item[i].type=='buy' ? 'buy':'sell')+" </td><td>"+formatPrice+"</td><td>"+arr[0]+"<g>."+arr[1]+"</g></td><td class='text-right'>"+formatMoney+"</td></tr>";
                        totalUls += "<tr data-tid='" + item[i].tid + "'><td>" + dateStr + "</td><td style='color:" + (item[i].type != 'sell' ? '#2BB38A;' : '#E55C62;') + "'>" + (item[i].type == 'buy' ? 'Buy' : '')+(item[i].type == 'sell' ? 'Sell' : '')+(item[i].type == 'repo' ? 'Repo' : '')+ " </td><td>" + formatPrice + "</td><td>" + arr[0] + "<g>." + arr[1] + "</g></td></tr>";
                    };
                    $trades.append(totalUls);

                    $this.last_trade_tid = item[item.length - 1].tid;
                }
            } else {

                for (var i = item.length - 1; i >= 0; i--) {
                    if (i >= item.length - $this.tradesLimit) {
                        if (item[i].tid <= $this.last_trade_tid) continue;
                        var dateStr = M.formatDate(item[i].date * 1000, "hh:mm:ss");
                        var formatPrice = M.fixNumber(item[i].price, exchangeBixDian);
                        var formatAmount = M.fixNumber(item[i].amount, numberBixDian);
                        var formatMoney = M.fixNumber(formatPrice * formatAmount, exchangeBixDian);
                        var arr = formatAmount.split(".");
                        //totalUls += "<tr data-tid='"+item[i].tid+"'><td>"+dateStr+"</td><td style='color:"+(item[i].type=='buy' ? '#2BB38A;':'#E55C62;')+"'>"+(item[i].type=='buy' ? 'buy':'sell')+" </td><td>"+formatPrice+"</td><td>"+arr[0]+"<g>."+arr[1]+"</g></td><td class='text-right'>"+formatMoney+"</td></tr>";                           
                        totalUls += "<tr class='newul' data-tid='" + item[i].tid + "'><td>" + dateStr + "</td><td style='color:" + (item[i].type != 'sell' ? '#2BB38A;' : '#E55C62;') + "'>" + (item[i].type == 'buy' ? 'Buy' : '')+(item[i].type == 'sell' ? 'Sell' : '')+(item[i].type == 'repo' ? 'Repo' : '')+ " </td><td>" + formatPrice + "</td><td>" + arr[0] + "<g>." + arr[1] + "</g></td></tr>";
                    }
                }
                if (item && item.length > 0) {
                    $this.last_trade_tid = item[item.length - 1].tid;
                }
                $trades.prepend(totalUls);
                totalUls = null;
                $trades.find("tr.newul").slideDown(1000, function() {
                    // $trades.find("tr.newul").animate({"background-color": "#FFF"},1000, function() {
                            $(this).removeClass("newul"); 
                    // });
                });
                $trades.find("tr:gt(" + ($this.tradesLimit - 1) + ")").remove();
            }
            $("#newTradesRecord").css({
                "max-height": $("#tradeForm").outerHeight() + "px",
                "overflow": "auto"
            })


            if ($.isFunction(callback)) {
                callback();
            }
        });
    };
    trans.doEntrust = function(isBuy) {
        if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
        var $this = this;
        var safeEntrust = function() {
            if (needSafeWord) {
                $this.checkSafePwd(function() {
                    $this.doSubmit(isBuy);
                });
            } else {
                $this.doSubmit(isBuy);
            }
        };
        $this.hasSafePwd(function() {
            $this.reConfirm(isBuy, function() {
                var leverAccountBuy = parseFloat($("#leverAccountBuy").text());
                var leverAccountSell = parseFloat($("#leverAccountSell").text());

                if (((isBuy && leverFlagBuy) || (!isBuy && leverFlagSell)) && (leverAccountBuy > 0 || leverAccountSell > 0)) {
                    $this.doLoan(isBuy, function() {
                        asset.getLoanAsset(function() {
                            $("#canLoanBTC").html(asset.btc.canLoanIn);
                            $("#canLoanCNY").html(asset.cny.canLoanIn);
                            $("#canLoanLTC").html(asset.ltc.canLoanIn);
                            $("#canLoanETH").html(asset.eth.canLoanIn);
                            $("#canLoanETC").html(asset.etc.canLoanIn);
                        });
                        safeEntrust();
                    });
                } else {
                    safeEntrust();
                }
            });
        });
    };
    trans.doLoan = function(isBuy, callback) {
            if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
            if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
            var $this = this;
            ajaxIng = true;
            if (isBuy == 1) {
                $("#buyBtn").data("loading-text", bitbank.L("正在申请杠杆..."));
                $("#buyBtn").button("loading");
            } else {
                $("#sellBtn").data("loading-text", bitbank.L("正在申请杠杆..."));
                $("#sellBtn").button("loading");
            }
            var moneyType = parseInt($("#moneyType").val()) + 1;
            var coinType = parseInt($("#coinType").val()) + 1;
            var fundsType = isBuy ? moneyType : coinType;

            var leverAccountBuy = parseFloat($("#leverAccountBuy").text());
            var leverAccountSell = parseFloat($("#leverAccountSell").text());
            var amount = isBuy ? leverAccountBuy : leverAccountSell;

            $.ajax({
                type: "post",
                url: DOMAIN_P2P + "/u/loan/doLoan?ftype=" + fundsType + "&amount=" + amount + "&onekeyentrust=1",
                dataType: "jsonp",
                error: function(json) {
                    ajaxIng = false;
                    JuaBox.sure(json.des);
                    $("#buyBtn, #sellBtn").button("reset");
                },
                success: function(json) {
                    ajaxIng = false;
                    $("#buyBtn, #sellBtn").button("reset");
                    if ($.isFunction(callback)) { callback(); };
                }
            });
        },
        trans.doSubmit = function(isBuy, callback) {
            if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
            if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
            if (!doEntrustStatus) return JuaBox.showWrong(bitbank.L("请勿非法提交数据"));
            var $this = this;
            ajaxIng = true;

            var buyUnitPrice = parseFloat($("#buyUnitPrice").val()),
                buyMaxPrice = parseFloat($("#buyMaxPrice").val()),
                buyNumber = parseFloat($("#buyNumber").val()),
                sellUnitPrice = parseFloat($("#sellUnitPrice").val()),
                sellMaxPrice = parseFloat($("#sellMaxPrice").val()),
                sellNumber = parseFloat($("#sellNumber").val()),
                safePwd = $("#safePwd").val(),
                buyPlanMoney = parseFloat($("#buyPlanMoney").val()),
                buyTriggerPrice = parseFloat($("#buyTriggerPrice").val()),
                buyPlanPrice = parseFloat($("#buyPlanPrice").val()),
                sellPlanNumber = parseFloat($("#sellPlanNumber").val()),
                sellTriggerPrice = parseFloat($("#sellTriggerPrice").val()),
                sellPlanPrice = parseFloat($("#sellPlanPrice").val());

            var coinPrice, coinPriceMax, coinPriceMin, coinNumber, isReal, isBatch;
            if (isBuy == 1) {
                coinPrice = buyUnitPrice;
                coinPriceMax = buyMaxPrice;
                coinPriceMin = buyUnitPrice;
                coinNumber = buyNumber;
                isReal = buyStrategy == 1 ? false : true;
                isBatch = buyStrategy == 2 ? true : false;
                $("#buyBtn").data("loading-text", bitbank.L("正在提交委托..."));
                $("#buyBtn").button("loading");
            } else {
                coinPrice = sellUnitPrice;
                coinPriceMax = sellMaxPrice;
                coinPriceMin = sellUnitPrice;
                coinNumber = sellNumber;
                isReal = sellStrategy == 1 ? false : true;
                isBatch = sellStrategy == 2 ? true : false;
                $("#sellBtn").data("loading-text", bitbank.L("正在提交委托..."));
                $("#sellBtn").button("loading");
            }

            if (isBatch) {
                $.getJSON(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doEntrustMore-" + market + "?jsoncallback=?", {
                    safePassword: safePwd,
                    priceLow: coinPriceMin,
                    priceHigh: coinPriceMax,
                    numbers: coinNumber,
                    isbuy: isBuy
                }, function(result) {
                    var des = result.des;
                    if (isBuy == 1) {
                        $("#buyBtn").button("reset");
                    } else {
                        $("#sellBtn").button("reset");
                    }

                    ajaxIng = false;
                    if (result.isSuc) {
                        if (des.indexOf(":") > 0) {
                            var data = des.split(":");
                            if (data[0] != "0") {
                                if (isBuy == 1) {
                                    $("#buyUnitPrice").val("").change();
                                    $("#buyMaxPrice").val("");
                                    $("#buyNumber").val("");
                                } else {
                                    $("#sellUnitPrice").val("").change();
                                    $("#sellMaxPrice").val("");
                                    $("#sellNumber").val("");
                                }
                                if (needSafeWord) {
                                    JuaBox.closeAll();
                                }
                                lockEntrust = false;
                                doEntrustStatus = false;
                                setTimeout(function() {
                                    $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                                }, 1000);
                                JuaBox.sure(bitbank.L("成功批量委托m笔，一共na，总金额va", data[0], parseFloat(data[1]), numberBi, parseFloat(data[2]), exchangeBi), { btnNum: 1 });
                            } else {
                                JuaBox.showWrong(bitbank.L("批量委托失败！"));
                            }
                        } else {
                            JuaBox.showWrong(bitbank.L("批量委托失败！"));
                        }
                    } else {
                        if (needSafeWord) {
                            JuaBox.closeAll(function() {
                                JuaBox.sure(des);
                            });
                        } else {
                            JuaBox.sure(des);
                        }
                    }
                }, "json");
            } else {
                if (!isReal) {
                    var planData;
                    if (isBuy == 1) {
                        planData = {
                            safePassword: safePwd,
                            isBuy: isBuy,
                            buyPlanMoney: isNaN(buyPlanMoney) ? 0 : buyPlanMoney,
                            buyTriggerPrice: isNaN(buyTriggerPrice) ? 0 : buyTriggerPrice,
                            buyPlanPrice: isNaN(buyPlanPrice) ? 0 : buyPlanPrice
                        }
                    } else {
                        planData = {
                            safePassword: safePwd,
                            isBuy: isBuy,
                            sellPlanNumber: isNaN(sellPlanNumber) ? 0 : sellPlanNumber,
                            sellTriggerPrice: isNaN(sellTriggerPrice) ? 0 : sellTriggerPrice,
                            sellPlanPrice: isNaN(sellPlanPrice) ? 0 : sellPlanPrice
                        }
                    }
                    $.getJSON(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doPlanEntrust-" + market + "?jsoncallback=?", planData, function(json) {
                        var code = json.datas.code;
                        var des = json.des;
                        if (isBuy == 1) {
                            $("#buyBtn").button("reset");
                        } else {
                            $("#sellBtn").button("reset");
                        }

                        ajaxIng = false;
                        if (code == 100) {
                            if (isBuy == 1) {
                                $("#buyPlanMoney").val("").change();
                                $("#buyTriggerPrice").val("").change();
                                $("#buyPlanPrice").val("").change();
                                $("#buyPlanMoney").val("");
                            } else {
                                $("#sellPlanNumber").val("").change();
                                $("#sellTriggerPrice").val("").change();
                                $("#sellPlanPrice").val("").change();
                                $("#sellPlanMoney").val("");
                            }
                            if (needSafeWord) {
                                JuaBox.closeAll();
                            }
                            JuaBox.showRight(des);
                            lockEntrust = false;
                            doEntrustStatus = false;
                            setTimeout(function() {
                                $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                            }, 1000);
                        } else {
                            if (needSafeWord) {
                                JuaBox.closeAll(function() {
                                    JuaBox.sure(des);
                                });
                            } else {
                                JuaBox.sure(des);
                            }
                        }
                    }, "json");

                } else {
                    $.getJSON(DOMAIN_TRANS + "" + entrustUrlBase + "entrust/doEntrust-" + market + "?jsoncallback=?", {
                        safePassword: safePwd,
                        unitPrice: coinPrice,
                        number: coinNumber,
                        isBuy: isBuy
                    }, function(json) {
                        var code = json.datas.code;
                        var des = json.des;
                        if (isBuy == 1) {
                            $("#buyBtn").button("reset");
                        } else {
                            $("#sellBtn").button("reset");
                        }

                        ajaxIng = false;
                        if (code == 100) {
                            if (isBuy == 1) {
                                $("#buyUnitPrice").val("").change();
                                $("#buyNumber").val("");
                                $("#realBuyAccount").val("");
                            } else {
                                $("#sellUnitPrice").val("").change();
                                $("#sellNumber").val("");
                                $("#realSellAccount").val("");
                            }
                            if (needSafeWord) {
                                JuaBox.closeAll();
                            }
                            JuaBox.showRight(des);
                            lockEntrust = false;
                            doEntrustStatus = false;
                            setTimeout(function() {
                                $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                            }, 1000);
                        } else {
                            if (needSafeWord) {
                                JuaBox.closeAll(function() {
                                    JuaBox.sure(des);
                                });
                            } else {
                                JuaBox.sure(des);
                            }
                        }
                    }, "json");
                }
            }
            if (isReal === false) {
                $("#bkEntrustTab .btn-group .btn").eq(1).click();
            } else {
                $("#bkEntrustTab .btn-group .btn").eq(0).click();
            }
            if (isBuy == 1 && $("#leverSwitchBuy:checked").length > 0) {
                $("#leverSwitchBuy").click();
            }
            if (isBuy != 1 && $("#leverSwitchSell:checked").length > 0) {
                $("#leverSwitchSell").click();
            }
        };

    trans.reConfirm = function(isBuy, callback, tryCode) {
        if (!user.isLogin()) return JuaBox.showWrong(bitbank.L("请先登录后再进行交易"));
        if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的提交申请，请等待后重试"));
        if ((isBuy == 1 && buyStrategy == 1 && leverFlagBuy) || (isBuy == 0 && sellStrategy == 1 && leverFlagSell)) return JuaBox.showWrong(bitbank.L("计划委托暂不支持使用一键杠杆，请关闭一键杠杆后再提交。"));

        var $this = this;
        var hiddenCoinType = $('#hidden_cointype').val();
        var hiddenMoneyType = $('#hidden_moneytype').val();
        var tryCode = tryCode || 0;
        if (tryCode == 99) {
            doEntrustStatus = true;
            if ($.isFunction(callback)) { callback(); };
            return false;
        };
        var canUseMoney = parseFloat(asset[exchangeBi.toLowerCase()]["usable"]);
        var canUseLeverMoney = leverFlagBuy ? (canUseMoney + asset[exchangeBi.toLowerCase()].canLoanIn) : canUseMoney;
        var canUseCoin = parseFloat(asset[numberBi.toLowerCase()]["usable"]);
        var canUseLeverCoin = leverFlagSell ? (canUseCoin + asset[numberBi.toLowerCase()].canLoanIn) : canUseCoin;
        var buyUnitPrice = parseFloat($("#buyUnitPrice").val()),
            buyMaxPrice = parseFloat($("#buyMaxPrice").val()),
            buyNumber = parseFloat($("#buyNumber").val()),
            buyPlanNumber = parseFloat($("#buyPlanNumber").val()),
            sellUnitPrice = parseFloat($("#sellUnitPrice").val()),
            sellMaxPrice = parseFloat($("#sellMaxPrice").val()),
            sellNumber = parseFloat($("#sellNumber").val()),
            safePwd = $("#safePwd").val(),
            buyPlanMoney = parseFloat($("#buyPlanMoney").val()),
            buyTriggerPrice = parseFloat($("#buyTriggerPrice").val()),
            buyPlanPrice = parseFloat($("#buyPlanPrice").val()),
            sellPlanNumber = parseFloat($("#sellPlanNumber").val()),
            sellTriggerPrice = parseFloat($("#sellTriggerPrice").val()),
            sellPlanPrice = parseFloat($("#sellPlanPrice").val());

        var canSellMoney = M.fixNumber(sellPlanNumber * sellPlanPrice, exchangeBixDian);
        var canBuyCoin = M.fixNumber(buyPlanMoney / buyPlanPrice, numberBixDian);

        var coinPrice, coinNumber, isReal, isBatch;
        var moneyRegEx = new RegExp("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1," + exchangeBixDian + "})?$");
        var coinRegEx = new RegExp("^(([1-9]{1}\\d*)|([0]{1}))(\\.(\\d){1," + numberBixDian + "})?$");
        if (isBuy == 1) {
            if (buyStrategy == 1) {
                if (isNaN(buyTriggerPrice) || buyTriggerPrice == 0) {
                    $("#buyTriggerPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入触发价格"));
                }
                if (isNaN(buyPlanPrice) || buyPlanPrice == 0) {
                    $("#buyPlanMoney").focus();
                    return JuaBox.showWrong(bitbank.L("请输入限价委托价格"));
                }
                if (isNaN(buyPlanNumber) || buyPlanNumber == 0) {
                    $("#buyPlanMoney").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托数量"));
                }
                if (buyPlanMoney > canUseMoney) {
                    $("#buyPlanMoney").focus();
                    return JuaBox.showWrong(bitbank.L("您的可用资金不足，请核实后再提交。", M.fixNumber(buyPlanMoney, exchangeBixDian), exchangeBi));
                }
            } else {
                coinPrice = M.fixNumber(buyUnitPrice, amountDecialCur);
                coinNumber = buyNumber;
                isReal = buyStrategy == 1 ? false : true;
                isBatch = buyStrategy == 2 ? true : false;
                if (!moneyRegEx.test(coinPrice) || coinPrice == 0) {
                    $("#buyUnitPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托价格", exchangeBixDian));
                } else {
                    coinPrice = M.fixNumber(parseFloat(coinPrice), amountDecialCur);
                }
                if (isBatch && (!moneyRegEx.test(buyMaxPrice) || buyMaxPrice == 0)) {
                    $("#buyMaxPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托价格", exchangeBixDian));
                } else {
                    buyMaxPrice = M.fixNumber(parseFloat(buyMaxPrice), amountDecialCur);
                }
                if (!coinRegEx.test(coinNumber) || coinNumber == 0) {
                    $("#buyNumber").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托数量", numberBixDian));
                } else {
                    coinNumber = parseFloat(coinNumber);
                }
                if (buyMaxPrice * coinNumber > canUseLeverMoney) {
                    $("#buyNumber").focus();
                    return JuaBox.showWrong(bitbank.L("您的可用资金不足，请核实后再提交。", M.fixNumber(buyMaxPrice * coinNumber, amountDecialCur), exchangeBi));
                }
                if (isBatch && (coinPrice - buyMaxPrice) > 0) {
                    return JuaBox.showWrong(bitbank.L("批量分散买入的最高限定价格m应高于最低限定价格n，请重设价格。", M.fixNumber(buyMaxPrice, exchangeBixDian), M.fixNumber(coinPrice, exchangeBixDian)));
                }
                return JuaBox.info(bitbank.L("确认买入", M.fixNumber(coinPrice, exchangeBixDian), hiddenMoneyType, coinNumber, hiddenCoinType), {
                    btnNum: 2,
                    btnFun1: function(JuaId) {
                        JuaBox.close(JuaId, function() {
                            $this.reConfirm(isBuy, callback, 99);
                        });
                    }
                });
            }
        } else {
            coinPrice = M.fixNumber(sellUnitPrice, amountDecialCur);
            coinNumber = sellNumber;
            isReal = sellStrategy == 1 ? false : true;
            isBatch = sellStrategy == 2 ? true : false;
            if (sellStrategy == 1) {
                if (isNaN(sellTriggerPrice) || sellTriggerPrice == 0) {
                    $("#sellTriggerPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入触发价格"));
                }
                if (isNaN(sellPlanPrice) || sellPlanPrice == 0) {
                    $("#sellPlanPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入限价委托价格"));
                }
                if (isNaN(sellPlanNumber) || sellPlanNumber == 0) {
                    $("#sellPlanNumber").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托数量"));
                }
                if (sellPlanNumber > canUseCoin) {
                    $("#sellPlanNumber").focus();
                    return JuaBox.showWrong(bitbank.L("您的可用资金不足，请核实后再提交。", M.fixNumber(sellPlanNumber, NumberBixDian), NumberBi));
                }
            } else {
                if (!moneyRegEx.test(coinPrice) || coinPrice == 0) {
                    $("#sellUnitPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托价格", exchangeBixDian));
                } else {
                    coinPrice = M.fixNumber(parseFloat(coinPrice), amountDecialCur);
                }
                if (isBatch && (!moneyRegEx.test(sellMaxPrice) || sellMaxPrice == 0)) {
                    $("#sellMaxPrice").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托价格", exchangeBixDian));
                } else {
                    sellMaxPrice = M.fixNumber(parseFloat(sellMaxPrice), amountDecialCur);
                }
                if (!coinRegEx.test(coinNumber) || coinNumber == 0) {
                    $("#sellNumber").focus();
                    return JuaBox.showWrong(bitbank.L("请输入委托数量", numberBixDian));
                } else {
                    coinNumber = parseFloat(coinNumber);
                }
                if (coinNumber > canUseLeverCoin) {
                    $("#sellNumber").focus();
                    return JuaBox.showWrong(bitbank.L("您的可卖数量不足，请核实后再提交。", M.fixNumber(coinNumber, numberBixDian), numberBi));
                }
                if (isBatch && (coinPrice - sellMaxPrice) > 0) {
                    return JuaBox.showWrong(bitbank.L("批量分散卖出的最低限定价格m应低于最高限定价格n，请重设价格。", M.fixNumber(coinPrice, exchangeBixDian), M.fixNumber(sellMaxPrice, exchangeBixDian)));
                }
                return JuaBox.info(bitbank.L("确认卖出", M.fixNumber(coinPrice, exchangeBixDian), hiddenMoneyType, coinNumber, hiddenCoinType), {
                    btnNum: 2,
                    btnFun1: function(JuaId) {
                        JuaBox.close(JuaId, function() {
                            $this.reConfirm(isBuy, callback, 99);
                        });
                    }
                });
            }
        }
        return $this.reConfirm(isBuy, callback, 99);
    };
    trans.checkSafePwd = function(callback) {
        var $this = this;
        JuaBox.info(user.safePwdForm(), {
            title: bitbank.L("本次交易需要安全密码验证"),
            btnName1: bitbank.L("提交"),
            btnName2: bitbank.L("取消"),
            btnFun1: function() {
                if ($("#safePwd").val() == "" || $("#safePwd").val().length < 6) {
                    return JuaBox.showWrong(bitbank.L("资金安全密码不能少于6位数，也不能为空。"))
                };
                $.ajax({
                    url:DOMAIN_VIP + "/manage/safePwdForEnturst",
                    type:"POST",
                    data:{payPass:$("#safePwd").val()},
                    dataType:"json",
                    success:function(json){
                        var des = json.des;
                        if (json.isSuc) {
                                if($('input[name="closeStatu"]:checked').val() != "") {
                                    $.post(DOMAIN_VIP + "/manage/useOrCloseSafePwd", {
                                        payPass: $("#safePwd").val(),
                                        closeStatu: $('input[name="closeStatu"]:checked').val(),
                                        needMobile: $("#needMobile").val(),
                                        needPwd: $("#needPwd").val()
                                    }, function() {

                                    }, "json");
                                }
                                if ($.isFunction(callback)) { callback(); };

                        } else {
                            JuaBox.sure(des);
                        }
                    }
                })

            },
            endFun: function(JuaId) {
                $("#closeSafePwd").on("click", function() {
                    JuaBox.closeAll(function() {
                        $("#buyBtn,#sellBtn").button("reset");
                        user.closeSafePwd(function() {
                            $this.hasSafePwd();
                        });
                    });
                });
                $("#JuaBox_" + JuaId).keypress(function(e) {
                    if (e.keyCode == 13) {
                        $("#JuaBtn_" + JuaId + "_1").click();
                    }
                });
            }
        });
    };
    trans.cancelEntrust = function(id, type, plantype) {
        if (!user.isLogin()) return false;
        var $this = this;
        JuaBox.info(bitbank.L("确定取消当前委托？"), {
            btnFun1: function(JuaId) {
                JuaBox.close(JuaId, function() {
                    $this.doCancelEntrust(id, type, plantype,
                        function() {
                            setTimeout(function() {
                                $this.getEntrustRecord({ listDiv: "#entrustRecord", lastTimeRecord: lastTime, status: 3, opEntrust: true });
                            }, 2000);
                        });
                });
            }
        });
    };
    trans.doCancelEntrust = function(id, type, plantype, callback) {
        if (!user.isLogin()) return false;
        if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        var $this = this;
        ajaxIng = true;

        $.getJSON(DOMAIN_TRANS + "" + entrustUrlBase + "Entrust/cancle-" + market + "-" + id + "-" + plantype + "?jsoncallback=?", function(result) {
            if (result.isSuc) {
                ajaxIng = false;
                if (result.datas == 200) {
                    JuaBox.showRight(result.des);
                } else {
                    JuaBox.showWrong(result.des);
                }
                if ($.isFunction(callback)) { callback(); };
                setTimeout(function() {
                    lastTimeRecord = lastTime;
                }, 2000);

                if ($.isFunction(callback)) {
                    callback();
                }
            } else {
                ajaxIng = false;
                JuaBox.sure(result.des);
            }
        });
    };
    trans.batchCancelEntrust = function(planType, callback) {
        if (!user.isLogin()) return false;
        var $this = this;
        var cancelHtml = "";
        cancelHtml += "<form role='form' id='batchCancelForm' class='form-inline' method='post' autocomplete='off'>";
        cancelHtml += "<input type='hidden' id='types' name='types' value='0'>";
        cancelHtml += "<div class='bk-tabList bk-tabList-entrust'>";
        cancelHtml += "  <div class='batch-close'>×</div>";
        cancelHtml += "  <div class='bk-tabList-hd bk-tabList-hdn clearfix'>";
        cancelHtml += "    <div class='btn-group bk-btn-group btn-batch' role='group'>";
        cancelHtml += "      <a class='btn active' role='button' onclick='$(\"#types\").val(0)'>" + bitbank.L("全部撤销") + "</a>";
        cancelHtml += "      <a class='btn' role='button' onclick='$(\"#types\").val(1)'>" + bitbank.L("撤销买单") + "</a>";
        cancelHtml += "      <a class='btn' role='button' onclick='$(\"#types\").val(2)'>" + bitbank.L("撤销卖单") + "</a>";
        cancelHtml += "    </div>";
        cancelHtml += "  </div>";
        cancelHtml += "  <div class='bk-tabList-bd bk-tabList-bdn clearfix'>";
        cancelHtml += "    <div class='form-group' style='width:100%; font-size:12px; margin-bottom:10px;'>";
        cancelHtml += "      <label for='minPrice' class='control-label'></label>";
        cancelHtml += "      <input type='checkbox' id='whetherFill' />" + bitbank.L("全部撤销");
        cancelHtml += "    </div>";
        cancelHtml += "    <div class='form-group' style='width:100%; font-size:12px; margin-bottom:10px;'>";
        cancelHtml += "      <label for='minPrice' class='control-label'>" + bitbank.L("价格高于：") + "</label>";
        cancelHtml += "      <input type='text' min='0' class='form-control' id='minPrice' name='minPrice'>";
        cancelHtml += "    </div>";
        cancelHtml += "    <div class='form-group' style='width:100%; font-size:12px; margin-bottom:10px;'>";
        cancelHtml += "      <label for='maxPrice' class='control-label'>" + bitbank.L("价格低于：") + "</label>";
        cancelHtml += "      <input type='text' min='0' class='form-control' id='maxPrice' name='maxPrice'>";
        cancelHtml += "    </div>";
        cancelHtml += "  </div>";
        cancelHtml += "</div>";
        cancelHtml += "</form>";

        JuaBox.info(cancelHtml, {
            title: bitbank.L("批量撤销"),
            btnFun1: function(JuaId) {
                $this.doBatchCancelEntrust(planType, callback);
            },
            endFun: function() {
                $("#batchCancelForm .bk-tabList").slide({
                    titCell: ".btn-group .btn",
                    effect: "fade",
                    trigger: "click",
                    titOnClassName: "active"
                });
                $('.batch-close').on('click', function() {
                    JuaBox.closeAll();
                });
                $('#whetherFill').on('click', function() {
                    if ($(this).is(':checked')) {
                        $(".bk-tabList-bdn input[type='number']").prop({ disabled: true });
                    } else {
                        $(".bk-tabList-bdn input[type='number']").prop({ disabled: false });
                    }
                });
                $("#minPrice,#maxPrice").on({
                    "keyup": function() { $this.checkNumber($(this), exchangeBixDian); },
                    "blur": function() { $this.checkNumber($(this), exchangeBixDian); }
                });
            }
        });
    };
    trans.doBatchCancelEntrust = function(plantype, callback) {
        if (!user.isLogin()) return false;
        if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        var $this = this;
        var maxPrice = 0,
            minPrice = 0;
        if ($("#maxPrice").val() != "")
            maxPrice = parseFloat($("#maxPrice").val());
        if ($("#minPrice").val() != "") {
            minPrice = parseFloat($("#minPrice").val());
        }
        if (maxPrice < minPrice) {
            return JuaBox.showWrong(bitbank.L("最高价m应该大于最低价n！", maxPrice, minPrice));
        }
        ajaxIng = true;
        $.ajax({
            type: "post",
            url: DOMAIN_TRANS + "/entrust/cancleMore-" + market + "-" + plantype + "?jsoncallback=?",
            data: $("#batchCancelForm").serialize(),
            dataType: "JSON",
            error: function(json) {
                ajaxIng = false;
                JuaBox.closeAll();
                JuaBox.showWrong(json.des);
            },
            success: function(json) {
                ajaxIng = false;
                JuaBox.closeAll();
                var des = json.des;
                if (des == 0) {
                    JuaBox.showTip(bitbank.L("没有需要取消的相关委托"));
                } else {
                    JuaBox.showRight(bitbank.L("成功取消n条委托！", des));
                }
                if ($.isFunction(callback)) { callback(); };
                setTimeout(function() {
                    lastTimeRecord = lastTime;
                }, 2000);
            }
        });
    };
    trans.batchCancelPlanEntrust = function(callback) {
        if (!user.isLogin()) return false;
        var $this = this;
        JuaBox.info(bitbank.L("确定取消全部计划委托？"), {
            btnFun1: function(JuaId) {
                JuaBox.close(JuaId, function() {
                    $this.doBatchCancelPlanEntrust(callback);
                })
            }
        });
    };
    trans.doBatchCancelPlanEntrust = function(callback) {
        if (!user.isLogin()) return false;
        if (ajaxIng) return JuaBox.showWrong(bitbank.L("您有未完成的请求，请等待后重试"));
        ajaxIng = true;
        $.ajax({
            type: "post",
            url: DOMAIN_TRANS + "/entrust/cancelmorePlanEntrust-" + market + "?jsoncallback=?",
            data: $("#batchCancelForm").serialize(),
            dataType: "JSON",
            error: function(json) {
                ajaxIng = false;
                JuaBox.closeAll();
                JuaBox.showWrong(json.des);
            },
            success: function(json) {
                ajaxIng = false;
                JuaBox.closeAll();
                var des = json.des;
                if (des == 0) {
                    JuaBox.showTip(bitbank.L("没有需要取消的相关委托"));
                } else {
                    JuaBox.showRight(bitbank.L("成功取消n条委托！", des));
                }
                if ($.isFunction(callback)) { callback(); };
                setTimeout(function() {
                    lastTimeRecord = lastTime;
                }, 2000);
            }
        });
    };
    trans.hasSafePwd = function(callback) {
        if (!user.isLogin()) return false;
        $.ajax({
            url: DOMAIN_VIP + "/manage/isTransSafe?callback=?",
            type: 'post',
            dataType: 'json',
            success: function(json) {
                if (json.des == "false") {
                    needSafeWord = false;
                    if ($.isFunction(callback)) { callback(); };
                    return false;
                } else {
                    needSafeWord = true;
                    if ($.isFunction(callback)) { callback(); };
                    return true;
                }
            }
        });
    };
    trans.isSameIp = function(callback) {
        if (!user.isLogin()) return false;
        $.ajax({
            async: false,
            url: DOMAIN_VIP + "/u/safe/isNotIpTransSafe?callback=?",
            type: 'post',
            dataType: 'json',
            success: function(json) {
                if (json.des == "true") {
                    needSafeWord = true;
                    JuaBox.info(bitbank.L("您当前IP与登录IP不一致，请输入资金安全密码验证。"), {
                        btnFun1: function(JuaId) {
                            JuaBox.close(JuaId, function() {
                                if ($.isFunction(callback)) { callback(); };
                            })
                        }
                    });
                    return true;
                } else {
                    if ($.isFunction(callback)) { callback(); };
                    return false;
                }
            }
        });
    };
    trans.formatNumberUse = function(num) {
        num = parseFloat(num);
        if (numberBixNormal != numberBixShow) {
            num = Math.floor(num);
            return Math.floor(num * Math.pow(10, numberBixDian));
        } else {
            return Math.floor(accMul(num, numberBixNormal));
        }
    };
    trans.formatMoneyUse = function(num) {
        num = parseFloat(num);
        if (exchangeBixNormal != exchangeBixShow) {
            num = Math.floor(num);
            return Math.floor(num * Math.pow(10, exchangeBixDian));
        } else {
            return Math.floor(accMul(num, exchangeBixNormal));
        }
    };
    /**
     * getRepoTradesRecord  start
     */
    trans.getRepo = function(){
        var $this = this;
        
        $this.getRepoTopData();
        
        var lasdRepoId = 0,lastRatioId=0;
        $this.nextRepoDate(lasdRepoId,function(id){
            lasdRepoId=id;
            wheel.wheeler("#repo_cover");
        });
        $(document).on("click","#repo_cover .moretwenty b",function(){
            // console.log(lasdRepoId);
            $this.nextRepoDate(lasdRepoId,function(id){
                lasdRepoId=id;
            });
        })
        if(user.isLogin()){
            $this.nextRatioDate(lastRatioId,function(id){
                wheel.wheeler("#related_cover");
                lastRatioId=id;
                if(id==0){
                    var nolisthtml = "<tr id='noRatioList'><td colspan='4' class='botnone'>" + bitbank.L("通用没有任何记录") + "</td></tr>"
                    $("#relatedList").html(nolisthtml);
                    // $("#related_cover .moretwenty").hide();
                }
            })    
        }
        $(document).on("click","#related_cover .moretwenty b",function(){

            $this.nextRatioDate(lastRatioId,function(id){
                lastRatioId=id;
            });
        })
    };
    trans.nextRepoDate = function(lastEntrustId,fun){
        var $this = this;
        $.getJSON(DOMAIN_VIP + "/backcapital/getEntrusts?callback=?&lastEntrustId="+lastEntrustId, function(result) {
            if(result.isSuc){
                $this.repoEntrustsHtml(result.datas,"bottom",function(){
                    var lastId = result.datas.entrusts.length>0?result.datas.entrusts[result.datas.entrusts.length-1].entrustId:1;
                    if(fun){
                        fun(lastId);
                    }
                });
            }
        })
        //模拟数据
        // var result = {
        //                 des: "success",
        //                 isSuc: true,
        //                 datas: {
        //                     hasMore:0,
        //                     entrusts:[
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         },
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         },
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         },
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         },
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         },
        //                         {
        //                             entrustId: Math.ceil(Math.random()*100000), //委托id
        //                             date: new Date(), //时间，单位秒
        //                             totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
        //                             amount: Math.ceil(Math.random()*100) //回购量
        //                         }
        //                     ]
        //                 }
        //             }
        // if(result.isSuc){
        //     $this.repoEntrustsHtml(result.datas,"bottom",function(){
        //         var lastId = result.datas.entrusts.length>0?result.datas.entrusts[result.datas.entrusts.length-1].entrustId:1;
        //         if(fun){
        //             fun(lastId);
        //         }
        //     });
        // }
        
    };
    trans.nextRatioDate = function(lastEntrustId,fun){
        var $this = this;
        $.getJSON(DOMAIN_VIP + "/backcapital/getEntrustsRelatedMe?callback=?&lastEntrustId="+lastEntrustId, function(result) {
            if(result.isSuc){
                $this.repoRatioHtml(result.datas,"bottom",function(){
                    var lastId = result.datas.entrusts.length>0?result.datas.entrusts[result.datas.entrusts.length-1].entrustId:0;
                    if(fun){
                        fun(lastId);
                    }
                });
            }
        })
        //模拟数据
        // var result = {
        //                 des: "success",
        //                 isSuc: true,
        //                 datas: {
        //                     hasMore:0,
        //                     entrusts:[
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // },
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // },
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // },
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // },
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // },
                                // {
                                //     entrustId: Math.ceil(Math.random()*100000), //委托id
                                //     date: new Date(), //时间，单位秒
                                //     totalMoney: Math.ceil(Math.random()*100000)/10000, //回购资金
                                //     amount: Math.ceil(Math.random()*100), //回购量
                                //     ratio:Math.ceil(Math.random()*100)/100,
                                // }
        //                     ]
        //                 }
        //             }
        // if(result.isSuc){
        //     $this.repoRatioHtml(result.datas,"bottom",function(){
        //         var lastId = result.datas.entrusts.length>0?result.datas.entrusts[result.datas.entrusts.length-1].entrustId:0;
        //         if(fun){
        //             fun(lastId);
        //         }
        //     });
        // }
    };
    trans.getRepoTopData = function(id){
        var $this = this;
        var lastEntrustId=id?id:0;
        var repoLister = $("#repoList").find("tr");
        if(repoLister.length>0){
            lastEntrustId = repoLister.eq(0).attr("id");
        }
        $.getJSON(DOMAIN_VIP + "/backcapital/countDown?callback=?&lastEntrustId="+lastEntrustId, function(result) {
            if(result.isSuc){
                $this.repoTradesRecordTop(result.datas,lastEntrustId);
            }
        })
        //模拟数据
        // var rate = 120;
        // var lastBackCapital = Math.ceil(Math.random()*100000)/10000
        // var result = {
        //     des: "success",
        //     isSuc: true,
        //     datas:{
        //         frequency:rate,//回购频率
        //         countDown:rate,//服务器倒计时剩余时间(距离下次回购剩余时间)S
        //         lastBackCapital:lastBackCapital,//上次回购资金 
        //         entrusts:[{
        //                         entrustId: 602691, //委托id
        //                         date:new Date(), //时间，单位秒
        //                         totalMoney: lastBackCapital, //回购资金
        //                         amount: Math.ceil(Math.random()*1000), //回购量
        //                         ratio:Math.floor(Math.random()*2),//个人占比，如果与我相关值大于0
        //                      }],
        //         capitals:[
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100,
        //             Math.ceil(Math.random()*50)/100
        //         ]//最近18次回购(倒计时)记录(包含0)
        //     }
        // }
        // if(result.isSuc){
        //     $this.repoTradesRecordTop(result.datas,lastEntrustId);
        // }
    };
    trans.repoTradesRecordTop = function(data,id){
        var $this = this;
        // prepend()

        var entrusts = data.entrusts;
        if(entrusts.length>=1){
            $this.repoEntrustsHtml(data,"top");
            $this.repoRatioHtml(data,"top");
        }
        //倒计时
        var frequency = data.frequency;// Repo 频率 N 秒
        var countDown = data.countDown;
        var lastBackCapital = new Big(data.lastBackCapital).toFixed(exchangeBixDian);
        var capitals = data.capitals;
        var circleBox = document.querySelector("#circleBox");
        var circle = circleBox.querySelectorAll("circle")[1];
        $("#repo-rate").text(frequency);
        var timeText = $("#circleBox em")
        $("#last-repo").html(lastBackCapital);
        var capitalsHtml = "";
        var maxTrade = 0;
        for(var i = capitals.length-1;i>=0;i--){
            if(capitals[i]>maxTrade){
                maxTrade = capitals[i];
            }
        }
        for(var i = capitals.length-1;i>=0;i--){
            var height = capitals[i]/maxTrade*100;
            var arryCapitalsOpen = String(capitals[i]).split(".");
            // console.log(arryCapitalsOpen);
            var textCapitals = arryCapitalsOpen[1]&&arryCapitalsOpen[1].length>=exchangeBixDian?new Big(capitals[i]).toFixed(exchangeBixDian):capitals[i];
            capitalsHtml+='<div class="pillar"><span style="height:'+height+'%"><em>'+textCapitals+'</em></span></div>'
        }
        $("#histogram_box").html(capitalsHtml);
        var thisTime =  new Date();
        var setTime = setInterval(function(){
            var timeN = new Date();
            var timeCha = (timeN - thisTime)/1000;
            // thisTime = timeN;
            var timeO = countDown-timeCha;
            // countDown = timeO;
            if(timeO<=0){
                countDown = frequency;
                // console.log("setTime++++++++++++++++++++++++")
                clearInterval(setTime);
                $this.getRepoTopData(entrusts.length>0?entrusts[0].entrustId:id);
            }
            if(timeO<0){timeO=frequency}
            var percent = timeO / frequency;
            var fen = Math.floor(timeO/60) ,miao=Math.ceil(timeO%60);
            if(miao<10){
                timeText.text(fen+":0"+miao);
            }else{
                timeText.text(fen+":"+miao);
            }
            var perimeter = Math.PI * 2 * 32;
            circle.setAttribute('stroke-dasharray', perimeter * percent + " " + perimeter * (1- percent));
            
        },20)
    };
    trans.repoEntrustsHtml = function(dataOrg,type,fun){
        var $this = this;
        var steation = type?type:"bottom";
        var fullTotalMoney = 10;
        var entrustsHtml = "";
        var data = dataOrg.entrusts;
        var className = "";
        Big.RM = 0;
        if(steation=="top"){
            className = "new_repo";
        }
        for(var em =0;em<data.length;em++){
            var persent = (data[em].totalMoney/fullTotalMoney)*100;
            entrustsHtml+='<tr id="'+data[em].entrustId+'" class="'+className+'">'+
                                '<td>'+M.formatDate(data[em].date, "hh:mm:ss")+'</td>'+
                                '<td><div><span style="width:'+persent+'%"></span></div></td>'+
                                '<td>'+new Big(data[em].totalMoney).toFixed(exchangeBixDian)+' <i>USDT</i></td>'+
                                '<td><i class="iconfont"> &#xe634; </i></td>'+
                                '<td>'+new Big(data[em].amount).toFixed(numberBixDian)+' <i>GBC</i></td>'+
                            '</tr>'
        }
        if(steation=="top"){
            $("#repoList").prepend(entrustsHtml);
        }else {
            if(dataOrg.hasMore == 0){
                console.log("hasMore:0++++++++++++++++++++++")
                $("#repo_cover .moretwenty").hide();
            }
            $("#repoList").append(entrustsHtml);
        }
        if(fun){fun()}
    };
    trans.repoRatioHtml = function(dataOrg,type,fun){
        var $this = this;
        var steation = type?type:"bottom";
        var fullTotalMoney = 10;
        var ratioHtml="";
        var data = dataOrg.entrusts;
        var className = "";
        Big.RM = 0;
        if(steation=="top"){
            className = "new_repo";
        }
        for(var em = 0;em<data.length;em++){
            if(data[em].ratio>0||steation!="top"){
                var persent = (data[em].totalMoney/fullTotalMoney)*100;
                ratioHtml+='<tr  id="'+data[em].entrustId+'" class="'+className+'">'+
                                '<td>'+M.formatDate(data[em].date, "yyyy-MM-dd")+'</td>'+
                                '<td><div><span style="width:'+persent+'%"></span></div></td>'+
                                '<td>'+new Big(data[em].totalMoney).toFixed(exchangeBixDian)+' <i>USDT</i></td>'+
                                '<td><i class="iconfont">&#xe634;</i></td>'+
                                '<td>'+new Big(data[em].amount).toFixed(numberBixDian)+' <i>GBC</i></td>'+
                            '</tr>'
            }
            if(data[em].ratio>0){
                $(document).find("#noRatioList").hide();
            }
        }
        if(steation=="top"){
            $("#relatedList").prepend(ratioHtml);
        }else{
            if(dataOrg.hasMore==0){
                $("#related_cover .moretwenty").hide();
            }
            $("#relatedList").append(ratioHtml);
        }
        if(fun){fun()}
    };
    trans.getRepoDetail = function(id,callback){
        var $this = this;
        $.getJSON(DOMAIN_VIP + "/backcapital/getEntrustById?callback=?&entrustId="+id, function(result) {
            if(result.isSuc){
                callback(result.datas);
            }
        })
        //模拟数据
        // var totalMoney = Math.ceil(Math.random()*100000)/10000
        // var result = {
        //                 des: "success",
        //                 isSuc: true,
        //                 datas: {
        //                     entrustId:Math.ceil(Math.random()*100000) , //委托id
        //                     date: new Date(), //时间，单位秒
        //                     totalMoney: totalMoney, //回购资金
        //                     amount: Math.ceil(Math.random()*100), //回购量
        //                     transRecords:[
        //                         {
        //                             date: 1510621860, //时间，单位秒
        //                             price: 0.0000862, //价格
        //                             amount: 36, //数量
        //                             totalMoney: 0.0001 //总量
        //                         },
        //                         {
        //                             date: 1510521860, //时间，单位秒
        //                             price: 0.000628, //价格
        //                             amount: 63, //数量
        //                             totalMoney: 0.0031 //总量
        //                         }
        //                     ],
        //                     //回购资金构成
        //                     capitals:[
        //                         {
        //                             date: 1510821860000, //时间，单位秒
        //                             ratio: 0.01, //个人占比
        //                             transRecordId:21678, //交易ID
        //                             market: "ETC/BTC", //市场
        //                             fee: 0.0014 //手续费
        //                         },
        //                         {
        //                             date: 1510621860000, //时间，单位秒
        //                             ratio: Math.ceil(Math.random()*100)/100, //个人占比
        //                             transRecordId:123345, //交易ID
        //                             market: "BTC/USDT", //市场
        //                             fee: 0.0004 //手续费
        //                         },
        //                         {
        //                             date: 1510421860000, //时间，单位秒
        //                             ratio:  Math.ceil(Math.random()*100)/100, //个人占比
        //                             transRecordId:54321, //交易ID
        //                             market: "LTC/BTC", //市场
        //                             fee: 0.0001 //手续费
        //                         }
        //                     ]
        //                 }
        //             }
        // callback(result.datas);
    };
    trans.returnPiebuySvg = function(ratio,r){
        var ru = r?r:3;
        var perimeter = Math.PI * 2 * ru;
        Big.RM = 0;
        var persentString = new Big(ratio).times(100);
        if(persentString<0.1){
            persentString = "<0.1";
        }else{
            persentString = new Big(persentString).toFixed(1);
        }
        var svgH = '<div class="svg_box">'+
                        '<svg width="20" height="20" viewbox="0 0 20 20">'+
                            '<circle cx="10" cy="10" r="3" stroke-width="6" stroke="#EAEAEA" fill="none"></circle>'+
                            '<circle cx="10" cy="10" r="3" stroke-width="6" stroke="#0084D3" fill="none" transform="rotate(-90,10 10)" stroke-dasharray="'+(ratio*perimeter)+' '+((1-ratio)*perimeter)+'"></circle>'+
                        '</svg>'+
                        '<em>'+persentString+'%</em>'+
                    '</div>'
        return svgH
    };
    trans.showRepoDetail=function(data){
        var $this = this;
        var Id = data.entrustId;
        var repoDetailDom = $("#repoDetail")
        repoDetailDom.find(".repo-id b").text(Id);
        var repoDate = repoDetailDom.find(".repo-date");
        repoDate.find("em").text(M.formatDate(data.date, "hh:mm:ss"));
        repoDate.find("b").text(M.formatDate(data.date, "yyyy-MM-dd"));
        var repoAmount = repoDetailDom.find(".content .repo-amount");
        Big.RM = 0;
        repoAmount.text(new Big(data.amount).toFixed(numberBixDian)+" GBC")
        var repoTotalMoney = repoDetailDom.find(".content .repo-total-money");
        repoTotalMoney.text(new Big(data.totalMoney).toFixed(exchangeBixDian)+" USDT");
        var repoRecordInfoHtml = "";
        var repoRecordInfo = $("#repoRecordInfo");
        var transRecords = data.transRecords;
        if(transRecords.length > 0){
            for(var m = 0;m < transRecords.length;m++){
                var item = transRecords[m];
                repoRecordInfoHtml+='<tr>'+
                                        '<td>'+M.formatDate(item.date, "yyyy-MM-dd hh:mm:ss")+'</td>'+
                                        '<td>'+new Big(item.price).toFixed(exchangeBixDian)+' USDT</td>'+
                                        '<td>'+new Big(item.amount).toFixed(numberBixDian)+' GBC</td>'+
                                        '<td>'+new Big(item.totalMoney).toFixed(exchangeBixDian)+' USDT</td>'+
                                    '</tr>';
            } 
        }
        repoRecordInfo.html(repoRecordInfoHtml);
        var repoRecordDetailHtml = "";
        var repoRecordDetail = $("#repoRecordDetail");
        var capitals = data.capitals;
        if(capitals.length > 0){
            for(var n = 0;n<capitals.length;n++){
                var iten = capitals[n];
                var classer="";
                if(iten.relatedMe && iten.relatedMe > 0){
                    classer = "blue";
                }
                repoRecordDetailHtml+='<tr class="'+classer+'">'+
                                            '<td>'+M.formatDate(iten.date, "yyyy-MM-dd hh:mm:ss")+'</td>'+
                                            '<td>'+
                                                '<div class="svg_box">'+
                                                    $this.returnPiebuySvg(iten.ratio,3)+
                                                '</div>'+
                                            '</td>'+
                                            '<td>'+iten.transRecordId+'</td>'+
                                            '<td>'+iten.market+'</td>'+
                                            '<td>'+new Big(iten.fee).toFixed(exchangeBixDian)+' USDT</td>'+
                                        '</tr>';
            }
        }
        repoRecordDetail.html(repoRecordDetailHtml);
        JuaBox.info(repoDetailDom.html(), { 
            title: bitbank.L("回购记录"), 
            width: 700,
            btnNum: 0,
            showClose:true,
            endFun:function(){
                var windHeight = $(window).height();
                var resultHeight = new Big(windHeight).minus(120);
                $(".bk-repo").parent().parent().parent().css({
                    maxHeight:resultHeight+"px",
                    overflowY:"scroll"
                });
            }
        });
    };
    /**
     * getRepoTradesRecord  end
     */

    // new entrust paper
    var usdtArr=[],btcArr=[],mType='',mNormType='USDT',mFinds=-1,includeCancel=0,timeType=0;
    trans.getEntrustInit = function() {
        var $this = this,
            pageIndex = 1;
        $this.getEntrustCoinList(pageIndex);
        $this.optionSwitch();
        $this.standardSelect();
        var sessionMarket = sessionStorage.getItem("market");
        if(sessionMarket){
            var sessionMarketArr = sessionMarket.split("_");
            mType = sessionMarketArr[0].toUpperCase();
            mNormType = sessionMarketArr[1].toUpperCase();
            $("#entrustMarketCoin .select-title").html(mType);
            $("#mNormTypeCon").html(mNormType)
        }

        $(".bk-entrust").on("click", ".detailEntrust", function() {
            $this.getEntrustDetail(
                $(this).data("id"),
                $(this).data("types"),
                $(this).data("numbers"),
                function() {
                    JuaBox.info($("#tradeList").html(), { title: bitbank.L("委托详情"), width: 600, btnNum: 1 });
                }
            );
        });
    }
    //normCoin -> click
    trans.standardSelect = function(){
        var self = this,
            preNum = 0;
        $('#normCoinTab > dd').on('click',function(){
            var subHtml='',
                hasType = false,
                thisIndex = $(this).index(),
                $subNormCoin = $('#entrustMarketCoin > a > span');
            if(preNum != thisIndex){
               var nowArr = thisIndex == 0? usdtArr : btcArr;
               for(var k=0;k<nowArr.length;k++){
                   subHtml += '<dd>'+nowArr[k]+'</dd>';
                   if($subNormCoin.html() == nowArr[k]){
                        hasType = true;
                   }
               }
               if(!hasType){
                 $subNormCoin.html(nowArr[0])
                 mType = nowArr[0];
               }
               preNum = thisIndex;
               $('#entrustMarketCoin > dl').html(subHtml);
            }
        })
    }

    //dd->option  click
    trans.optionSwitch = function(){
        var self = this;
        $(".beaSelect").on('click',function(e){
            e.stopPropagation();
            var $this = $(this)
            var selectType = $this.data("type")
            var $options = $(this).find('dl');
            var $selectVal = $(this).find('li > a > span');
            $('.beaShow').not($options).hide().removeClass("beaShow");
            if(!$options.hasClass("beaShow")){
                $options.addClass("beaShow").slideDown("fast");
            }else{
                $options.removeClass("beaShow").hide();
            }
            $options.find('dd').on('click',function(e){
                e.stopPropagation();
                if($selectVal.html() != $(this).html()){
                    $selectVal.html($(this).html());
                    if(selectType == 'mType'){
                        mType = $(this).html();
                        self.getMarket((mType+"_"+mNormType).toLowerCase(),function(){
                            self.getEntrusNewtRecord();
                        });
                    }else if(selectType == 'mNormType'){
                        mNormType = $(this).html()
                        self.getMarket((mType+"_"+mNormType).toLowerCase(),function(){
                            self.getEntrusNewtRecord();
                        });
                    }else{
                        mFinds = $(this).data("value");
                        self.getEntrusNewtRecord();
                    }
                    sessionStorage.getItem("market",mType+"_"+mNormType)
                    // self.getMarket((mType+"_"+mNormType).toLowerCase(),function(){
                    //     self.getEntrusNewtRecord();
                    // });
                }
                $options.removeClass("beaShow").hide();
                
            })
        })
        //checkbox
        $("#undoTrade").on("click",function(){
            var isCheck = $(this).is(":checked")
            includeCancel = isCheck ? 0 : 1;
            self.getEntrusNewtRecord();
        })
        //time
        $(".tab-time > li").on('click',function(){
            $(this).addClass("active").siblings().removeClass("active");
            timeType = $(this).index();
            self.getEntrusNewtRecord();            
        })
    }

    //history entrust record
    trans.getEntrusNewtRecord = function(pageIndex){
        var $this = this,listDiv = "#historyEntrustList";
        var htmlNoLogin = "<tr class='topnone'><td colspan='9' class='botnone'>" + bitbank.L("通用未登录提示") + "</td></tr>";
        var htmlNoRecord = "<tr class='topnone'><td colspan='9' class='botnone'>" + bitbank.L("委托记录提示记录") + "</td></tr>" ;
        var loadingHtml = "<div class='loading'></div>";
        $("#historyEntrustList").append(loadingHtml);
        if (!user.isLogin()) {
            $(listDiv).html(htmlNoLogin);
            $(listDiv + "_Page").html("");
            return false;
        };
        var pageEIndex = pageIndex || 1;
        var url = window.location.pathname;
        if(url.indexOf("/entrust/list") != -1){
            requestUrl = "/Record/getEntrustHistory?jsoncallback=?"
        }else {
            requestUrl = "/Record/getTransRecordHistory?jsoncallback=?"
        }
        market = (mType+"_"+mNormType).toLowerCase();
        $.ajax({
            url:DOMAIN_TRANS+requestUrl,
            type:'POST',
            data:{
                market:market,
                type:mFinds,
                includeCancel:includeCancel,
                timeType:timeType,
                pageNum:pageEIndex
            },
            dataType:'json',
            success:function(res){
                var result = res[0];
                if(result.count > 0){
                    if(url.indexOf("/entrust/list") != -1){
                        $(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatEntrustData(result.records)));                        
                    }else{
                        $(listDiv).html(tmpl("tmpl-"+(listDiv.replace(/[#,.]/g,"")), $this.formatTradedData(result.records)));
                    }
                    $this.showEntrustPage(listDiv,pageEIndex,result.count);                    
                }else{
                    $(listDiv).html(htmlNoRecord);
                    $this.showEntrustPage(listDiv,pageEIndex,result.count); 
                }
            }
        })
    };
    trans.formatEntrustData = function(result){
        var $this = this,
            record = [];
            Big.RM = 0
            console.log("exchangeBixDian:"+exchangeBixDian)
            console.log("numberBixDian:"+numberBixDian)
            
        for(var i=0;i<result.length;i++){
            record[i] = {};
            record[i].date = LANG == 'en'? M.formatDate(result[i].date, "MM-dd-yyyy hh:mm:ss") : M.formatDate(result[i].date, "yyyy-MM-dd hh:mm:ss");
            record[i].typeRes = result[i].type == 1? bitbank.L("买入") : bitbank.L("卖出");
            record[i].type = result[i].type;
            record[i].price = result[i].price;
            record[i].amount = result[i].amount;
            record[i].averagePrice = result[i].completeNumber >0 ?new Big(result[i].completeTotalMoney).div(new Big(result[i].completeNumber)).toFixed(exchangeBixDian): "--";
            record[i].completeNumber = result[i].completeNumber>0?result[i].completeNumber:"--";
            record[i].completeTotalMoney = result[i].completeTotalMoney>0?new Big(result[i].completeTotalMoney).toFixed(exchangeBixDian)+" "+mNormType:"--";
            record[i].statusRes = result[i].status ==1? bitbank.L("已撤销Entrust") : bitbank.L("已完成Entrust");
            record[i].status = result[i].status;            
            record[i].entrustId = result[i].entrustId;
        }
        return record;
    }
    trans.formatTradedData = function(result){
        var $this = this,
            record = [],
            outAmountUnit,
            intAmountUnit;
            Big.RM = 0;
        for(var i=0;i<result.length;i++){
            record[i] = {};
            record[i].date = LANG == 'en'? M.formatDate(result[i].date, "MM-dd-yyyy hh:mm:ss") : M.formatDate(result[i].date, "yyyy-MM-dd hh:mm:ss");
            record[i].typeRes = result[i].type == 1? bitbank.L("买入") : bitbank.L("卖出");
            record[i].type = result[i].type;
            record[i].price = result[i].price+" "+ mNormType;
            record[i].outAmount = result[i].type == 1? new Big(result[i].outAmount).toFixed(exchangeBixDian)+" "+mNormType:new Big(result[i].outAmount).toFixed(numberBixDian)+" "+mType;
            record[i].intAmount = result[i].type == 1? new Big(result[i].intAmount).toFixed(numberBixDian)+" "+mType:new Big(result[i].intAmount).toFixed(exchangeBixDian)+" "+mNormType;
        }
        return record;
    }

    trans.showEntrustPage = function(listDiv,pageIndex,rsCount) {
        var $this = this;
        var $pageDiv = $(listDiv + "_Page"),
            pageSize = 30;
        if (rsCount < pageSize && pageIndex == 1) {
            $pageDiv.html("");
            return false;
        }
        var pageCount = rsCount % pageSize == 0 ? parseInt(rsCount / pageSize) : parseInt(rsCount / pageSize) + 1 ;
        $pageDiv.createPage({
            noPage:false,
            rsCount:rsCount,
            pageSize:pageSize,
            pageCount:pageCount,
            current: pageIndex || 1,
            backFn: function(pageNum) {
                $this.getEntrusNewtRecord(pageNum);
            }
        });
    };
   
    trans.getEntrustCoinList = function() {
        var self = this;
        $.ajax({
            url:DOMAIN_TRANS+'/getMarketRelate',
            type:'GET',
            dataType:'json',
            success:function(res){
                var coinHtml = '';
                if(res.isSuc){
                    var result = res.datas;
                    usdtArr = result.USDT;
                    btcArr = result.BTC;
                    for(var i=0;i<usdtArr.length;i++){
                        coinHtml += '<dd>'+usdtArr[i]+'</dd>';
                    }

                    $('#entrustMarketCoin').find('dl').html(coinHtml);
                    var sessionMarket = sessionStorage.getItem("market");
                    if(sessionMarket){
                        if(mNormType == "BTC"){
                            $('#normCoinTab > dd').eq(1).click().click();
                        }
                    }else{
                        mType = usdtArr[0];
                        $('#entrustMarketCoin .select-title').html(usdtArr[0])
                    }
                    var defaultMarket = (mType+"_"+mNormType).toLowerCase()
                    self.getMarket(defaultMarket,function(){
                        self.getEntrusNewtRecord();
                    });
                    
                }
            }
        })
    };

    module.exports = trans;
    (function() { return this || (0, eval)('this'); }()).TRANS = trans;
});

function formatNumber(num) {
    num = parseFloat(num) / numberBixNormal;
    if (numberBixNormal != numberBixShow)
        return Math.floor(Math.pow(10, numberBixDian) * parseFloat(num));
    else
        return parseFloat(num.toFixed(numberBixDian));
}

function formatNumberUse(num) {
    num = parseFloat(num);
    if (numberBixNormal != numberBixShow) {
        num = Math.floor(num);
        return Math.floor(num * Math.pow(10, numberBixDian));
    } else {
        return Math.floor(accMul(num, numberBixNormal));
    }
}

function accMul(arg1, arg2) {
    var m = 0,
        s1 = arg1.toString(),
        s2 = arg2.toString();
    try { m += s1.split(".")[1].length } catch (e) {}
    try { m += s2.split(".")[1].length } catch (e) {}
    return Number(s1.replace(".", "")) * Number(s2.replace(".", "")) / Math.pow(10, m);
}

function accDiv_old(arg1, arg2) {
    var t1 = 0,
        t2 = 0,
        r1, r2;
    try { t1 = arg1.toString().split(".")[1].length } catch (e) {}
    try { t2 = arg2.toString().split(".")[1].length } catch (e) {}
    with(Math) {
        r1 = Number(arg1.toString().replace(".", ""));
        r2 = Number(arg2.toString().replace(".", ""));
        return (r1 / r2) * pow(10, t2 - t1);
    }
}

function formatMoney(num) {
    num = parseFloat(num) / exchangeBixNormal;
    if (exchangeBixNormal != exchangeBixShow) {
        return Math.floor(Math.pow(10, exchangeBixDian) * parseFloat(num));
    } else {
        return parseFloat(num.toFixed(exchangeBixDian));
    }
}

function formatMoneyUse(num) {
    num = parseFloat(num);
    if (exchangeBixNormal != exchangeBixShow) {
        num = Math.floor(num);
        return Math.floor(num * Math.pow(10, exchangeBixDian));
    } else {
        return Math.floor(accMul(num, exchangeBixNormal));
    }
}
Date.prototype.format = function(format) {
    var o = {
        "M+": this.getMonth() + 1,
        "d+": this.getDate(),
        "h+": this.getHours(),
        "m+": this.getMinutes(),
        "s+": this.getSeconds(),
        "q+": Math.floor((this.getMonth() + 3) / 3),
        "S": this.getMilliseconds()
    }
    if (/(y+)/.test(format)) {
        format = format.replace(RegExp.$1, (this.getFullYear() + "").substr(4 - RegExp.$1.length));
    }
    for (var k in o) {
        if (new RegExp("(" + k + ")").test(format)) {
            format = format.replace(RegExp.$1, RegExp.$1.length == 1 ? o[k] : ("00" + o[k]).substr(("" + o[k]).length));
        }
    }
    return format;
}