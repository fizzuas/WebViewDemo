var targetEleA = null;
/*生成从minNum到maxNum的随机数*/
function randomNum(minNum, maxNum) {
  switch (arguments.length) {
    case 1:
      return parseInt(Math.random() * minNum + 1, 10);
      break;
    case 2:
      return parseInt(Math.random() * (maxNum - minNum + 1) + minNum, 10);
      break;
    default:
      return 0;
      break;
  }
}
function scrollTo(target) {
  return function () {
    target.scrollIntoView({
      behavior: "smooth"
    });
  };
}

function clickHref(targetA) {
  return function () {
    targetA.click();
  };
}
var time = randomNum(300, 1250);
var nodeNext = document.getElementById("ajax_next_page");
setTimeout(scrollTo(nodeNext), time);
time += randomNum(300, 1250);
setTimeout(clickHref(nodeNext), time);
