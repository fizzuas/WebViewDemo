function toTop(target) {
  return function () {
    target.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });
  };
}
/*生成从minNum到maxNum的随机数*/
function randomNum(minNum,maxNum){
    switch(arguments.length){
        case 1:
            return parseInt(Math.random()*minNum+1,10);
        break;
        case 2:
            return parseInt(Math.random()*(maxNum-minNum+1)+minNum,10);
        break;
            default:
                return 0;
            break;
    }
}

function toBottom(target) {
  return function () {
    target.scrollIntoView({
      behavior: "smooth",
      block: "end"
    });
  };
}

function finish(){
  window.java_obj.finish();
}

var divs = document.getElementsByTagName("body")[0].getElementsByTagName("div");
console.log("\n divs.length= " + divs.length);
var step = 1;
var time=100;
if (divs.length > 10) {
  step = parseInt(divs.length / 10);
}
console.log("\n step= " + step);
for(i=0;i<divs.length;i=i+step){
  setTimeout(toTop(divs[i]),time);
  time+=randomNum(300,1250);
   if((i+step)>(divs.length-1)){
    setTimeout(finish,time);
   }
}

