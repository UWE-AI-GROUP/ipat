/*
 * 
 */
package Algorithms;

import Src.Controller;
import Src.SolutionAttributes;
import Src.Kernel;
import Src.Profile;
import Src.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * The Class ESEvolution.
 */
public class ESEvolution implements MetaHeuristic {

    private ArrayList<Profile> best = new ArrayList<>(); //holds copies of all the current best solutions

    /**
     * F1.
     *
     * @param x the x
     * @return the double
     */
    private double F1(double x) {
        
        double F1val = x*10;
        //0.106 - 0.0713 * x * x * x + 1.6707 * x * x - 14.6554 * x + 50.6783;
        if (F1val>100)
            F1val=100;
        if (F1val <0)
            F1val=0;
        F1val = 0.5 - F1val/200.0;//truncate to range 0-0.5
        
        return (F1val );
        //return (2 * F1val / 100);
        // "+ 0.106" : so F1(10) in not negative
    }

    /**
     * mutateProfile.
     *
     * @param prof the prof
     * @param mutation_rate the mutation_rate
     * @return true, if successful
     */
    private boolean mutateProfile(Profile prof, double mutation_rate) {
        File thisfile = prof.getFile();
        
        
        Profile mutatedProf = getProfile(thisfile);
        int numberofactivekernels = 0;
        double newval;
        
        
        
        System.out.println(".......mutation parameter is " +mutation_rate);
        Hashtable kernels = mutatedProf.getKernels();
        System.out.println(".....the number of kernels is " + kernels.size());
        Enumeration enuKer = kernels.elements();
           SolutionAttributes currentVariable = null;
        // loop through each kernel in turn,
        while (enuKer.hasMoreElements()) 
        {
            //get the next kernel
            Kernel kernel = (Kernel) enuKer.nextElement();
            
            //get all of its variables
            Hashtable vars = kernel.getVariables();
            Enumeration eVar = vars.keys();
             System.out.println(".....Kernel " + kernel.getName() + "has " + vars.size() + "elements");

            // and then mutate each of the variables within kernel in turn
            while (eVar.hasMoreElements()) 
            {
                currentVariable = (SolutionAttributes) vars.get(eVar.nextElement().toString());
                 newval = mutateVariable(currentVariable, mutation_rate);
                currentVariable.setValue(newval);
            }    
            // finally mutate the probability that the kernel is active
            if (Utils.GetRandDouble01() < mutation_rate) {
                // mutatedProf.KernelFamily[k].active =
                // (mutatedProf.KernelFamily[k].active = true)? false:true;
            }
            // Aug 2011
            // need to keep track of number of active kernels
            // if(mutatedProf.KernelFamily[k].active = true)
            // numberofactivekernels++;
            // Aug 2011
        }// end of loop mutating individual kernels
        // need to ensure that enough kernels are still active
		/*
         * while(numberofactivekernels< Attributess.minKernels) { chosen =
         * IpatUtils.GetRandIntInRange(1,MAX_KERNELS);
         * if(mutatedProf.KernelFamily[chosen].active ==false) {
         * mutatedProf.KernelFamily[chosen].active = true;
         * numberofactivekernels++; } }
         */

        Hashtable vars = mutatedProf.getSolutionAttributes();
        System.out.println(".....the number of profile variables is " + vars.size());
        Enumeration pVar = vars.keys();
        // finally the profile level variables
        // for(int pvar=0;pvar<vars.size();pvar++)
        while (pVar.hasMoreElements()) 
            {
                currentVariable = (SolutionAttributes) vars.get(pVar.nextElement().toString());
                 newval = mutateVariable(currentVariable, mutation_rate);
                currentVariable.setValue(newval);
        }
        
        //finally write the mutated profile back to file
        mutatedProf.writeProfileToFile(mutatedProf.getName());
        
        
        return true;
    }

    /**
     * mutateVariable.
     *
     * @param variableToChange the variable to be changed
     * @param mutation_rate the mutation_rate
     * @return new  value for variable
     */
    private double mutateVariable(SolutionAttributes variableToChange, double mutation_rate)
    {
        double oldVal = variableToChange.getValue();
        int possibilities, chosen;
        double stepsize, dchosen,myrand;
        double newValue=0;
                
        //pick a random numnber
         myrand = Utils.GetRandDouble01();
        //System.out.println("my random number is " + myrand);
                
        //the way that mutation paramter is interpreted, and mutation worksd, depedns on the type of variable
        if (variableToChange.getType().equalsIgnoreCase("boolean"))  
            {
                if(myrand < mutation_rate* variableToChange.getRateOfEvolution()) 
                {//Boolean - mutation sets 1s to 0s and vice versa
                    if (variableToChange.getValue() == 1.0) 
                      {
                       newValue = 0.0;
                      } 
                    else 
                      {
                       newValue = 1.0;        
                      }
                    //System.out.println("..................flipping binary variable " + variableToChange.getName() + "to value " + variableToChange.getValue()) ;
                }
            }
        
        else if (variableToChange.getType().equalsIgnoreCase("cardinal")) 
            {
                if(myrand < mutation_rate* variableToChange.getRateOfEvolution()) 
                    {// a list of different catgorical values with no natural oerdering so just pick a new value at random
     
                        // how many discrete values could this variable take?
                        possibilities = (int) ((variableToChange.getUbound() - variableToChange.getLbound()) / variableToChange.getGranularity());
                        // if the values were indexed chose one index at random
                        chosen = Utils.GetRandIntInRange(0, possibilities);
                        // now compute what actual value this would be
                        newValue = variableToChange.getLbound() + variableToChange.getGranularity() * chosen;
                        //System.out.println("...............randomly choosing new value " + newValue + "for cardinal variable " + variableToChange.getName() );
                    }
             } 
                
        else if (variableToChange.getType().equalsIgnoreCase("ordinal")) 
            {
                // ordinal varibles - for exanmple continuos variables of integers where sequence counts 
                //mutation rate is interpreted as a normalised step size
                // factor first pick N(0,stepsize) deviate, then convert to the
                // allowed granularity
                stepsize = (variableToChange.getUbound() - variableToChange.getLbound()) * mutation_rate* variableToChange.getRateOfEvolution();
                dchosen = Utils.GetGaussN01Double() * stepsize;
                dchosen = variableToChange.getGranularity() * Math.floor(0.5 + dchosen / variableToChange.getGranularity());
                newValue= oldVal + dchosen;
                if (newValue < variableToChange.getLbound()) 
                    {
                        newValue = variableToChange.getLbound();
                    }
                if (newValue > variableToChange.getUbound()) 
                    {
                        newValue = variableToChange.getUbound();
                    }
                    //System.out.println("...................choosing new value " + newValue + "for ordinal variable " + variableToChange.getName() );
            } 
   
         else 
            {
                System.out.println("Error - unkown variable type " + variableToChange.getType() + "for variable " + variableToChange.getName());
            }
        return newValue;
    }
    
    
    
    
    @Override
    public Profile[] generateNextSolutions(int howMany) {

        int copied, toCopy;
        //check that the working memory is not empty
        if (best.size() <= 0) {
            throw new UnsupportedOperationException("Can't call generateNextSolutions without calling UpdateWorkingMemory() First");
        }
        // creating space to return the Profiles which are generated by this method
        Profile[] nextGenerationOfProfiles = new Profile[howMany];
    //if the user has resized the population so that we have more "best" solutions than we want new profiles
        // then we must lose some of our "best" solutions at random
        while (best.size() > howMany) {
            best.remove(Utils.GetRandIntInRange(0, howMany));
        }
   
        //make copies if all the best
         for ( copied=0;copied < best.size();copied++)
         {
    
            //copy all the profiles from the  set of the previous best
             File thisFile = best.get(copied).getFile();
             nextGenerationOfProfiles[copied] = new Profile(thisFile);
             System.out.println("have made a copy of best[" + copied +"]");
             
            //nextGenerationOfProfiles[copied].setProfile((Profile) best.get(copied));
            
// TESTING : check to ensure profiles from the best set are being applied to the nextGenerationOfProfiles correctly
            System.out.println("BEST COPY; "  + best.get(copied).getName()  + "     COPIED TO;  " + nextGenerationOfProfiles[copied].getName());
        }
        System.out.println("number copied without change : " + best.size());
        
        
        //and then fill up the rest with mutated copies of the best.
        System.out.println("about to copy and mutate another " + (howMany-copied) + " and there are " + best.size() + " in best");
        while (copied < howMany) 
        {
            //pick random one from the best set and copy it
            if(best.size()==1)
                toCopy = 0;
            else
                toCopy = Utils.GetRandIntInRange(0, best.size() - 1);
            System.out.println(".... chosen to copy number " + toCopy);
            File thisFile = best.get(toCopy).getFile();
             nextGenerationOfProfiles[copied] = new Profile(thisFile);
             System.out.println("..... have made a copy of best[" + toCopy +"]");
             copied++;
        }

       
        // update profile names by incrementing the generation count in each name and write them to file
        for (int i = 0; i < nextGenerationOfProfiles.length; i++) 
        {
            try {
                String profileName = nextGenerationOfProfiles[i].getName(); // "gen_x-profile_y.xml"
               
                String profile = profileName.substring(profileName.indexOf('-') , profileName.lastIndexOf('_')+1); // profile_.xml
                int generation = Integer.parseInt(profileName.substring((profileName.indexOf('_') + 1), profileName.indexOf('-')));
                generation++;
                String outProfileName = "gen_" + generation + profile + i + ".xml";
                System.out.println("outprofilename = " + outProfileName);

                // set name in profile to match new name
                nextGenerationOfProfiles[i].setName(outProfileName);
            // TODO TEST ME
                // String outProfilePath = nextGenerationOfProfiles[i].getFile().getParent()   + "/generations/" + outProfileName;
                String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
                // write out the profile to file for safe keeping
                File file = new File(Controller.outputFolder.getAbsolutePath() + "/generations/");
                file.mkdir();
                nextGenerationOfProfiles[i].writeProfileToFile(outProfilePath);
                //jim 28/4, having delted the next two lines on 27/4 I haveput them basck in case tyhat is why the info is gettijg lost
                File filed = new File(outProfilePath);
                nextGenerationOfProfiles[i] = getProfile(filed);
            } catch (StringIndexOutOfBoundsException ex) {
                System.out.println("The profile names do not follow the correct convention to be processed."
                        + "/nLook within the Profiles Folder, and ensure the names appear as: gen_0-Profile_x.xml");
                System.out.println(ex.getMessage());
            }
        }
        
        
        //finally apply mutation where necessary - i.e. not to the duplicates of the best
        for(int toMutate = best.size(); toMutate < howMany;toMutate++)
        {
            //decide on a mutation rate parameter  according to how the user rated it.  We can use fixed rates to test the operation of the EA
            //double rateToApply = 0.5; 
            // double rateToApply = 1.0; 
            double rateToApply = this.F1(nextGenerationOfProfiles[toMutate].getGlobalScore());
            System.out.println("global score for this profile is " + nextGenerationOfProfiles[toMutate].getGlobalScore() + "and mutation parameter is " + rateToApply);
            //now apply mutation with this parameter
            this.mutateProfile(nextGenerationOfProfiles[toMutate], rateToApply);
            System.out.println("..... mutate profile " + toMutate + " complete");
        }
        return nextGenerationOfProfiles;
    }
//TODO  why do we have  this function with the same name in two different classes?
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
                    profile.addVariable(variable);
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


    @Override
    public void updateWorkingMemory(Profile[] evaluatedSolutions) {
        int popmember = 0; //loop variable
        //this is an EA so we are going to start by clearing the previous population if it isnt the first
        best.clear();
        //now we want to find out what the best fitness seen is
        double bestFitness = 0.0;
        for (popmember = 0; popmember < evaluatedSolutions.length; popmember++) {
            if (evaluatedSolutions[popmember].getGlobalScore() > bestFitness) {
                bestFitness = evaluatedSolutions[popmember].getGlobalScore();
            }
        }
        //finally see which of our evaluated solutions are the equal best and add them to the list.
        for (popmember = 0; popmember < evaluatedSolutions.length; popmember++) {
            if (evaluatedSolutions[popmember].getGlobalScore() >= bestFitness) {
                best.add(evaluatedSolutions[popmember]);
            }
        }
        // TESTING : check to see if the fitness values are being evaluated and assigned to "Best" List
        for (Profile best : best) {
            System.out.println("those assigned as best in ESEvolution.updateWorkingMemory(): " + best.getName() + " : " + best.getGlobalScore());
        }
    }

}
