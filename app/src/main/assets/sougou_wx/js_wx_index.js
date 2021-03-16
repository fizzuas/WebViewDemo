function clickHref(targetA) {
    return function() {
        targetA.click();
    };
}


var input=document.getElementById("query");

input.value="钥匙机";


var btn_searchs=document.getElementsByClassName("btn-search");

setTimeout(clickHref(btn_searchs[0]),1000);
