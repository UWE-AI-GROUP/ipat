/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


$(document).ready(function () {
    var form = document.getElementById('fileUploadForm');
    var nextGen = document.getElementById("nextGeneration");
    var genCount = document.getElementById("generationCount");
    var abort = document.getElementById("abort");
    var reset = document.getElementById("resetScores");
    var image = "data/ajaxSpinner.gif";
    var artifactCount;
    var hints;
//================================================
// file upload detected

    form.addEventListener('change', function (event) {
        event.preventDefault();
        $('#tabs-byProfile').html("<img src='" + image + "' />");
        var files = document.getElementById('filesToUpload').files;
        var formData = new FormData();
        var valid = true;
        for (var i = 0; i < files.length; i++) {
            var file = files[i];
            if (!file.type.match('text/html')) {
                alert('Please only select files with .htm and .html extensions.');
                $('#tabs-byProfile').empty();
                $('#filelist').val('');
                $('#filesToUpload').replaceWith("<input class='filesToUpload' name='filesToUpload' id='filesToUpload' type='file' multiple />");
                genCount.value = 0;
                files = null;
                valid = false;
                break;
            }
            formData.append('filesToUpload', file, file.name);
            document.getElementById("filelist").value += file.name + "\n";
        }

        if (valid) {
            artifactCount = 0;
            var xhr = new XMLHttpRequest();
            xhr.open('POST', 'FileUpload', true);
            xhr.onload = function () {
                if (xhr.status !== 200) {
                    alert('Server Error. We apologise for the inconvenience and will be up and running again shortly.');
                    $('#tabs-byProfile').empty();
                }
            };
            xhr.onreadystatechange = function () {
                if (xhr.readyState === 4) {
                    var result = JSON.parse(xhr.responseText);
                    artifactCount = result["count"];
                    hints = result["hintString"].split(",");
                    setTimeout(function () {
                        $('#tabs-byProfile').empty();
                        $('#tabs-byProfile').append(result["byProfile"]);
                        $('#tabs-byImage').empty();
                        $('#tabs-byImage').append(result["byImage"]);
                    }, 1000);
                }
            };
            xhr.send(formData);
            genCount.value = 0;
            genCount.value = parseInt(genCount.value) + 1;
            valid = false;
        }
    }, false);
//================================================
    // next Generation button pressed

    nextGen.addEventListener('click', function () {

        var h = $(".hint :input");
        
        var txt = "";
        for (var i = 0; i < h.length; i++) {


            txt = txt + " / " + $(h[i]).attr('id') + " = ";

            if ($(h[i]).prop('type') === "checkbox") {
                txt = txt + $(h[i]).is(':checked') + ", ";
            }
            else if ($(h[i]).prop('type') === "range") {
                txt = txt + $(h[i]).attr('value') + ", ";
            }
            else {
                alert("Error: A javascript JQuery check needs to be implemented for " + $(h[i]).attr('id') + " in javascript.js");
            }
        }
        alert(txt);



//       var data = {};
//       var source = [];
//       var globalScore = [];
//       var FreezeBGColour = [];
//       var FreezeFGFonts = [];
//       var ChangeFontSize = [];
//       var ChangeGFContrast = [];
//
//       for (var i = 0; i < hints.length; i++) {
//           
//           source.push(document.getElementById("frame_" + i).src);
//           globalScore.push(document.getElementById("globalScore_" + i).value);
//           ChangeFontSize.push(document.getElementById("ChangeFontSize_" + i).value);
//           ChangeGFContrast.push(document.getElementById("ChangeGFContrast_" + i).value);
//           FreezeBGColour.push($('#FreezeBGColour_' + i).is(':checked'));
//           FreezeFGFonts.push($('#FreezeFGFonts_' + i).is(':checked'));
//       }
//       artifactCount =0;
//       data['source'] = source;
//       data['globalScore'] = globalScore;
//       data['FreezeBGColour'] = FreezeBGColour;
//       data['FreezeFGFonts'] = FreezeFGFonts;
//       data['ChangeFontSize'] = ChangeFontSize;
//       data['ChangeGFContrast'] = ChangeGFContrast;

//        $('#tabs-byProfile').empty();
//        $('#tabs-byProfile').html("<img src='" + image + "' />");
//        $.ajax({
//            url: "newGen",
//            type: "POST",
//            data: {data: JSON.stringify(data)},
//            success: function (result) {
//
//                $('#loading').html("<img src='" + image + "' />");
//                artifactCount = result["count"];
//                setTimeout(function () {
//                    $('#tabs-byProfile').empty();
//                    $('#tabs-byProfile').append(result["byProfile"]);
//                    $('#tabs-byImage').empty();
//                    $('#tabs-byImage').append(result["byImage"]);
//                }, 1000);
//            }
//        }, false);
//        genCount.value = parseInt(genCount.value) + 1;
    });
//================================================
// abort button pressed
    abort.addEventListener('click', function () {
        $('#tabs-byProfile').empty();
        $('#filelist').val('');
        $('#filesToUpload').replaceWith("<input class='filesToUpload' name='filesToUpload' id='filesToUpload' type='file' multiple />");
        genCount.value = 0;
        $.ajax({
            url: "abort",
            type: "POST",
            success: function (result) {
                alert(result);
            }
        });
    }, false);
//================================================
// reset button pressed
    reset.addEventListener('click', function () {
        for (var i = 0; i < artifactCount; i++) {
            document.getElementById("globalScore_" + i).value = 5;
        }
    }, false);
//================================================
// end of "on window load"
});
//================================================
// iframe icon clicked (preview tile)
function frameClick(id) {
    var num = id.split("_");
    var src = document.getElementById("frame_" + num[1]).src;
    document.getElementById("previewFrame").src = src;
}
//================================================
// changing the profile tab being displayed
function tabClicked(item) {
    $('[id^="byProfile_"]').hide();
    var num = item.slice(3);
    $('#byProfile_' + num).show();
}
//================================================