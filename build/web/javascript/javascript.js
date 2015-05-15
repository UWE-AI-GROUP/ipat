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
    var populationSize;



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
            populationSize = 0;
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
                    // get number of profiles in result
                    var size = 0, key;
                    for (key in result) {
                        if (result.hasOwnProperty(key))
                            size++;
                    }

                    // [layer one] create the list for the profile tabs 
                    var content = "<div id='tabs-container'><ul class='tabs-menu'>";
                    for (var i = 0; i < size; i++) {
                        content += "<li  id='li_" + i + "' onclick='tabClicked(this.id)'><a href='#byProfile_" + i + "'>Profile " + i + "</a></li>";
                    }
                    // [layer two] create div which will contain all the seporate tabs and their content this is needed for the CSS 
                    content += " </ul> <div class='tabstuff'>";

                    // populate div sections containing tables for each profile tab
                    for (var i = 0; i < size; i++) {
                        var cnt = i.toString();
                        var res = result[cnt];
                        var imageArray = res.toString().split(",");
                        content += "<div id='byProfile_" + i + "' class='tab-content'>";
                        // commented sections here allow the table cells to create new rows every 3 columns
                        for (var j = 0; j < imageArray.length; j++) {
                            content += "<div class='cell'>"
                                    + "<div id='overlay_" + populationSize + "' class='overlay' onclick='frameClick(this.id)'></div>"
                                    + "<iframe src='" + imageArray[j] + "' scrolling='no' class='cellFrames' id='frame_" + populationSize + "' ></iframe>"
                                    + "<div class='hint'><input type='checkbox' id='FreezeBGColour_" + populationSize + "' class='FreezeBGColour' ><label for='FreezeBGColour_" + populationSize + "' class='label'>Freeze Background</label></div>"
                                    + "<div class='hint'><input type='checkbox' id='FreezeFGFonts_" + populationSize + "' class='FreezeFGFonts' ><label for='FreezeFGFonts_" + populationSize + "' class='label'>Freeze Fonts</label></div>"
                                    + "<div class='hint'><input type='range' id ='score_" + populationSize + "' min='0' max='10' value='5' step='1'/><label for='score_" + populationSize + "' class='label'>Score</label></div>"
                                    + "<div class='hint'><input type='range' id ='ChangeFontSize_" + populationSize + "' min='0' max='2' value='1' step='1' /><label for='ChangeFontSize_" + populationSize + "' class='label'>Change Font</label></div>"
                                    + "<div class='hint'><input type='range' id ='ChangeGFContrast_" + populationSize + "' min='0' max='2' value='1' step='1'  /><label for='ChangeGFContrast_" + populationSize + "' class='label'>Change Contrast</label></div>";
                            populationSize += 1;
                        }
                        content += "</div></div>";
                    }
                    content += "</div>";
                    // populate the byProfiles tab 
                    setTimeout(function () {
                        $('#tabs-byProfile').empty();
                        $('#tabs-byProfile').append(content);
                    }, 3000);
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


        var data = {};
        var source = [];
        var score = [];
        var FreezeBGColour = [];
        var FreezeFGFonts = [];
        var ChangeFontSize = [];
        var ChangeGFContrast = [];

        for (var i = 0; i < populationSize; i++) {
            source.push(document.getElementById("frame_" + i).src);
            score.push(document.getElementById("score_" + i).value);
            ChangeFontSize.push(document.getElementById("ChangeFontSize_" + i).value);
            ChangeGFContrast.push(document.getElementById("ChangeGFContrast_" + i).value);
            FreezeBGColour.push($('#FreezeBGColour_' + i).is(':checked'));
            FreezeFGFonts.push($('#FreezeFGFonts_' + i).is(':checked'));
        }

        data['source'] = source;
        data['score'] = score;
        data['FreezeBGColour'] = FreezeBGColour;
        data['FreezeFGFonts'] = FreezeFGFonts;
        data['ChangeFontSize'] = ChangeFontSize;
        data['ChangeGFContrast'] = ChangeGFContrast;

        $('#tabs-byProfile').empty();
        $('#tabs-byProfile').html("<img src='" + image + "' />");

        $.ajax({
            url: "newGen",
            type: "POST",
            data: {data: JSON.stringify(data)},
            success: function (result) {

                $('#loading').html("<img src='" + image + "' />");

                // get number of profiles in result
                var size = 0, key;
                for (key in result) {
                    if (result.hasOwnProperty(key))
                        size++;
                }

                // [layer one] create the list for the profile tabs 
                var content = "<div id='tabs-container'><ul class='tabs-menu'>";
                for (var i = 0; i < size; i++) {
                    content += "<li  id='li_" + i + "' onclick='tabClicked(this.id)'><a href='#byProfile_" + i + "'>Profile " + i + "</a></li>";
                }
                // [layer two] create div which will contain all the seporate tabs and their content this is needed for the CSS 
                content += " </ul> <div class='tabstuff'>";

                // populate div sections containing tables for each profile tab
                for (var i = 0; i < size; i++) {
                    var cnt = i.toString();
                    var res = result[cnt];
                    var imageArray = res.toString().split(",");
                    content += "<div id='byProfile_" + i + "' class='tab-content'>";

                    for (var j = 0; j < imageArray.length; j++) {
                        content += "<div class='cell'>"
                                + "<div id='overlay_" + populationSize + "' class='overlay' onclick='frameClick(this.id)'></div>"
                                + "<iframe src='" + imageArray[j] + "' scrolling='no' class='cellFrames' id='frame_" + populationSize + "' ></iframe>"
                                + "<div class='hint'><input type='checkbox' id='FreezeBGColour_" + populationSize + "' class='FreezeBGColour' ><label for='FreezeBGColour_" + populationSize + "' class='label'>Freeze Background</label></div>"
                                + "<div class='hint'><input type='checkbox' id='FreezeFGFonts_" + populationSize + "' class='FreezeFGFonts' ><label for='FreezeFGFonts_" + populationSize + "' class='label'>Freeze Fonts</label></div>"
                                + "<div class='hint'><input type='range' id ='score_" + populationSize + "' min='0' max='10' value='5' step='1'/><label for='score_" + populationSize + "' class='label'>Score</label></div>"
                                + "<div class='hint'><input type='range' id ='ChangeFontSize_" + populationSize + "' min='0' max='2' value='1' step='1' /><label for='ChangeFontSize_" + populationSize + "' class='label'>Change Font</label></div>"
                                + "<div class='hint'><input type='range' id ='ChangeGFContrast_" + populationSize + "' min='0' max='2' value='1' step='1'  /><label for='ChangeGFContrast_" + populationSize + "' class='label'>Change Contrast</label></div>";
                        populationSize += 1;
                    }
                    content += "</div></div>";
                }
                content += "</div>";
                // populate the byProfiles tab 
                setTimeout(function () {
                    $('#tabs-byProfile').empty();
                    $('#tabs-byProfile').append(content);
                }, 3000);
            }
        }, false);
        populationSize = 0;
        genCount.value = parseInt(genCount.value) + 1;
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
        for (var i = 0; i < populationSize; i++) {
            document.getElementById("score_" + i).value = 5;
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
