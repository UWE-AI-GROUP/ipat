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
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Vector;

public class Controller {

   
    Display display;
    CSSProcessor cssp = new CSSProcessor();
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
    public Profile[] currentGenerationOfProfiles;
    public static File inputFolder;
    public static File outputFolder;
    public static File profileFolder;

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
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        evolution.generateNextSolutions(noOfProfiles, currentGenerationOfProfiles);
        getResultArtifacts();
     }

    
    
    public void mainloop(){
     
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        //3. now you are ready to create the next generation - which since they all were sorted the same should contain all the initial provided profiles
        evolution.generateNextSolutions(noOfProfiles, currentGenerationOfProfiles);
        //4. now apply those profiles ot the raw artefeacts to get something to display
        getResultArtifacts();
        //5. now display them
        //6. load user feedback back into the appropriate parameter values (e.g. profile.globalscore) in currentGenerationOfProfiles
    }
    
    
    // initialises the Profiles in memory from the files in the profile folder, generating new ones if the count is <6
    // or if the count is >9 accepts only 6 of them 
    private void bootstrapApplication() {
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };

        File[] profiles_list = profileFolder.listFiles(filter);
        System.out.println(profiles_list);
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
      

        for (int i = 0; i < profiles_list.length; i++) {
            currentGenerationOfProfiles[i] = evolution.getProfile(profiles_list[i]);
            System.out.println(currentGenerationOfProfiles[i].getName());
        }
    }
    
    
    
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


    // Responsible for applying profiles to artifacts
    private void getResultArtifacts() {

        Artifact rawArtifact;
        Artifact processedArtifact = null;
        processedArtifacts = new Artifact[raw_artifacts.length * noOfProfiles];
        int count = 0;
       
        // Generate CSS based on the new profiles
        for (int profileID = 0; profileID < noOfProfiles; profileID++) {
            currentProfile = currentGenerationOfProfiles[profileID];
            System.out.println("Processing : " + currentProfile.getName());
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

}