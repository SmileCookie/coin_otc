var Box = function(ctx,img){
    this.id = 0;
    this.ctx = ctx;
    this.x = 0;
    this.y = 0;
    this.w = 0;
    this.h = 0;     
    this.vx = 1;    //x轴移动速度
    this.img = img  //要绘制的对象
    this.isShow = false; //是否绘制
}
Box.prototype.update = function() {
        this.x += this.vx
}
Box.prototype.draw = function(){
    if(this.isShow){
        this.ctx.drawImage(this.img,this.x,this.y,this.img.width,this.img.height);
    }
}
