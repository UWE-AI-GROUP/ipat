/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {



    var form = document.getElementById('fileUploadForm');
    var fileSelect = document.getElementById('filesToUpload');

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
     
                var content = "<table border='1px'><tr>";
                for (var i = 0; i < result.length; i++) {
                    if(i % 3 === 0){content += "<tr>";}
                 content += "<td class='cell'><iframe src='" + result[i] + "' scrolling='yes' class='cellFrames' id='frame_"+i+"' ></iframe><div id='overlay_"+i+"' class='overlay' onclick='frameClick(this.id)'></div><input type='range' min='0' max='50' value='25' step='5'  class='sliders'/></td>";
                      if(i % 3 === 2){content += "</tr>";} 
                }
                content += "</tr></table>";
               $('#tabs-byProfile').append(content);
          }
        };

        xhr.send(formData);
    }, false);
});

function frameClick (id){
      var num = id.split("_");
  var src = document.getElementById("frame_" + num[1] ).src;
  document.getElementById("previewFrame").src = src;
    }