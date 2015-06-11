package Src;

/*
 Controller will be responsible for determining from the parameters how many times to call
 metaHeuristic interface in order to generate the next solution based on what kind of heuristic
 it uses.

 When user requests next generation the Controller may also call pKernel virtual display that implements 
 pKernel surrogate model instead of the real web or app several times between actual user interactions via
 the web/app.
 */
import Algorithms.CSSProcessor;
import Algorithms.ESEvolution;
import Algorithms.HintsProcessor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author kieran
 */
public class Controller {

    Display display;
    CSSProcessor cssp = new CSSProcessor();
    Profile currentProfile = null;
    Profile leader = null;
    ESEvolution evolution = new ESEvolution();
    ArrayList<ResultItem> data = new ArrayList<ResultItem>();
    HashMap<String, HintsProcessor> hints = new HashMap<String, HintsProcessor>();
    long lastTime = 0;

    // From GlobalVariables
    /**
     *
     */
    public int iterationCount = 0;

    /**
     *
     */
    public int noOfProfiles = 6;

    /**
     *
     */
    public Artifact[] raw_artifacts;

    /**
     *
     */
    public Artifact[] processedArtifacts;

    /**
     *
     */
    public Profile[] currentGenerationOfProfiles;

    /**
     *
     */
    public static File inputFolder;

    /**
     *
     */
    public static File outputFolder;

    /**
     *
     */
    public static File profileFolder;

    /**
     *
     */
    public static File hintsXML;

    // TODO Constructor which takes Artifact + what view to use
    /**
     *
     * @param inputFolder
     * @param outputFolder
     * @param profileFolder
     * @param hintsXML
     * @throws IOException
     */
    public Controller(File inputFolder, File outputFolder, File profileFolder, File hintsXML) throws IOException {
        this.outputFolder = outputFolder;
        this.inputFolder = inputFolder;
        this.profileFolder = profileFolder;
        this.hintsXML = hintsXML;
    }

    // Generates the first set of results and returns them in the appropriate display to the view
    /**
     *
     * @return
     */
    public HashMap initialArtifacts() {
        bootstrapApplication();
        loadRawArtifacts();
        hints = loadHintsXML();
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        evolution.generateNextSolutions(noOfProfiles);
        for (int i = 0; i < noOfProfiles; i++) {
            currentGenerationOfProfiles[i] = evolution.getNextGenProfileAtIndex(i);
        }
        getResultArtifacts();
        HashMap loadWebDisplay = loadWebDisplay();
        return loadWebDisplay;
    }

    // Generates the next generation of results and returns them to the view
    /**
     *
     * @return
     */
    public HashMap mainloop() {
//        //deal with the hints the user provided
//        Kernel h1Kernel, h2Kernel, pKernel;//used for testing
//        for (int i = 0; i < noOfProfiles; i++) {
//            //I'm putting the code explicitly in here for now - need sorting out but first I want to deal with more generic hints
//            HintsProcessor hint;
//            hint = hints.get("FreezeBGColours");//TODO test that i got pKernel hint back
//            if (currentGenerationOfProfiles[i].isFreezeBGColour()) {
//                currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], 0.0);
//            } else {
//                currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], 1.0);
//            }
//            System.out.println("profile " + i + "freeze BG colours is " + currentGenerationOfProfiles[i].isFreezeBGColour()
//                    + " page bg RGB rates of evolution  are "
//                    + " " + ((SolutionAttributes) (currentGenerationOfProfiles[i].getSolutionAttributes().get("Page_bg_Red"))).getRateOfEvolution()
//                    + " " + ((SolutionAttributes) (currentGenerationOfProfiles[i].getSolutionAttributes().get("Page_bg_Green"))).getRateOfEvolution()
//                    + " " + ((SolutionAttributes) (currentGenerationOfProfiles[i].getSolutionAttributes().get("Page_bg_Blue"))).getRateOfEvolution());
//
//            hint = hints.get("FreezeFGFonts");
//            if (currentGenerationOfProfiles[i].isFreezeFGFonts()) {
//                currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], 0.0);
//            } else {
//                currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], 1.0);
//            }
//
//            h1Kernel = currentGenerationOfProfiles[i].getKernelCalled("h1");
//            h2Kernel = currentGenerationOfProfiles[i].getKernelCalled("h2");
//            pKernel = currentGenerationOfProfiles[i].getKernelCalled("p");
////               System.out.println("profile " + i + "freeze FG fonts is " 
////                        + currentGenerationOfProfiles[i].isFreezeFGFonts() 
////                        + " p italic,  and font-family rates of evolution  are " 
////                        + ((SolutionAttributes)(pKernel.getVariables().get("italic"))).getRateOfEvolution()
////                        + " " + ((SolutionAttributes)(pKernel.getVariables().get("font-family"))).getRateOfEvolution() 
////                        + " h1 bold,  and font-size rates of evolution  are " 
////                        +  ((SolutionAttributes)(h1Kernel.getVariables().get("bold"))).getRateOfEvolution()
////                        +  " " + ((SolutionAttributes)(h1Kernel.getVariables().get("font-size"))).getRateOfEvolution() );
//
//            hint = hints.get("ChangeFontSize");
////              h1Kernel = currentGenerationOfProfiles[i].getKernelCalled("h1");
////              h2Kernel = currentGenerationOfProfiles[i].getKernelCalled("h2");
////              pKernel = currentGenerationOfProfiles[i].getKernelCalled("p");
////               System.out.println("profile " + i 
////                        + " h1, h2 and  p,  font-sizes were  " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("font-size"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("font-size"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("font-size"))).getValue());
//            currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], currentGenerationOfProfiles[i].getChangeFontSize());
////              h1Kernel = currentGenerationOfProfiles[i].getKernelCalled("h1");
////              h2Kernel = currentGenerationOfProfiles[i].getKernelCalled("h2");
////              pKernel = currentGenerationOfProfiles[i].getKernelCalled("p");
////               System.out.println("profile " + i + "ChangeFontSize is " 
////                        + currentGenerationOfProfiles[i].getChangeFontSize() 
////                        + " h1, h2 and  p,  font-sizes are now  " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("font-size"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("font-size"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("font-size"))).getValue());
//
//            hint = hints.get("ChangeFGContrast");
//            h1Kernel = currentGenerationOfProfiles[i].getKernelCalled("h1");
//            h2Kernel = currentGenerationOfProfiles[i].getKernelCalled("h2");
//            pKernel = currentGenerationOfProfiles[i].getKernelCalled("p");
////               System.out.println("profile " + i 
////                        + " h1, h2 and  p,  bold values were   " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("bold"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("bold"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("bold"))).getValue()
////                        + " h1, h2 and  p,  italic values were   " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("italic"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("italic"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("italic"))).getValue()               );
//            currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], currentGenerationOfProfiles[i].getChangeFGContrast());
////              h1Kernel = currentGenerationOfProfiles[i].getKernelCalled("h1");
////              h2Kernel = currentGenerationOfProfiles[i].getKernelCalled("h2");
////              pKernel = currentGenerationOfProfiles[i].getKernelCalled("p");
////               System.out.println("profile " + i + "ChangeFGContrast is " 
////                        + currentGenerationOfProfiles[i].getChangeFGContrast() 
////                        + " h1, h2 and  p,  bold values are now   " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("bold"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("bold"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("bold"))).getValue()
////                        + " h1, h2 and  p,  italic values are now   " 
////                        + " " +((SolutionAttributes)(h1Kernel.getVariables().get("italic"))).getValue()
////                        + " " +((SolutionAttributes)(h2Kernel.getVariables().get("italic"))).getValue()
////                        + " " +((SolutionAttributes)(pKernel.getVariables().get("italic"))).getValue()               );
//        }

        //tell the metaheuristic to update its working memory
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        //now you are ready to create the next generation - which since they all were sorted the same should contain all the initial provided profiles
        evolution.generateNextSolutions(noOfProfiles);
        for (int i = 0; i < noOfProfiles; i++) {
            currentGenerationOfProfiles[i] = evolution.getNextGenProfileAtIndex(i);
            //System.out.println("in controller.mainloop() just read profile with name " + currentGenerationOfProfiles[i].getName());
        }
        //now apply those profiles ot the raw artifacts to get something to display
        getResultArtifacts();
        // load user feedback back into the appropriate parameter values (e.g. profile.globalscore) in currentGenerationOfProfiles
        return loadWebDisplay();
    }

    // initialises the Profiles in memory from the files in the profile folder, generating new ones if the count is <6
    // or if the count is >9 accepts only 6 of them 
    private void bootstrapApplication() {

        //set up pKernel filter ot pick up all the filesending with .xml
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        //create an array holding all the files ending .xml in the profile folder to act as seeds
        File[] profiles_list = profileFolder.listFiles(filter);
        if (profiles_list == null) {
            System.out.println("Error : profiles_list  == null in bootstrap application. Please check the web.xml in WEB-INF to ensure paths to config folders are correct.");
        }

        //declare an array to hold the new profiles
        File[] new_profiles_list = new File[noOfProfiles];

        //next steps depend on  number of seeds
        if (profiles_list == null || profiles_list.length < noOfProfiles) {// if there are no, or less than desired numner of seeds
            int i = 0;
            //copy those we have
            for (i = 0; i < profiles_list.length; i++) {
                new_profiles_list[i] = profiles_list[i];
            }
            //work out how many to make
            int diffOfNrOfProfilesToMake = noOfProfiles - profiles_list.length;
            for (int j = 0; j < diffOfNrOfProfilesToMake; j++) {
                new_profiles_list[i + j] = profiles_list[1];
            }
            System.out.println("Found only " + profiles_list.length + " profiles, randomly generated remaining " + diffOfNrOfProfilesToMake);

        } else if (profiles_list.length > noOfProfiles
                && profiles_list.length < 9) {//if there ar pKernel few more but less than 9
            //just increase the number used and copy all the seeds
            noOfProfiles = profiles_list.length;
            new_profiles_list = new File[noOfProfiles];
            new_profiles_list = profiles_list;
        } else {//we had just the right number - or more than 9
            for (int i = 0; i < noOfProfiles; i++) {
                new_profiles_list[i] = profiles_list[i];
            }
        }
        //copy the updated list of prpfile names back into the profiles list array of file names
      //  profiles_list = new_profiles_list;

        //finally create the first generation
        //declare an array to hold the next gneration of profiles
        currentGenerationOfProfiles = new Profile[noOfProfiles];
        //and for each one read the actual profile from the relevant file
        for (int i = 0; i < profiles_list.length; i++) {
            {
                currentGenerationOfProfiles[i] = Utils.getProfileFromFile(new_profiles_list[i]);
                // randomise the generated extra profiles
                if (i > profiles_list.length) {
                    currentGenerationOfProfiles[i].randomiseProfileVariableValues();
                }
            }
            System.out.println(currentGenerationOfProfiles[i].getName());
        }
    }

    // populates the Artifacts in memory from the new_profiles_list found in the input folder
    private void loadRawArtifacts() {
        //create pKernel filter to pick put all the files ending .htm
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
               boolean bool = name.endsWith(".xml") || name.endsWith(".htm");
                return bool;
            }
        };
        //create an array of all the files in the inpurfolder using the filter        
        File[] file = inputFolder.listFiles(filter);
        //alocate space for arraty of artefacts - one for eacg file
        raw_artifacts = new Artifact[file.length];
        //read in each artefact from file and store in the array
        for (int i = 0; i < file.length; i++) {
            Artifact artifact = new Artifact(file[i]);
            raw_artifacts[i] = artifact;
        }
    }

    // Responsible for applying profiles to artifacts
    private void getResultArtifacts() {

        Artifact rawArtifact;
        Artifact processedArtifact = null;
        processedArtifacts = new Artifact[raw_artifacts.length * noOfProfiles];
        int count = 0;

        // Generate CSS based on the new profiles
        for (int profileID = 0; profileID < noOfProfiles; profileID++) {
            currentProfile = currentGenerationOfProfiles[profileID];
            // TESTING : distinguishing the Raw artifact name from the processed one (raw artifact)
            //  System.out.println("Processing : " + currentProfile.getName());

            // Process the profile to generate CSS
            for (int artifactID = 0; artifactID < raw_artifacts.length; artifactID++) {

                rawArtifact = raw_artifacts[artifactID];
                //System.out.println(rawArtifact.getFilename());
                processedArtifact = cssp.applyProfileToArtifact(currentProfile, rawArtifact, outputFolder.getAbsolutePath() + "/");
                processedArtifacts[count] = processedArtifact;
                count++;

            }
        }
    }

    public HashMap loadHintsXML() {

        HashMap<String, HintsProcessor> hintMap = new HashMap<>();

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(hintsXML);
            // doc.getDocumentElement().normalize();
            NodeList interactionList = doc.getElementsByTagName("interaction");

            for (int i = 0; i < interactionList.getLength(); i++) {
                Node item = interactionList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element interaction = (Element) interactionList.item(i);
                    HintsProcessor hint = new HintsProcessor();
                    NodeList elements = interaction.getChildNodes();
                    for (int j = 0; j < elements.getLength(); j++) {
                        Node attribute = elements.item(j);
                        if (attribute.getNodeType() == Node.ELEMENT_NODE) {
                            Element element = (Element) elements.item(j);
                            String att = element.getTagName();
                            String value = element.getTextContent();
                            if (value != null) {
                                value = value.trim().replaceAll("\\s+", " ");
                                switch (att) {
                                    case "name":
                                        hint.setHintName(value);
                                        break;
                                    case "displaytype":
                                        hint.setDisplaytype(value);
                                        break;
                                    case "displayText":
                                        hint.setDisplaytext(value);
                                        break;
                                    case "default":
                                        hint.setDefaultValue(value);
                                        break;
                                    case "range-min":
                                        hint.setRangeMin(Double.parseDouble(value));
                                        break;
                                    case "range-max":
                                        hint.setRangeMax(Double.parseDouble(value));
                                        break;
                                    case "KernelAffected":
                                        hint.AddAffectedKernel(value);
                                        break;
                                    case "KernelVariableAffected":
                                        hint.addAffectedKernelVariable(value);
                                        break;
                                    case "ProfileVariablesAffected":
                                        hint.addAffectedProfileVariable(value);
                                        break;
                                    case "Effect":
                                        hint.setEffect(value);
                                        break;
                                    default:

                                        System.out.println("Error with Hint [ " + i + " ] = Tag: " + att + " / Value: " + value);
                                        System.out.println("Check hints.xml for incorrect Tag names");
                                        throw new AssertionError();
                                }
                            }
                        }
                    }
                    hintMap.put(hint.getHintName(), hint);
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException | DOMException e) {
            System.out.println(e.getMessage());
        }
        return hintMap;
    }

    public HashMap loadWebDisplay() {

        HashMap hintMap = this.hints;
        Artifact[] artifacts = this.processedArtifacts;
        HashMap<String, String> byImageArray = new HashMap<>();
        HashMap<String, String> HM = new HashMap();
        int resultCount = 0;
        String hintString = "";

        // [layer one] create the list for the profile tabs 
        String cells = "<div id='tabs-container'><ul class='tabs-menu'>"; // tabs menu div
        for (int i = 0; i < noOfProfiles; i++) {
            cells += "<li  id='li_" + i + "' onclick='tabClicked(this.id)'><a class='tabText_" + i + "' href='#byProfile_" + i + "'>" + i + "</a></li>";
        }

        // [layer two] create div which will contain all the seporate tabs and their cells this is needed for the CSS 
        cells += " </ul> <div class='tabstuff'>"; // tabStuff div
        String cell = "";
        // populate div sections containing tables for each profile tab
        for (int i = 0; i < noOfProfiles; i++) {
            cells += "<div id='byProfile_" + i + "' class='tab-content'>"; // byProfile div
            for (Artifact artifact : artifacts) {
                cell = "";
                // if the artifact corresponds to the profile number, print out the cell
                String name = artifact.getFilename().substring(artifact.getFilename().indexOf("-") + 1);
                String[] parts = name.split("-");
                int profileNum = Integer.parseInt(parts[0].substring(parts[0].indexOf("_") + 1));
                if (profileNum == i) {
                    // get the string for the artifact file being displayed
                    String relativeSrcPath = artifact.getFilepath().substring(artifact.getFilepath().lastIndexOf("Client Data"));
                    // this section of the cell will contain the image itself, as well as the overlay which allows it to be previewed-(see javascript)
                    cell = "<div class='cell'>" // cell div
                            + "<div id='overlay_" + resultCount + "' class='overlay' onclick='frameClick(this.id)'></div>"
                            + "<iframe src='" + relativeSrcPath + "' scrolling='no' class='cellFrames' id='frame_" + resultCount + "' ></iframe>";
                    // read off the hints and apply their values to the cell, populating it with options for the user to choose and input data with
                    Set keySet = hintMap.keySet();
                    for (Object key : keySet) {
                        String k = (String) key;
                        HintsProcessor h = (HintsProcessor) hintMap.get(k);
                        String displaytype = h.getDisplaytype();

                        // ***ADD ADDITIONAL HINT INPUTS ↓HERE IN THIS SWITCH STATEMENT↓ AND FOLLOW THE CONVENTION SET OUT***
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

                    cell += "</div>"; // cell div close
                    resultCount += 1;

                    // populate the array which will display the "byImage" View
                    String key = name.substring(name.indexOf("-") + 1);
                    if (byImageArray.containsKey(key)) {
                        String get = byImageArray.get(key);
                        get += cell;
                        byImageArray.put(key, get);
                    } else {
                        byImageArray.put(key, cell);
                    }
                }
                cells += cell; // byProfile div close
            }
            cells += "</div>";
        }
        cells += "</div>"; // tabStuff div close

        HM.put("byProfile", cells);

        //=================================================================
        // Create byImage String for view.
        cells = "<div id='tabs-container'><ul class='tabs-menu'>"; // div_1
        Set<String> keySet = byImageArray.keySet();
        Iterator<String> iterator = keySet.iterator();
        int count = 0;

        while (iterator.hasNext()) {
            String next = iterator.next();
            cells += "<li  id='li_" + count + "' onclick='tabClicked(this.id)'><a class='tabText_" + count + "' href='#byImage_" + count + "'>" + next + "</a></li>";
            count++;
        }
        cells += " </ul> <div class='tabstuff'>"; // div_2
        iterator = keySet.iterator();
        count = 0;

        while (iterator.hasNext()) {
            cells += "<div id='byImage_" + count + "' class='tab-content'>"; // div_3
            String get = byImageArray.get(iterator.next());
            cells += get; // ###########################################JEFNPAOSJG{
            count++;
        }
        cells += "</div>"; // div_/2
        HM.put("byImage", cells);

        HM.put("hintString", hintString);
        HM.put("count", Integer.toString(artifacts.length));
        return HM;
    }
}
