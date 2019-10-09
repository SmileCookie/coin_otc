var BoxControll = function () {
    this.isReady = false;
    this.canvas = null;
    this.ctx = null;
     var domain = GLOBAL.DOMAIN_STATIC;
    this.res = [domain+"/statics/img/icoimgs/box1.png", domain+"/statics/img/icoimgs/box2.png", domain+"/statics/img/icoimgs/box_bg0.png", domain+"/statics/img/icoimgs/box_bg1.png", domain+"/statics/img/icoimgs/box_bg2.png"]
    this.RES = [],
        this.isHaveCenter = false,
        this.mapImg = null,
        this.timer = 0

    //静态数据
    this.Data = {
        minX: 0,
        maxX: 290,
        min2X: 420,
        max2X: 713,
        boxWidth: 60,
        box1: { //小车1的初始坐标
            x: 35,
            y: 92
        },
        box2: { //小车2的初始坐标
            x: 180,
            y: 92
        },
        box3: { //小车2的初始坐标
            x: 325,
            y: 92
        },
        box4: { //小车2的初始坐标
            x: 470,
            y: 92
        },
        box5: { //小车2的初始坐标
            x: 615,
            y: 92
        }

    }

    this.needLoop = [];

    this.init();
}

BoxControll.prototype.init = function () {

    this.canvas = document.getElementById("myCanvas2");
    this.ctx = this.canvas.getContext('2d');

}

//加载图片资源
BoxControll.prototype.initRes = function (callback) {

    var count = 0; //加载完成图片的数量
    var _self = this

    for (var i = 0; i < this.res.length; i++) {
        this.RES[i] = new Image();
        this.RES[i].src = this.res[i];
        this.RES[i].onload = function () {
            count++;
            if (count == _self.res.length) {

                _self.isReady = true;
                _self.start(callback);
            }
        }
    }

}

BoxControll.prototype.start = function (callback) {

    this.initCar();
    callback();

}



//初始化小车
BoxControll.prototype.initCar = function () {
    
    for (var i = 1; i <= 5; i++) {
        var box = new Box(this.ctx, this.getRes('box1'));
        var ind = 'box' + i;
        box.id = 1
        box.x = this.Data[ind].x
        box.y = this.Data[ind].y
        box.isShow = true;
        this.needLoop.push(box);
    }

}


//初始化地图背景
BoxControll.prototype.drawMap = function () {
    this.ctx.drawImage(this.mapImg, 0, 0, this.canvas.width, this.canvas.height);
}


//draw小车
BoxControll.prototype.drawCar = function () {
    for (i in this.needLoop) {
        this.needLoop[i].draw();
    }
}
var t = new Date().getTime()
//更新小车
BoxControll.prototype.update = function () {

    if(!this.isHaveCenter){
        for (var i = 0; i < this.needLoop.length; i++) {

            var b = this.needLoop[i];
            if (b.x == this.Data.box3.x) {
                this.isHaveCenter = true;
                t = new Date().getTime();
                break;
            }
        }
    }
    if(this.isHaveCenter){
        var ss = new Date().getTime()
        var dt = ss - t;
        if (this.isHaveCenter) { //有到了中心的box,换背景图

            if (dt >= 0 && dt < 500) {
                this.mapImg = this.getRes('box_bg1')
                return;
            } else if (dt >= 500 && dt < 1000) {
                this.mapImg = this.getRes('box_bg0')
                return;
            } else if (dt >= 1000 && dt <= 1500) {
                this.mapImg = this.getRes('box_bg2')
                return;
            } else {
                this.mapImg = this.getRes('box_bg0')
                this.isHaveCenter = false;
            }
        }

    }
    

    for (var i = 0; i < this.needLoop.length; i++) {

        var b = this.needLoop[i];

        b.update();
        if (b.x > this.Data.minX && b.x < this.Data.maxX) {
            b.isShow = true;
            b.img = this.getRes('box1');
        } else if (b.x + this.Data.boxWidth > this.Data.min2X && b.x < this.Data.max2X) {
            b.isShow = true;
            b.img = this.getRes('box2');
        } else {
            b.isShow = false;
        }

        if (b.x >= this.Data.max2X) {
            b.x = this.Data.minX;
            b.isShow = true;
        }


    }



}

//根据名字去资源
BoxControll.prototype.getRes = function (name) {
    for (i in this.RES) {
        if (this.RES[i].src.indexOf(name) != -1) {
            return this.RES[i]
        }
    }
}