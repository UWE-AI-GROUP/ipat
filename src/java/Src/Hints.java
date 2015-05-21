/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author kieran
 */
public class Hints {

    /**
     *
     * @param data
     * @param controller
     */
    public void updateProfileHints(HashMap data, Controller controller) {

        ArrayList< HashMap> profileResults = new ArrayList<>();

        ArrayList<String> profileFilepaths = (ArrayList<String>) data.get((Object) "source");
        ArrayList<String> FreezeBGColour = (ArrayList<String>) data.get((Object) "FreezeBGColour");
        ArrayList<String> ChangeGFContrast = (ArrayList<String>) data.get((Object) "ChangeGFContrast");
        ArrayList<String> score = (ArrayList<String>) data.get((Object) "score");
        ArrayList<String> ChangeFontSize = (ArrayList<String>) data.get((Object) "ChangeFontSize");
        ArrayList<String> FreezeFGFonts = (ArrayList<String>) data.get((Object) "FreezeFGFonts");

        // cycle through all results (will be dependent on how many files were uploaded)
        for (int i = 0; i < profileFilepaths.size(); i++) {

            // extract the current generation profile names from the "data" results containing hint scores
            String urlWithValue = profileFilepaths.get(i);
            String file = urlWithValue.substring(urlWithValue.lastIndexOf("/") + 1, urlWithValue.lastIndexOf("-"));
            file = file.concat(".xml");
            int profileID = Integer.parseInt(file.substring(file.lastIndexOf("_") + 1, file.lastIndexOf(".")));

            // place scores in profileResults = Arraylist < Hashmap <String, ArrayList < scores > > > structure
            // initiate Arrays with different data types for values
            HashMap<String, ArrayList> profileValues = new HashMap();
            ArrayList<Boolean> booleanArray = new ArrayList<>();
            ArrayList<Integer> integerArray = new ArrayList<>();

            // globalScoreValueArray (int)
            integerArray = new ArrayList<>();
            integerArray.add(Integer.parseInt(score.get(i)));
            profileValues.put("globalScore", integerArray);

            // ChangeGFContrastValueArray (int)
            integerArray = new ArrayList<>();
            integerArray.add(Integer.parseInt(ChangeGFContrast.get(i)));
            profileValues.put("ChangeGFContrast", integerArray);

            // ChangeFontSizeValueArray (int)
            integerArray = new ArrayList<>();
            integerArray.add(Integer.parseInt(ChangeFontSize.get(i)));
            profileValues.put("ChangeFontSize", integerArray);

            // FreezeBGColourValueArray (String)
            booleanArray = new ArrayList<>();

            booleanArray.add(Boolean.parseBoolean(String.valueOf(FreezeBGColour.get(i))));
            profileValues.put("FreezeBGColour", booleanArray);

            //FreezeFGFontsValueArray (String)
            booleanArray = new ArrayList<>();
            booleanArray.add(Boolean.parseBoolean(String.valueOf(FreezeFGFonts.get(i))));
            profileValues.put("FreezeFGFonts", booleanArray);

            //--------------
            profileResults.add(profileID, profileValues);

        }

        // loop through sessions profileResults and update their scores
        // get the controllers currentGeneration of profileResults
        Profile[] profiles = controller.currentGenerationOfProfiles;

        // Loop through current gerenation of profileResults
        for (int i = 0; i < profiles.length; i++) {
            Profile profile = profiles[i];
            System.out.println("ITERATING THROUGH CURRENT GEN PROFILES IN MEMORY : " + profile.getName());

            //  for each profile cycle through the results and apply interactions to profiles
            HashMap get = profileResults.get(i);
            Set keySet = get.keySet();
            for (Object keyObj : keySet) {
                int sum = 0;
                String key = (String) keyObj;
                switch (key) {
                    case "globalScore":
                        ArrayList<Integer> globalScores = (ArrayList<Integer>) get.get(key);
                        for (Integer globalScore : globalScores) {
                            sum += globalScore;
                            int average = sum / globalScores.size();
                            profile.setGlobalScore(average);
                            System.out.println(i + " globalScore " + average);
                        }
                        break;
                    case "ChangeGFContrast":
                        ArrayList<Integer> GFContrasts = (ArrayList<Integer>) get.get(key);
                        for (Integer GFContrast : GFContrasts) {
                            sum += GFContrast;
                            int average = sum / GFContrasts.size();
                            profile.setChangeFGContrast(average);
                            System.out.println(i + " ChangeGFContrast " + average);
                        }
                        break;
                    case "ChangeFontSize":
                        ArrayList<Integer> fontSizes = (ArrayList<Integer>) get.get(key);
                        for (Integer frontSize : fontSizes) {
                            sum += frontSize;
                            int average = sum / fontSizes.size();
                            profile.setChangeFontSize(average);
                            System.out.println(i + " ChangeFontSize " + average);
                        }
                        break;
                    case "FreezeBGColour":
                        ArrayList<Boolean> BGColours = (ArrayList<Boolean>) get.get(key);
                        for (Boolean BGColour : BGColours) {
                            if (BGColour == true) {
                                profile.setFreezeBGColour(true);
                                System.out.println(i + " FreezeBGColour " + "on");
                            } else {
                                System.out.println(i + " FreezeBGColour " + "off");
                            }
                        }
                        break;
                    case "FreezeFGFonts":
                        ArrayList<Boolean> FGFonts = (ArrayList<Boolean>) get.get(key);
                        for (Boolean FGFont : FGFonts) {
                            if (FGFont == true) {
                                profile.setFreezeFGFonts(true);
                                System.out.println(i + " FreezeFGFonts " + "on");
                            } else {
                                System.out.println(i + " FreezeFGFonts " + "off");
                            }
                        }
                        break;
                    default:
                        System.out.println("Error unrecognised score value in newGenRequest");
                        throw new AssertionError();
                }
            }
        }
    }

    /**
     *
     * @param hintsXML The filePath for the hints.xml file containing the hints
     * @return HashMap containing the hints as keys and ArrayLists containing as
     * values
     */
    public HashMap getHintsVariables(String hintsXML) {
        HashMap<String, String> variables = new HashMap();

//        <interaction>
//	<ChangeGFContrast>
//		<type>range</type>
//		<defaultValue>Cruise</defaultValue>
//	</ChangeGFContrast>
//	<ChangeFontSize>
//		<type>range</type>
//		<defaultValue>Enderson</defaultValue>
//	</ChangeFontSize>
//	<FreezeBGColour>
//		<type>checkbox</type>
//		<defaultValue>Bush</defaultValue>
//	</FreezeBGColour>
//</interaction>
        try {
            File file = new File(hintsXML);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(file);
            doc.getDocumentElement().normalize();
            System.out.println("Root element " + doc.getDocumentElement().getNodeName());
            NodeList nodeLst = doc.getElementsByTagName("interaction");
            System.out.println("Gathering Interactions");

            for (int s = 0; s < nodeLst.getLength(); s++) {

                Node fstNode = nodeLst.item(s);

                if (fstNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element fstElmnt = (Element) fstNode;
                    NodeList fstNmElmntLst = fstElmnt.getElementsByTagName("attribute");
                    Element fstNmElmnt = (Element) fstNmElmntLst.item(0);
                    NodeList fstNm = fstNmElmnt.getChildNodes();
                    System.out.println("attribute : " + ((Node) fstNm.item(0)).getNodeValue());
                    String attribute = ((Node) fstNm.item(0)).getNodeValue();
                    NodeList lstNmElmntLst = fstElmnt.getElementsByTagName("value");
                    Element lstNmElmnt = (Element) lstNmElmntLst.item(0);
                    NodeList lstNm = lstNmElmnt.getChildNodes();
                    System.out.println("value : " + ((Node) lstNm.item(0)).getNodeValue());
                    String value = ((Node) lstNm.item(0)).getNodeValue();
                    variables.put(attribute, value);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variables;
    }

    /**
     *
     * @param processedArtifacts The paths to the result artifacts which have
     * been created on the server, stored into a structure whereby each profile
     * number is a key to an ArrayList of Image file paths.
     * @param hints
     * @return
     */
    public String getByProfileHTML(HashMap processedArtifacts, HashMap hints) {

        int populationSize = 0;

        // [layer one] create the list for the profile tabs 
        String cells = "<div id='tabs-container'><ul class='tabs-menu'>";
        for (int i = 0; i < processedArtifacts.size(); i++) {
            cells += "<li  id='li_" + i + "' onclick='tabClicked(this.id)'><a href='#byProfile_" + i + "'>Profile " + i + "</a></li>";
        }
        // [layer two] create div which will contain all the seporate tabs and their cells this is needed for the CSS 
        cells += " </ul> <div class='tabstuff'>";

        // populate div sections containing tables for each profile tab
        for (int i = 0; i < processedArtifacts.size(); i++) {
            String profileNum = String.valueOf(i);
            ArrayList<String> imageArray = (ArrayList<String>) processedArtifacts.get(profileNum);

            cells += "<div id='byProfile_" + i + "' class='tab-content'>";
            // TODO inject hint into HTML Strings
            for (int j = 0; j < imageArray.size(); j++) {
                cells += "<div class='cell'>"
                        + "<div id='overlay_" + populationSize + "' class='overlay' onclick='frameClick(this.id)'></div>"
                        + "<iframe src='" + imageArray.get(j) + "' scrolling='no' class='cellFrames' id='frame_" + populationSize + "' ></iframe>"
                        + "<div class='hint'><input type='checkbox' id='FreezeBGColour_" + populationSize + "' class='FreezeBGColour' ><label for='FreezeBGColour_" + populationSize + "' class='label'>Freeze Background</label></div>"
                        + "<div class='hint'><input type='checkbox' id='FreezeFGFonts_" + populationSize + "' class='FreezeFGFonts' ><label for='FreezeFGFonts_" + populationSize + "' class='label'>Freeze Fonts</label></div>"
                        + "<div class='hint'><input type='range' id ='score_" + populationSize + "' min='0' max='10' value='5' step='1'/><label for='score_" + populationSize + "' class='label'>Score</label></div>"
                        + "<div class='hint'><input type='range' id ='ChangeFontSize_" + populationSize + "' min='0' max='2' value='1' step='1' /><label for='ChangeFontSize_" + populationSize + "' class='label'>Change Font</label></div>"
                        + "<div class='hint'><input type='range' id ='ChangeGFContrast_" + populationSize + "' min='0' max='2' value='1' step='1'  /><label for='ChangeGFContrast_" + populationSize + "' class='label'>Change Contrast</label></div>";
                populationSize += 1;
            }
            cells += "</div>";
        }
        cells += "</div></div>";
        return cells;
    }

//    public String getByImageHTML(HashMap processedArtifacts, HashMap hints) {
//
//        int populationSize = 0;
//
//        // get size of a profile array of results
//        Collection values = processedArtifacts.values();
//        ArrayList<String> next = (ArrayList<String>) values.iterator().next();
//        var imageArray = res.toString().split(",");
//
//        // [layer one] create the list for the profile tabs 
//        var content = "<div id='tabs-container'><ul class='tabs-menu'>";
//        for (var i = 0; i < imageArray.length; i++) {
//            // get the name of the image for the tab heading
//            var sourcePath = imageArray[i];
//            var n = sourcePath.lastIndexOf("-");
//            var imageName = sourcePath.substring(n);
//            content += "<li  id='li_" + i + "_2' onclick='tabClicked(this.id)'><a href='#byImage_" + i + "'>" + imageName + "</a></li>";
//        }
//        // [layer two] create div which will contain all the seporate tabs and their content this is needed for the CSS 
//        content += " </ul> <div class='tabstuff'>";
//
//        for (int j = 0; j < imageArray.length; j++) {
//            content += "<div id='byImage_" + i + "' class='tab-content'>";
//            for (int j = 0; j < size; j++) {
//
//            }
//        }
//        return cells;
//    }

    /**
     *
     * @param sessionID
     * @param results
     * @return
     */
    public HashMap getResultsHashMap(String sessionID, Artifact[] results) {

        HashMap<String, ArrayList<String>> HM = new HashMap();
        for (Artifact result : results) {
            // cut out the generation (gen_y)
            String name = result.getFilename().substring(result.getFilename().indexOf("-") + 1);
            // split the result into its profile_x  and  fileName
            String[] parts = name.split("-");
            String fileName = parts[1];
            String profileNum = parts[0].substring(parts[0].indexOf("_") + 1);

            // if the hashmap is empty add the first element to it
            if (HM.isEmpty()) {
                System.out.println("CREATING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
                ArrayList<String> imageList = new ArrayList<>();
                imageList.add("Client%20Data/" + sessionID + "/output/" + result.getFile().getName());
                HM.put(profileNum, imageList);

                // if the hashmap is not empty 
            } else {
                // check if hashmap already has the profile with a result in it and add the current result to this list
                if (HM.containsKey(profileNum)) {
                    System.out.println("ADDING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
                    ArrayList<String> imageList = (ArrayList<String>) HM.get(profileNum);
                    imageList.add("Client%20Data/" + sessionID + "/output/" + result.getFile().getName());
                    HM.put(profileNum, imageList);

                    // if there are no matches, add a new hashmap element with the current result placed into a new list 
                } else {
                    System.out.println("CREATING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
                    ArrayList<String> imageList = new ArrayList<>();
                    imageList.add("Client%20Data/" + sessionID + "/output/" + result.getFile().getName());
                    HM.put(profileNum, imageList);
                }
            }
        }
        return HM;
    }

}
