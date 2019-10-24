const BigNumber = require('big.js');

export const DepthConfig = (props,chartHeight) => {
    const { data ,currentMarket,marketsConf,intl,skin } = props
    BigNumber.RM = 0;
    let exchangeBixDian = 6;
    let numberBixDian = 4;
    if(marketsConf[currentMarket]){
       exchangeBixDian = marketsConf[currentMarket].exchangeBixDian ? marketsConf[currentMarket].exchangeBixDian : exchangeBixDian;
       numberBixDian = marketsConf[currentMarket].numberBixDian;
    }
    let AmountString =intl.formatMessage({id: 'Amount'});
    let TotalString = intl.formatMessage({id: 'Total'});
    let buyData = [];
    let sellData = [];
    if(data){
        buyData = data.listDown;
        sellData = data.listUp;
    }
    let bgColor,color1
    if(skin=='dark'){
        color1 = '#3D4454'
        bgColor = '#2F3542'
    }else{
        color1 = '#f9f9fc'
        bgColor = '#FFFFFF'
    }

    return {
        title: {
            text: ""
        },
        credits: {
            enabled: false
        },
        chart: {
            type: "area",
            height:chartHeight
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
            lineColor:"rgb(115, 122, 141)",
            gridLineWidth: 0,
            tickColor:"rgb(115, 122, 141)",
            labels:{
                style:{
                    color:"rgb(115, 122, 141)"
                }
            },
            crosshair:{
                color:"rgb(115, 122, 141)"
            }
        },
        yAxis: {
            title: {
                text: ''
            },
            gridLineColor:color1,
            gridLineWidth: 1,
            labels:{
                style:{
                    color:"rgb(115, 122, 141)"
                }
            },
            crosshair:{
                color:"rgb(115, 122, 141)"
            }
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
                
                return "<div class='tooltip_box' id='tooltip_box' style='border-left-color:"+this.series.name+"'>"+
                "<h5>"+
            // bitbank.L("价格") + ": " +
                new BigNumber(this.x).toFixed(exchangeBixDian) +" "+ currentMarket.split("_")[1].toUpperCase()+" </h5>"+
                "<div class='clearfix'><p>" +AmountString + ":<br/>" + new BigNumber(this.y).toFixed(numberBixDian) + " "+currentMarket.split("_")[0].toUpperCase()+"</p>"+
                "<p>"+TotalString + ":<br/>" + new BigNumber(thisZ).toFixed(exchangeBixDian) +" "+ currentMarket.split("_")[1].toUpperCase()+" </p>"+
                "</div></div>";
            },
            backgroundColor:"#2F3542",
            shadow:true,
            borderColor: "#737A8D",
            borderWidth: 0,
            borderRadius: 0,
            style: {
                color: "#737A8D"
            }
        },
        series: [{
            animation: false,
            lineColor: "#737A8D",
            lineWidth: 1,
            marker: {
                symbol: 'circle',
                enabled: false,
                lineColor:"#737A8D",
            },
            name: "#2BB38A",//bitbank.L("买单")
            data: buyData,
            color: "#82d8be"
        }, {
            animation: false,
            lineColor: "#737A8D",
            lineWidth: 1,
            marker: {
                symbol: 'circle',
                enabled: false,
                lineColor:"#737A8D",
            },
            name: "#E34B51",//bitbank.L("卖单")
            data: sellData,
            color: "#ea9a9e"
        }]
    }
}


























