/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.util.HashMap;
import java.util.Set;

/**
 *
 * @author kieran
 */
public class Interaction {

    /**
     * We don't know the order in which hints are initialised in hints.xml so organisation of return values is required
     * @param data
     * @param controller
     */
    public void updateProfileHints(HashMap data, Controller controller) {

        Profile[] profiles = controller.currentGenerationOfProfiles;
        int numOfProfiles = profiles.length;
        int numOfArtifacts = data.size();
        int numOfArtifactsPerProfile = numOfArtifacts / (numOfArtifacts / numOfProfiles);

        // initialise holding structures
        String[] sources = new String[numOfArtifactsPerProfile];
        Integer[] GFContrasts = new Integer[numOfArtifactsPerProfile];
        Integer[] globalScores = new Integer[numOfArtifactsPerProfile];
        Integer[] fontSizes = new Integer[numOfArtifactsPerProfile];
        Boolean[] FGFonts = new Boolean[numOfArtifactsPerProfile];
        Boolean[] BGColours = new Boolean[numOfArtifactsPerProfile];

        // We don't know the order in which hints are initialised in hints.xml so organisation of return values is required
        Set keySet = data.keySet();
        for (Object keySet1 : keySet) {
            String key = (String) keySet1;
            String[] hintAndProfile = key.split("_");
            String hint = hintAndProfile[0];
            int iterationCount = Integer.parseInt(hintAndProfile[1]);
            System.out.println("=================");
            System.out.println("Iteration: " + iterationCount + "\nHint: " + hint + "\nValue: " + data.get(key));

            switch (hint) {
                case "frame":
                        String artifactSource = (String) data.get(key);
                        sources[iterationCount] = artifactSource;
                       // String profile = profileName.substring(profileName.indexOf('_') + 1, profileName.indexOf('-'));
                       
                    break;
                case "globalScore":
                    if (globalScores[iterationCount] == null) {
                        globalScores[iterationCount] = Integer.parseInt((String) data.get(key));
                    } else {
                        globalScores[iterationCount] += Integer.parseInt((String) data.get(key));
                    }
                    break;
                case "ChangeGFContrast":
                    if (GFContrasts[iterationCount] == null) {
                        GFContrasts[iterationCount] = Integer.parseInt((String) data.get(key));
                    } else {
                        GFContrasts[iterationCount] += Integer.parseInt((String) data.get(key));
                    }
                    break;
                case "ChangeFontSize":
                    if (fontSizes[iterationCount] == null) {
                        fontSizes[iterationCount] = Integer.parseInt((String) data.get(key));
                    } else {
                        fontSizes[iterationCount] += Integer.parseInt((String) data.get(key));
                    }
                    break;
                case "FreezeBGColours":
                    if (BGColours[iterationCount] == null) {
                        BGColours[iterationCount] = (Boolean) data.get(key);
                    } else if ((Boolean) data.get(key) == true) {
                        BGColours[iterationCount] = true;
                    }
                    break;
                case "FreezeFGFonts":
                    if (FGFonts[iterationCount] == null) {
                        FGFonts[iterationCount] = (Boolean) data.get(key);
                    } else if ((Boolean) data.get(key) == true) {
                        FGFonts[iterationCount] = true;
                    }
                    break;
                default:
                    System.out.println("Error unrecognised score value in newGenRequest: " + hint);
                    throw new AssertionError();

            }
        }

        for (int i = 0; i < profiles.length; i++) {
            Profile profile = profiles[i];

            profile.setGlobalScore(globalScores[i] / numOfProfiles);
            System.out.println(i + " globalScore " + globalScores[i] / numOfProfiles);

            profile.setChangeFGContrast(GFContrasts[i] / numOfProfiles);
            System.out.println(i + " ChangeGFContrast " + GFContrasts[i] / numOfProfiles);

            profile.setChangeFontSize(fontSizes[i] / numOfProfiles);
            System.out.println(i + " ChangeFontSize " + fontSizes[i] / numOfProfiles);

            if (BGColours[i] == true) {
                profile.setFreezeBGColour(true);
                System.out.println(i + " FreezeBGColour " + "on");
            } else {
                System.out.println(i + " FreezeBGColour " + "off");
            }

            if (FGFonts[i] == true) {
                profile.setFreezeFGFonts(true);
                System.out.println(i + " FreezeFGFonts " + "on");
            } else {
                System.out.println(i + " FreezeFGFonts " + "off");
            }

        }
    }
}
