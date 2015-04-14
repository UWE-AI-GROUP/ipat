/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {



    var form = document.getElementById('fileUploadForm');
    var fileSelect = document.getElementById('filesToUpload');
    var nextGen = document.getElementById("nextGeneration");
    var populationSize;

    form.addEventListener('change', function (event) {
        event.preventDefault();

        var files = fileSelect.files;
        var formData = new FormData();

        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (!file.type.match('text/html')) {
                continue;
            }
            formData.append('filesToUpload', file, file.name);
            document.getElementById("filelist").value += file.name + "\n";
        }

        var xhr = new XMLHttpRequest();
        xhr.open('POST', 'FileUpload', true);
        xhr.onload = function () {
            if (xhr.status !== 200) {
                alert('An error occurred!');
            }
        };

        xhr.onreadystatechange = function ()
        {
            if (xhr.readyState === 4)
            {
                var result = JSON.parse(xhr.responseText);
                populationSize = result.length;
                var content = "<table border='1px'><tr>";
                for (var i = 0; i < result.length; i++) {
                    if(i % 3 === 0){content += "<tr>";}
                 content += "<td class='cell'><iframe src='" + result[i] + "' scrolling='yes' class='cellFrames' id='frame_"+i+"' ></iframe><div id='overlay_"+i+"' class='overlay' onclick='frameClick(this.id)'></div><input type='range' id ='slider_"+i+"' min='0' max='10' value='5' step='1'  class='sliders'/></td>";
                      if(i % 3 === 2){content += "</tr>";} 
                }
                content += "</tr></table>";
               $('#tabs-byProfile').append(content);
          }
        };
        xhr.send(formData);
    }, false);

        nextGen.addEventListener('click', function () {
    var data = [];
     for (var i =0; i < populationSize; i++){
     data.push( document.getElementById("frame_" + i ).src + "~" + document.getElementById("slider_" + i ).value);
 }
  $('#tabs-byProfile').empty();
$.ajax({
            url:"newGen",
            type:"POST",
            data: {data:data},
            success:function(result){
                populationSize = result.length;
                var content = "<table border='1px'><tr>";
                for (var i = 0; i < result.length; i++) {
                    if(i % 3 === 0){content += "<tr>";}
                 content += "<td class='cell'><iframe src='" + result[i] + "' scrolling='yes' class='cellFrames' id='frame_"+i+"' ></iframe><div id='overlay_"+i+"' class='overlay' onclick='frameClick(this.id)'></div><input type='range' id ='slider_"+i+"' min='0' max='10' value='5' step='1'  class='sliders'/></td>";
                      if(i % 3 === 2){content += "</tr>";} 
                }
                content += "</tr></table>";
               $('#tabs-byProfile').append(content);
          }
 }, false);
});
    
});


function frameClick (id){
      var num = id.split("_");
  var src = document.getElementById("frame_" + num[1] ).src;
  document.getElementById("previewFrame").src = src;
    }