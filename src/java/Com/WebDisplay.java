/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Algorithms.Hint;
import Src.Artifact;
import Src.Controller;
import Src.Display;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

/**
 *
 * @author kieran
 */
public class WebDisplay implements Display {

    @Override
    public HashMap<String, String> loadWebDisplay(Controller controller) {
        HashMap hintMap = controller.getHints();
        Artifact[] artifacts = controller.processedArtifacts;
        HashMap<String, String> byImageArray = new HashMap<>();
        HashMap<String, String> HM = new HashMap();
        int resultCount = 0;
        String hintString = "";
        String cells = "<div id='tabs-container'><ul class='tabs-menu'>";
        for (int i = 0; i < controller.noOfProfiles; i++) {
            cells += "<li  id='li_" + i + "' onclick='tabClicked(this.id)'><a class='tabText_" + i + "' href='#byProfile_" + i + "'>" + i + "</a></li>";
        }
        cells += " </ul> <div class='tabstuff'>";
        String cell = "";
        for (int i = 0; i < controller.noOfProfiles; i++) {
            cells += "<div id='byProfile_" + i + "' class='tab-content'>";
            for (Artifact artifact : artifacts) {
                cell = "";
                String name = artifact.getFilename().substring(artifact.getFilename().indexOf("-") + 1);
                String[] parts = name.split("-");
                int profileNum = Integer.parseInt(parts[0].substring(parts[0].indexOf("_") + 1));
                if (profileNum == i) {
                    String relativeSrcPath = artifact.getFilepath().substring(artifact.getFilepath().lastIndexOf("Client Data"));
                    cell = "<div class='cell'>" + "<div id='overlay_" + resultCount + "' class='overlay' onclick='frameClick(this.id)'></div>" + "<iframe src='" + relativeSrcPath + "' scrolling='no' class='cellFrames' id='frame_" + resultCount + "' ></iframe>";
                    Set keySet = hintMap.keySet();
                    for (Object key : keySet) {
                        String k = (String) key;
                        Hint h = (Hint) hintMap.get(k);
                        String displaytype = h.getDisplaytype();
                        switch (displaytype) {
                            case "range":
                                cell += "<div class='hint'><input type='range' class='hintScore' id ='" + h.getHintName() + "_" + resultCount + "' min='" + h.getRangeMin() + "' max='" + h.getRangeMax() + "' value='" + h.getDefaultValue() + "' step='1'/><label for='" + h.getHintName() + "_" + resultCount + "' class='label'>" + h.getDisplaytext() + "</label></div>";
                                hintString += h.getHintName() + "_" + resultCount + ",";
                                break;
                            case "checkbox":
                                cell += "<div class='hint'><input type='checkbox' id='" + h.getHintName() + "_" + resultCount + "' class='hintScore' ><label for='" + h.getHintName() + "_" + resultCount + "' class='label'>" + h.getDisplaytext() + "</label></div>";
                                hintString += h.getHintName() + "_" + resultCount + ",";
                                break;
                            default:
                                throw new AssertionError();
                        }
                    }
                    cell += "</div>";
                    resultCount += 1;
                    String key = name.substring(name.indexOf("-") + 1);
                    if (byImageArray.containsKey(key)) {
                        String get = byImageArray.get(key);
                        get += cell;
                        byImageArray.put(key, get);
                    } else {
                        byImageArray.put(key, cell);
                    }
                }
                cells += cell;
            }
            cells += "</div>";
        }
        cells += "</div>";
        HM.put("byProfile", cells);
        cells = "<div id='tabs-container'><ul class='tabs-menu'>";
        Set<String> keySet = byImageArray.keySet();
        Iterator<String> iterator = keySet.iterator();
        int count = 0;
        while (iterator.hasNext()) {
            String next = iterator.next();
            cells += "<li  id='li_" + count + "' onclick='tabClicked(this.id)'><a class='tabText_" + count + "' href='#byImage_" + count + "'>" + next + "</a></li>";
            count++;
        }
        cells += " </ul> <div class='tabstuff'>";
        iterator = keySet.iterator();
        count = 0;
        while (iterator.hasNext()) {
            cells += "<div id='byImage_" + count + "' class='tab-content'>";
            String get = byImageArray.get(iterator.next());
            cells += get + "</div>";
            count++;
        }
        cells += "</div>";
        HM.put("byImage", cells);
        HM.put("hintString", hintString);
        HM.put("count", Integer.toString(artifacts.length));
        return HM;
    }
    
}
