/* 
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

$(document).ready(function () {
    abortCurrentSession();
    
    // global variables
    window.artifactCount;
    window.hints;

//================================================
// file upload detected
    $('#fileUploadForm').change(function () {
        chooseFiles();
    });
//================================================
// next Generation button pressed
    $("#nextGeneration").click(function () {
        callNextGeneration();
    });
//================================================
// abort button pressed
    $("#abort").click(function () {
        abortCurrentSession();
    });
//================================================
// reset button pressed
    $("#resetScores").click(function () {
        resetScores();
    });
//================================================
}); // end of "on window load"
//================================================
//
function frameClick(id) {
    var num = id.split("_");
    var src = document.getElementById("frame_" + num[1]).src;
    document.getElementById("previewFrame").src = src;
}
//================================================
//
function tabClicked(item) {
    $('[id^="byProfile_"]').hide();
    $('[id^="byImage_"]').hide();
    $('[class^="tabText_"]').css('color', 'white');
    var num = item.slice(3);
    $('#byProfile_' + num).show();
    $('#byImage_' + num).show();
    $('.tabText_' + num).css('color', 'blue');
}
//================================================
//
function resetScores() {
    for (var i = 0; i < artifactCount; i++) {
        document.getElementById("globalScore_" + i).value = 5;
    }
}
//================================================
//
function chooseFiles() {
    event.preventDefault();
    var files = document.getElementById('filesToUpload').files;
    var formData = new FormData();
    var valid = true;
    for (var i = 0; i < files.length; i++) {
        var file = files[i];
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
                    tabClicked('li_0');
                }, 300);
            }
        };
        xhr.send(formData);
        $("#generationCount").val = 0;
        $("#generationCount").val += 1;
        valid = false;
    }
}

//================================================
//
function callNextGeneration() {
    var scores = {};
    var vars = {};
    var data = {};

    if (/^[1-8]*$/.test($("#numOfProfiles").val())) {
        vars["ProfileNum"] = $("#numOfProfiles").val();
    }

    if ($("#tabs-byProfile").attr('aria-hidden') === 'false') {
        var all = $("#tabs-byProfile .cell");
    } else if ($("#tabs-byImage").attr('aria-hidden') === "false") {
        var all = $("#tabs-byImage .cell");
    } else {
        alert("Error: unable to determine which tab is selected");
    }

    for (var i = 0; i < all.length; i++) {
        var inputs = $(all[i]).find("input");
        for (var j = 0; j < inputs.length; j++) {
            var name = $(inputs[j]).prop('id');
            if ($(inputs[j]).prop('type') === "checkbox") {
                var value = $(inputs[j]).is(':checked');
                scores[name] = value;
            }
            else if ($(inputs[j]).prop('type') === "range") {
                var value = $(inputs[j]).val();
                scores[name] = value;
            }
            // incase the src for the "cells" is ever needed
//                else if ($(inputs[j]).prop('tagName') === 'IFRAME') {
//                    var value = $(inputs[j]).attr('src');
//                    data[name] = value;
//                }
            else {
                alert("Error: A javascript JQuery check needs to be implemented for " + $(inputs[i]).attr('id') + " in javascript.js");
            }

        }
    }

    data['scores'] = scores;
    data['vars'] = vars;

    $('#tabs-byProfile').empty();
    $('#tabs-byImage').empty();
    $.ajax({
        url: "newGen",
        type: "POST",
        data: {data: JSON.stringify(data)},
        success: function (result) {
            artifactCount = result["count"];
            setTimeout(function () {
                $('#tabs-byProfile').empty();
                $('#tabs-byProfile').append(result["byProfile"]);
                $('#tabs-byImage').empty();
                $('#tabs-byImage').append(result["byImage"]);
                tabClicked('li_0');
            }, 300);
        }
    }, false);
    $("#generationCount").val += 1;
}
//================================================
//
function abortCurrentSession() {
    $('#tabs-byProfile').empty();
    $('#tabs-byImage').empty();
    $('#previewFrame').attr('src', "");
    $('#filelist').val('');
    $('#filesToUpload').replaceWith("<input class='filesToUpload' name='filesToUpload' id='filesToUpload' type='file' multiple />");
    $("#generationCount").val = 0;
    $.ajax({
        url: "abort",
        type: "POST"
    });
}