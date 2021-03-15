var nodes = document.getElementsByTagName("input");
var input;
console.log("input length=" + nodes.length);

function findInputNode() {
    for (i = 0; i < nodes.length; i++) {
        console.log(i + "=" + nodes[i].getAttribute("name"));
        if (nodes[i].getAttribute("type") == "search") {
            input = nodes[i];
        }
    }

}

/* 点击 search*/
function clickHref(targetA) {
    return function() {
        targetA.click();
    }
    ;
}

function click_input() {
    if (input != null) {
        setTimeout(clickHref(input), 1000);
        console.log("点击 input 输入框");
    } else {
        console.log("找不到 input 按钮");
    }
}

findInputNode();
if (input != null) {
    var input_class_str = input.getAttribute("class");
    console.log("input class str=" + input_class_str);

    if (input_class_str.search("inputIndex") != -1) {
        console.log("包含 input_index");
        /* */
        click_input();
    } else {
        console.log("不包含 input_index");
        var btn_search = document.getElementById("head_search-ug-btn");
        if (btn_search != null) {
            setTimeout(clickHref(btn_search), 1500);
        } else {
            console.log("btn_search不存在");
        }
    }

} else {
    console.log("input =null");
}
