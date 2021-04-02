document.getElementsByName('word')[0].value=keyword;
var nodes=document.getElementsByTagName('form');
var lastNode=nodes[0].lastChild;
function time(){lastNode.click()}
setTimeout(time,1500);


