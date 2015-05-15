/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author kieran
 */
public class Interaction {

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
                            profile.setChangeGFContrast(average);
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

}
