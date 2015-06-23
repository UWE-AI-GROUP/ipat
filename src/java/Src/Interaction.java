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
import org.apache.log4j.Logger;

/**
 *
 * @author kieran
 */
public class Interaction {
private static final Logger logger = Logger.getLogger(Interaction.class);
    /**
     *
     * @param data
     * @param controller
     */
    public void updateProfileHints(HashMap data, Controller controller) {

        int numOfProfiles = controller.currentGenerationOfProfiles.length;
        int numOfHints = controller.hints.size();
        HashMap<String, HashMap> averageCounters = new HashMap();
        HashMap<String, HashMap> ordered = new HashMap();
        HintsProcessor hintProc;

        // We don't know the order in which hints are initialised in hints.xml so organisation of return values is required
        // Run through the different hints (keys) in the data set
        Set keySet = data.keySet();
        logger.debug("Num of Hints =" + numOfHints);
        int numOfResults = data.size() / numOfHints;
        int numOfUploads = numOfResults / numOfProfiles;
        for (Object keySet1 : keySet) {

            String key = (String) keySet1;
            String[] hint_Iteration = key.split("_");
            // name of the hint in question
            String hint = hint_Iteration[0];
            int iteration = Integer.parseInt(hint_Iteration[1]);
            // which array position to add the different results to
            int profileNum = iteration / numOfUploads;

            Object rawValue = data.get(key);

            // print statements to ensure that the cells value are placed into the right array and positions for averaging
           // System.out.println("=================");
          //  System.out.println("Iteration: " + iteration + "\nHint: " + hint + "\nValue: " + data.get(key) + "\nArray Postition: " + profileNum + "\n");

            // if the ordered list hasn't yet initialised this profile's hint's averages map then create it and add the value as its first entry
            if (!ordered.containsKey(hint)) {

                // determine the data type, add it to the appropriate hashmap which is then added to "ordered"
                if (rawValue instanceof Boolean) {
                    HashMap<Integer, Boolean> profilesBooleanHintAverages = new HashMap();
                    Boolean value = (Boolean) rawValue;
                    profilesBooleanHintAverages.put(profileNum, value);
                    ordered.put(hint, profilesBooleanHintAverages);
                //    System.out.println("Created Boolean value (first input)");
                }

                if (rawValue instanceof String) {
                    HashMap<Integer, Double> averageCountMap = new HashMap();
                    HashMap<Integer, Double> profilesDoubleHintAverages = new HashMap();
                    profilesDoubleHintAverages.put(profileNum, Double.parseDouble((String) rawValue));
                    averageCountMap.put(profileNum, 1.0);
                    ordered.put(hint, profilesDoubleHintAverages);
                    averageCounters.put(hint, averageCountMap);
                //    System.out.println("Added new hint [" + hint + "] averaged value for profile [" + profileNum + "] with value [" + ((String) rawValue) + "]");
                }

                // else add the value to the existing profile's hint's averages map, 
            } else {

                if (rawValue instanceof Boolean) {
                    HashMap<Integer, Boolean> PBHA = ordered.get(hint);
                    if (!PBHA.containsKey(profileNum)) {
                        PBHA.put(profileNum, ((Boolean) rawValue));
                        ordered.put(hint, PBHA);
                    } else {
                        if (!PBHA.get(profileNum) && (Boolean) rawValue) {
                            PBHA.put(profileNum, true);
                            ordered.put(hint, PBHA);
                        }
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

                    //    System.out.println("runningAverage " + runningAverage);
                     //   System.out.println("currentCount " + currentCount);
                     //   System.out.println("rawValue " + rawValue);

                        Double av = (runningAverage * currentCount + Double.parseDouble((String) rawValue)) / (currentCount + 1);
                        PDHA.put(profileNum, av);
                      //  System.out.println("Updated hint [" + hint + "] in profilesDoubleHintAverages at [" + profileNum + "] from value [" + runningAverage + "] to value [" + av + "] as the [" + (currentCount + 1) + "] entry");
                        averageCount.put(profileNum, currentCount + 1);
                        averageCounters.put(hint, averageCount);
                        ordered.put(hint, PDHA);

                        // if it doesnt, create the first entry
                    } else {
                        HashMap averageCount = averageCounters.get(hint);
                        averageCount.put(profileNum, 1.0);
                        PDHA.put(profileNum, Double.parseDouble((String) rawValue));
                        averageCounters.put(hint, averageCount);
                        ordered.put(hint, PDHA);
                    }
                }
            }
        }

        // for each profile
        for (int i = 0; i < numOfProfiles; i++) {
         
            logger.debug("Updating hints for Profile: " + i + "\n");

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
                    logger.info("Updated " + key + " : " + intValue.intValue());
                    
                } else {

                    // apply the average value of the current profile to the profile
                    Object value = profilesHintAverages.get(i);

                    if (value instanceof Boolean) {
                        Boolean booleanValue = (Boolean) value;
                        hintProc = controller.hints.get(key);
                        if (booleanValue) {
                            logger.info("Updated " + key + " : true");
                            controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 0.0);
                        } else {
                            logger.info("Updated " + key + " : false");
                            controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], 1.0);
                        }
                    } else {
                        Double doubleValue = (Double) value;
                        hintProc = controller.hints.get(key);
                        logger.info("Updated " + key + " : " + doubleValue);
                        controller.currentGenerationOfProfiles[i] = hintProc.InterpretHintInProfile(controller.currentGenerationOfProfiles[i], doubleValue);
                    }
                }
            }
        }
    }
}
