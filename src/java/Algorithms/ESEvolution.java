/*
 * 
 */
package Algorithms;

import Src.Controller;
import Src.GlobalVariables;
import Src.SolutionAttributes;
import Src.Kernel;
import Src.Profile;
import Src.Utils;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;


/**
 * The Class ESEvolution.
 */
public class ESEvolution implements MetaHeuristic {

    private List  best; //holds copies of all the current best solutions

    
    /**
     * F1.
     *
     * @param x the x
     * @return the double
     */
    private double F1(double x) {
        double F1val = 0.106 - 0.0713 * x * x * x + 1.6707 * x * x - 14.6554
                * x + 50.6783;
        return (F1val / 100);
		//return (2 * F1val / 100);
        // "+ 0.106" : so F1(10) in not negative
    }

    /**
     * Mutate.
     *
     * @param prof the prof
     * @param mutation_rate the mutation_rate
     * @return true, if successful
     */
    private boolean mutate(Profile prof, double mutation_rate) {
        Profile mutatedProf = prof;
        int possibilities, chosen;
        int numberofactivekernels = 0;
        double stepsize, dchosen;

        Hashtable kernels = prof.getKernels();
        Enumeration enuKer = kernels.elements();

        // loop through each kernel,in turn,
        while (enuKer.hasMoreElements()) {
            Kernel kernel = (Kernel) enuKer.nextElement();
            Hashtable vars = kernel.getVariables();
            Enumeration eVar = vars.keys();
            SolutionAttributes variable = null;

            // variables within kernel
            while (eVar.hasMoreElements()) {
                variable = (SolutionAttributes) vars.get(eVar.nextElement().toString());
                double oldVal = variable.getValue();

                if (variable.getType().equalsIgnoreCase("cardinal")) {
                    // how many discrete values could this variable take?
                    possibilities = (int) ((variable.getUbound() - variable
                            .getLbound()) / variable.getGranularity());
                    // if the values were indexed chose one index at random
                    chosen = Utils.GetRandIntInRange(0, possibilities);
                    // now compute what actual value this would be
                    variable.setValue(variable.getLbound() + variable.getGranularity() * chosen);

                } else if (variable.getType().equalsIgnoreCase("ordinal")) {
					// mutation rate is interpreted as a normalised step size
                    // factor first pick N(0,stepsize) deviate, then convert to the
                    // allowed granularity
                    stepsize = (variable.getUbound() - variable.getLbound()) * mutation_rate;
                    dchosen = Utils.GetGaussN01Double() * stepsize;
                    dchosen = variable.getGranularity() * Math.floor(0.5 + dchosen / variable.getGranularity());
                    variable.setValue(oldVal + dchosen);
                    if (variable.getValue() < variable.getLbound()) {
                        variable.setValue(variable.getLbound());
                    }
                    if (variable.getValue() > variable.getUbound()) {
                        variable.setValue(variable.getUbound());
                    }
                } else // Boolean
                {
                    if (Utils.GetRandDouble01() < mutation_rate) {
                        if (variable.getValue() == 1.0) {
                            variable.setValue(0.0);
                        } else {
                            variable.setValue(1.0);
                        }
                    }
                }
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

        Hashtable vars = prof.getSolutionAttributes();
        Enumeration pVar = vars.keys();
		// finally the profile level variables
        // for(int pvar=0;pvar<vars.size();pvar++)
        while (pVar.hasMoreElements()) {
            SolutionAttributes var = (SolutionAttributes) vars.get(pVar.nextElement()
                    .toString());
            double oval = var.getValue();
            if (var.getType().equalsIgnoreCase("cardinal")) { // how many discrete values could this variable take?

                if (Utils.GetRandDouble01() < mutation_rate * var.getRateOfEvolution()) {
                    possibilities = (int) ((var.getUbound() - var.getLbound()) / var
                            .getGranularity());
                    // if the values were indexed chose one index at random
                    chosen = Utils.GetRandIntInRange(0, possibilities);
					// now compute what actual value this would be
                    // mutatedProf.profile_vars[pvar] = prof.PVar_MinVal[pvar] +
                    // prof.PVar_Granularity[pvar]*chosen;
                    var.setValue(var.getLbound() + var.getGranularity() * chosen);
                }
            } else if (var.getType().equalsIgnoreCase("ordinal")) {
				// mutation rate is interpreted as a normalised step size factor
                // first pick N(0,stepsize) deviate, then convert to the allowed
                // granularity
                stepsize = (var.getUbound() - var.getLbound()) * mutation_rate * var.getRateOfEvolution();
                dchosen = Utils.GetGaussN01Double() * stepsize;

                dchosen = var.getGranularity()
                        * Math.floor(0.5 + dchosen / var.getGranularity());
                var.setValue(oval + dchosen);
                if (var.getValue() < var.getLbound()) {
                    var.setValue(var.getLbound());
                }
                if (var.getValue() > var.getUbound()) {
                    var.setValue(var.getUbound());
                }

            } else // Boolean
            {
                if (Utils.GetRandDouble01() < mutation_rate * var.getRateOfEvolution()) {
                    if (var.getValue() == 1.0) {
                        var.setValue(0.0);
                    } else {
                        var.setValue(1.0);
                    }
                }
            }
        }
		// finally mutate the probability that the kernel is active
        // Set scores to 0, the mutated profile being unscored as yet
        // mutatedProf.ResetAllExistingScores();
        // mutatedProf.ResetAllExistingHints();

		// return mutatedProf;
        // prof.printProfile();
        return true;
    }

   
    
    @Override
    public void generateNextSolutions(int howMany, Profile[] nextGenerationOfProfiles) {
    int copied, toCopy, generationnumber;
 
    //check that the working memory is not empty
    if(best.size()<=0)
        {
        throw new UnsupportedOperationException("Can't call generateNextSolutions without calling UpdateWorkingMemory() First");
        }
    //if the user has resized the population so that we have more "best" solutions than we want new profiles
    // then we must lose some of our "best" solutions at rndom
    while (best.size() >  howMany)    
        {
        best.remove(Utils.GetRandIntInRange(0, howMany));
        }
    
    //now  the copy the best ones
    for ( copied=0;copied < best.size();copied++)
        {
        //copy the profile from the set of the previous best
        nextGenerationOfProfiles[copied].setProfile((Profile)best.get(copied));
        }
            
    //and then fill up the rest with mutated copues of the best.
    while(copied < howMany )
        {
        //pick random one from the bestr set nd copy it
            toCopy = Utils.GetRandIntInRange(0, best.size());
            nextGenerationOfProfiles[copied].setProfile((Profile)best.get(toCopy));
        }
    
    //to finish off we need to change the names in the profiles to reflect the current iteration number
    //JIM 30-03 : Kieran, we need ot discuss whether this gets done here or in the controller class
    // - if they get done in the controller class i could copy out the names before i overwirtw the est of the content
    
    
    
    //Jim 30-3andthen write them to filefor safe keeping - should i do this here or is it done in the controller?
    // TODO write profile array to file for safe keeping
    }
    
    
    public Boolean generateNextSolution(Profile profile) {
        double score=0.0, mutation_rate = 0.0;
        score = profile.getGlobalScore();
        mutation_rate = F1(score);
        return this.mutate(profile, mutation_rate);
    }

    @Override
    public void updateWorkingMemory(Profile[] evaluatedSolutions) {
        int popmember=0; //loop variable
        //this is an EA so we are going to start by clearing the previous population
        best.clear();
        //now we want to find out whatthe best fitness seen is
        double bestFitness = 0.0;
        for(popmember=0; popmember < evaluatedSolutions.length; popmember++)
            if( evaluatedSolutions[popmember].getGlobalScore() >bestFitness)
                bestFitness = evaluatedSolutions[popmember].getGlobalScore();
        //finally see which of our evaluated solutions are the equal best and add them to the list.
        for(popmember=0; popmember < evaluatedSolutions.length; popmember++)
            if( evaluatedSolutions[popmember].getGlobalScore() >bestFitness)
                best.add(evaluatedSolutions[popmember]);
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
