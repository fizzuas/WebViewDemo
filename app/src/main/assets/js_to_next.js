function scrollTo(target) {
  return function () {
    target.scrollIntoView({
      behavior: "smooth"
    });
  };
}

function clickHref(target) {
  return function () {
    target.click();
  };
}
var curTime=new Date().toLocaleString();
console.log(curTime);
var logBack = ""+ curTime;
var pageNo = document.getElementsByClassName("pageNo");
if (pageNo.length == 0) {
  /* 第一页--》第二页 ，打印第一页 */
  logBack = logBack+"第一页:";
  console.log("第一页:");
  var scTitles = document.getElementsByClassName("ec_title");
  logBack = logBack + " \n(广告总共" + scTitles.length + "项)";
  for (i = 0; i < scTitles.length; i++) {
    /* 获取 ad 的title、href */
    var href = scTitles[i].href;
    logBack =
      logBack +
      "\n title: \t" +
      scTitles[i].innerText +
      "\t href:\t" +
      href.substring(0, 50);
    console.log(
      "\n title: \t" +
        scTitles[i].innerText +
        "\t href:\t" +
        href.substring(0, 50)
    );
  }
  var rssultTitles = document.getElementsByClassName("result_title");
  logBack = logBack + "\n\n(result总共" + rssultTitles.length + "项)";
  for (i = 0; i < rssultTitles.length; i++) {
    /* 获取 ad 的title、href，并打印 */
    var href = rssultTitles[i].href;
    logBack =
      logBack +
      "\n title: \t" +
      rssultTitles[i].innerText +
      "\t href:\t" +
      href.substring(0, 50);
    console.log(
      "\n title: \t" +
        rssultTitles[i].innerText +
        "\t href:\t" +
        href.substring(0, 50)
    );
  }
  /* 模拟滑动，然后跳转到第二页 */
  var time = 1500;
  for (i = 0; i < scTitles.length; i++) {
    setTimeout(scrollTo(scTitles[i]), time);
    time = time + 1500;
  }
  for (i = 0; i < rssultTitles.length; i++) {
    setTimeout(scrollTo(rssultTitles[i]), time);
    time = time + 1500;
  }
  /* 滑到nextonly 点击 */
  var nextOnlys = document.getElementsByClassName("nextOnly");
  if (nextOnlys.length == 1) {
    /*  跳转到下一页前logBack会更新 */
    window.java_obj.saveLog(logBack);
    setTimeout(scrollTo(nextOnlys[0]), time);
    time += 1500;
    setTimeout(clickHref(nextOnlys[0]), time);
  } else {
    logBack += "not found 下一页节点";
    window.java_obj.saveLog(logBack);
    console.log("not found 下一页节点");

  }
} else {
  /* 第二..页--》第+1页 ，打印第二..页 */
  logBack = logBack+"\n\n" + pageNo[0].innerText + ":";
  var rssultTitles = document.getElementsByClassName("result_title");
  logBack = logBack + "\n\n(result总共" + rssultTitles.length + "项)";
  console.log("\n\n" + pageNo[0].innerText + ":");
  for (i = 0; i < rssultTitles.length; i++) {
    /* 获取 ad 的title、href */
    var href = rssultTitles[i].href;
    logBack =
      logBack +
      "\n title: \t" +
      rssultTitles[i].innerText +
      "\t href:\t" +
      href.substring(0, 50);
    console.log(
      "\n title: \t" +
        rssultTitles[i].innerText +
        "\t href:\t" +
        href.substring(0, 50)
    );
  }
  /* 模拟滑动，然后跳转到3..N页 */
  var time = 1500;
  for (i = 0; i < rssultTitles.length; i++) {
    setTimeout(scrollTo(rssultTitles[i]), time);
    time = time + 1500;
  }
  /* 判断有没有目标节点，有则滑动到 目标节点，然后点击 */

  /* 滑到nextonly 点击 */
  var nextOnlys = document.getElementsByClassName("next");
  if (nextOnlys.length == 1) {
    setTimeout(scrollTo(nextOnlys[0]), time);
    /* 当前页="第40页" 就不跳转了 */
    if (pageNo[0].innerText == "第10页") {
      window.java_obj.saveLog(logBack);
      console.log("跳入最大设置页");
    } else {
      window.java_obj.saveLog(logBack);
      time += 1500;
      setTimeout(clickHref(nextOnlys[0]), time);
    }
  } else {
    /* 回调给java */
    console.log("not found" + pageNo[0].innerText );
    logBack += "not found" + pageNo[0].innerText + "节点";
    window.java_obj.saveLog(logBack);
  }
}


