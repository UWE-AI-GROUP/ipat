/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.io.File;

/**
 *
 * @author kieran
 */
public class ResultItem {
    
    /**
     * The Constant serialVersionUID.
     */
    private static final long serialVersionUID = 1L;
    /**
     * The css file.
     */
    private File cssFile;
    /**
     * The html file.
     */
    private File htmlFile;
   
    /**
     * The ipat file.
     */
    private Artifact ipatFile;
  
    /**
     * The profile.
     */
    private Profile profile;
    


    /**
     * Instantiates a new ipat result item.
     *
     * @param file the file
     * @param css the css
     * @param manager the manager
     */
    public ResultItem(Artifact file, File css, File html) {

        this.ipatFile = file;
        this.cssFile = css;
        this.htmlFile = html;

    }



    /**
     * Sets the css file.
     *
     * @param cssFile the new css file
     */
    public void setCssFile(File cssFile) {
        this.cssFile = cssFile;
    }

    /**
     * Sets the html file.
     *
     * @param htmlFile the new html file
     */
    public void setHtmlFile(File htmlFile) {
        this.htmlFile = htmlFile;
    }

    /**
     * Sets the profile.
     *
     * @param file the new profile
     */
    public void setProfile(Profile file) {
        this.profile = file;
    }

    /**
     * Gets the css file.
     *
     * @return the css file
     */
    public File getCssFile() {
        return cssFile;
    }

    /**
     * Gets the html file.
     *
     * @return the html file
     */
    public File getHtmlFile() {
        return htmlFile;
    }

    /**
     * Gets the ipat file.
     *
     * @return the ipat file
     */
    public Artifact getIpatFile() {
        return this.ipatFile;
    }

    /**
     * Gets the profile.
     *
     * @return the profile
     */
    public Profile getProfile() {
        return this.profile;
    }
}
