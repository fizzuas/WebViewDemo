var targetEleA = null;
var curPage=1;
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
function clickToTarget() {
  return function () {
     targetEleA.scrollIntoView({
      behavior: "smooth"
    });
    setTimeout(clickHref(targetEleA), randomNum(300,1250));
  };
}


var curTime = new Date().toLocaleString();
console.log(curTime);
var logBack = "";
var pageNo = document.getElementsByClassName("pageNo");
if (pageNo.length == 0) {
    curPage=1;
    console.log("curPage="+curPage);
  /* 结果 第一页--》第二页 ，打印第一页 */
  logBack = logBack + "第一页:" + "\t(" + curTime + ")";
  console.log("第一页:");
  var scTitles = document.getElementsByClassName("ec_title");
  logBack = logBack + " \n(广告总共" + scTitles.length + "项)";
  for (i = 0; i < scTitles.length; i++) {
    /* 获取 ad 的title、href */
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
   for (let k in targetSites) {
   var m=site.search(targetSites[k]);
     console.log("\tsearch="+m);
        if (m!=-1) {
          targetEleA = scTitles[i];
        }
      }
  }
  var resultTitles = document.getElementsByClassName("result_title");
  logBack = logBack + "\n\n(result总共" + resultTitles.length + "项)";
  var start=0;
  if(curPage==pinPage){
    start=pinIndex;
     window.java_obj.pinIndex(0,0);
  }else{
    start=0;
  }
  for (i = start; i < resultTitles.length; i++) {
    /* 获取 ad 的title、href，并打印 */
    var href = resultTitles[i].href;
    var site = resultTitles[i].parentElement.getElementsByClassName("site")[0]
      .innerText;
    logBack =
      logBack +
      "\n title: \t" +
      resultTitles[i].innerText +
      "\t href:\t" +

      href.substring(0, 50) +
      "\t site:\t" +
      site;

    console.log(
      "\n title: \t" +
        resultTitles[i].innerText +
        "\t href:\t" +
        href.substring(0, 50) +
        "\t site:\t" +
        site
    );
     for (let k in targetSites) {
     var m=site.search(targetSites[k]);
       console.log("\tsearch="+m);
          if (m!=-1) {
            targetEleA = resultTitles[i];
            pinIndex=i+1;
            pinPage=curPage;
          }
      }
  }


  /* 模拟滑动，然后跳转到第二页 */
  var time = randomNum(300,1250);
  for (i = 0; i < scTitles.length; i++) {
    setTimeout(scrollTo(scTitles[i]), time);
    time = time + randomNum(300,1250);
  }
  for (i = 0; i < resultTitles.length; i++) {
  if(scTitles[i]!=null){
     setTimeout(scrollTo(scTitles[i]), time);
      time = time + randomNum(300,1250);
  }
  }
  if (targetEleA == null) {
    /* 滑到nextonly 点击 */
    var nextOnlys = document.getElementsByClassName("nextOnly");
    if (nextOnlys.length == 1) {
      /*  跳转到下一页前logBack会更新 */
      window.java_obj.saveLog(logBack);
      setTimeout(scrollTo(nextOnlys[0]), time);
      time += randomNum(300,1250);
      setTimeout(clickHref(nextOnlys[0]), time);
    } else {
      /*结果第一页找不到关键词结果清醒 或者 中间页*/
       var bnNodes = document.getElementsByClassName("bn");
       console.log("bnNodes.length=="+bnNodes.length);
       if(bnNodes.length>0){
       /*结果第一页 关键词找不到结果*/
      window.java_obj.requestFinished();
       }else{
       /*非结果页，可能是中间跳转页*/
       console.log("非百度一下后正常结果页");
       }
             logBack += "not found 下一页节点";
             console.log("not found 下一页节点");
             window.java_obj.saveLog(logBack);

    }
  } else {
    time += randomNum(300,1250);
    setTimeout(clickToTarget(), time);
    window.java_obj.pinIndex(pinPage,pinIndex);
  }
} else {
  /* 第2..N页--》第3..+ N+1页 ，打印第2..页 */
    var pageInfo= pageNo[0].innerText;
    if(pageInfo.length==3){
    curPage=parseInt(pageInfo[1]);
    console.log("curPage="+curPage);
    }

  logBack =
    logBack + "\n\n" + pageNo[0].innerText + ":" + "\t(" + curTime + ")";
  var rssultTitles = document.getElementsByClassName("result_title");
  logBack = logBack + "\n\n(result总共" + rssultTitles.length + "项)";
  console.log("\n\n" + pageNo[0].innerText + ":");
    var start=0;
    if(curPage==pinPage){
      start=pinIndex;
      window.java_obj.pinIndex(0,0);
    }else{
      start=0;
    }
  for (i = start; i < rssultTitles.length; i++) {
    /* 获取 ad 的title、href */
    var href = rssultTitles[i].href;
    var site = rssultTitles[i].parentElement.getElementsByClassName("site")[0]
      .innerText;
    logBack =
      logBack +
      "\n title: \t" +
      rssultTitles[i].innerText +
      "\t href:\t" +
      href.substring(0, 50) +
      "\t site:\t" +
      site;
    console.log(
      "\n title: \t" +
        rssultTitles[i].innerText +
        "\t href:\t" +
        href.substring(0, 50) +
        "\t site:\t" +
        site
    );

       for (let k in targetSites) {
        var m=site.search(targetSites[k]);
         console.log("\tsearch="+m);
          if (m!=-1) {
          console.log("\t 点击 site;\n"+site);
            targetEleA = rssultTitles[i];
            pinIndex=i+1;
            pinPage=curPage;
          }
        }
  }

  /* 模拟滑动，然后跳转到3..N页 */
  var time = randomNum(300,1250);
  for (i = 0; i < rssultTitles.length; i++) {
    setTimeout(scrollTo(rssultTitles[i]), time);
    time = time + randomNum(300,1250);
  }

  if (targetEleA == null) {
    /* 滑到nextonly 点击 */
    var nextOnlys = document.getElementsByClassName("next");
    if (nextOnlys.length == 1) {
      setTimeout(scrollTo(nextOnlys[0]), time);
      if (pageNo[0].innerText == "第"+page_max+"页") {
        /* 当前页="第页" 就不跳转了 */
        console.log("一次循环结束"+page_max);
        window.java_obj.saveLog(logBack);
        window.java_obj.requestFinished();
      } else {
        /* 跳转 */
        window.java_obj.saveLog(logBack);
        time += randomNum(300,1250);
        setTimeout(clickHref(nextOnlys[0]), time);
      }
    } else {
      /* 找不到2...N页*/
      console.log("not found" + pageNo[0].innerText);
      logBack += "not found" + pageNo[0].innerText + "节点";
      window.java_obj.saveLog(logBack);
      window.java_obj.requestFinished();
    }
  } else {
    time += randomNum(300,1250);
    setTimeout(clickToTarget(), time);
     window.java_obj.pinIndex(pinPage,pinIndex);
  }
}


