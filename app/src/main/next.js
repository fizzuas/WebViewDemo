// class=next
function nextClick() {
  var target=null ;

  if (document.getElementsByClassName("nextOnly").length > 0) {
    target = document.getElementsByClassName("nextOnly")[0];
    console.log("nextOnly");

  }
//   if (document.getElementsByClassName("new-nextpage").length > 0) {
//     target = document.getElementsByClassName("next")[0];
//     console.log("new-nextpage");
//   }

  target.scrollIntoView({
    behavior: "smooth"
  });

  target.click();
}
setTimeout(nextClick(), 5000);