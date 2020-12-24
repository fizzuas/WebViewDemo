var s = 0;
var length = 0;
function slide() {
  
  console.log("slidelift "+length);
  var but = document.getElementById("vcode-slide-button250");
  var cover = document.getElementById("vcode-slide-cover250");
  length += 1;
//  but.style.left = length;
//  cover.style.left = length;
  if (length > 207) {
    console.log("clearInterval");
    clearInterval(s);
  }
}
function slideLeft() {

  s = setInterval(slide, 10);
}

function check() {
  setTimeout(slideLeft, 5000);
}

console.log("check");
check();
window.java_obj.saveLog(logBack);