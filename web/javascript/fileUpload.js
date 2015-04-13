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
              
                var content = "<table><tr>";
                for (var i = 0; i < result.length; i++) {
                    if(i % 3 === 0){content += "<tr>";}
                 content += "<td><iframe src='" + result[i] + "' scrolling='no' class='tableframes'></iframe></td>";
                      if(i % 3 === 2){content += "</tr>";} 
                }
                content += "</tr></table>";
               $('#tabs-byProfile').append(content);
          }
        };

        xhr.send(formData);
    }, false);
});
