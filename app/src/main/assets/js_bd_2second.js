document.getElementsByName('word')[0].value='家政';
var nodes=document.getElementsByTagName('form');
var lastNode=nodes[0].lastChild;
function time(){lastNode.click()}
setTimeout(time,3000);