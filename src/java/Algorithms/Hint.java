package Algorithms;

import Src.Kernel;
import Src.Profile;
import Src.IpatVariable;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

/**
 *
 * @author kieran
 */
public class Hint {
    private static final Logger logger = Logger.getLogger(Hint.class);

    private ArrayList profileVariablesAffected;
    private ArrayList kernelsAffected;
    private ArrayList kernelVariablesAffected;
    private String hintName;
    private String displaytype;
    private String displaytext;
    private double rangeMin;
    private double rangeMax;
    private String defaultValue;
    private String effect;

    public Hint() {
        profileVariablesAffected = new ArrayList();
        kernelsAffected = new ArrayList();
        kernelVariablesAffected = new ArrayList();
        hintName = null;
    }

    public Hint(String name) {
        profileVariablesAffected = new ArrayList();
        kernelsAffected = new ArrayList();
        kernelVariablesAffected = new ArrayList();
        hintName = name;
    }

    public Hint(String theName, String theDisplaytype, String theDisplaytext, double theRangeMin, double theRangeMax, String theDefaultVal, String theEffect) {
        profileVariablesAffected = new ArrayList();
        kernelsAffected = new ArrayList();
        kernelVariablesAffected = new ArrayList();
        hintName = theName;
        displaytype = theDisplaytype;
        displaytext = theDisplaytext;
        if (theRangeMax < theRangeMin)//check that they haven;t been entered the wrong way around
        {
            rangeMax = theRangeMin;
            rangeMin = theRangeMax;
        } else {
            rangeMin = theRangeMin;
            rangeMax = theRangeMax;
        }

        defaultValue = theDefaultVal;
        effect = theEffect;
    }

    public void AddAffectedKernel(String kernelName) {
        kernelsAffected.add(kernelName);
    }

    public ArrayList getKernelsAffected() {
        return kernelsAffected;
    }

    public String getHintName() {
        return hintName;
    }

    public void setHintName(String hintName) {
        this.hintName = hintName;
    }

    public void addAffectedProfileVariable(String newVarName) {
        profileVariablesAffected.add(newVarName);
    }

    public ArrayList getProfileVariablesAffected() {
        return profileVariablesAffected;
    }

    public void addAffectedKernelVariable(String newVarName) {
        kernelVariablesAffected.add(newVarName);
    }

    public ArrayList getKernelVariablesAffected() {
        return kernelVariablesAffected;
    }

    public String getDisplaytype() {
        return displaytype;
    }

    public void setDisplaytype(String displaytype) {
        this.displaytype = displaytype;
    }

    public String getDisplaytext() {
        return displaytext;
    }

    public void setDisplaytext(String displaytext) {
        this.displaytext = displaytext;
    }

    public double getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(double rangeMin) {
        this.rangeMin = rangeMin;
    }

    public double getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(double rangeMax) {
        this.rangeMax = rangeMax;
    }

    public String getDefaultValue() {
        return this.defaultValue;
    }

    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    public String getEffect() {
        return effect;
    }

    public void setEffect(String effect) {
        this.effect = effect;
    }

    public Profile InterpretHintInProfile(Profile toChange, double amount) {
        Profile thisProfile = toChange;
        if (effect.equalsIgnoreCase("setRateOfEvolutionEqualZero")) {
            if ((amount != 1.0) && (amount != 0.0)) {
               logger.error("wrong value for amount with setRateOfEvolutionEqualZero hint should be 0 or 1");
            } else {
                thisProfile = InterpretSetRateOfEvolutionEqualZeroHintInProfile(toChange, amount);
            }
        } else if (effect.equalsIgnoreCase("moderateByValue")) {
            thisProfile = InterpretModeratingHintInProfile(toChange, amount);
        } else if (effect.equalsIgnoreCase("toggle")) {
            thisProfile = InterpretToggleHintInProfile(toChange, amount);
        } else if (effect.equalsIgnoreCase("setNewValue")) {
            thisProfile = InterpretSetNewValueInProfile(toChange, amount);
        } else {
            thisProfile = toChange;//no other ttypes impl;emetned yet
        }

        //finally write the changed values to file
        String profilePath = thisProfile.getFile().getPath();
        String profileNameAndPath = profilePath + thisProfile.getName();
        thisProfile.writeProfileToFile(profileNameAndPath);

        //and then return
        return thisProfile;
    }

    public Profile InterpretSetNewValueInProfile(Profile toChange, double amount) {
        Profile thisProfile = toChange;
        IpatVariable currentVariable = null;
        HashMap profileLevelVars = thisProfile.getSolutionAttributes();
        HashMap kernels = thisProfile.getKernels();
        String currentVarName;

        assert ((amount >= rangeMin) && (amount <= rangeMax));

        //  System.out.println("in InterpretModeratingHintInProfile(), range min = " + rangeMin + "rangeMax = " + rangeMax + "amount = " + amount + "multiplier is " + multiplier);   
        //start off with the profile variables that are affected
        for (Iterator profileVariableIterator = profileVariablesAffected.iterator(); profileVariableIterator.hasNext();) {
            currentVarName = (String) profileVariableIterator.next();
            //get the variable from the local copy in the hashtable
            currentVariable = (IpatVariable) profileLevelVars.get(currentVarName);
            if (currentVariable == null) {
                logger.error("error - trying to change  variable " + currentVarName + " which does not exist in profile");
            } else {
                //reset the value in thecopy of the variable
                currentVariable.setValue(amount);
                //remove the old variable with this name from thisProfile
                thisProfile.removeVariable(currentVarName);
                //write back in the changed variable
                thisProfile.addVariable(currentVariable);
            }
        }
        //then for each of the affected kernels
        for (Iterator kernelIterator = kernelsAffected.iterator(); kernelIterator.hasNext();) {
            String kernelname = (String) kernelIterator.next();
            Kernel kernel = (Kernel) thisProfile.getKernelCalled(kernelname);
            if (0 == 1) ;//TODO if kernel == null throw an exception
            else {
                HashMap vars = kernel.getVariables();
                Iterator kvarIterator;
                // if we don't have a list of which kernel variables to change use all
                if (kernelVariablesAffected.isEmpty()) {//boring conversion because Profile uses hashmaps and enumerators
                    ArrayList allKernelVariables = new ArrayList();
                    Set keySet = vars.keySet();
                    Iterator kVarNames = keySet.iterator();
                    while (kVarNames.hasNext()) {
                        allKernelVariables.add(kVarNames.next().toString());
                    }
                    kvarIterator = allKernelVariables.iterator();
                } else // otherwise, if we have some specified, just use them
                {
                    kvarIterator = kernelVariablesAffected.iterator();
                }

                while (kvarIterator.hasNext()) {
                    currentVarName = (String) kvarIterator.next();
                    currentVariable = (IpatVariable) vars.get(currentVarName);
                    //reset the value in thecopy of the variable
                    currentVariable.setValue(amount);
                    vars.put(currentVarName, currentVariable);
                }
                Kernel changedKernel = new Kernel(kernel.getName(), vars);
                //finally need to write this new kernel back to the profile in the  nextGen arraylist
                //delete the old one the add the new one
                thisProfile.removeKernel(kernel.getName());
                thisProfile.addKernel(changedKernel);
            } //end of code dealing with affected kernels
        }

        return thisProfile;
    }

    /**
     * method that interprets the hints provided by the user and makes
     * appropriate application-specific changes ot the profile variables
     *
     * @param thisProfile
     * @return changed profile
     */
    public Profile InterpretSetRateOfEvolutionEqualZeroHintInProfile(Profile toChange, double amount) {
        Profile thisProfile = toChange;
        IpatVariable currentVariable = null;
        HashMap profileLevelVars = thisProfile.getSolutionAttributes();
        HashMap kernels = thisProfile.getKernels();
        String currentVarName;

        //first freeze all of the profile level variables
        for (Iterator profileVariableIterator = profileVariablesAffected.iterator(); profileVariableIterator.hasNext();) {
            currentVarName = (String) profileVariableIterator.next();
            //get the variable from the local copy in the hashtable
            currentVariable = (IpatVariable) profileLevelVars.get(currentVarName);
            //set the rate of evoltion to zero so mutation has no effect
            currentVariable.setRateOfEvolution(amount);
            //remove the old variable with this name from thisProfile
            thisProfile.removeVariable(currentVarName);
            //write back in the changed variable
            thisProfile.addVariable(currentVariable);
        }

        //then for each of the affected kernels
        for (Iterator kernelIterator = kernelsAffected.iterator(); kernelIterator.hasNext();) {
            String kernelname = (String) kernelIterator.next();
            Kernel kernel = (Kernel) thisProfile.getKernelCalled(kernelname);
            if (0 == 1) ;//TODO if kernel == null throw an exception
            else {
                HashMap vars = kernel.getVariables();
                Iterator kvarIterator;
                // if we don't have a list of which kernel variables to change use all
                if (kernelVariablesAffected.isEmpty()) {//boring conversion because Profile uses hashmaps and enumerators
                    ArrayList allKernelVariables = new ArrayList();
                    Set keySet = vars.keySet();
                    Iterator kVarNames = keySet.iterator();
                    while (kVarNames.hasNext()) {
                        allKernelVariables.add(kVarNames.next().toString());
                    }
                    kvarIterator = allKernelVariables.iterator();
                } else // otherwise, if we have some specified, just use them
                {
                    kvarIterator = kernelVariablesAffected.iterator();
                }

                while (kvarIterator.hasNext()) {
                    currentVarName = (String) kvarIterator.next();
                    currentVariable = (IpatVariable) vars.get(currentVarName);
                    //set the rate of evoltion to zero so mutation has no effect
                    currentVariable.setRateOfEvolution(amount);
                    vars.put(currentVarName, currentVariable);
                }
                Kernel changedKernel = new Kernel(kernel.getName(), vars);
                //finally need to write this new kernel back to the profile in the  nextGen arraylist
                //delete the old one the add the new one
                thisProfile.removeKernel(kernel.getName());
                thisProfile.addKernel(changedKernel);
            } //end of code dealing with affected kernels
        }

        // all hints have been dealt with - we can exit
        return thisProfile;
    }

    /**
     * method that interprets the hints provided by the user and makes
     * appropriate application-specific changes ot the profile variables
     *
     * @param thisProfile
     * @return changed profile
     */
    public Profile InterpretModeratingHintInProfile(Profile toChange, double amount) {
        Profile thisProfile = toChange;
        IpatVariable currentVariable = null;
        HashMap profileLevelVars = thisProfile.getSolutionAttributes();
        HashMap kernels = thisProfile.getKernels();
        String currentVarName;

        assert ((amount >= rangeMin) && (amount <= rangeMax));
        double range = rangeMax - rangeMin;
        double midpoint = rangeMin + 0.5 * range;
        amount = (amount - midpoint);
        double multiplier = Math.pow(2.0, amount);

        //  System.out.println("in InterpretModeratingHintInProfile(), range min = " + rangeMin + "rangeMax = " + rangeMax + "amount = " + amount + "multiplier is " + multiplier);   
        //start off with the profile variables that are affected
        for (Iterator profileVariableIterator = profileVariablesAffected.iterator(); profileVariableIterator.hasNext();) {
            currentVarName = (String) profileVariableIterator.next();
            //get the variable from the local copy in the hashtable
            currentVariable = (IpatVariable) profileLevelVars.get(currentVarName);
            //set the rate of evoltion to zero so mutation has no effect
            double oldValue = currentVariable.getValue();

            //calculate raw new value
            double newValue = oldValue * multiplier;
            //take account of granularity
            newValue = newValue - Math.IEEEremainder(newValue, currentVariable.getGranularity());
            //truncate to range
            newValue = Math.max(currentVariable.getLbound(), newValue);
            newValue = Math.min(currentVariable.getUbound(), newValue);

            //reset the value in thecopy of the variable
            currentVariable.setValue(newValue);
            //remove the old variable with this name from thisProfile
            thisProfile.removeVariable(currentVarName);
            //write back in the changed variable
            thisProfile.addVariable(currentVariable);
        }
        //then for each of the affected kernels
        for (Iterator kernelIterator = kernelsAffected.iterator(); kernelIterator.hasNext();) {
            String kernelname = (String) kernelIterator.next();
            Kernel kernel = (Kernel) thisProfile.getKernelCalled(kernelname);
            if (0 == 1) ;//TODO if kernel == null throw an exception
            else {
                HashMap vars = kernel.getVariables();
                Iterator kvarIterator;
                // if we don't have a list of which kernel variables to change use all
                if (kernelVariablesAffected.isEmpty()) {//boring conversion because Profile uses hashmaps and enumerators
                    ArrayList allKernelVariables = new ArrayList();
                    Set keySet = vars.keySet();
                    Iterator kVarNames = keySet.iterator();
                    while (kVarNames.hasNext()) {
                        allKernelVariables.add(kVarNames.next().toString());
                    }
                    kvarIterator = allKernelVariables.iterator();
                } else // otherwise, if we have some specified, just use them
                {
                    kvarIterator = kernelVariablesAffected.iterator();
                }

                while (kvarIterator.hasNext()) {
                    currentVarName = (String) kvarIterator.next();
                    currentVariable = (IpatVariable) vars.get(currentVarName);
                    double oldValue = currentVariable.getValue();

                    //calculate raw new value
                    double newValue = oldValue * multiplier;
                    //take account of granularity
                    newValue = newValue - Math.IEEEremainder(newValue, currentVariable.getGranularity());
                    //truncate to range
                    newValue = Math.max(currentVariable.getLbound(), newValue);
                    newValue = Math.min(currentVariable.getUbound(), newValue);

                    //reset the value in thecopy of the variable
                    currentVariable.setValue(newValue);
                    vars.put(currentVarName, currentVariable);
                }
                Kernel changedKernel = new Kernel(kernel.getName(), vars);
                //finally need to write this new kernel back to the profile in the  nextGen arraylist
                //delete the old one the add the new one
                thisProfile.removeKernel(kernel.getName());
                thisProfile.addKernel(changedKernel);
            } //end of code dealing with affected kernels
        }

        return thisProfile;
    }

    /**
     * method that interprets the hints provided by the user and makes
     * appropriate application-specific changes ot the profile variables
     *
     * @param thisProfile
     * @return changed profile
     */
    public Profile InterpretToggleHintInProfile(Profile toChange, double amount) {
        Profile thisProfile = toChange;
        IpatVariable currentVariable = null;
        HashMap profileLevelVars = thisProfile.getSolutionAttributes();
        HashMap kernels = thisProfile.getKernels();
        String currentVarName;
        double newValue;

        //start off with the profile variables that are affected
        for (Iterator profileVariableIterator = profileVariablesAffected.iterator(); profileVariableIterator.hasNext();) {
            currentVarName = (String) profileVariableIterator.next();
            //get the variable from the local copy in the hashtable
            currentVariable = (IpatVariable) profileLevelVars.get(currentVarName);

            if (currentVariable == null) {
                logger.error("trying to change profile variable " + currentVarName + " but it doesnt exist in the profile");
            }

            //toggle the value of the variables
            if (amount == rangeMin) {
                newValue = currentVariable.getLbound();
            } else if (amount == rangeMax) {
                newValue = currentVariable.getUbound();
            } else {
                newValue = currentVariable.getValue();
            }
            //reset the value in thecopy of the variable
            currentVariable.setValue(newValue);
            //remove the old variable with this name from thisProfile
            thisProfile.removeVariable(currentVarName);
            //write back in the changed variable
            thisProfile.addVariable(currentVariable);
        }
        //then for each of the affected kernels
        for (Iterator kernelIterator = kernelsAffected.iterator(); kernelIterator.hasNext();) {
            String kernelname = (String) kernelIterator.next();
            Kernel kernel = (Kernel) thisProfile.getKernelCalled(kernelname);
            if (kernel != null) {
                if (0 == 1) ;//TODO if kernel == null throw an exception
                else {
                    HashMap vars = kernel.getVariables();
                    Iterator kvarIterator;
                    // if we don't have a list of which kernel variables to change use all
                    if (kernelVariablesAffected.isEmpty()) {//boring conversion because Profile uses hashmaps and enumerators
                        ArrayList allKernelVariables = new ArrayList();
                        Set keySet = vars.keySet();
                        Iterator kVarNames = keySet.iterator();
                        while (kVarNames.hasNext()) {
                            allKernelVariables.add(kVarNames.next().toString());
                        }
                        kvarIterator = allKernelVariables.iterator();
                    } else // otherwise, if we have some specified, just use them
                    {
                        kvarIterator = kernelVariablesAffected.iterator();
                    }

                    while (kvarIterator.hasNext()) {
                        currentVarName = (String) kvarIterator.next();
                        currentVariable = (IpatVariable) vars.get(currentVarName);
                        //toggle the value of the variables
                        if (amount == rangeMin) {
                            newValue = currentVariable.getLbound();
                        } else if (amount == rangeMax) {
                            newValue = currentVariable.getUbound();
                        } else {
                            newValue = currentVariable.getValue();
                        }

                        //reset the value in thecopy of the variable
                        currentVariable.setValue(newValue);
                        vars.put(currentVarName, currentVariable);
                    }
                    Kernel changedKernel = new Kernel(kernel.getName(), vars);
                    //finally need to write this new kernel back to the profile in the  nextGen arraylist
                    //delete the old one the add the new one
                    thisProfile.removeKernel(kernel.getName());
                    thisProfile.addKernel(changedKernel);
                } //end of code dealing with affected kernels
            }
        }

        return thisProfile;
    }

}