
package Src;

import java.io.File;


public class GlobalVariables {

   
 
    private Artifact[] raw_artifacts;
    private File[] rawArtifactsList;
    private File[] processedArtifactsList;
    private Artifact[] processed_artifacts;
    private File[] profiles_list;
    private Profile[] nextGenerationOfProfiles;
    private Profile[] currentGenerationOfProfiles; 
 



    public Artifact[] getRaw_artifacts() {
        return raw_artifacts;}

    public void setRaw_artifacts_list_from_files(File[] file) {
        assert file != null;
        this.raw_artifacts = new Artifact [file.length];
        for (int i = 0; i < file.length; i++) {
            Artifact artifact = new Artifact(file[i]);
            this.raw_artifacts[i] = artifact;
        }
    }

    public File[] getProfiles_list() {
        return profiles_list;}

    public void setProfiles_list(File[] profiles_list) {
        this.profiles_list = profiles_list;}


    public Profile[] getNextGenerationOfProfiles() {
        return nextGenerationOfProfiles;}

    public void setNextGenerationOfProfiles(Profile[] new_xml_profiles_list) {
        this.nextGenerationOfProfiles = new_xml_profiles_list;}

    public void setNextGenerationOfProfilesAtI(Profile new_xml_profile, int j) {
        this.nextGenerationOfProfiles[j] = new_xml_profile;}

    public Profile[] getCurrentGenerationOfProfiles() {
        return currentGenerationOfProfiles;}

    public void setCurrentGenerationOfProfiles(Profile[] nextGenerationOfProfiles) {
        this.currentGenerationOfProfiles = nextGenerationOfProfiles;}

    public void setCurrentGenerationOfProfilesAtI(Profile xml_profile, int i) {
        this.currentGenerationOfProfiles[i] = xml_profile;}

}
