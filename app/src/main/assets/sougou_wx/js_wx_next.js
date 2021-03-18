function clickHref(targetA) {
    return function() {
        targetA.click();
    };
}

function scrollTo(target) {
    return function() {

    console.log("scrollTo="+(target));
        target.scrollIntoView({
            behavior: "smooth"
        });
    }
    ;
}

var next=document.getElementById("next_page");

console.log("next="+(next.innerText));

setTimeout(scrollTo(next),1000);

setTimeout(clickHref(next),2500);