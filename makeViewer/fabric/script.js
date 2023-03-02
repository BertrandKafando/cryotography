
// Get Html Elements By Id

let addLineBtn = document.getElementById('add-line-btn');




var canvas = new fabric.Canvas('the-canvas',
    {
        width: window.innerWidth,
        //black
        backgroundColor: '#000000',
     }
 );
            var rect = new fabric.Rect({
                left: 100,
                top: 100,
                fill: 'red',
                width: 20,
                height: 20
            });

var circle = new fabric.Circle({
    radius: 20, fill: 'green', left: 100, top: 100
});

canvas.add(rect);
canvas.add(circle);
    

// Add Events

addLineBtn.addEventListener('click', activateAddLineBtn);

canvas.on('mouse:down', function(options) {
  console.log(options.e.clientX, options.e.clientY);
});

console.log(JSON.stringify(canvas));

//functions

function activateAddLineBtn() {
    canvas.isDrawingMode = true;
    canvas.freeDrawingBrush.color = 'white';
    canvas.freeDrawingBrush.width = 3;
    canvas.renderAll();

 }