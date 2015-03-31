package Algorithms;

import Src.Artifact;
import Src.Profile;



/**
 * The Interface Processor.
 */
public interface Processor {
	
	public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder); 

}
