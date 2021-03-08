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

var count = 0;
var waitTime = 500;
function checkLoadNext() {
    var nodeNext = document.getElementById("ajax_next_page");
    var pn = parseInt(nodeNext.getAttribute("pagenumpara"));
    /*console.log("下一页的标签值：pn=" + pn + ",mPageNum=" + mPageNum);*/
    if ((parseInt(pn) - 1) > mPageNum) {
        console.log((parseInt(pn) - 1) + "页加载完成");
        count = 0;
        if ((parseInt(pn) - 1) > page_max) {
            console.log("前"+page_max+"页分析完毕");
            window.java_obj.requestFinished();
        } else {
            window.java_obj.requestPageNUM((parseInt(pn) - 1));
            dealPage();
        }
    } else {
        if (count < 5) {
            count++;
            console.log((parseInt(pn) - 1) + "页加载 未完成，已等待" + ((count + 1) * waitTime) + "ms");
            setTimeout(checkLoadNext, waitTime);
        } else {
            console.log("已用" + ((1 + count) * waitTime) + "ms用于加载下一页，超时");
        }
    }
}
var mPageNum = 0;

/* 分析页面->匹配元素->滑动到底部点击下一页， 循环circle_count次*/
function dealPage() {
    var nodeNext = document.getElementById("ajax_next_page");
    if(nodeNext==null){
        window.java_obj.requestFinished();
        return;
    }
    mPageNum = parseInt(nodeNext.getAttribute("pagenumpara")) - 1;
    console.log("正在分析页面=" + mPageNum);

    var time = randomNum(300, 1250);
    /*分析mPageNum页，并匹配 */
    var results = document.getElementsByClassName("results");
    if(results==null||results.length==0){
      window.java_obj.requestFinished();
            return;
    }

    for (var i = 0; i < results.length; i++) {
        var data_page;
        if (results[i].getAttribute("data-page") == null) {
            data_page = 1;
        } else {
            data_page = parseInt(results[i].getAttribute("data-page"));
        }

        if (parseInt(data_page) == mPageNum) {
            console.log("\t 找到当前页results[" + i + "]" + (data_page == mPageNum));
            /*找到当前页*/
            var vrRsults = results[i].getElementsByClassName('vrResult');
            /*找到 当前页 的所有item的 citeurl*/
            for (var j = itemStartIndex; j < vrRsults.length; j++) {
                time+=randomNum(300,1250);
                setTimeout(scrollTo(vrRsults[j]),time);
                var citeurls = vrRsults[j].getElementsByClassName('citeurl');

                if (citeurls.length > 0) {
                    console.log("\t j=" + j + ",citeurl=" + (vrRsults[j].getElementsByClassName('citeurl')[0]).innerText);
                    var citeurlContent = (vrRsults[j].getElementsByClassName('citeurl')[0]).innerText;

                        for (let k in targetSites) {
                            var m=citeurlContent.search(targetSites[k]);
                              if (m!=-1) {
                                 console.log("匹配到" + citeurlContent);
                                 var links = vrRsults[j].getElementsByClassName('resultLink  ');
                                 if (links.length > 0) {
                                          scrollTo(links[0]);
                                          time+=randomNum(300,1250);
                                           setTimeout(clickHref(links[0]), time);
                                           window.java_obj.setItemStartIndex(j+1);
                                           return;
                                 }
                              }
                            }
                }

            }

        }
    }

    itemStartIndex=0;
    /*滑倒底部*/
    time+=randomNum(300,1250);
    setTimeout(scrollTo(nodeNext), time);

    /*点击下一页*/
    time += randomNum(500, 1200);
    setTimeout(clickHref(nodeNext), time);

    /*判断有没有加载新的页面*/
    time += waitTime;
    setTimeout(checkLoadNext, time);

}
dealPage();
