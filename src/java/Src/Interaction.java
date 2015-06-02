/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import Algorithms.HintsProcessor;
import java.util.HashMap;
import java.util.Iterator;
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
        HashMap<String, HashMap> ordered = new HashMap();

        // We don't know the order in which hints are initialised in hints.xml so organisation of return values is required
        Set keySet = data.keySet();
        for (Object keySet1 : keySet) {
            String key = (String) keySet1;
            String[] hint_Iteration = key.split("_");
            String hint = hint_Iteration[0];
            int iteration = Integer.parseInt(hint_Iteration[1]);
            int profileNum = iteration % numOfProfiles;

            // print statements to ensure that the cells value are placed into the right array and positions for averaging
            System.out.println("=================");
            System.out.println("Iteration: " + iteration + "\nHint: " + hint + "\nValue: " + data.get(key) + "\nArray Postition: " + profileNum + "\n");

            // if the ordered list hasn't yet initialised this hint then create it
            if (ordered.get(hint) == null) {
                // determine the type of data, if boolean - only add, if double - average and add
                if (data.get(key) instanceof Boolean) {
                    Boolean value = (Boolean) data.get(key);
                    HashMap<Integer, Boolean> profilesBooleanHintAverages = new HashMap();
                    profilesBooleanHintAverages.put(profileNum, value);
                }

                if (data.get(key) instanceof String) {

                    HashMap<Integer, Double> profilesDoubleHintAverages = new HashMap();
                    averageCounters.put(hint, 1.0);
                    profilesDoubleHintAverages.put(profileNum, Double.parseDouble((String) data.get(key)));
                    ordered.put(hint, profilesDoubleHintAverages);
                }

                // else add the <hint, value> to the existing hint key
            } else {

                if (data.get(key) instanceof Boolean) {
                    HashMap<Integer, Boolean> profilesBooleanHintAverages = ordered.get(hint);
                    if (profilesBooleanHintAverages.get(profileNum) == null) {
                        profilesBooleanHintAverages.put(profileNum, ((Boolean) data.get(key)));
                    } else if ((Boolean) data.get(key) == true) {
                        profilesBooleanHintAverages.put(profileNum, true);
                    }
                }

                if (data.get(key) instanceof String) {
                    HashMap<Integer, Double> profilesDoubleHintAverages = ordered.get(hint);
                    if (profilesDoubleHintAverages.get(profileNum) == null) {
                        averageCounters.put(hint, 1.0);
                        profilesDoubleHintAverages.put(profileNum, Double.parseDouble((String) data.get(key)));
                    } else {
                        Double get = profilesDoubleHintAverages.get(profileNum);
                        profilesDoubleHintAverages.put(profileNum, (get * averageCounters.get(hint) + Double.parseDouble((String) data.get(key)) / averageCounters.get(hint) + 1));
                        averageCounters.put(hint, averageCounters.get(hint) + 1);
                    }
                }
            }
        }

        for (int i = 0; i < numOfProfiles; i++) {
            System.out.println("\nUpdating hints for Profile: " + i);
            HintsProcessor hint;

            Set<String> hints = ordered.keySet();
            Iterator<String> iterator = hints.iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                HashMap profilesHintAverages = ordered.get(key);
                Object value = profilesHintAverages.get(i);

                if (value instanceof Boolean) {
                    Boolean booleanValue = (Boolean) value;
                    hint = controller.hints.get(key);
                    if (booleanValue) {
                        System.out.println("Updated " + key + " : true");
                        controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 0.0);
                    } else {
                        System.out.println("Updated " + key + " : false");
                        controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 1.0);
                    }
                } else {
                    Double doubleValue = (Double) value;
                    hint = controller.hints.get(key);
                    System.out.println("Updated " + key + " : " + doubleValue);
                    controller.currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], doubleValue);
                }
            }
        }
    }
}
