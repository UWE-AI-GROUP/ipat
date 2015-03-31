package Src;

/*
 Controller will be responsible for determining from the parameters how many times to call
 metaHeuristic interface in order to generate the next solution based on what kind of heuristic
 it uses.

 When user requests next generation the Controller may also call a virtual display that implements 
 a surrogate model instead of the real web or app several times between actual user interactions via
 the web/app.
 */
import Algorithms.CSSProcessor;
import Algorithms.ESEvolution;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class Controller {

    File inputFolder;
    File outputFolder;
    File profileFolder;
    Display display;
    GlobalVariables globalVariables = new GlobalVariables();
    CSSProcessor cssp = new CSSProcessor(globalVariables);
    Profile currentProfile = null;
    Profile leader = null;
    ESEvolution evolution = new ESEvolution();
    Vector<ResultItem> data = new Vector<ResultItem>();
    long lastTime = 0;

    // From GlobalVariables
    public int iterationCount = 0;
    public int noOfProfiles = 6;
    public Artifact[] raw_artifacts;
    public Artifact[] processedArtifacts;
    public Profile[] nextGenerationOfProfiles;
    public Profile[] currentGenerationOfProfiles;

    // TODO Constructor which takes Artifact + what view to use
    public Controller(File inputFolder, File outputFolder, File profileFolder) throws IOException {
        this.outputFolder = outputFolder;
        this.inputFolder = inputFolder;
        this.profileFolder = profileFolder;
        System.out.println("input Folder : " + inputFolder);
        System.out.println("output Folder : " + outputFolder);
        System.out.println("profiles Folder : " + profileFolder);
    }

    // Generates the first set of results and returns them in the appropriate display to the view
    public void initialArtifacts() {
        bootstrapApplication();
        loadRawArtifacts();
       // initiateVariables();
        copyNextGenerationProfiles();
        getResultArtifacts();
    }

    // initialises the Profiles from the profile folder, generating new ones if the count is <6
    // or if the count is >9 takes only 6 of them 
    private void bootstrapApplication() {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        FilenameFilter filter2 = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith(Integer.toString(iterationCount));
            }
        };

        File[] profiles_list = profileFolder.listFiles(filter);
        File[] new_profiles_list = new File[noOfProfiles];

        if (profiles_list == null || profiles_list.length < noOfProfiles) {
            int diffOfNrOfProfilesToMake = noOfProfiles - profiles_list.length;
            int i = 0;
            for (i = 0; i < profiles_list.length; i++) {
                new_profiles_list[i] = profiles_list[i];
            }
            for (int j = 0; j < diffOfNrOfProfilesToMake; j++) {
                new_profiles_list[i + j] = profiles_list[1];// IpatEA.make_random_profile();
            }
            System.out.println("Found only " + profiles_list.length + " profiles, randomly generated remaining " + diffOfNrOfProfilesToMake);

        } else if (profiles_list.length > noOfProfiles
                && profiles_list.length < 9) {
            noOfProfiles = profiles_list.length;
            new_profiles_list = new File[noOfProfiles];
            new_profiles_list = profiles_list;
        } else {
            for (int i = 0; i < noOfProfiles; i++) {
                new_profiles_list[i] = profiles_list[i];
            }
        }

        profiles_list = new_profiles_list;
        currentGenerationOfProfiles = new Profile[noOfProfiles];
        nextGenerationOfProfiles = new Profile[noOfProfiles];

        for (int i = 0; i < profiles_list.length; i++) {
            currentGenerationOfProfiles[i] = getProfile(profiles_list[i]);
            System.out.println(currentGenerationOfProfiles[i].getName());
        }
    }

    // TODO refactor email Jim about
    public Profile getProfile(File file) {
        Profile profile = new Profile(file);
        try {
            Document XmlDoc = new SAXBuilder().build(file);

            Element root = XmlDoc.getRootElement();
            Element profileNode = root.getChild("profile", root.getNamespace());
            Iterator iterator = profileNode.getChildren().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Element hint = (Element) iterator.next();
                if (hint.getName().equalsIgnoreCase("variable")) {
                    String name = hint.getChildText("name");
                    String type = hint.getChildText("type");

                    String temp = hint.getChildText("lbound");
                    Double dub = new Double(temp);
                    double lbound = dub.doubleValue();

                    temp = hint.getChildText("ubound");
                    dub = new Double(temp);
                    double ubound = dub.doubleValue();

                    temp = hint.getChildText("granularity");
                    dub = new Double(temp);
                    double granularity = dub.doubleValue();

                    temp = hint.getChildText("rateOfEvolution");
                    dub = new Double(temp);
                    double rateOfEvolution = dub.doubleValue();

                    temp = hint.getChildText("value");
                    dub = new Double(temp);
                    double value = dub.doubleValue();

                    String dfault = hint.getChildText("default");
                    String flag = hint.getChildText("flag");
                    String unit = hint.getChildText("unit");

                    SolutionAttributes variable = new SolutionAttributes(name, type,
                            lbound, ubound, granularity, rateOfEvolution, value, dfault, flag, unit);
                    profile.addVaraiable(variable);
                } else if (hint.getName().equalsIgnoreCase("kernel")) {

                    Iterator it = hint.getChildren().iterator();
                    Element nm = (Element) it.next();
                    String kernelName = nm.getText();
                    Hashtable vars = new Hashtable();
                    while (it.hasNext()) {
                        Element hintt = (Element) it.next();
                        String name = hintt.getChildText("name");
                        String type = hintt.getChildText("type");

                        String temp = hintt.getChildText("lbound");
                        Double dub = new Double(temp);
                        double lbound = dub.doubleValue();

                        temp = hintt.getChildText("ubound");
                        dub = new Double(temp);
                        double ubound = dub.doubleValue();

                        temp = hintt.getChildText("granularity");
                        dub = new Double(temp);
                        double granularity = dub.doubleValue();

                        temp = hintt.getChildText("rateOfEvolution");
                        dub = new Double(temp);
                        double rateOfEvolution = dub.doubleValue();

                        temp = hintt.getChildText("value");
                        dub = new Double(temp);
                        double value = dub.doubleValue();

                        String dfault = hintt.getChildText("default");
                        String flag = hintt.getChildText("flag");
                        String unit = hintt.getChildText("unit");

                        SolutionAttributes variable = new SolutionAttributes(name, type,
                                lbound, ubound, granularity, rateOfEvolution, value, dfault,
                                flag, unit);
                        vars.put(name, variable);
                    }
                    Kernel kernel = new Kernel(kernelName, vars);
                    profile.addKernel(kernel);
                }
            }
        } catch (Exception pce) {
            pce.printStackTrace();
        }
        return profile;
    }

    // ButtonsListener - called upon selection of new_profiles_list with JFileChooser
//    private void initiateVariables() { // taken from buttonsListener  line:158
//        globalVariables.setResult_files(new File[globalVariables.getRaw_artifacts().length][globalVariables.getProfiles_list().length]);
//        globalVariables.setNew_result_files(new File[globalVariables.getRaw_artifacts().length][globalVariables.getProfiles_list().length]);
//        globalVariables.setIpat_result_files(new ResultItem[globalVariables.getRaw_artifacts().length][globalVariables.getProfiles_list().length]);
//        globalVariables.setCss_files(new File[globalVariables.getProfiles_list().length]);
//        globalVariables.setHtml_files(new File[globalVariables.getRaw_artifacts().length][globalVariables.getProfiles_list().length]);
//    }

    // populates the Artifacts in memory from the new_profiles_list found in the input folder
    private void loadRawArtifacts() {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".htm");
            }
        };
        File[] file = inputFolder.listFiles(filter);
         raw_artifacts = new Artifact [file.length];
        for (int i = 0; i < file.length; i++) {
            Artifact artifact = new Artifact(file[i]);
            raw_artifacts[i] = artifact;
        }
    }

    private void copyNextGenerationProfiles() {
        // -- Reset result new_profiles_list array to store next generation
        //  globalVariables.setIpat_result_files(new ResultItem[globalVariables.getRaw_artifacts().length][globalVariables.getProfiles_list().length]);
        //globalVariables.xml_profiles_list = globalVariables.new_xml_profiles_list;
        // -- 3. Create new set of profiles for future generation 
        // -- 4. Copy the leader profile to new profiles
        // -- 5. Load profiles from new_profiles_list to XML objects
        for (int j = 0; j < currentGenerationOfProfiles.length; j++) {
            String profileName = currentGenerationOfProfiles[j].getName();
            if (iterationCount == 0) {
                profileName = profileName.substring(0, profileName.lastIndexOf('.'));
            } else {
                profileName = profileName.substring(profileName.indexOf('_') + 1, profileName.lastIndexOf('.'));
            }
            //profileName = profileName.substring(profileName.indexOf('_') + 1, profileName.lastIndexOf('.'));
            String outProfileName = "gen_" + iterationCount + "_" + profileName + ".xml";
            String outProfilePath = profileFolder + "/generations/" + outProfileName;

            //this line implements the 1+ lambda parent selection strategy - each offspring is created as a copy of the best
            //so this means that the FILE pointed at by new_xml_profiles[artifactID] now contains  copy of the best from the last generation 
            if (leader != null) {
                currentProfile.copyProfile(leader, outProfilePath);
            } else {
                // TODO tell Jim this method which he thought should be in the Profile class
                // has an instance of itself as a parameter so also causes a null pointer exception
                currentGenerationOfProfiles[j].writeProfileToFile(outProfilePath);
            }
            File filed = new File(outProfilePath);
            nextGenerationOfProfiles[j] = getProfile(filed);
        }
    }


    // Responsible for applying profiles to artifacts
    private void getResultArtifacts() {

        Artifact rawArtifact;
        Artifact processedArtifact = null;

        // Generate CSS based on the new profiles
        for (int profileID = 0; profileID < noOfProfiles; profileID++) {
            currentProfile = nextGenerationOfProfiles[profileID];
            System.out.println("Processing : " + currentProfile.getName());
            // Process the profile to generate CSS
            for (int artifactID = 0; artifactID < raw_artifacts.length; artifactID++) {

                rawArtifact = raw_artifacts[artifactID];
                //System.out.println(rawArtifact.getFilename());
                processedArtifact = cssp.applyProfileToArtifact(currentProfile, rawArtifact, outputFolder.getAbsolutePath() + "/");
            }
//                for (int j = 0; j < globalVariables.getRaw_artifacts().length; j++) {
//                    System.out.println("");
//                    ResultItem item = new ResultItem(processedArtifact, globalVariables.getCss_files()[profileID], globalVariables.getHtml_files()[j][profileID]);
//                    item.setProfile(globalVariables.getNextGenerationOfProfiles()[profileID]);
//                    item.setHtmlFile(globalVariables.getHtml_files()[j][profileID]);
//                    data.add(item);
//                }
        }
    }

}
