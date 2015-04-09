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
        return true;
    }

   
    
    @Override
    public void generateNextSolutions(int howMany, Profile[] currentGenerationOfProfiles) {
        
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
        currentGenerationOfProfiles[copied].setProfile((Profile)best.get(copied));
        }
            
    //and then fill up the rest with mutated copies of the best.
    while(copied < howMany )
        {
        //pick random one from the best set and copy it
            toCopy = Utils.GetRandIntInRange(0, best.size());
            currentGenerationOfProfiles[copied].setProfile((Profile)best.get(toCopy));
        }
    
      
        // update profile names by incrementing the generation count in each name
        for (int i = 0; i < currentGenerationOfProfiles.length; i++) {
            String profileName = currentGenerationOfProfiles[i].getName(); // "gen_x-profile_y.xml"
            String profile = profileName.substring(profileName.indexOf('-') + 1, profileName.lastIndexOf('.')); // profile_y.xml
            int generation = Integer.parseInt(profileName.substring((profileName.indexOf('_')+1), profileName.indexOf('-')));
            generation++; 
            String outProfileName = "gen_" + generation + "-" + profile + ".xml";
            // set name in profile to match new name
         currentGenerationOfProfiles[i].setName(outProfileName);
            // TODO controller outputFolder variable needed but out of scope from Controller 
         String outProfilePath =  Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
         // write out the profile to file for safe keeping
         File file = new File(Controller.outputFolder.getAbsolutePath() + "/generations/");
         file.mkdir();
         currentGenerationOfProfiles[i].writeProfileToFile(outProfilePath);
         // get written out profile and apply xml formatting
         File filed = new File(outProfilePath);
         currentGenerationOfProfiles[i] = getProfile(filed);
      
        }
    }
    
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
    
    public Boolean generateNextSolution(Profile profile) {
        double score=0.0, mutation_rate = 0.0;
        score = profile.getGlobalScore();
        mutation_rate = F1(score);
        return this.mutate(profile, mutation_rate);
    }

    @Override
    public void updateWorkingMemory(Profile[] evaluatedSolutions) {
        int popmember=0; //loop variable
        //this is an EA so we are going to start by clearing the previous population if it isnt the first
        best.clear();
        //now we want to find out what the best fitness seen is
        double bestFitness = 0.0;
        for(popmember=0; popmember < evaluatedSolutions.length; popmember++){
            if( evaluatedSolutions[popmember].getGlobalScore() > bestFitness)
                bestFitness = evaluatedSolutions[popmember].getGlobalScore();
        }
        //finally see which of our evaluated solutions are the equal best and add them to the list.
        for(popmember=0; popmember < evaluatedSolutions.length; popmember++){
            if( evaluatedSolutions[popmember].getGlobalScore() >= bestFitness)
                best.add(evaluatedSolutions[popmember]);
        }
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}