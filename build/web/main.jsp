<%-- 
    Document   : index
    Created on : 06-Mar-2015, 16:56:07
    Author     : kieran
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>

<!DOCTYPE html>
<html>
    <head>
        <title>IPAT Web Application</title>
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <noscript><h3>This site requires JavaScript</h3></noscript>
    <script src="//ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js"></script>
    <script src="http://malsup.github.com/jquery.form.js"></script> 
    <script src="//code.jquery.com/jquery-1.10.2.js"></script>
    <script src="//code.jquery.com/ui/1.11.4/jquery-ui.js"></script>
    <script src="javascript/javascript.js"></script>
    <script>$(function () {
            $("#tabs").tabs();
        });</script>
    <link rel="stylesheet" type="text/css" href="css/StyleSheet.css"> 
    <link rel="stylesheet" type="text/css" href="css/tabStyle.css"> 
</head>
<body>
    <div id="program">
        <div id="preview">
             <div id="variables">
                <div class="variableInfo">
                    <label for="loadedProfile">loaded Profile:</label>
                    <input name="loadedProfile" id="loadedProfile" value="N/A" type="text">
                </div>
                <div class="variableInfo">
                    <label for="generationCount">Generation Count:</label>
                    <input name="generationCount" id="generationCount" value="0" type="text">
                </div>
                <div class="variableInfo">
                    <label for="applicationMode">Application Mode:</label>
                    <input name="applicationMode" id="applicationMode" value="Standard" type="text">
                </div>
                <div class="variableInfo">
                    <label for="Generation">Profiles/Generation:</label>
                    <input name="Generation" id="Generation" value="6" type="text">
                </div>
            </div>
            <iframe class="previewFrame" id="previewFrame" name="previewFrame" ></iframe>
        </div>
        <div id="form">
           
            <div id="fileChoose">
                <form name="fileUploadForm" id="fileUploadForm" method="post" 
                      action="FileUpload" enctype="multipart/form-data">
                    <input class="filesToUpload" name="filesToUpload" id="filesToUpload" type="file" multiple />
                </form>
                <textarea id="filelist" name="filelist">${fileNames}</textarea>
            </div>
            <div id="iframeTable"> 
                <div id="tabs">
                    <ul>
                        <li id="viewTabProfile"><a href="#tabs-byProfile">View By Profiles</a></li>
                        <li id="viewTabImage"><a href="#tabs-byImage">View By Images</a></li>
                    </ul>
                    <div id="tabs-byProfile">        
                    </div>
                    <div id="tabs-byImage">
                    </div>
                </div>
            </div>
            <div id="buttons">
             <button type="button" name="abortButton" id="abort">Abort</button>         
             <button type="button" name="resetScoresButton" id="resetScores">Reset Scores</button>
             <button  name="nextGenerationButton" id="nextGeneration">Next Generation</button>   
            </div> 
        </div>
</body>