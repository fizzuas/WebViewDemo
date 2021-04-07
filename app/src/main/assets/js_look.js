console.log("浏览时间" + look_time);
function toTop(target) {
    return function() {
        target.scrollIntoView({
            behavior: "smooth",
            block: "start"
        });
    }
    ;
}
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

function toBottom(target) {
    return function() {
        target.scrollIntoView({
            behavior: "smooth",
            block: "end"
        });
    }
    ;
}

function finish() {
    console.log("浏览结束");
    window.java_obj.finish();
}

var body = document.getElementsByTagName("body");
if (body.length == 0) {
    window.java_obj.lookPageError();
} else {

    var divs = body[0].getElementsByTagName("div");
    console.log("\n 当前页面divs.length= " + divs.length);
    var step = 1;
    var time = look_time / 10;
    if (divs.length > 10) {
        step = parseInt(divs.length / 10);
    }
    console.log("\n 开始浏览， steps= " + step);
    if (divs.length < 3) {
        window.java_obj.lookPageError();
        window.java_obj.showSource(document.getElementsByTagName('html')[0].innerHTML, url);

    } else {
        for (i = 0; i < divs.length; i = i + step) {
            setTimeout(toTop(divs[i]), time);
            time += (look_time / 10);
            if ((i + step) > (divs.length - 1)) {
                setTimeout(finish, time);
            }
        }

    }
}
