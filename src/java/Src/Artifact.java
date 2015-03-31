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
public class Artifact {
    	
	private File file;
	private String filename;
	private String filepath;
        
	
	public Artifact(File f){
		this.file = f;
		this.setupFile();
	}

	public File getFile() {
		return file;
	}

	public String getFilename() {
		return filename;
	}

	public String getFilepath() {
		return filepath;
	}
	
	public void setupFile(){
		filename = file.getName();
		filepath = file.getAbsolutePath();
	}
}
