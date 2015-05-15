package Algorithms;
import Src.Kernel;
import Src.Profile;
import Src.SolutionAttributes;
import Src.Utils;
import java.util.Hashtable;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;

/**
 *
 * @author kieran
 */
public class HintsProcessor {

    /**
     * method that interprets the hints provided by the user and makes appropriate application-specific changes ot the profile variables
     * @param thisProfile
     * @return changed profile
     */
    public void InterpretHintInProfile( Profile toChange)
  {
    Profile thisProfile =  toChange;
    SolutionAttributes currentVariable = null;
    Hashtable profileLevelVars = thisProfile.getSolutionAttributes();
    Hashtable kernels =thisProfile.getKernels();
    String currentVarName;
    
    //for now I'll use an arraylist holding the names of all of the hints
    //mainly so i cna code in the logic of checking the name before deidnign what to do
    ArrayList hintList = new ArrayList();
    hintList.add("freezeBGColour");
    hintList.add("freezeFGFonts");
    hintList.add("changeFontSize");
    hintList.add("changeFGContrast");
    
    ArrayList variablesAffected = new ArrayList();
    ArrayList textKernelNames = new ArrayList();
    textKernelNames.add("h1");
    textKernelNames.add("h2");
    textKernelNames.add("p");   
          
          
    
    //loop over all of the hints that might be available
      for (Iterator hintIterator = hintList.iterator(); hintIterator.hasNext();)
        {
          String nextHint = (String) hintIterator.next();
          
          if (nextHint.equalsIgnoreCase("freezeBGColour"))
            {
                //System.out.println("dealing with bg colour");;
                //free back ground colour affects the page bg RGB values
                //relatively simple because these are profile level variables
                variablesAffected.clear();
                variablesAffected.add("Page_bg_Red");
                variablesAffected.add("Page_bg_Blue");
                variablesAffected.add("Page_bg_Green");          
          
                for (Iterator varIterator = variablesAffected.iterator(); varIterator.hasNext();)
                    {
                        currentVarName = (String) varIterator.next();
                        //get the variable from the local copy in the hashtable
                        currentVariable = (SolutionAttributes) profileLevelVars.get(currentVarName);
                        //set the rate of evoltion to zero so mutation has no effect
                        if (thisProfile.isFreezeBGColour())
                            currentVariable.setRateOfEvolution(0.0);
                        else
                           currentVariable.setRateOfEvolution(1.0);
                       //remove the old variable with this name from thisProfile
                       thisProfile.removeVariable(currentVarName);
                        //write back in the changed variable
                        thisProfile.addVariable(currentVariable);
                    }
                variablesAffected.clear(); 
            }//end of code to freeze background colours
      
          if (nextHint.equalsIgnoreCase("freezeFGFonts"))
            { //freeze FG fonts needs to go through each text kernel in turn and set the rate
              //System.out.println("dealing with freeze fg fonts, value in profile is " + thisProfile.isFreezeFGFonts());;
              for (Iterator kerneliterator = textKernelNames.iterator(); kerneliterator.hasNext();)
                {//loop over the names in my listarray of strings
                  //eventually replace loop criteria with loop over AffectedKernels for this hint
                  String kernelName = (String) kerneliterator.next();
                  //System.out.println("... paragraph type " + kernelName );
                  Kernel thisKernel =  (Kernel) thisProfile.getKernels().get(kernelName);
                  //  System.out.println( "... kernel name " + thisKernel.getName());
                   //get all of its variables
                        Hashtable vars = thisKernel.getVariables();
                        Enumeration eVar = vars.keys();
                        //System.out.println(".....Kernel " + kernel.getName() + " has " + vars.size() + " elements");

                        // and then set the value each of the variables within kernel in turn
                        while (eVar.hasMoreElements()) 
                          {
                            currentVarName = eVar.nextElement().toString();
                              //System.out.println("current var name is " +currentVarName );
                            currentVariable = (SolutionAttributes) vars.get(currentVarName);
                            //if asked to freeze then set the rate of evoltion to zero so mutation has no effect
                            if(thisProfile.isFreezeFGFonts())  
                                currentVariable.setRateOfEvolution(0.0);
                            //otherwise set it back to 1 again if unfrozen
                            else    
                                currentVariable.setRateOfEvolution(1.0);
                            
                            vars.put(currentVarName, currentVariable);
                          }    
                    Kernel changedKernel = new Kernel(thisKernel.getName(), vars);
                    //finally need to write this new kernel back to the profile in the  nextGen arraylist
                    //delete the old one the add the new one
                    thisProfile.removeKernel(thisKernel.getName());
                    thisProfile.addKernel(changedKernel);
                }

            }//end of code to freeze all aspects of foreground fonts
            
      
            //next piece of code deals with the text size slider - 5 is the default value
            // this version is deterministic because we dnt yet have a bias value in a soltion attribute as well as a rate of evolution
          if (nextHint.equalsIgnoreCase("changeFontSize"))
            {
             // System.out.println("dealing with change font size");;
            //state which variables are affected - just th font size in this case
            variablesAffected.clear();
            variablesAffected.add("font-size");
            //get a list of all the kernels present
            for (Iterator kerneliterator = textKernelNames.iterator(); kerneliterator.hasNext();)
                {//loop over the names in my listarray of strings
                  //eventually replace loop criteria with loop over AffectedKernels for this hint
                  String kernelName = (String) kerneliterator.next();
                  //System.out.println("... paragraph type " + kernelName );
                  Kernel thisKernel =  (Kernel) thisProfile.getKernels().get(kernelName);
                    //get all of its  variables
                    Hashtable vars = thisKernel.getVariables();
                    //loop through the ones ot be changed
                    for (Iterator iterator = variablesAffected.iterator(); iterator.hasNext();)
                     {
                       currentVarName = (String) iterator.next();
                       //get the variable from the local copy in the hashtable
                       currentVariable = (SolutionAttributes) vars.get(currentVarName);
                       //get the old value
                       double value = currentVariable.getValue();
                        //change it according to the hint 
                       //THIS IS THE DETERMIISTIC BIT 
                         try
                           {
                             if( thisProfile.getChangeFontSize()==0)///"smaller"
                                value = value*0.5;
                             else if ( thisProfile.getChangeFontSize()==1)///"leave the same"
                                value = value;
                            else if ( thisProfile.getChangeFontSize()==2)///"bigger"
                                value = value*2.0;
                            else //anything else
                                throw new UnsupportedOperationException("thisProfile.getChangeFontSize() returned a value that is not 0 1 or 2");
                           } catch (Exception e)
                           {
                              //e.printStackTrace();
                           }
                       
                        //write this new value into the current variable
                          currentVariable.setValue(value);
                        //put this back into the hash table of vars for the kernel  
                          vars.put(currentVarName, currentVariable);
                     }    
                    Kernel changedKernel = new Kernel(thisKernel.getName(), vars);
                    //finally need to write this new kernel back to the local copy of the profile 
                    //delete the old one the add the new one
                    thisProfile.removeKernel(thisKernel.getName());
                    thisProfile.addKernel(changedKernel);
                }//end of relevant kernels
            }//end of text size case
       
         if (nextHint.equalsIgnoreCase("changeFGContrast"))
           {
               //System.out.println("dealing with fg contrast");
                       // System.out.println("dealing with change font size");;
            //state which variables are affected - just the font size in this case
            variablesAffected.clear();
            variablesAffected.add("bold");
            variablesAffected.add("italic");
            //get a list of all the kernels present
            for (Iterator kerneliterator = textKernelNames.iterator(); kerneliterator.hasNext();)
                {//loop over the names in my listarray of strings
                  //eventually replace loop criteria with loop over AffectedKernels for this hint
                  String kernelName = (String) kerneliterator.next();
                  //System.out.println("... paragraph type " + kernelName );
                  Kernel thisKernel =  (Kernel) thisProfile.getKernels().get(kernelName);
                    //get all of its  variables
                    Hashtable vars = thisKernel.getVariables();
                    //loop through the ones ot be changed
                    for (Iterator iterator = variablesAffected.iterator(); iterator.hasNext();)
                     {
                       currentVarName = (String) iterator.next();
                       //get the variable from the local copy in the hashtable
                       currentVariable = (SolutionAttributes) vars.get(currentVarName);
                       //get the old value
                       double value = currentVariable.getValue();
                        //change it according to the hint: for less[more] contrast turn italic and bold off[on] 
                        
                         try
                           {
                             if( thisProfile.getChangeGFContrast()==0)///"less"
                                value = 0;
                             else if ( thisProfile.getChangeGFContrast()==1)///"leave the same"
                                value = value;
                            else if ( thisProfile.getChangeGFContrast()==2)///"more"
                                value = 1;
                            else //anything else
                                throw new UnsupportedOperationException("thisProfile.getChangeFontSize() returned a value that is not 0 1 or 2");
                           } catch (Exception e)
                           {
                              //e.printStackTrace();
                           }
                       
                        //write this new value into the current variable
                          currentVariable.setValue(value);
                        //put this back into the hash table of vars for the kernel  
                          vars.put(currentVarName, currentVariable);
                     }    
                    Kernel changedKernel = new Kernel(thisKernel.getName(), vars);
                    //finally need to write this new kernel back to the local copy of the profile 
                    //delete the old one the add the new one
                    thisProfile.removeKernel(thisKernel.getName());
                    thisProfile.addKernel(changedKernel);
                }//end of relevant kernels
           }
        }// all hints have been dealt with - we can exit
      
  }
}