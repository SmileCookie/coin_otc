function singleNum(num){
    let sum = null;
    for(let i = 0;i<num.length;i++){
        sum ^= num[i]
    }
    return sum
}


console.log(singleNum([1,1,2,2,3,3,6,5,5]))