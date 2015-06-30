/*
 * 
 */
package Algorithms;

import Src.Controller;
import Src.IpatVariable;
import Src.Kernel;
import Src.Profile;
import Src.Utils;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 * The Class ESEvolution.
 */
public class ESEvolution implements MetaHeuristic {

    private static final Logger logger = Logger.getLogger(ESEvolution.class);
    //holds copies of all the current best solutions
    private final ArrayList<Profile> best = new ArrayList<>();
    //holds copies of all the new generation of  solutions
    private final ArrayList<Profile> nextGen = new ArrayList<>();

    /**
     * F1.
     *
     * @param x the x
     * @return the double
     */
    private double F1(double x) {

        double F1val = x * 10;
        //0.106 - 0.0713 * x * x * x + 1.6707 * x * x - 14.6554 * x + 50.6783;
        if (F1val > 100) {
            F1val = 100;
        }
        if (F1val < 0) {
            F1val = 0;
        }
        F1val = 0.5 - F1val / 200.0;//truncate to range 0-0.5

        return (F1val);
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
    private boolean mutateProfile(int which, double mutation_rate) {
        double newval;
        HashMap kernels = nextGen.get(which).getKernels();
        Collection values = kernels.values();
        Iterator iterateKernels = values.iterator();
        IpatVariable currentVariable;
        String currentvarname;
        logger.debug("in evolution.mutateprofile() name of profile nextgen[" +which +"] is " + nextGen.get(which).getName() + "\n");
        logger.debug(".......mutation parameter is : " +mutation_rate + "\n");
        logger.debug(".......number of kernels is  : " + kernels.size() + "\n");
        
        while (iterateKernels.hasNext()) {
            Kernel kernel = (Kernel) iterateKernels.next();
            HashMap kernelVariables = kernel.getVariables();
            Set keySet1 = kernelVariables.keySet();
            Iterator eVar = keySet1.iterator();
            logger.debug(".......Kernel " + kernel.getName() + " number of elements : " + kernelVariables.size() + "\n");
            
            while (eVar.hasNext()) {
                currentvarname = eVar.next().toString();
                currentVariable = (IpatVariable) kernelVariables.get(currentvarname);
                newval = mutateVariable(currentVariable, mutation_rate);
                
                if (newval != currentVariable.getValue()) {
                 logger.debug("mutating variable " + currentvarname + " in kernel " + kernel.getName() + "\n");
                 logger.debug(".......old value " + currentVariable.getValue() + " is changing  to " + newval + "\n");
                 currentVariable.setValue(newval);
                 logger.debug(".......have set value in currentVariable\n");
                 kernelVariables.put(currentvarname, currentVariable);
                 logger.debug("Value in vars is now " + ((IpatVariable) kernelVariables.get(currentvarname)).getValue() + "\n");
                }
            }
            // mutate the probability that the kernel is active
            if (Utils.GetRandDouble01() < mutation_rate) {
                // mutatedProf.KernelFamily[k].active =
                // (mutatedProf.KernelFamily[k].active = true)? false:true;
            }
            nextGen.get(which).replaceKernel(kernel);
        }
        // need to ensure that enough kernels are still active
        /*
         * while(numberofactivekernels< Attributess.minKernels) { chosen =
         * IpatUtils.GetRandIntInRange(1,MAX_KERNELS);
         * if(mutatedProf.KernelFamily[chosen].active ==false) {
         * mutatedProf.KernelFamily[chosen].active = true;
         * numberofactivekernels++; } }
         */

        HashMap variables = nextGen.get(which).getProfileLevelVariables();
        logger.debug(".......the number of profile variables is : " + variables.size() + "\n");
        Set keySet2 = variables.keySet();
        Iterator profileVariables = keySet2.iterator();
        while (profileVariables.hasNext()) {
            currentvarname = profileVariables.next().toString();
            currentVariable = (IpatVariable) variables.get(currentvarname);
            newval = mutateVariable(currentVariable, mutation_rate);
            if (newval != currentVariable.getValue()) {
                logger.debug("mutating profile variable " + currentvarname + "\n");
                logger.debug(".......old value " + currentVariable.getValue() + " is changing  to " + newval + "\n");
                currentVariable.setValue(newval);
                logger.debug(".......have set value in currentVariable\n");
                variables.put(currentvarname, currentVariable);
                currentVariable = (IpatVariable) variables.get(currentvarname);
                logger.debug(".......Value in variables is now" + currentVariable.getValue()    );
                logger.debug(".......now changing the profile in the nextgen arraylist");
                nextGen.get(which).replaceVariable(currentVariable);
                IpatVariable valInNextGen = (IpatVariable)nextGen.get(which).getProfileLevelVariables().get(currentvarname);
                logger.debug(".......Value in nextGen is now" 
                + valInNextGen.getValue() + "\n");
            }
        }
        return true;
    }

    /**
     * mutateVariable.
     *
     * @param variableToChange the variable to be changed
     * @param mutation_rate the mutation_rate
     * @return new value for variable
     */
    private double mutateVariable(IpatVariable variableToChange, double mutation_rate) {
        double oldVal = variableToChange.getValue();
        int possibilities, chosen;
        double stepsize, dchosen, myrand;
        double newValue = 0;
        myrand = Utils.GetRandDouble01();
        logger.debug("random number in ESEvolution.mutateVariable is : " + myrand + "\n");

        //the way that mutation paramter is interpreted, and mutation works, depends on the type of variable
        
        // boolean = negation ( 0 = 1 | 1 = 0)
        if (variableToChange.getType().equalsIgnoreCase("boolean")) {
            if (myrand < mutation_rate * variableToChange.getRateOfEvolution()) {
                if (variableToChange.getValue() == 1.0) {
                    newValue = 0.0;
                } else {
                    newValue = 1.0;
                }
                logger.debug("flipping binary variable " + variableToChange.getName() + "to value " + variableToChange.getValue()) ;
            }
        } else if (variableToChange.getType().equalsIgnoreCase("cardinal")) {
            if (myrand < mutation_rate * variableToChange.getRateOfEvolution()) {// a list of different catgorical values with no natural oerdering so just pick a new value at random

                // how many discrete values could this variable take?
                possibilities = (int) ((variableToChange.getUbound() - variableToChange.getLbound()) / variableToChange.getGranularity());
                // if the values were indexed chose one index at random
                chosen = Utils.GetRandIntInRange(0, possibilities);
                // now compute what actual value this would be
                newValue = variableToChange.getLbound() + variableToChange.getGranularity() * chosen;
                logger.debug("...............randomly choosing new value " + newValue + "for cardinal variable " + variableToChange.getName() + "\n" );
            }
        } else if (variableToChange.getType().equalsIgnoreCase("ordinal")) {
            // ordinal varibles - for exanmple continuos variables of integers where sequence counts 
            //mutation rate is interpreted as a normalised step size
            // factor first pick N(0,stepsize) deviate, then convert to the
            // allowed granularity
            stepsize = (variableToChange.getUbound() - variableToChange.getLbound()) * mutation_rate * variableToChange.getRateOfEvolution();
            dchosen = Utils.GetGaussN01Double() * stepsize;
            dchosen = variableToChange.getGranularity() * Math.floor(0.5 + dchosen / variableToChange.getGranularity());
            newValue = oldVal + dchosen;
            if (newValue < variableToChange.getLbound()) {
                newValue = variableToChange.getLbound();
            }
            if (newValue > variableToChange.getUbound()) {
                newValue = variableToChange.getUbound();
            }
            logger.debug("...................choosing new value " + newValue + "for ordinal variable " + variableToChange.getName() );
        } else {
            logger.error("Error - unkown variable type " + variableToChange.getType() + "for variable " + variableToChange.getName());
        }
        return newValue;
    }

    /**
     *
     * @param which
     * @return
     */
    @Override
    public Profile getNextGenProfileAtIndex(int which) {
        //TODO add logging
        if (which < 0) {
            logger.error("tried to acces nextGen item with negative index\n");
            throw new UnsupportedOperationException("tried to acces nextGen item with negative index");
        } else if (which > nextGen.size()) {
            logger.error("tried to acces nextGen item with index " + which + "but there are only" + best.size() + "\n");
            throw new UnsupportedOperationException("tried to acces nextGen item with index " + which + "but there are only" + best.size());
        } else {
            Profile toreturn = nextGen.get(which);
            return toreturn;
        }

    }

    /**
     *
     * @param howMany
     */
    @Override
    public void generateNextSolutions(int howMany) {
       
        logger.debug("How Many in ESEvolution.generateNextSolutions : " + howMany +"\n");
       
        int copied, toCopy;
        if (best.size() <= 0) {
            logger.error("Can't call generateNextSolutions without calling UpdateWorkingMemory() First\n");
        }
        
        nextGen.clear();

        while (best.size() > howMany) {
            best.remove(Utils.GetRandIntInRange(0, howMany));
        }

        //make at least one copy of all the best, and howMany in total
        for (copied = 0; copied < howMany; copied++) {
            if (copied < best.size())//at least one copy of each
            {
                toCopy = copied;
            } else if (best.size() == 1)//if theres only one clone it repeatedly
            {
                toCopy = 0;
            } else //otherwise fill up with clones of randomly selected members of best
            {
                toCopy = Utils.GetRandIntInRange(0, best.size() - 1);
            }
            //copy all the profiles from the  set of the previous best 
            // create new profile object so that adding best to nextGen doesnt just reference the same object
            File thisfile = best.get(toCopy).getFile();
            Profile toAdd = new Profile(thisfile);
            nextGen.add(toAdd);
            logger.debug("have made a copy of best[" + copied + "] with filename " + thisfile.getName()+"\n");
        }

        // apply mutation where necessary - i.e. leaving one dulicate of each of the best
        for (int toMutate = best.size(); toMutate < howMany; toMutate++) {
            //decide on a mutation rate parameter  according to how the user rated it.  We can use fixed rates to test the operation of the EA
            //double rateToApply = 0.5; 
            // double rateToApply = 1.0; 
            double rateToApply = this.F1(nextGen.get(toMutate).getGlobalScore());
            logger.debug("global score for the profile " + nextGen.get(toMutate).getName());
            //        + " is " + nextGen.get(toMutate).getGlobalScore() 
            //       + " and mutation parameter is " + rateToApply);
            //now apply mutation with this parameter
            this.mutateProfile(toMutate, rateToApply);

            logger.debug("..... mutate profile " + toMutate + " complete\n");
        }

        //make the folder to hold the files in which we will store the next generation
        File file = new File(Controller.outputFolder.getAbsolutePath() + "/generations/");
        file.mkdir();

        int generation = 0;
        for (int k = 0; k < nextGen.size(); k++) {

            try {
                Profile profile = nextGen.get(k);
                String profileName = profile.getName();

                //get base name for profile
                String profileTemplate = profileName.substring(profileName.indexOf('-'), profileName.lastIndexOf('_') + 1); // should be of form "profile_"

                //get generation and increment it
                generation = Integer.parseInt(profileName.substring((profileName.indexOf('_') + 1), profileName.indexOf('-')));
          
                logger.debug("after reading generation  has value " + generation + "\n");
                generation++;
                String outProfileName = "gen_" + generation + profileTemplate + k + ".xml";

                // set name in profile to match new name
                nextGen.get(k).setName(outProfileName);

                // write out the profile to file for safe keeping
                //build the path by fetching the session details from the controller and adding generaios + this file name
                String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;

                //write to file
                nextGen.get(k).copyToNewFile(outProfilePath);
                File thisfile = new File(outProfilePath);
                nextGen.get(k).setFile(thisfile);

            } catch (StringIndexOutOfBoundsException ex) {
                logger.error("The profile names do not follow the correct convention to be processed."
                        + "/nLook within the Profiles Folder, and ensure the names appear as: gen_0-Profile_x.xml\n");
                logger.error(ex.getMessage());
            }
        }
    }

    /**
     *
     * @param evaluatedSolutions
     */
    @Override
    public void updateWorkingMemory(Profile[] evaluatedSolutions) {
        int popmember; //loop variable
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
        for (Profile best1 : best) {
            logger.debug("those assigned as best in ESEvolution.updateWorkingMemory(): " + best1.getName() + " : " + best1.getGlobalScore() + "\n");
        }
    }
}
