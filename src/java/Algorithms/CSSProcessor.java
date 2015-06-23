/*
 * 
 */
package Algorithms;

import Src.Artifact;
import Src.SolutionAttributes;
import Src.Kernel;
import Src.Profile;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;
import org.apache.log4j.Logger;

/**
 * The Class CSSProcessor processes profiles to generate next generation HTML,
 * CSS and PNG files.
 */
public class CSSProcessor implements Processor {
private static final Logger logger = Logger.getLogger(CSSProcessor.class);
    private HashMap cssLabels;

    public CSSProcessor() {
        this.cssLabels = setupCSSLabelStore();

    }

    /*
     Profile - the profile being applied to artifact
     Artifact - the raw artifact to be processed
     index - the identifier for the profile
     */
    @Override
    public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder) {

        HashMap kernels = profile.getKernels();
        if (kernels == null) {
            logger.error("Error: applyProfileToArtifcat in CSSProcessor. No kernels present in Profile.");
        }

        // ----------- CSS Formatters -------------------------------//
        String CSS_Start_Braces = "{";
        String CSS_End_Braces = "}";
        String CSS_PropSeparator = ":";
        String CSS_PropPairSeparator = ";";

        // ------------ CSS Generation ------------------------------//
        String css = "";
        HashMap pv = profile.getSolutionAttributes();
        if (pv == null) {
            logger.error("Error: applyProfileToArtifcat in CSSProcessor. No solution attributes in Profile.");
        }
        Set keySet = pv.keySet();
        Iterator iterator = keySet.iterator();
        String csspLine = "body{";
        int colorCheck = 0;
        int red = 0;
        int blue = 0;
        int green = 0;
        while (iterator.hasNext()) {
            String vkey = iterator.next().toString();
            SolutionAttributes ipvar = (SolutionAttributes) pv.get(vkey);

            if (ipvar.getName().contains("Page")) {
                colorCheck++;
                if (ipvar.getName().contains("Red")) {
                    Double dd = ipvar.getValue();
                    red = dd.intValue();
                }
                if (ipvar.getName().contains("Blue")) {
                    Double dd = ipvar.getValue();
                    blue = dd.intValue();
                }
                if (ipvar.getName().contains("Green")) {
                    Double dd = ipvar.getValue();
                    green = dd.intValue();
                }
                if (colorCheck >= 3) {
                    csspLine += "background-color:rgb(" + red + "," + blue + "," + green + ");";
                }
            }
        }

        csspLine += CSS_End_Braces + "\n";
        css += csspLine;
        Set keySet1 = kernels.keySet();
        Iterator kernelsEnuTemp = keySet1.iterator();
        String[] tempVector = new String[kernels.size()];
        int k = 3;
        while (kernelsEnuTemp.hasNext()) {
            String kernelName = kernelsEnuTemp.next().toString();
            if (kernelName.equalsIgnoreCase("h1")) {
                tempVector[0] = kernelName;
            } else if (kernelName.equalsIgnoreCase("h2")) {
                tempVector[1] = kernelName;
            } else if (kernelName.equalsIgnoreCase("p")) {
                tempVector[2] = kernelName;
            } else {
                tempVector[k] = kernelName;
                k++;
            }
        }
        Vector tempVector2 = new Vector();
        for (int n = 0; n < tempVector.length; n++) {
            tempVector2.add(n, tempVector[n]);
        }
        double CSS_lastfontsize = 72.0;

        Enumeration kernelsEnu = tempVector2.elements();
        while (kernelsEnu.hasMoreElements()) {
            String cssLine = "";
            String ktype = kernelsEnu.nextElement().toString();
            Kernel kernel1 = (Kernel) kernels.get(ktype);
            cssLine += kernel1.getName() + CSS_Start_Braces;
            HashMap vars = kernel1.getVariables();
            Set keySet2 = vars.keySet();

            Iterator evars = keySet2.iterator();
            colorCheck = 0;
            red = 0;
            blue = 0;
            green = 0;
            while (evars.hasNext()) {
                String vkey = evars.next().toString();
                SolutionAttributes ipvar = (SolutionAttributes) vars.get(vkey);

                if (ipvar.getName().contains("color")) {
                    colorCheck++;
                    if (ipvar.getName().contains("red")) {
                        Double dd = ipvar.getValue();
                        red = dd.intValue();
                    }
                    if (ipvar.getName().contains("blue")) {
                        Double dd = ipvar.getValue();
                        blue = dd.intValue();
                    }
                    if (ipvar.getName().contains("green")) {
                        Double dd = ipvar.getValue();
                        green = dd.intValue();
                    }
                    if (colorCheck >= 3) {
                        cssLine += "color:rgb(" + red + "," + blue + "," + green + ");";
                    }
                } else {
                    if (ipvar.getType().equalsIgnoreCase("cardinal")) {
                        Double val = ipvar.getValue();
                        Vector values = (Vector) cssLabels.get(ipvar.getName());
                        String value = (String) values.get(val.intValue());
                        cssLine += ipvar.getName() + CSS_PropSeparator + value
                                + CSS_PropPairSeparator;

                    } else if (ipvar.getType().equalsIgnoreCase("ordinal")) {
                        Double val = ipvar.getValue();

                        if (ktype.equalsIgnoreCase("h1")) {
                            val = CSS_lastfontsize * ((val / 100));
                            CSS_lastfontsize = val;

                        } else if (ktype.equalsIgnoreCase("h2") || ktype.equalsIgnoreCase("p")) {
                            val = CSS_lastfontsize * 0.5 * (1.0 + (val) / 100);
                            CSS_lastfontsize = val;
                        }

                        cssLine += ipvar.getName() + CSS_PropSeparator + val.intValue()
                                + ipvar.getUnit() + CSS_PropPairSeparator;
                    } else if (ipvar.getType().equalsIgnoreCase("boolean")) {
                        if (ipvar.getValue() == 1.0) {
                            cssLine += "font-style" + CSS_PropSeparator + ipvar.getName() + CSS_PropPairSeparator;
                        }
                    }
                }
            }
            cssLine += CSS_End_Braces + "\n";
            css += cssLine;
        }
        String CSS = css;

        // ---------- Filenames Generation------------//
        String outHtmlPath;
        String processedArtifactName;
        String profileName = profile.getName();
        // just want the name of the profile without the .xml extension
        profileName = profileName.substring(0, profileName.lastIndexOf('.'));

        try {
            String rawArtifactName = artifact.getFilename();
            rawArtifactName = rawArtifactName.substring(0, rawArtifactName.lastIndexOf('.'));
             // TESTING : distinguishing the raw artifact name from the processed one (processed one)
            //  System.out.println("Raw artifact name = " + rawArtifactName + " : profilename = " + profileName);
            processedArtifactName = profileName + "-" + rawArtifactName + ".html";
            // System.out.println("Processed artifact name = " + processedArtifactName);
            outHtmlPath = outputFolder + processedArtifactName;
            String htmlFile = "";
            BufferedReader reader = new BufferedReader(new FileReader(artifact.getFile().getAbsolutePath()));
            String temp;
            while ((temp = reader.readLine()) != null) {
                htmlFile += temp + "\n";
                if (temp.contains("<head>")) {
                    htmlFile += "<style type=\"text/css\">";
                    htmlFile += CSS;
                    htmlFile += "</style>";
                }
            }

            BufferedWriter writer = new BufferedWriter(new FileWriter(outHtmlPath));
            writer.write(htmlFile);
            writer.close();

            return new Artifact(new File(outHtmlPath));
        } catch (Exception e) {
          logger.fatal(e.getMessage());
        }
        return null;
    }

    public HashMap setupCSSLabelStore() {
        // Cardinal variables store

        // The fontfamilies.
        String[] fontfamilies
                = {"Arial Black", "Calibiri", "Helvetica", "Courier", "Times",
                    "sans-serif", "Console", "Tahoma", "Century Gothic",
                    "Palatino", "Cambria"};

        //The floatvals.
        String[] floatvals
                = {"left", "right", "top", "bottom", "center"};

        //The margin.
        String[] margin
                = {"0px 0px 10px 10px", "10px 10px 0px 0px", "0px 10px 10px 0px",
                    "10px 0px 0px 10px", "10px 0px 10px 0px"};

        HashMap cssStore = new HashMap();

        Vector temp = new Vector();
        for (int i = 0; i < fontfamilies.length; i++) {
            temp.add(fontfamilies[i]);
        }
        cssStore.put("font-family", temp);

        temp = new Vector();
        for (int i = 0; i < floatvals.length; i++) {
            temp.add(floatvals[i]);
        }
        cssStore.put("float", temp);

        temp = new Vector();
        for (int i = 0; i < margin.length; i++) {
            temp.add(margin[i]);
        }
        cssStore.put("margin", temp);

        return cssStore;
    }

}