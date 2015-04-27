package Algorithms;

import Src.Profile;

/**
 * The Interface Evolution.
 */
public interface MetaHeuristic {

    
    public void updateWorkingMemory(Profile[] evaluatedSolutions);
    //Jim 30/3 changed signature for method to take the array of profiles as a parameter so we know how many ot make and where ot put them
    public Profile[] generateNextSolutions( int howMany);

    //controller gives x number of profiles with some ID (generation)
    // generate next solutions creates x Profiles and writes them into file with the ID attached
    

}