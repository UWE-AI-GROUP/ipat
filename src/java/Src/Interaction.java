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

        ArrayList<String> profileFileNames = (ArrayList<String>) data.get((Object) "source");
        ArrayList<String> FreezeBGColour = (ArrayList<String>) data.get((Object) "FreezeBGColour");
        ArrayList<String> ChangeGFContrast = (ArrayList<String>) data.get((Object) "ChangeGFContrast");
        ArrayList<String> score = (ArrayList<String>) data.get((Object) "score");
        ArrayList<String> ChangeFontSize = (ArrayList<String>) data.get((Object) "ChangeFontSize");
        ArrayList<String> FreezeFGFonts = (ArrayList<String>) data.get((Object) "FreezeFGFonts");

        // cycle through all results 
        for (int i = 0; i < profileFileNames.size(); i++) {

            // extract the current generation profile names from the "data" results containing scores + hint scores
            String urlWithValue = profileFileNames.get(i);
            String file = urlWithValue.substring(urlWithValue.lastIndexOf("/") + 1, urlWithValue.lastIndexOf("-"));
            file = file.concat(".xml");
            int profileID = Integer.parseInt(file.substring(file.lastIndexOf("_") + 1, file.lastIndexOf(".")));

                // place scores in profileResults = Arraylist < Hashmap <String, ArrayList < scores > > > 
            // initiate Arrays with different data types for values
            HashMap<String, ArrayList> profileValues = new HashMap();
            ArrayList<String> stringArray = new ArrayList<>();
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
            stringArray = new ArrayList<>();
            stringArray.add(FreezeBGColour.get(i));
            profileValues.put("FreezeBGColour", stringArray);

            //FreezeFGFontsValueArray (String)
            stringArray = new ArrayList<>();
            stringArray.add(FreezeFGFonts.get(i));
            profileValues.put("FreezeFGFonts", stringArray);

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
                        ArrayList<Integer> value0 = (ArrayList<Integer>) get.get(key);
                        for (Integer value01 : value0) {
                            sum += value01;
                            int average = sum / value0.size();
                            profile.setGlobalScore(average);
                            System.out.println(i + " globalScore " + average);
                        }
                        break;
                    case "ChangeGFContrast":
                        ArrayList<Integer> value1 = (ArrayList<Integer>) get.get(key);
                        for (Integer value01 : value1) {
                            sum += value01;
                            int average = sum / value1.size();
                            profile.setChangeGFContrast(average);
                            System.out.println(i + " ChangeGFContrast " + average);
                        }
                        break;
                    case "ChangeFontSize":
                        ArrayList<Integer> value2 = (ArrayList<Integer>) get.get(key);
                        for (Integer value01 : value2) {
                            sum += value01;
                            int average = sum / value2.size();
                            profile.setChangeFontSize(average);
                            System.out.println(i + " ChangeFontSize " + average); 
                        }
                        break;
                    case "FreezeBGColour":
                        ArrayList<String> value3 = (ArrayList<String>) get.get(key);
                        for (String value31 : value3) {
                            if (value31.equalsIgnoreCase("on")) {
                                profile.setFreezeBGColour(true);
                                System.out.println(i + " FreezeBGColour " + "on");
                            } else {
                                System.out.println(i + " FreezeBGColour " + "off");
                            }
                        }
                        break;
                    case "FreezeFGFonts":
                        ArrayList<String> value4 = (ArrayList<String>) get.get(key);
                        for (String value31 : value4) {
 
                            if (value31.equalsIgnoreCase("on")) {
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
