/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.util.Collection;
import java.util.HashMap;

/**
 *
 * @author kieran
 */
public class Kernel {
    
	/** The name. */
	private String name= "";
	
	/** The variables. */
	private HashMap variables;

	/**
	 * Instantiates a new ipat kernel.
	 *
	 * @param name the name
	 * @param variables the variables
	 */
	public Kernel(String name, HashMap variables){
		this.name = name;
		this.variables = variables;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Gets the variables.
	 *
	 * @return the variables
	 */
	public HashMap getVariables() {
		return variables;
	}
        
        public void randomiseValues(){
            Collection<IpatVariable> values = this.variables.values();
            for (IpatVariable SA : values) {
                SA.randomiseValues();
            }
        }
        
}