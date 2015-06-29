package Algorithms;

import Src.Artifact;
import Src.Profile;



/**
 * Application specific code to handle generation of candidate solutions (Phenotypes) from their representations (Genotypes)
 */
public interface Processor {

	public Artifact applyProfileToArtifact(Profile profile, Artifact artifact, String outputFolder); 

}
