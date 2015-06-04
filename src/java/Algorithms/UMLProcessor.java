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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

/**
 * The Class CSSProcessor processes profiles to generate next generation HTML,
 * CSS and PNG files.
 */
public class UMLProcessor implements Processor {

    private Hashtable cssLabels;
  
    
    public UMLProcessor() {
       this.cssLabels = setupCSSLabelStore();
      
    }

    /*
    Profile - the profile being applied to artifact
    Artifact - the raw artifact to be processed
    index - the identifier for the profile
    */
    @Override
    public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder) {

        HashMap<Integer, ArrayList> classMethodsMap = new HashMap();
        HashMap<Integer, ArrayList> classAttributesMap = new HashMap();
        Set classesPresent = new HashSet();
        
//1. Create new html content to display the processed uml
        String jointjsScriptStart = "var graph = new joint.dia.Graph;\n " 
                                  + "\n +var paper = new joint.dia.Paper({" 
                                  + "\n    el: $('#paper'),\n    width: 800,\n    height: 600,\n    gridSize: 1,\n    model: graph\n});\n" 
                                  + "\n\n var uml = joint.shapes.uml;\n \n var classes = {";
        String jointjsScript =  jointjsScriptStart;  
        

//2. Read through the profile  and make  lists of which methods and attributes are in which class 
Hashtable pv = profile.getSolutionAttributes();
if (pv == null) { System.out.println("Error: applyProfileToArtifcat in UMLProcessor. No solution attributes in Profile.");}
Enumeration pvarsEnu = pv.keys();
while (pvarsEnu.hasMoreElements()) 
  {
    //get name of elements as a string
    String elementName = pvarsEnu.nextElement().toString();
    //get the class it is in - held in the SolutionAttribute variable as its value
    SolutionAttributes ipvar = (SolutionAttributes) pv.get(elementName);
    Integer elementClass = (int) ipvar.getValue();
       //add it to the list of classes present
    classesPresent.add(elementClass);
    //gbet the type of elememnt it is - held as the unit
    String elementtype = ipvar.getUnit();
    
    ArrayList membersList;
    //now we need to get the correct list of members
    if((elementtype.equalsIgnoreCase("method"))&& (classMethodsMap.containsKey(elementClass)))
            membersList = classMethodsMap.get(elementClass);
    else if ((elementtype.equalsIgnoreCase("attribute"))&& (classAttributesMap.containsKey(elementClass)))
            membersList = classAttributesMap.get(elementClass);
    else
      membersList = new ArrayList();
    //then add the element to the list of members for this cvlass
    membersList.add(elementName);
     //finally add the changed or new arraylist back to the appropriate hashmap
    if(elementtype.equalsIgnoreCase("method"))
        classMethodsMap .put(elementClass, membersList);
    else
        classAttributesMap.put(elementClass, membersList);
}



//3. For each of the classes create a box that looks like a UML class with the method and attribute names in (if present - their numerical id’s if not)
        for (Iterator iterator = classesPresent.iterator(); iterator.hasNext();)
          {
            Integer nextClass = (Integer) iterator.next();
            //create the string to add to our html
            String textToAdd = String.valueOf(nextClass) + ": new uml.Class({position: { x:300  , y: 50 },size: { width: 240, height: 100 },name: '"
                    + String.valueOf(nextClass)  + "',attributes: ['";
            if(classAttributesMap.containsKey(nextClass))
              {
                ArrayList memberslist = classAttributesMap.get(nextClass);
                  for (Iterator iterator1 = memberslist.iterator(); iterator1.hasNext();)
                    {
                      String nextAttribute = (String) iterator1.next();
                      textToAdd = textToAdd + "'" + nextAttribute + "'";
                      if (iterator1.hasNext())
                          textToAdd = textToAdd + ",";
                    }
              }
 
            textToAdd  = textToAdd         +"'], \n     methods: ['";
            if(classMethodsMap.containsKey(nextClass))
              {
                ArrayList memberslist = classMethodsMap.get(nextClass);
                  for (Iterator iterator1 = memberslist.iterator(); iterator1.hasNext();)
                    {
                      String nextMethod = (String) iterator1.next();
                      textToAdd = textToAdd + "'" + nextMethod + "'";
                      if (iterator1.hasNext())
                          textToAdd = textToAdd + ",";
                    }
              }
            textToAdd = textToAdd + "']\n})";
            //noew add this string to the one we are building up
            jointjsScript = jointjsScript + textToAdd;
            //and finally either a comma if ther are more classes por a semicolon if this is the last
            if (iterator.hasNext())
                jointjsScript = jointjsScript + ",\n";
            else
                jointjsScript = jointjsScript + "};\n";
          }
    
    
        
        
 //5. Read in the use matrix from the raw artefact and make a list of:
//- the numbers of uses going from each class to each other
//- the number of uses within each class

//
//6. Create arrows whose thickness represents out of class uses and put it on the output frame  (showing the coupling)
        //5. Place those in the output frame within the html of the processed artefact
//7. Assign a background colour to box for each class that reflects the level of internal uses (related to cohesion)
//8. Possibly reposition the classes in the display to minimise the number of crossing arrows so the result is clearer

       


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
             processedArtifactName = profileName + "-" +rawArtifactName + ".html";
             System.out.println("Processed artifact name = " + processedArtifactName);
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

               BufferedWriter  writer = new BufferedWriter(new FileWriter(outHtmlPath));
                writer.write(htmlFile);
                writer.close();
              
                return new Artifact(new File(outHtmlPath));
        } catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }
        return null;
    }

     
    
}


//This is the html