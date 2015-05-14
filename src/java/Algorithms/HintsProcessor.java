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
    public Profile InterpretHintInProfile( Profile toChange)
  {
    Profile thisProfile =  toChange;
    SolutionAttributes currentVariable = null;
    Hashtable profileLevelVars = thisProfile.getSolutionAttributes();
    Hashtable kernels =thisProfile.getKernels();
    String currentVarName;
    
    ArrayList variablesAffected = new ArrayList();
    ArrayList textKernels = new ArrayList();
    textKernels.add("h1");
    textKernels.add("h2");
    textKernels.add("p");   
          
          
    
    //free back ground colour affects the page bg RGB values
    //relatively simple becuase these are profile level variables
      if (thisProfile.isFreezeBGColour())
        {
          variablesAffected.clear();
          variablesAffected.add("Page_bg_Red");
          variablesAffected.add("Page_bg_Blue");
          variablesAffected.add("Page_bg_Green");          
          
            for (Iterator iterator = variablesAffected.iterator(); iterator.hasNext();)
              {
                 currentVarName = (String) iterator.next();
                //get the variable from the local copy in the hashtable
                currentVariable = (SolutionAttributes) profileLevelVars.get(currentVarName);
                //set the rate of evoltion to zero so mutation has no effect
                currentVariable.setRateOfEvolution(0.0);
                //remove thee old variable with this name from thisProfile
                thisProfile.removeVariable(currentVarName);
                //write back in the changed variable
                thisProfile.addVariable(currentVariable);
              }
           variablesAffected.clear(); 
        }//end of code to freeze background colours
      
    //next hint freezes the colour and size of the foreground fonts
    //so it need ot do it in each kernel that represents a paragraph style
    if(thisProfile.isFreezeFGFonts())
      {     
        //get a list of all the kernels present
        Enumeration enuKer = kernels.elements();
        // loop through each kernel in turn,
        while (enuKer.hasMoreElements()) 
        {
            //get the next kernel
            Kernel kernel = (Kernel) enuKer.nextElement();
            //see if it is one of the paragraph types affected
            for (Iterator kerneliterator = textKernels.iterator(); kerneliterator.hasNext();)
              if (kernel.getName().equalsIgnoreCase((String) kerneliterator.next()))
                {
                //get all of its variables
                Hashtable vars = kernel.getVariables();
                Enumeration eVar = vars.keys();
                //System.out.println(".....Kernel " + kernel.getName() + " has " + vars.size() + " elements");

                // and then set the value each of the variables within kernel in turn
                while (eVar.hasMoreElements()) 
                    {
                        currentVarName = eVar.nextElement().toString();
                        currentVariable = (SolutionAttributes) vars.get(currentVarName);
                      //set the rate of evoltion to zero so mutation has no effect
                        currentVariable.setRateOfEvolution(0.0);
                        vars.put(currentVarName, currentVariable);
                    }    
                Kernel changedKernel = new Kernel(kernel.getName(), vars);
                //finally need to write this new kernel back to the profile in the  nextGen arraylist
                //delete the old one the add the new one
                thisProfile.removeKernel(kernel.getName());
                thisProfile.addKernel(changedKernel);
                } //end of code dealing with paragrpah kerel
        }//end of loop over all kernels
      }//end of code to freeze all aspects of foreground fonts
            
      
    //next piece of code deals with the text size slider - 5 is the default value
    // this version is deterministic because we dnt yet have a bias value in a soltion attribute as well as a rate of evolution
    if(thisProfile.getChangeFontSize() !=1)
      {
        //state which variables are affected - just th font size in this case
        variablesAffected.clear();
          variablesAffected.add("font-size");
            //get a list of all the kernels present
        Enumeration enuKer = kernels.elements();
        // loop through each kernel in turn,
        while (enuKer.hasMoreElements()) 
        {
            //get the next kernel
            Kernel kernel = (Kernel) enuKer.nextElement();
            //see if it is one of the paragraph types affected
            for (Iterator kerneliterator = textKernels.iterator(); kerneliterator.hasNext();)
              if (kernel.getName().equalsIgnoreCase((String) kerneliterator.next()))
                {
                    //get all of its  variables
                    Hashtable vars = kernel.getVariables();
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
                            else if ( thisProfile.getChangeFontSize()==0)///"bigger"
                                value = value*2.0;
                            else //anything else
                                throw new UnsupportedOperationException("thisProfile.getChangeFontSize() returned a value that is not 0 1 or 2");
                           } catch (Exception e)
                           {
                              e.printStackTrace();
                           }
                       
                        //write this new value into the current variable
                          currentVariable.setValue(value);
                        //put this back into the hash table of vars for the kernel  
                          vars.put(currentVarName, currentVariable);
                     }    
                    Kernel changedKernel = new Kernel(kernel.getName(), vars);
                    //finally need to write this new kernel back to the local copy of the profile 
                    //delete the old one the add the new one
                    thisProfile.removeKernel(kernel.getName());
                    thisProfile.addKernel(changedKernel);
                }//end of relevant kernels
   
        
        }//end of loop over all kernels
        
      }//end of text size case
       
      
      // all hints have been dealt with - we can exit
      return thisProfile;
  }
}
