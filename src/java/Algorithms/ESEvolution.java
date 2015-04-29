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
import jdk.nashorn.internal.parser.TokenType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * The Class ESEvolution.
 */
public class ESEvolution implements MetaHeuristic {

    private ArrayList<Profile> best = new ArrayList<>(); //holds copies of all the current best solutions
private ArrayList<Profile> nextGen = new ArrayList<>(); //holds copies of all the new generation of  solutions

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
    private boolean mutateProfile(int which, double mutation_rate) 
    {
        
        int numberofactivekernels = 0;
        double newval;
     
        String profilename = nextGen.get(which).getName();
        System.out.println("in evolution.mutateprofile() name of profile nextgen[" +which +"] is " + profilename);
        //System.out.println(".......mutation parameter is " +mutation_rate);
        Hashtable kernels = nextGen.get(which).getKernels();
        //System.out.println(".....the number of kernels is " + kernels.size());
        Enumeration enuKer = kernels.elements();
           SolutionAttributes currentVariable = null;
           String currentvarname;
        // loop through each kernel in turn,
        while (enuKer.hasMoreElements()) 
        {
            //get the next kernel
            Kernel kernel = (Kernel) enuKer.nextElement();
            
            //get all of its variables
            Hashtable vars = kernel.getVariables();
            Enumeration eVar = vars.keys();
             //System.out.println(".....Kernel " + kernel.getName() + " has " + vars.size() + " elements");

            // and then mutate each of the variables within kernel in turn
            while (eVar.hasMoreElements()) 
            {
                currentVariable = (SolutionAttributes) vars.get(eVar.nextElement().toString());
                 newval = mutateVariable(currentVariable, mutation_rate);
                currentVariable.setValue(newval);
                currentvarname = currentVariable.getName();
                //System.out.println("trying to write back new value for variable " + currentvarname);
                //nextGen.get(which).setProfileVariableValue( currentvarname, newval);
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

        Hashtable vars = nextGen.get(which).getSolutionAttributes();
        //System.out.println(".....the number of profile variables is " + vars.size());
        Enumeration pVar = vars.keys();
        // finally the profile level variables
        // for(int pvar=0;pvar<vars.size();pvar++)
        while (pVar.hasMoreElements()) 
            {
                currentVariable = (SolutionAttributes) vars.get(pVar.nextElement().toString());
                 newval = mutateVariable(currentVariable, mutation_rate);
                currentVariable.setValue(newval);
                currentvarname = currentVariable.getName();
                System.out.println("trying to write back new value for variable " + currentvarname);
                nextGen.get(which).setProfileVariableValue( currentvarname, newval);
        }
        
        //finally write the mutated profile back to file
        
        //nextGen.get(which).writeProfileToFile(profilename);
        
        
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
    public Profile getNextGenProfileAtIndex(int which)
    {
        if(which <0)
            throw new UnsupportedOperationException("tried to acces nextGen item with negative index");
        else if (which > nextGen.size())
            throw new UnsupportedOperationException("tried to acces nextGen item with index " + which + "but there are only" + best.size());
        else
            {
                //System.out.println("in evolution.getNextGenProfileAtIndex with index: " + which );
                File thisfile = nextGen.get(which).getFile();
                //System.out.println("... nextgen profile name is: " + nextGen.get(which).getName() + " and filename " + thisfile.getName());
                return getProfileFromFile(thisfile);
            } 
        
    }
    
    
    @Override
    public void generateNextSolutions(int howMany) {

        int copied, toCopy;
        //check that the working memory is not empty
        if (best.size() <= 0) {
            throw new UnsupportedOperationException("Can't call generateNextSolutions without calling UpdateWorkingMemory() First");
        }
        //clear  the array nextGen
        nextGen.clear();
    
    //if the user has resized the population so that we have more "best" solutions than we want new profiles
        // then we must lose some of our "best" solutions at random
        while (best.size() > howMany) {
            best.remove(Utils.GetRandIntInRange(0, howMany));
        }
   
        //make at least one copy of all the best and howMany in total
         for ( copied=0;copied < howMany;copied++)
         {
            if(copied < best.size())//at least one copy of each
                 toCopy = copied;
             else if(best.size()==1)//if there s only one clone it repeatedly
                toCopy = 0;
             else  //oherwise fill up with clones of randomly selected members of best
                toCopy = Utils.GetRandIntInRange(0, best.size() - 1);
            //copy all the profiles from the  set of the previous best
             File thisfile = best.get(toCopy).getFile();
             Profile toAdd = getProfileFromFile(thisfile);
             nextGen.add(toAdd);
             //System.out.println("have made a copy of best[" + copied +"] with filename " + thisfile.getName());
             
        }
        //System.out.println("number copied without change : " + best.size());
        

       
        // update profile names by incrementing the generation count in each name and write them to file
        //first make the folde to hold them
         File file = new File(Controller.outputFolder.getAbsolutePath() + "/generations/");
         file.mkdir();
         for (int i = 0; i < nextGen.size(); i++) 
          {
            try {
                String profileName = nextGen.get(i).getName(); // "gen_x-profile_y.xml"
               
                String profile = profileName.substring(profileName.indexOf('-') , profileName.lastIndexOf('_')+1); // profile_.xml
                int generation = Integer.parseInt(profileName.substring((profileName.indexOf('_') + 1), profileName.indexOf('-')));
                generation++;
                String outProfileName = "gen_" + generation + profile + i + ".xml";
                //System.out.println("outprofilename = " + outProfileName);

                // set name in profile to match new name
                nextGen.get(i).setName(outProfileName);
            
                // write out the profile to file for safe keeping
                String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
                nextGen.get(i).writeProfileToFile(outProfilePath);
                File thisfile = new File(outProfilePath);
                nextGen.get(i).setFile(thisfile);
            } catch (StringIndexOutOfBoundsException ex) {
                System.out.println("The profile names do not follow the correct convention to be processed."
                        + "/nLook within the Profiles Folder, and ensure the names appear as: gen_0-Profile_x.xml");
                System.out.println(ex.getMessage());
            }
        }
        
         //System.out.println("changed names and saved files");
        
        // apply mutation where necessary - i.e. not to the duplicates of the best
        for(int toMutate = best.size(); toMutate < howMany;toMutate++)
        {
            //decide on a mutation rate parameter  according to how the user rated it.  We can use fixed rates to test the operation of the EA
            //double rateToApply = 0.5; 
            // double rateToApply = 1.0; 
            double rateToApply = this.F1(nextGen.get(toMutate).getGlobalScore());
               //System.out.println("global score for the profile " + nextGen.get(toMutate).getName() 
               //        + " is " + nextGen.get(toMutate).getGlobalScore() 
               //       + " and mutation parameter is " + rateToApply);
            //now apply mutation with this parameter
            this.mutateProfile(toMutate, rateToApply);
            System.out.println("..... mutate profile " + toMutate + " complete");
        }
        
        //finally write all ofthe profiles to file for safe keeping
        for(int toSave=0; toSave < howMany;toSave++)
        {
            String outProfileName= nextGen.get(toSave).getName();
            String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
            //System.out.println("saving next gen profile to file: " + outProfileName);
            nextGen.get(toSave).writeProfileToFile(outProfilePath);
            
        }
        
 
    }
//TODO  why do we have  this function with the same name in two different classes?
    public Profile getProfileFromFile(File file) {
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
