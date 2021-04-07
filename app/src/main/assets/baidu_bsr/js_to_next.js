var targetEleA = null;
var curPage = 1;
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



function clickNextNode(nextNode) {
    return function() {
           window.java_obj.clickNextPage();
        console.log("点击下一页");
        nextNode.click();
    }
    ;
}

function clickTargetEleA(targetA) {
    return function() {
        console.log("点击目标网站");
        window.java_obj.pinIndex(pinPage, pinIndex);
        console.log("添加别针"+pinPage+"页"+pinIndex+"项");
        targetA.click();
    }
}

function clickToTarget() {
    return function() {
        targetEleA.scrollIntoView({
            behavior: "smooth"
        });
        setTimeout(clickTargetEleA(targetEleA), randomNum(300, 1250));
    }
    ;
}
;var logBack = "";
function finish() {
    return function() {
        console.log("一次关键词遍历循环结束" + page_max);
        window.java_obj.saveLog(logBack);
        window.java_obj.nextKeyword();
    }
    ;
}

function getContent(sites) {
    var content = "[";
    for (var j = 0; j < sites.length; j++) {
        content = content + sites[j] + ",";
    }
    content = content + "]";
    return content
}

function dealPageOne() {
    if(pinPage<=1){
        curPage = 1;
        window.java_obj.pageIndex(curPage);
    }
    console.log("-----------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage);
    /* 结果 第一页--》第二页 ，打印第一页 */
    logBack = logBack + "第一页:" + "\t(" + curTime + ")";
    var scTitles = document.getElementsByClassName("ec_title");
    logBack = logBack + " \n(广告总共" + scTitles.length + "项)";
    /*  for (i = 0; i < scTitles.length; i++) {
        */
    /* 获取 ad 的title、href */
    /*
        var href = scTitles[i].href;
        var site = scTitles[i].parentElement.getElementsByClassName("ec_site")[0]
          .innerText;
        logBack =
          logBack +
          "\n title: \t" +
          scTitles[i].innerText +
          "\t href:\t" +
          href.substring(0, 50) +
          "\t site:\t" +
          site;
        console.log(
          "\n title: \t" +
            scTitles[i].innerText +
            "\t href:\t" +
            href.substring(0, 50) +
            "\t site:\t" +
            site
        );
      }*/
    var resultTitles = document.getElementsByClassName("result_title");
    logBack = logBack + "\n\n(result总共" + resultTitles.length + "项)";
    var start = 0;
    if (curPage == pinPage) {
        start = pinIndex + 1;
        /*window.java_obj.pinIndex(0,0);*/
    } else {
        start = 0;
    }

    /* 模拟滑动 */
    var time = randomNum(300, 1250);
    for (i = 0; i < scTitles.length; i++) {
        setTimeout(scrollTo(scTitles[i]), time);
        time = time + randomNum(300, 1250);
    }


    /*匹配*/
    for (i = start; i < resultTitles.length; i++) {
        /* 获取 ad 的title、href，并打印 */
        var href = resultTitles[i].href;
        var site = resultTitles[i].parentElement.getElementsByClassName("site")[0].innerText;
        logBack = logBack + "\n title: \t" + resultTitles[i].innerText + "\t href:\t" + href.substring(0, 50) + "\t site:\t" + site;

        console.log("第" + curPage + "页" + i + "项,site=" + site+",  匹配池:"+getContent(targetSites));

        if (scTitles[i] != null) {
            setTimeout(scrollTo(scTitles[i]), time);
            time = time + randomNum(300, 1250);
        }

        for (let k in targetSites) {
            var m = site.search(targetSites[k]);
            var n= targetSites[k].search(site);
            if ((m != -1 ||n!=-1)&& (curPage >= pinPage) && (i > pinIndex)) {
                console.log("\t 发现第" + i + "项 site=" + site + "匹配到匹配池中" + targetSites[k]+"\t 匹配池"+getContent(targetSites));

                targetEleA = resultTitles[i];
                pinIndex = i;
                pinPage = curPage;

                time += randomNum(300, 1250);
                setTimeout(clickToTarget(), time);
                return;

            }
        }
    }

    /* 滑到nextonly 点击 */
    var nextOnlys = document.getElementsByClassName("nextOnly");
    if (nextOnlys.length == 1) {
        /*  跳转到下一页前logBack会更新 */
        window.java_obj.saveLog(logBack);
        setTimeout(scrollTo(nextOnlys[0]), time);
        time += randomNum(300, 1250);
        setTimeout(clickNextNode(nextOnlys[0]), time);
    } else {
        console.log("找不到下一页节点");
        /*结果第一页找不到关键词结果情形 或者 中间页*/
        var bnNodes = document.getElementsByClassName("bn");
        if (bnNodes.length > 0) {
            console.log("百度最后一页，没有下一页节点");
            window.java_obj.nextKeyword();
        } else {
            /*非结果页，可能是中间跳转页*/
            console.log("可能是百度搜索中间跳转页");
        }
    }

}

function dealPageNext() {
    /* 第2..N页--》第3..+ N+1页 ，打印第2..页 */
    var pageInfo = pageNo[0].innerText;
    if (pageInfo.length >= 3) {
        curPage = parseInt(pageInfo.substring(1, pageInfo.length - 1));
        console.log("-----------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage + "------curPage:" + curPage);
        window.java_obj.pageIndex(curPage);
    }

    logBack = logBack + "\n\n" + pageNo[0].innerText + ":" + "\t(" + curTime + ")";
    var rssultTitles = document.getElementsByClassName("result_title");
    logBack = logBack + "\n\n(result总共" + rssultTitles.length + "项)";
    console.log("\n\n" + pageNo[0].innerText + ":");
    var start = 0;
    if (curPage == pinPage) {
        start = pinIndex + 1;
    } else {
        start = 0;
    }

    var time = randomNum(300, 1250);
    /*匹配*/
    for (i = start; i < rssultTitles.length; i++) {
        /* 获取 ad 的title、href */
        var href = rssultTitles[i].href;
        var site = rssultTitles[i].parentElement.getElementsByClassName("site")[0].innerText;
        logBack = logBack + "\n title: \t" + rssultTitles[i].innerText + "\t href:\t" + href.substring(0, 50) + "\t site:\t" + site;
        console.log("第" + curPage + "页" + i + "项,site=" + site+"\t 匹配池:"+getContent(targetSites));

        setTimeout(scrollTo(rssultTitles[i]), time);
        time = time + randomNum(300, 1250);

        for (let k in targetSites) {
            var m = site.search(targetSites[k]);
            var n= targetSites[k].search(site);
            if ((m != -1 || n!=-1)&& curPage >= pinPage && i > pinIndex) {
                console.log("\t 发现第" + i + "项 site=" + site + "匹配到匹配池" +getContent(targetSites));
                targetEleA = rssultTitles[i];
                pinIndex = i;
                pinPage = curPage;

                time += randomNum(300, 1250);
                setTimeout(clickToTarget(), time);
                return;

            }
        }
    }
    /* 滑到nextonly 点击 */
    var nextOnlys = document.getElementsByClassName("next");
    if (nextOnlys.length == 1) {
        setTimeout(scrollTo(nextOnlys[0]), time);
        if (pageNo[0].innerText == "第" + page_max + "页") {
            /* 当前页="第页" 就不跳转了 */
            time += 50;
            setTimeout(finish(), time);
        } else {
            /* 跳转 */
            window.java_obj.saveLog(logBack);
            time += randomNum(300, 1250);
            setTimeout(clickNextNode(nextOnlys[0]), time);
        }
    } else {
               console.log("找不到下一页节点");
                /*结果第一页找不到关键词结果情形 或者 中间页*/
                var bnNodes = document.getElementsByClassName("bn");
                if (bnNodes.length > 0) {
                    console.log("百度最后一页，没有下一页节点");
                    window.java_obj.nextKeyword();
                } else {
                    /*非结果页，可能是中间跳转页*/
                    console.log("可能是百度搜索中间跳转页");
                }
    }

}

var curTime;
var pageNo;
function dealPage() {
    curTime = new Date().toLocaleString();
    pageNo = document.getElementsByClassName("pageNo");

    if (pageNo.length == 0) {
        dealPageOne();
    } else {
        dealPageNext();
    }

}

dealPage();
