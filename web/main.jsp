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
    <script src="/Neat/javascript/fileUpload.js"></script>
    <script>$(function () {$("#tabs").tabs();});</script>
</head>
<body>
    <!--boarder of program-->
    <div id="program">
        <!--start of preview box-->
        <div id="preview"> <br>
            <iframe class="previewFrame" id="previewFrame" name="previewFrame" ></iframe>
        </div>
        <!--end of preview box-->
        <!--start of form box-->
        <div id="form">
            <!--start of top variables table-->
            <div id="variables">
                <table class="variablesTable">
                    <tbody>
                        <tr>
                            <td>
                                <label for="loadedProfile">loaded Profile:</label><br>
                                <input name="loadedProfile" id="loadedProfile" value="N/A" type="text">
                            </td>
                            <td>
                                <label for="generationCount">Generation Count:</label><br>
                                <input name="generationCount" id="generationCount" value="0" type="text">
                            </td>
                            <td>
                                <label for="applicationMode">Application Mode:</label><br>
                                <input name="applicationMode" id="applicationMode" value="Standard" type="text">
                            </td>
                            <td>
                                <label for="Generation">Profiles/Generation:</label><br>
                                <input name="Generation" id="Generation" value="6" type="text">
                            </td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <!--end of top variables table-->
            <!--start of file choosing section-->
            <div id="fileChoose">
                <form name="fileUploadForm" id="fileUploadForm" method="post" 
                      action="FileUpload" enctype="multipart/form-data">
                    <input class="filesToUpload" name="filesToUpload" id="filesToUpload" type="file" multiple />
                </form>
                <textarea id="filelist" name="filelist" rows="3" cols="100">${fileNames}</textarea>
            </div>
            <!--end of file choosing section-->
            <!--start of iframe table--> 
            <div id="iframeTable"> 

                <div id="tabs">
                    <ul>
                        <li><a href="#tabs-1">View By Profiles</a></li>
                        <li><a href="#tabs-2">View By Images</a></li>
                    </ul>
                    <div id="tabs-byProfile">        
                       
                    </div>
                    <div id="tabs-byImage">
                       
                    </div>
                </div>
                <!--end of iframe table-->
                <!--start of button table-->
                <div id="buttons">
                    <form id="buttonsForm">
                        <table style="width: 100%; height: 100%" border="0">
                            <tbody>
                                <tr>
                                    <td><button type="button" name="abortButton" id="abort">Abort</button>
                                    </td>
                                    <td><button type="button" name="resetScoresButton" id="resetScores">Reset Scores</button>
                                    </td>
                                    <td>
                                    </td>
                                    <td><button type="submit" name="nextGenerationButton" id="nextGeneration">Next Generation</button>
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                    </form>
                </div>
                <!--end of button table-->                
            </div>
            <!--end of form box-->           
        </div>
        <!--end of boarder of program-->
</body>
</html>