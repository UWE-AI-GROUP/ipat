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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import org.apache.tomcat.util.digester.ArrayStack;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The Class CSSProcessor processes profiles to generate next generation HTML,
 * CSS and PNG files.
 */
public class UMLProcessor implements Processor {

    private HashMap<String,ArrayList> UsesMap;
    private ArrayList<String> methodList, attributeList;
    ArrayList<String> classNames; 
    
    public UMLProcessor(){
    UsesMap = new HashMap<>();
    methodList = new ArrayList<>();
    attributeList = new ArrayList<>();
    classNames = new ArrayList(Arrays.asList("zero","a", "b","c","d","e","f","g","h","i","j","k","l","m","n","p","q","r","s","t","u","v","w","x","y","z")); 
    }
    /*
    Profile - the profile being applied to artifact
    Artifact - the raw artifact to be processed
    index - the identifier for the profile
    */
    @Override
    public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder) {

        //System.out.println("in umprocessor.applyprofiletoartefact()");
        HashMap<Integer, ArrayList> classMethodsMap = new HashMap();
        HashMap<Integer, ArrayList> classAttributesMap = new HashMap();
        ArrayList<Integer> classesPresent = new ArrayList();
        ArrayList<String> methodsSeen = new ArrayList<>();
        ArrayList<String> attributesSeen = new ArrayList<>();
        HashMap<String,Integer> classAssignments = new HashMap();
        //should really need to clear the hashmaps but might as well
        UsesMap.clear();
        methodList.clear();
        attributeList.clear();
        //read the problem defintion from the xml file
        ReadProblemDefinition(artifact);
        

//1. Read through the profile  and make  lists of which methods and attributes are in which class 
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
    //add it to the list of classes present if not already there
    if ( ! classesPresent.contains(elementClass))
        classesPresent.add(elementClass);
    //get the type of element it is - held as the unit
    String elementtype = ipvar.getUnit();
    //  System.out.println("profile variable " + elementName + "is of type (from unit) " + elementtype);
    
    //add this assignment in this design
    classAssignments.put(elementName, elementClass);
    
    
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
    //and record the fact we haveseem it
    if(elementtype.equalsIgnoreCase("method"))
      {
        classMethodsMap.put(elementClass, membersList);
        methodsSeen.add(elementName);
      }
    else
      {
        classAttributesMap.put(elementClass, membersList);
        attributesSeen.add(elementName);
      }
}

//2 check we have a class for every element in the problem definition and that everything we have aclassfor is in the problem defintion
if( haveSameElements(methodsSeen, methodList)==false)
  {
    System.out.println("problem - the list of methods in the profile is not the same as in the problem defintion");
      System.out.println("defintion has " + methodList.size() + " but profile has " + methodsSeen.size());
  }
 
else if( haveSameElements(attributesSeen, attributeList)==false)
  {
    System.out.println("problem - the list of attributes in the profile is not the same a in the problem defintion");
        System.out.println("defintion has " + attributeList.size() + " but profile has " + attributesSeen.size());
  }
 
else
            ;//System.out.println("problem defintion read from xml matches variables in " + profile.getName());




//2. now make a list of all the in-class and between-class uses in this design candidate


  //2.1start by sorting the list of classes present for appearances sake
 Collections.sort(classesPresent);
 int highestClasses = 0;
 for (int i=0; i<classesPresent.size();i++)
     if(classesPresent.get(i)>highestClasses)
         highestClasses = classesPresent.get(i);
 
        //System.out.println("highest class id used is " + highestClasses);
 int numUses[][] = new int[highestClasses+1][highestClasses+1];
//2.2 loop through each class
for (int i=0;i < classesPresent.size();i++)
    {
      Integer thisClass = classesPresent.get(i);
     //get all the methods in this class 
     ArrayList methodsInthisClass =    classMethodsMap.get(thisClass);
     if(methodsInthisClass!= null)
       {
     //2.3 foreach methid in the class
        for (Iterator<String> iterator = methodsInthisClass.iterator(); iterator.hasNext();)
          {
            String thisMethod = iterator.next();
            //get all of the attributes it uses
            ArrayList<String> thisMethodAtts = UsesMap.get(thisMethod);
              for (Iterator<String> iterator1 = thisMethodAtts.iterator(); iterator1.hasNext();)
                {
                  //2.4 get their name
                  String attrString = iterator1.next();
                  //and then what class they are in
                  int attClass = classAssignments.get(attrString);
                    //System.out.println("dealing with method " + thisMethod + "in class " + thisClass + ": it uses attribute " + attrString + " which is is class " + attClass);
                  //2.5 finally increment the numberof uses
                  numUses[thisClass][attClass]++;
                }         
          }
       }
    }


//3. For each of the classes create a javascript that will display a box that looks like a UML class with the method and attribute names in (if present - their numerical id’s if not)
  
    //initialsie the s tring and the poosiution values
    String jointjsClassesScript =  ""; 
           int xpos=0,ypos=0;
           
           
    //then do the loop
    for (Iterator iterator = classesPresent.iterator(); iterator.hasNext();)
          {
            Integer nextClass = (Integer) iterator.next();
            xpos = xpos + nextClass;
            ypos = ypos+nextClass;
            //create the string to add to our html
            //TODO chaneg the box colour according ot cohesion
            String textToAdd =    classNames.get(nextClass)
                            + ": new uml.Class({position: { x:"
                            + xpos + "  , y: " + ypos+ "},size: { width: 150, height: 100 },name:'"
                            + classNames.get(nextClass)  + "',attributes: [";
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
 
            textToAdd  = textToAdd         +"], \n     methods: [";
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
            textToAdd = textToAdd + "]\n})";
            //now add this string to the one we are building up
            jointjsClassesScript = jointjsClassesScript + textToAdd;
            //and finally either a comma if ther are more classes por a semicolon if this is the last
            if (iterator.hasNext())
                jointjsClassesScript = jointjsClassesScript + ",\n";
            
            
            //move the next box along
            xpos = (xpos+200)%500;
            ypos = (ypos+100)%500;
          }
    
    
      

//
//4. Create arrows whose thickness represents out of class uses and put it on the output frame  (showing the coupling)
String jointjsCouplingScript ="";        //6.1
//for each pair of classes
for(int class1=0;class1<=highestClasses;class1++)
    for(int class2=0;class2<=highestClasses;class2++)
        //if a couple existis between class1 and class2
        if(numUses[class1][class2]>0 && (class1 != class2))
          {
            jointjsCouplingScript = jointjsCouplingScript 
                    + "new joint.dia.Link({ source: { id: classes." 
                    + classNames.get(class1) + ".id }, target: { id: classes." 
                    + classNames.get(class2) +".id }}),";
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
           //  System.out.println("Raw artifact name = " + rawArtifactName + " : profilename = " + profileName);
             processedArtifactName = profileName + "-" +rawArtifactName + ".html";
             //System.out.println("Processed artifact name = " + processedArtifactName);
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
                        htmlFile += jointjsClassesScript ;
                    }
                    //and the coupling between them similarly
                    if (temp.contains("var relations")) {
                        htmlFile += jointjsCouplingScript ;
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

    private void ReadProblemDefinition(Artifact artifact)
      {
        
        try{
            //the problem defintion has the same name a the artefact but with a xml ending
            String problemDefinition = artifact.getFilename();
            String pathToArtefactFile =   artifact.getFile().getParent();
            String pathToXML = pathToArtefactFile.substring(0, pathToArtefactFile.lastIndexOf("Client")) + "samples";
             problemDefinition = problemDefinition.substring(0, problemDefinition.lastIndexOf('.'));
             problemDefinition = problemDefinition + ".xml";
             File definitionFile = new File(pathToXML, problemDefinition);
             //at this stage we'll put a copy in the session directpory as well
             String pathToOutputFile = pathToArtefactFile.substring(0, pathToArtefactFile.lastIndexOf("input")) + "output/" + problemDefinition;
             File copyOfDefinition = new File(pathToOutputFile);
             if(!copyOfDefinition.exists())
                     {
                         System.out.println("putting a copy of the problem definition in the output directory");
                       Files.copy(definitionFile.toPath(), copyOfDefinition.toPath(), StandardCopyOption.REPLACE_EXISTING);
                     }

            
             //now we need to read this xml file into a structure so we can access the uses
            Document XmlDoc = new SAXBuilder().build(definitionFile);

            Element root = XmlDoc.getRootElement();
            Element profileNode = root.getChild("design", root.getNamespace());
            Iterator iterator = profileNode.getChildren().iterator();
			
            while (iterator.hasNext()) 
              {

                Element item = (Element) iterator.next();
                if (item.getName().equalsIgnoreCase("designElement")) 
                  {
                        String name = item.getChildText("name");
                        //if(name!= null) System.out.println("designElement name is " +name);
			String type = item.getChildText("type");
                        //if(type!= null) System.out.println("designElement type is " +type);
                        if(type.equalsIgnoreCase("method"))
                            methodList.add(name);
                        else
                            attributeList.add(name);
                  }
                else if (item.getName().equalsIgnoreCase("designUse")) 
                  {
                        String method = item.getChildText("methodName");
			String attribute = item.getChildText("attributeName");
                        if ( (! methodList.contains(method))|| ( !attributeList.contains(attribute)))
                                {
                                   // System.out.println("declared designUse names method" + method 
                                    //                    + " or attribute " + attribute 
                                    //                  + "that has not been declarted as a design element");
                                    //throw "";
                                }
                        else
                          {
                            //System.out.println("designUse: method " + method 
                             //                           + " uses attribute " + attribute);
                            ArrayList methodUsesList;
                            if(UsesMap.containsKey(method))
                                //get the list of uses associated with this method
                                methodUsesList = UsesMap.get(method);
                            else
                                //or make a new one if it doesn;t exist
                                methodUsesList = new ArrayList();
                            //add the new attribute
                            methodUsesList.add(attribute);
                            //write this back to the HashMap
                            UsesMap.put(method, methodUsesList);
                          }         
                  }
                }  
            //calculatethe total numberof uses
            int totaluses=0;
            for (Map.Entry<String, ArrayList> entrySet : UsesMap.entrySet())
              {
                String key = entrySet.getKey();
                ArrayList value = entrySet.getValue();
                totaluses += value.size();
              }
            //System.out.println("read " + methodList.size() + " methods and " + attributeList.size() + " attributes and " + totaluses + " uses from problem defintion xml file");
            }
         catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }      
      }
     
    
    
    /**
 * Returns if both {@link Collection Collections} contains the same elements, in the same quantities, regardless of order and collection type.
 * <p>
 * Empty collections and {@code null} are regarded as equal.
 */
public static <T> boolean haveSameElements(Collection<T> col1, Collection<T> col2) {
    if (col1 == col2)
        return true;

    // If either list is null, return whether the other is empty
    if (col1 == null)
        return col2.isEmpty();
    if (col2 == null)
        return col1.isEmpty();

    // If lengths are not equal, they can't possibly match
    if (col1.size() != col2.size())
        return false;

    // Helper class, so we don't have to do a whole lot of autoboxing
    class Count
    {
        // Initialize as 1, as we would increment it anyway
        public int count = 1;
    }

    final Map<T, Count> counts = new HashMap<>();

    // Count the items in list1
    for (final T item : col1) {
        final Count count = counts.get(item);
        if (count != null)
            count.count++;
        else
            // If the map doesn't contain the item, put a new count
            counts.put(item, new Count());
    }

    // Subtract the count of items in list2
    for (final T item : col2) {
        final Count count = counts.get(item);
        // If the map doesn't contain the item, or the count is already reduced to 0, the lists are unequal 
        if (count == null || count.count == 0)
            return false;
        count.count--;
    }

    // If any count is nonzero at this point, then the two lists don't match
    for (final Count count : counts.values())
        if (count.count != 0)
            return false;

    return true;
}
    
}


//This is the html