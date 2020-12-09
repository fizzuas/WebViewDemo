function toTop(target) {
  return function () {
    target.scrollIntoView({
      behavior: "smooth",
      block: "start"
    });
  };

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

var time=1500;
for(i=0;i<divs.length;i++){
  setTimeout(toTop(divs[i]),time);
  time+=1000;
   if(i==divs.length-1){
    setTimeout(finish,time);
   }
}

