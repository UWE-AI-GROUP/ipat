/*
 * 
 */
package Algorithms;

import Src.Artifact;
import Src.IpatVariable;
import Src.Profile;
import Src.Utils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * The Class CSSProcessor processes profiles to generate next generation HTML,
 * CSS and PNG files.
 */
public class UMLProcessor implements Processor {

    private static final Logger logger = Logger.getLogger(UMLProcessor.class);

    private HashMap<String, ArrayList> UsesMap;
    private ArrayList<String> methodList, attributeList;
    ArrayList<String> classNames;

    // TODO javadoc
    /**
     *
     */
    public UMLProcessor() {
        UsesMap = new HashMap<>();
        methodList = new ArrayList<>();
        attributeList = new ArrayList<>();
        classNames = new ArrayList(Arrays.asList("zero", "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z"));
    }

    /*
     Profile - the profile being applied to artifact
     Artifact - the raw artifact to be processed
     index - the identifier for the profile
     */
    /**
     *
     * @param profile
     * @param artifact
     * @param outputFolder
     * @return
     */
    @Override
    public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder) {

        logger.debug("in UMLProcessor.applyprofiletoartefact()\n");
        HashMap<Integer, ArrayList> classMethodsMap = new HashMap();
        HashMap<Integer, ArrayList> classAttributesMap = new HashMap();
        ArrayList<Integer> classesPresent = new ArrayList();
        ArrayList<String> methodsSeen = new ArrayList<>();
        ArrayList<String> attributesSeen = new ArrayList<>();
        HashMap<String, Integer> classAssignments = new HashMap();
        //should really need to clear the hashmaps but might as well
        UsesMap.clear();
        methodList.clear();
        attributeList.clear();
        //read the problem defintion from the xml file
        ReadProblemDefinition(artifact);

//1. Read through the profile Â and make  lists of which methods and attributes are in which class 
        HashMap<String, IpatVariable> pv = profile.getProfileLevelVariables();
        if (pv == null) {
            logger.error("Error: applyProfileToArtifcat in UMLProcessor. No solution attributes in Profile\n");
        } else {
            for (Map.Entry<String, IpatVariable> entrySet : pv.entrySet()) {
                String elementName = entrySet.getKey();
                IpatVariable ipvar = entrySet.getValue();

                //get the class it is in - held in the SolutionAttribute variable as its value
                Integer elementClass = (int) ipvar.getValue();
                //add it to the list of classes present if not already there
                if (!classesPresent.contains(elementClass)) {
                    classesPresent.add(elementClass);
                }
                //get the type of element it is - held as the unit
                String elementtype = ipvar.getUnit();
                logger.debug("profile variable " + elementName + "is of type (from unit) " + elementtype + "\n");

                //add this assignment in this design
                classAssignments.put(elementName, elementClass);

                ArrayList membersList;
                //now we need to get the correct list of members
                if ((elementtype.equalsIgnoreCase("method")) && (classMethodsMap.containsKey(elementClass))) {
                    membersList = classMethodsMap.get(elementClass);
                } else if ((elementtype.equalsIgnoreCase("attribute")) && (classAttributesMap.containsKey(elementClass))) {
                    membersList = classAttributesMap.get(elementClass);
                } else {
                    membersList = new ArrayList();
                }
                //then add the element to the list of members for this cvlass
                membersList.add(elementName);
                //finally add the changed or new arraylist back to the appropriate hashmap and record the fact we have seen it
                if (elementtype.equalsIgnoreCase("method")) {
                    classMethodsMap.put(elementClass, membersList);
                    methodsSeen.add(elementName);
                } else {
                    classAttributesMap.put(elementClass, membersList);
                    attributesSeen.add(elementName);
                }
            }
        }
        
        //2 check we have a class for every element in the problem definition and that everything we have aclassfor is in the problem defintion
        if (Utils.haveSameElements(methodsSeen, methodList) == false) {
            logger.error("problem - the list of methods in the profile is not the same as in the problem defintion"
                    + "defintion has " + methodList.size() + " but profile has " + methodsSeen.size());
        } else if (Utils.haveSameElements(attributesSeen, attributeList) == false) {
            logger.error("problem - the list of attributes in the profile is not the same a in the problem defintion"
                    + "defintion has " + attributeList.size() + " but profile has " + attributesSeen.size());
        } else {
            logger.debug("problem defintion read from xml matches variables in " + profile.getName());
        }

        //2.1 now make a list of all the in-class and between-class uses in this design candidate
        //2.1 start by sorting the list of classes present for appearances sake
        Collections.sort(classesPresent);
        int highestClasses = 0;
        for (Integer classesPresent1 : classesPresent) {
            if (classesPresent1 > highestClasses) {
                highestClasses = classesPresent1;
            }
        }

        logger.debug("highest class id used is " + highestClasses+"\n");
        int numUses[][] = new int[highestClasses + 1][highestClasses + 1];
        //2.2 loop through each class
        for (Integer thisClass : classesPresent) {
            //get all the methods in this class
            ArrayList methodsInthisClass = classMethodsMap.get(thisClass);
            if (methodsInthisClass != null) {
                //2.3 foreach methid in the class
                for (Iterator<String> iterator = methodsInthisClass.iterator(); iterator.hasNext();) {
                    String thisMethod = iterator.next();
                    //get all of the attributes it uses
                    ArrayList<String> thisMethodAtts = UsesMap.get(thisMethod);
                    for (String attrString : thisMethodAtts) {
                        //and then what class they are in
                        int attClass = classAssignments.get(attrString);
                        logger.debug("dealing with method " + thisMethod + "in class " + thisClass + ": it uses attribute " + attrString + " which is is class " + attClass+"\n");
                        //2.5 finally increment the numberof uses
                        numUses[thisClass][attClass]++;
                    }
                }
            }
        }

        //3. For each of the classes create a javascript that will display a box that looks like a 
        // UML class with the method and attribute names in (if present - their numerical idâ€™s if not)
        // initialsie the s tring and the poosiution values
        String jointjsClassesScript = "";
        double xpos = 0, ypos = 0, height = 0;
        double vertex = 0.0, numVertices = (double) classesPresent.size();
        double halfBoxSize = 250;

        for (Iterator iterator = classesPresent.iterator(); iterator.hasNext();) {
            Integer nextClass = (Integer) iterator.next();
            //put the class boxes at the vertices of a regular polyogn
            xpos = halfBoxSize + halfBoxSize * Math.cos(2 * Math.PI * vertex / numVertices);

            ypos = halfBoxSize + halfBoxSize * Math.sin(2 * Math.PI * vertex / numVertices);
            vertex++;
            //next thtee lines build up the height programmatically
            height = 10; //always be a name
            //then add 10 for each attribute
            if (classAttributesMap.containsKey(nextClass)) {
                ArrayList memberslist = classAttributesMap.get(nextClass);
                height += 20*(memberslist.size());
            }
            else
              {height +=10;}
            if (classMethodsMap.containsKey(nextClass)) {
                ArrayList memberslist = classMethodsMap.get(nextClass);
                height += 20*(memberslist.size());
            }
            else
              {height+=10;}
            //and 10 for each method
            //create the string to add to our html
            //TODO change the box colour according to cohesion
            String textToAdd = classNames.get(nextClass)
                    + ": new uml.Class({position: { x:"
                    + xpos + "  , y: " + ypos + "},size: { width: 150, height: " + height + " },name:'"
                    + classNames.get(nextClass) + "',attributes: [";
            if (classAttributesMap.containsKey(nextClass)) {
                ArrayList memberslist = classAttributesMap.get(nextClass);
                for (Iterator iterator1 = memberslist.iterator(); iterator1.hasNext();) {
                    String nextAttribute = (String) iterator1.next();
                    textToAdd = textToAdd + "'" + nextAttribute + "'";
                    if (iterator1.hasNext()) {
                        textToAdd = textToAdd + ",";
                    }
                }
            }

            textToAdd = textToAdd + "], \n     methods: [";
            if (classMethodsMap.containsKey(nextClass)) {
                ArrayList memberslist = classMethodsMap.get(nextClass);
                for (Iterator iterator1 = memberslist.iterator(); iterator1.hasNext();) {
                    String nextMethod = (String) iterator1.next();
                    textToAdd = textToAdd + "'" + nextMethod + "'";
                    if (iterator1.hasNext()) {
                        textToAdd = textToAdd + ",";
                    }
                }
            }
            textToAdd = textToAdd + "]\n})";
            //now add this string to the one we are building up
            jointjsClassesScript = jointjsClassesScript + textToAdd;
            //and finally either a comma if ther are more classes por a semicolon if this is the last
            if (iterator.hasNext()) {
                jointjsClassesScript = jointjsClassesScript + ",\n";
            }

        }

//
//4. Create arrows whose thickness represents out of class uses and put it on the output frame Â (showing the coupling)
        String jointjsCouplingScript = "";        //6.1
//for each pair of classes
        for (int class1 = 0; class1 <= highestClasses; class1++) {
            for (int class2 = 0; class2 <= highestClasses; class2++) //if a couple existis between class1 and class2
            {
                if (class1 != class2) {
                    for (int j = 0; j < numUses[class1][class2]; j++) {
                        jointjsCouplingScript = jointjsCouplingScript
                                + "new joint.dia.Link({ source: { id: classes."
                                + classNames.get(class1) + ".id }, target: { id: classes."
                                + classNames.get(class2) + ".id }})," + "\n";
                    }
                }
            }
        }
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
            logger.debug ("Raw artifact name = " + rawArtifactName + " : profilename = " + profileName +"\n");
            processedArtifactName = profileName + "-" + rawArtifactName + ".html";
            logger.debug("Processed artifact name = " + processedArtifactName+"\n");
            outHtmlPath = outputFolder + processedArtifactName;
            String htmlFile = "";
            BufferedReader reader = new BufferedReader(new FileReader(artifact.getFile().getAbsolutePath()));
            String temp;
            //so copy each line of the original html file into the new one
            while ((temp = reader.readLine()) != null) {
                // copy the line from the old file into the new one 
                htmlFile += temp + "\n";
                //we want to insert the new classes into the java script in the place they have been defined
                if (temp.contains("var classes")) {
                    htmlFile += jointjsClassesScript;
                }
                //and the coupling between them similarly
                if (temp.contains("var relations")) {
                    htmlFile += jointjsCouplingScript;
                }
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(outHtmlPath))) {
                writer.write(htmlFile);
            }

            return new Artifact(new File(outHtmlPath));
        } catch (Exception e) {
            logger.fatal(e.getMessage() + " In UMLProcessor");
        }
        return null;
    }

    /*
     * 
     * CBSProblem.html (generic) - find XML (singular) with appropriate attributes and methods, create problem definitions
     * by linking up the XML with the problem.html
     */
    private void ReadProblemDefinition(Artifact artifact) {
        try {
  
            FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        //read the xml file with thew problem defintion from the input folder and check there is ionly one
        File[] xmlfiles_list =  artifact.getFile().getParentFile().listFiles(filter);
        if (xmlfiles_list == null) {
            logger.error("Error : xmlfiles_list  == null in UMLPRocessor.RedProblemDefintion. Please check that xml files with problem defintionms were selected\n");
            System.exit(0);
        }
        else if (xmlfiles_list.length>1)
            {
            logger.error("Error : xmlfiles_list  >1in UMLPRocessor.RedProblemDefintion. Please check that only one xml files with problem defintionms was selected\n");
            System.exit(0);
        }
        
        
        // if we;ve got this far then there must be just one problem defintion
        File definitionFile = xmlfiles_list[0];
        String problemDefinitionName = definitionFile.getName();
        String inputFolderPath = definitionFile.getAbsolutePath();
        String outputFileName = inputFolderPath.substring(0, inputFolderPath.lastIndexOf("input")) + "output/" + problemDefinitionName;
            File copyOfDefinition = new File(outputFileName);
            if (!copyOfDefinition.exists()) {
                logger.info("putting a copy of the problem definition in the output directory\n");
                Files.copy(definitionFile.toPath(), copyOfDefinition.toPath(), StandardCopyOption.REPLACE_EXISTING);
            }
            
            
            //now we need to read this xml file into a structure so we can access the uses
            Document XmlDoc = new SAXBuilder().build(definitionFile);

            Element root = XmlDoc.getRootElement();
            Element profileNode = root.getChild("design", root.getNamespace());
            Iterator iterator = profileNode.getChildren().iterator();

            while (iterator.hasNext()) {

                Element item = (Element) iterator.next();
                if (item.getName().equalsIgnoreCase("designElement")) {
                    String name = item.getChildText("name");
                    if(name!= null) logger.error("designElement name is " +name + "\n");
                    String type = item.getChildText("type");
                    if(type!= null) logger.error("designElement type is " +type + "\n");
                    if (type.equalsIgnoreCase("method")) {
                        methodList.add(name);
                    } else {
                        attributeList.add(name);
                    }
                } else if (item.getName().equalsIgnoreCase("designUse")) {
                    String method = item.getChildText("methodName");
                    String attribute = item.getChildText("attributeName");
                    if ((!methodList.contains(method)) || (!attributeList.contains(attribute))) {
                 
                       logger.error("declared designUse names method" +method+ " or attribute " +attribute+ "that has not been declarted as a design element\n");
                      
                    } else {
                        logger.debug("designUse: method " + method + " uses attribute " + attribute + "\n");
                        ArrayList methodUsesList;
                        if (UsesMap.containsKey(method)) //get the list of uses associated with this method
                        {
                            methodUsesList = UsesMap.get(method);
                        } else {
                            //or make a new one if it doesn't exist
                            methodUsesList = new ArrayList();
                        }
                        //add the new attribute
                        methodUsesList.add(attribute);
                      
                        //write this back to the HashMap
                        UsesMap.put(method, methodUsesList);
                    }
                }
            }
            //calculatethe total numberof uses
            int totaluses = 0;
            for (Map.Entry<String, ArrayList> entrySet : UsesMap.entrySet()) {
                String key = entrySet.getKey();
                ArrayList value = entrySet.getValue();
                totaluses += value.size();
            }
            logger.debug("read " + methodList.size() + " methods and " + attributeList.size() + " attributes and " + totaluses + " uses from problem defintion xml file\n");
        } catch (Exception e) {
            logger.fatal(e.getMessage());
        }
    }
}
