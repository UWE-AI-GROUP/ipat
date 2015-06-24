/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import org.apache.log4j.Logger;

/**
 *
 * @author kieran
 */
public class SolutionAttributes {

    private static final Logger logger = Logger.getLogger(SolutionAttributes.class);

    /**
     * The dfault.
     */
    private String dfault = "";

    /**
     * The flag.
     */
    private String flag = "";

    /**
     * The granularity.
     */
    private double granularity = 0.0;

    /**
     * the rate of evolution e.g. step size for ranges
     */
    private double rateOfEvolution = 0.0;

    /**
     * The lbound.
     */
    private double lbound = 0.0;

    /**
     * The name.
     */
    private String name = "";

    /**
     * The type.
     */
    private String type = "";

    /**
     * The ubound.
     */
    private double ubound = 0.0;

    /**
     * The value.
     */
    private double value = 0.0;

    /**
     * The unit.
     */
    private String unit;

    /**
     * Instantiates a new ipat variable.
     *
     * @param name the name
     * @param type the type
     * @param lbound the lbound
     * @param ubound the ubound
     * @param granularity the granularity
     * @param rateOfEvolution
     * @param value the value
     * @param dfault the dfault
     * @param flag the flag
     * @param unit the unit
     */
    public SolutionAttributes(String name, String type, double lbound, double ubound,
            double granularity, double rateOfEvolution, double value, String dfault, String flag, String unit) {
        this.name = name;
        this.type = type;
        this.lbound = lbound;
        this.ubound = ubound;
        this.granularity = granularity;
        this.rateOfEvolution = rateOfEvolution;
        this.value = value;
        this.dfault = dfault;
        this.flag = flag;
        this.unit = unit;
    }

    public void randomiseValues() {
        double range = (this.ubound - this.lbound);
        if (this.granularity == 0.0) {
            logger.info("granularity in solutionAttributes has been found to be 0.0 ");
        } else {
           // logger.debug("solution attribute value before randomisation : " + this.value);
            this.value = this.granularity * Math.floor(((Math.random() * range) + this.lbound)/this.granularity); 
            // logger.debug("solution attribute value after randomisation : " + this.value);
        }
    }

    /**
     * Gets the dfault.
     *
     * @return the dfault
     */
    public String getDfault() {
        return dfault;
    }

    /**
     * Gets the flag.
     *
     * @return the flag
     */
    public String getFlag() {
        return flag;
    }

    /**
     * Gets the rateOfEvolution.
     *
     * @return the rateOfEvolution
     */
    public double getRateOfEvolution() {
        return rateOfEvolution;
    }

    /**
     * Sets the rateOfEvolution.
     *
     * @param the new rateOfEvolution
     */
    public void setRateOfEvolution(double newval) {
        this.rateOfEvolution = newval;
    }

    /**
     * Gets the granularity.
     *
     * @return the granularity
     */
    public double getGranularity() {
        return granularity;
    }

    /**
     * Gets the lbound.
     *
     * @return the lbound
     */
    public double getLbound() {
        return lbound;
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
     * Gets the type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the ubound.
     *
     * @return the ubound
     */
    public double getUbound() {
        return ubound;
    }

    /**
     * Gets the value.
     *
     * @return the value
     */
    public double getValue() {
        return value;
    }

    /**
     * Sets the value.
     *
     * @param value the new value
     */
    public void setValue(double value) {
        this.value = value;
    }

    /**
     * Gets the unit.
     *
     * @return the unit
     */
    public String getUnit() {
        return unit;
    }

    /**
     * Sets the unit.
     *
     * @param unit the new unit
     */
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
