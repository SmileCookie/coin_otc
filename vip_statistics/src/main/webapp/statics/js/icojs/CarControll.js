var CarControll = function(){
    this.isReady = false;
    this.canvas = null;
    this.ctx = null;
    var domain = GLOBAL.DOMAIN_STATIC;
    this.res = [domain+"/statics/img/icoimgs/erweima1qun.png", domain+"/statics/img/icoimgs/car_bg1.png", domain+"/statics/img/icoimgs/car1.png", domain+"/statics/img/icoimgs/car2.png"]
    this.RES = []

    //静态数据
    this.Data = {
        car1: {//小车1的初始坐标
            x: 190,
            y: 170,
            vx:2,
            vy:0,
            minX:205,//能够行驶的最小x坐标
            maxX:470 //能够行驶的最大x坐标 553是路面右侧离着画布左侧的位置
        },
        car2: {//小车2的初始坐标
            x: 670,
            y: 213,
            vx:0,
            vy:2,
            minX:205,//能够行驶的最小x坐标
            maxX:710, //能够行驶的最大x坐标 553是路面右侧离着画布左侧的位置
            maxY:345
        },
        mask:{
            x:760,
            y:98,
            w:110,
            h:80,
            lineH:80,
            lineTo:760
        }
    }

    this.needLoop = [];

    this.init();
}

CarControll.prototype.init = function(){
    this.canvas = document.getElementById("myCanvas");
    this.ctx = this.canvas.getContext('2d');
}

//加载图片资源
CarControll.prototype.initRes = function(callback) {

    var count = 0; //加载完成图片的数量
    var _self = this

    for (var i = 0; i < this.res.length; i++) {
        this.RES[i] = new Image();
        this.RES[i].src = this.res[i];
        this.RES[i].onload = function () {
            count++;
            
            if (count == _self.res.length){
                _self.isReady = true;
                _self.start(callback);
            } 
        }
    }

}

CarControll.prototype.start = function(callback) {

    this.initCar();
    callback();

}


//初始化地图背景
CarControll.prototype.drawMap = function() {
    this.ctx.drawImage(this.getRes('car_bg1'), 0, 0, this.canvas.width, this.canvas.height);
}

//初始化小车
CarControll.prototype.initCar = function() {
    var car1 = new Car(this.ctx, this.getRes('car1'));
    car1.id = 1
    car1.x = this.Data.car1.x
    car1.y = this.Data.car1.y
    car1.vx = this.Data.car1.vx;
    car1.vy = this.Data.car1.vy;
    car1.isShow = true;
    this.needLoop.push(car1);

    var car2 = new Car(this.ctx, this.getRes('car2'));
    car2.id = 2
    car2.x = this.Data.car2.x
    car2.y = this.Data.car2.y
    car2.vx = this.Data.car2.vx;
    car2.vy = this.Data.car2.vy;
    this.needLoop.push(car2);
}

//draw小车
CarControll.prototype.drawCar = function() {
    for (i in this.needLoop) {
        this.needLoop[i].draw();
    }
}
//更新小车
CarControll.prototype.update = function () {
    var car1 = this.needLoop[0];
    var car2 = this.needLoop[1];

    this.ctx.beginPath();
    this.ctx.lineWidth= this.Data.mask.lineH;
    this.ctx.strokeStyle="#61A2D7"; // 红色路径
    this.ctx.moveTo(this.Data.mask.x + this.Data.mask.w, this.Data.mask.y+40);
    this.ctx.lineTo(this.Data.mask.lineTo,this.Data.mask.y+40);
    this.ctx.stroke(); // 进行绘制
    
    var lineIsOver = this.Data.mask.lineTo  < this.Data.mask.x + this.Data.mask.w
    if(lineIsOver){
        car1.update();
        if(car1.x >= this.Data.car1.maxX || car1.x<=this.Data.car1.minx){
            car1.isShow = false;
            this.Data.mask.lineTo++;
        }
    }else{
        car2.isShow = true;
        car2.update();
        if(car2.x > this.Data.car2.maxX || car2.x < this.Data.car2.minX){
            this.needLoop = []
            this.initCar()
            this.Data.mask.lineTo = this.Data.mask.x;
        }else{
            if(car2.y > this.Data.car2.maxY){
                car2.vy = 0;
                car2.vx = -2;
            }
        }
    }
}

//根据名字去资源
CarControll.prototype.getRes = function (name) {
    for (i in this.RES) {
        if (this.RES[i].src.indexOf(name) != -1) {
            return this.RES[i]
        }
    }
}