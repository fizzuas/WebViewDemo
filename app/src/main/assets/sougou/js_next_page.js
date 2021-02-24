/*
<div class="results">
    <div  class="vrResult " id="sogou_vr_30010097_1" >



<div class="results" data-page="2">
    <div class="vrResult" id="sogou_vr_30000000_2" >
      <a class="resultLink" href=, id="sogou_vr_30000000_12" > </a>

      <div class="citeurl"><span>www.yaoshiji.cn</span>  </div>

*/

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
    return function() {
        target.scrollIntoView({
            behavior: "smooth"
        });
    }
    ;
}

function clickHref(targetA) {
    return function() {
        targetA.click();
    }
    ;
}

function logNext() {
    console.log("nodeNext.pagenumpara=" + nodeNext.getAttribute("pagenumpara"));
}

var count=0;
var waitTime=500;
function checkLoadNext() {
    var nodeNext = document.getElementById("ajax_next_page");
    var pn = parseInt(nodeNext.getAttribute("pagenumpara"));
    /*console.log("下一页的标签值：pn=" + pn + ",mPageNum=" + mPageNum);*/
    if ((parseInt(pn)-1) > mPageNum) {
        console.log((parseInt(pn)-1)+"页加载完成" );
         count=0;
        if ((parseInt(pn)-1) > 40) {
            console.log("前40页分析完毕");
        } else {
            dealPage();
        }
    } else {
        if(count<5){
          count++;
          console.log((parseInt(pn)-1)+"页加载 未完成，已等待"+((count+1)*waitTime)+"ms");
          setTimeout(checkLoadNext, waitTime);
        }else{
          console.log("已用"+((1+count)*waitTime)+"ms用于加载下一页，超时");
        }
    }
}
var mPageNum = 0;

/* 分析页面->匹配元素->滑动到底部点击下一页， 循环40次*/
function dealPage() {
    var nodeNext = document.getElementById("ajax_next_page");
    mPageNum = parseInt(nodeNext.getAttribute("pagenumpara"))-1;
    console.log("正在分析页面=" + mPageNum);

    /*分析mPageNum页，并匹配 */
    var results=document.getElementsByClassName("results");
    for(var i=0;i<results.length;i++){
       var data_page=results[i].getAttribute("data-page");
       if(parseInt(data_page)==mPageNum){
        var vrRsults=results[i].getElementsByClassName("vrResult");


       }
    }


    var time = randomNum(300, 1250);
    /*滑倒底部*/
    setTimeout(scrollTo(nodeNext), time);

    /*点击下一页*/
    time += randomNum(500, 1200);
    setTimeout(clickHref(nodeNext), time);

    /*判断有没有加载新的页面*/
    time += waitTime;
    setTimeout(checkLoadNext, time);

}
dealPage();
