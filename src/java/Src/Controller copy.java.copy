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
import Algorithms.HintsProcessor;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;
import java.lang.IllegalAccessException;

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
    Vector<ResultItem> data = new Vector<ResultItem>();
    Hashtable hints;
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

    // TODO Constructor which takes Artifact + what view to use

    /**
     *
     * @param inputFolder
     * @param outputFolder
     * @param profileFolder
     * @throws IOException
     */
        public Controller(File inputFolder, File outputFolder, File profileFolder) throws IOException {
        this.outputFolder = outputFolder;
        this.inputFolder = inputFolder;
        this.profileFolder = profileFolder;
        System.out.println("input Folder : " + inputFolder);
        System.out.println("output Folder : " + outputFolder);
        System.out.println("profiles Folder : " + profileFolder);
    }

    // Generates the first set of results and returns them in the appropriate display to the view

    /**
     *
     */
        public void initialArtifacts() {
        bootstrapApplication();
        loadRawArtifacts();
        loadHints();
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        evolution.generateNextSolutions(noOfProfiles);
        for(int i=0;i < noOfProfiles;i++)
           currentGenerationOfProfiles[i] = evolution.getNextGenProfileAtIndex(i);
        
        getResultArtifacts();
     }

    
    // Generates the next generation of results and returns them to the view

    /**
     *
     */
        public void mainloop(){
            
            HintsProcessor myHintsProcessor = new HintsProcessor();
        //deal with the hints the user provided
            //TODO test this before  and after
        for(int i=0;i < noOfProfiles;i++)
            {
              //I'm putting the code explicitly in here for now - need sorting out but first I want to deal with more generic hints
              HintsProcessor hint;
              //first two hints use boolean values
              hint = (HintsProcessor)hints.get("FreezeBGColour");
              if (currentGenerationOfProfiles[i].isFreezeBGColour())
                  currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i],1);
              hint = (HintsProcessor)hints.get("FreezeFGFonts");
              if (currentGenerationOfProfiles[i].isFreezeBGColour())
                  currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i],1);
              hint = (HintsProcessor)hints.get("ChangeFontSize");
              currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i],currentGenerationOfProfiles[i].getChangeFontSize());
              hint = (HintsProcessor)hints.get("ChangeFGContrast");
              currentGenerationOfProfiles[i] = hint.InterpretHintInProfile(currentGenerationOfProfiles[i], currentGenerationOfProfiles[i].getChangeFGContrast());
            }
        //tell the metaheuristic to update its working memory
        evolution.updateWorkingMemory(currentGenerationOfProfiles);
        //now you are ready to create the next generation - which since they all were sorted the same should contain all the initial provided profiles
        evolution.generateNextSolutions(noOfProfiles);
       for(int i=0;i < noOfProfiles;i++)
       {
           currentGenerationOfProfiles[i] = evolution.getNextGenProfileAtIndex(i);
          //System.out.println("in controller.mainloop() just read profile with name " + currentGenerationOfProfiles[i].getName());
       }
        //now apply those profiles ot the raw artifacts to get something to display
        getResultArtifacts();
        // load user feedback back into the appropriate parameter values (e.g. profile.globalscore) in currentGenerationOfProfiles
    }
    
    
    // initialises the Profiles in memory from the files in the profile folder, generating new ones if the count is <6
    // or if the count is >9 accepts only 6 of them 
    private void bootstrapApplication() {
        
        
        //set up a filter ot pick up all the filesending with .xml
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".xml");
            }
        };
        //create an array holding all the files ending .xml in the profile folder to act as seeds
        File[] profiles_list = profileFolder.listFiles(filter);
        if (profiles_list == null){
        System.out.println("Error : profiles_list  == null in bootstrap application. Please check the web.xml in WEB-INF to ensure paths to config folders are correct.");
        }
        
        //declare an array to hold the new profiles
        File[] new_profiles_list = new File[noOfProfiles];

        //next steps depend on  number of seeds
        if (profiles_list == null || profiles_list.length < noOfProfiles) 
            {// if there are no, or less than desired numner of seeds
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

            } 
        else if (profiles_list.length > noOfProfiles
                && profiles_list.length < 9) 
            {//if there ar a few more but less than 9
                //just increase the number used and copy all the seeds
                noOfProfiles = profiles_list.length;
                new_profiles_list = new File[noOfProfiles];
                new_profiles_list = profiles_list;
            } 
        else {//we had just the right number - or more than 9
            for (int i = 0; i < noOfProfiles; i++) 
            {
                new_profiles_list[i] = profiles_list[i];
            }
        }
        //copy the updated list of prpfile names back into the profiles list array of file names
        profiles_list = new_profiles_list;
        
        //finally create the first generation
        //declare an array to hold the next gneration of profiles
        currentGenerationOfProfiles = new Profile[noOfProfiles];
        //and for each one read the actual profile from the relevant file
        for (int i = 0; i < profiles_list.length; i++) {
            currentGenerationOfProfiles[i] = evolution.getProfileFromFile(profiles_list[i]);
            System.out.println(currentGenerationOfProfiles[i].getName());
        }
    }
    
    
    
    // populates the Artifacts in memory from the new_profiles_list found in the input folder
    private void loadRawArtifacts() {
        //create a filter to pick put all the files ending .htm
        FilenameFilter filter = new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".htm");
            }
        };
        //create an array of all the files in the inpurfolder using the filter        
        File[] file = inputFolder.listFiles(filter);
        //alocate space for arraty of artefacts - one for eacg file
         raw_artifacts = new Artifact [file.length];
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

    
    private void loadHints()
      {
        HintsProcessor freezeBGcolours = new HintsProcessor("FreezeBGColours", "onOff","Freeze Background",  0.0, 1.0, 0.0, "freeze");
        freezeBGcolours.addAffectedProfileVariable("Page_bg_Red");
        freezeBGcolours.addAffectedProfileVariable("Page_bg_Blue");
        freezeBGcolours.addAffectedProfileVariable("Page_bg_Green");
        hints.put("FreezeBGColours",freezeBGcolours);
        
        
        HintsProcessor FreezeFGFonts = new HintsProcessor("FreezeFGFonts", "onOff","Freeze Text",  0.0, 1.0, 0.0, "freeze");
        FreezeFGFonts.AddAffectedKernel("h1");
        FreezeFGFonts.AddAffectedKernel("h2");
        FreezeFGFonts.AddAffectedKernel("p");
        hints.put("FreezeFGFonts",FreezeFGFonts);
        
        HintsProcessor ChangeFGContrast = new HintsProcessor("ChangeFGContrast", "slider","Change Text Contrast",  0.0, 2.0, 1.0, "moderateByValue");
        ChangeFGContrast.AddAffectedKernel("h1");
        ChangeFGContrast.AddAffectedKernel("h2");
        ChangeFGContrast.AddAffectedKernel("p");
        ChangeFGContrast.addAffectedKernelVariable("bold");
        ChangeFGContrast.addAffectedKernelVariable("italic");
        
        HintsProcessor ChangeFontSize = new HintsProcessor("ChangeFontSize", "slider","Change Text Size",  0.0, 2.0, 1.0, "moderateByValue");
        ChangeFontSize.AddAffectedKernel("h1");
        ChangeFontSize.AddAffectedKernel("h2");
        ChangeFontSize.AddAffectedKernel("p");
        ChangeFontSize.addAffectedKernelVariable("font-size");
      }
}