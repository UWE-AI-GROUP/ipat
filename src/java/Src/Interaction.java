/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import Algorithms.HintsProcessor;
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

        int numOfProfiles = controller.currentGenerationOfProfiles.length;
        HashMap<String, Double> averageCounters = new HashMap();

        
        // initialise holding structures
        HashMap<Integer, Double> FGContrasts = new HashMap();
        HashMap<Integer, Double> globalScores = new HashMap();
        HashMap<Integer, Double> fontSizes = new HashMap();
        HashMap<Integer, Boolean> FGFonts = new HashMap();
        HashMap<Integer, Boolean> BGColours = new HashMap();


        // We don't know the order in which hints are initialised in hints.xml so organisation of return values is required
        Set keySet = data.keySet();
        System.out.println("Num of Hints =" + numOfHints);
        int numOfResults = data.size() / numOfHints;
        int numOfUploads = numOfResults / numOfProfiles;
        for (Object keySet1 : keySet) {

            String key = (String) keySet1;
            String[] hint_Iteration = key.split("_");
            String hint = hint_Iteration[0];
            int iteration = Integer.parseInt(hint_Iteration[1]);
            // which array position to add the different results to
            int profileNum = iteration / numOfUploads;

            Object rawValue = data.get(key);

            // print statements to ensure that the cells value are placed into the right array and positions for averaging
            System.out.println("=================");
            System.out.println("Iteration: " + iteration + "\nHint: " + hint + "\nValue: " + data.get(key) + "\nArray Postition: " + arrayPosition + "\n");

            switch (hint) {
                case "frame":
                    // to get the corrosponding profile for the src  (not really needed, just here incase someone wants it)
//                    String source = (String) data.get(key);
//                    String profile = source.substring(source.indexOf('_') , source.lastIndexOf('-')+1);
                    break;
                case "globalScore":
                    if (globalScores.get(arrayPosition) == null) {
                        averageCounters.put(hint, 1.0);
                        globalScores.put(arrayPosition, Double.parseDouble((String) data.get(key)));
                    } else {
                        Double get = globalScores.get(arrayPosition);
                        globalScores.put(arrayPosition, (get * averageCounters.get(hint) + Double.parseDouble((String) data.get(key)) / averageCounters.get(hint) + 1));
                        averageCounters.put(hint, averageCounters.get(hint) + 1);
                    }

                    // if its a string or other
                } else {

                    // get the averageMap for this hint
                    HashMap<Integer, Double> PDHA = ordered.get(hint);

                    // check if the profile we are adding a value to already has a value, if it does add to its average
                    if (PDHA.containsKey(profileNum)) {
                        //if (PDHA.get(profileNum) != null) {
                        Double runningAverage = PDHA.get(profileNum);
                        HashMap averageCount = averageCounters.get(hint);
                        Double currentCount = (Double) averageCount.get(profileNum);

                        System.out.println("runningAverage " + runningAverage);
                        System.out.println("currentCount " + currentCount);
                        System.out.println("rawValue " + rawValue);

                        Double av = (runningAverage * currentCount + Double.parseDouble((String) rawValue)) / (currentCount + 1);
                        PDHA.put(profileNum, av);
                        System.out.println("Updated hint [" + hint + "] in profilesDoubleHintAverages at [" + profileNum + "] from value [" + runningAverage + "] to value [" + av + "] as the [" + (currentCount + 1) + "] entry");
                        averageCount.put(profileNum, currentCount + 1);
                        averageCounters.put(hint, averageCount);
                        ordered.put(hint, PDHA);

                        // if it doesnt, create the first entry
                    } else {
                        Double get = FGContrasts.get(arrayPosition);
                        FGContrasts.put(arrayPosition, (get * averageCounters.get(hint) + Double.parseDouble((String) data.get(key)) / averageCounters.get(hint) + 1));
                        averageCounters.put(hint, averageCounters.get(hint) + 1);
                    }
                    break;
                case "ChangeFontSize":
                    if (fontSizes.get(arrayPosition) == null) {
                        averageCounters.put(hint, 1.0);
                        fontSizes.put(arrayPosition, Double.parseDouble((String) data.get(key)));
                    } else {
                        Double get = fontSizes.get(arrayPosition);
                        fontSizes.put(arrayPosition, (get * averageCounters.get(hint) + Double.parseDouble((String) data.get(key)) / averageCounters.get(hint) + 1));
                        averageCounters.put(hint, averageCounters.get(hint) + 1);
                    }
                    break;
                case "FreezeBGColours":
                    if (BGColours.get(arrayPosition) == null) {
                        BGColours.put(arrayPosition, ((Boolean) data.get(key)));
                    } else if ((Boolean) data.get(key) == true) {
                        BGColours.put(arrayPosition, true);
                    }
                    break;
                case "FreezeFGFonts":
                    if (FGFonts.get(arrayPosition) == null) {
                        FGFonts.put(arrayPosition, ((Boolean) data.get(key)));
                    } else if ((Boolean) data.get(key) == true) {
                        FGFonts.put(arrayPosition, true);
                    }
                    break;
                default:
                    System.out.println("Error unrecognised score value in newGenRequest: " + hint);
                    throw new AssertionError();

            }
        }

        for (int i = 0; i < numOfProfiles; i++) {
            }
//==========================================================================================================================================================
            if (!fontSizes.isEmpty()) {
               hint = controller.hints.get("ChangeFontSize");
               System.out.println("Updated ChangeFontSize : " + FGContrasts.get(i));
                controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], fontSizes.get(i));
            } else {
                System.out.println("No hint for ChangeFontSize");
            }
//==========================================================================================================================================================
            if (!BGColours.isEmpty()) {
                hint = controller.hints.get("FreezeBGColours");
                if (BGColours.get(i)) {
                    System.out.println("Updated FreezeBGColours : true");
                    controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 0.0);
                } else {
                    System.out.println("Updated FreezeBGColours : false");
                    controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 1.0);
                }
            } else {
                System.out.println("No hint for FreezeBGColours");
            }
//==========================================================================================================================================================
            if (!FGFonts.isEmpty()) {
                hint = controller.hints.get("FreezeFGFonts");
                if (FGFonts.get(i)) {
                    System.out.println("Updated FreezeFGFonts : true");
                    controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 0.0);
                } else {
                    System.out.println("Updated FreezeFGFonts : false");
                    controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 1.0);
                }
            } else {
                System.out.println("No hint for FreezeFGFonts");
            }
//==========================================================================================================================================================
            if (!globalScores.isEmpty()) {
                System.out.println("Updated globalScore : " + globalScores.get(i).intValue());
                controller.currentGenerationOfProfiles[i].setGlobalScore( globalScores.get(i).intValue() );
            } else {
                System.out.println("Error: There is no hint for global score set in Class Interaction."
                        + "\nEnsure that the hint for globalScore is added to hints.xml. If the problem persists:"
                        + "\n-Check the values given back from the javascript.js, within the NextGenerationButton.actionListener"
                        + "\n-Check the Interaction.updateProfileHints() algorithm is correctly minipulating data");
            }
//==========================================================================================================================================================
            System.out.println("\n");
        }
    }
}
            System.out.println("##############################");
            System.out.println("Updating hints for Profile: " + i + "\n");

            // run through the hints getting each averageMap
            Set<String> hints = ordered.keySet();
            Iterator<String> iterator = hints.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                HashMap profilesHintAverages = ordered.get(key);

                if (key.equalsIgnoreCase("globalScore")) {
                    Object value = profilesHintAverages.get(i);
                    Double intValue =  (Double) value;
                    controller.currentGenerationOfProfiles[i].setGlobalScore(intValue.intValue());
                    System.out.println("Updated " + key + " : " + intValue.intValue());
                    
                } else {

                    // apply the average value of the current profile to the profile
                    Object value = profilesHintAverages.get(i);

                    if (value instanceof Boolean) {
                        Boolean booleanValue = (Boolean) value;
                        hintProc = controller.hints.get(key);
                        if (booleanValue) {
                            System.out.println("Updated " + key + " : true");
                            controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 0.0);
                        } else {
                            System.out.println("Updated " + key + " : false");
                            controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 1.0);
                        }
                    } else {
                        Double doubleValue = (Double) value;
                        hintProc = controller.hints.get(key);
                        System.out.println("Updated " + key + " : " + doubleValue);
                        controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], doubleValue);
                    }
                }
