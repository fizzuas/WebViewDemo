/*function clickHref(targetA) {
    return function() {
        targetA.click();
    };
}


var input=document.getElementById("query");

input.value="钥匙机";


var btn_searchs=document.getElementsByClassName("btn-search");

setTimeout(clickHref(btn_searchs[0]),1000);
*/

function test(){
var result=window.JSInvoker.getContent("sss");
console.log("window.JSInvoker="+(window.JSInvoker));
console.log("(typeof window.JSInvoker.eWallGetWithResponse ="+(typeof window.JSInvoker.eWallGetWithResponse !== "undefined"));
var flag=(window.JSInvoker && (typeof window.JSInvoker.eWallGetWithResponse !== "undefined"));
console.log("flag="+flag);
console.log("appCurPageNum="+window.JSInvoker.appCurPageNum);
console.log("isLogin="+window.JSInvoker.isLogin);
}
test();





