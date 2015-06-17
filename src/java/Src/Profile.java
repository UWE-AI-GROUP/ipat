/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author kieran
 */
public class Profile {

    /**
     * The file.
     */
    private File file;

    /**
     * The global score.
     */
    private int globalScore = 5;

    /**
     * The kernels.
     */
    private HashMap kernels;

    /**
     * The name.
     */
    private String name;

    /**
     * The no of kernels.
     */
    private int noOfKernels;

    /**
     * The no of kernel solutionAttributes.
     */
    private int noOfKernerlVariables;

    /**
     * The no of profile solutionAttributes.
     */
    private int noOfProfileVariables;

    /**
     * The solutionAttributes.
     */
    private HashMap solutionAttributes;

    /**
     * Instantiates a new ipat profile.
     *
     * @param file the file
     */
    public Profile(File file) {
        solutionAttributes = new HashMap();
        kernels = new HashMap();
        this.file = file;
        this.name = file.getName();
    }

    public void randomiseProfileVariableValues() {
        Collection vals = this.solutionAttributes.values();
        Iterator iterator = vals.iterator();
        while (iterator.hasNext()) {
            SolutionAttributes SA = (SolutionAttributes) iterator.next();
            SA.randomizeValues();
        }
    }

    // TODO randomise kernel Values
    public void randomiseKernelValues() {
        Collection vals = this.kernels.values();
        Iterator iterator = vals.iterator();
        while (iterator.hasNext()) {
            Kernel SA = (Kernel) iterator.next();
            // SA.randomizeValues();
        }
    }

    /**
     * Adds the kernel.
     *
     * @param kernel the kernel
     */
    public void addKernel(Kernel kernel) {
        kernels.put(kernel.getName(), kernel);
    }

    /**
     *
     * @param kernelName
     */
    public void removeKernel(String kernelName) {
        kernels.remove(kernelName);
    }

    /**
     * Adds the variable.
     *
     * @param kernelName string
     * @return Kernel with the name kernelName if that key-value pair exists in
     * thisProfile.kernels else null
     */
    public Kernel getKernelCalled(String kernelName) {
        Kernel found = null;
        found = (Kernel) kernels.get(kernelName);
        return found;
    }

    /**
     * Adds the profile level variable to the HashMap in thisProfile
     *
     * @param var the variable to be added to the solutionattributes hashtable
     */
    public void addVariable(SolutionAttributes var) {
        solutionAttributes.put(var.getName(), var);
    }

    public void removeVariable(String varname) {
        solutionAttributes.remove(varname);
    }

    /**
     * Gets the file.
     *
     * @return the file associated with this profile
     */
    public File getFile() {
        return file;
    }

    public void setFile(File thisfile) {
        file = thisfile;
    }

    /**
     * Gets the global score.
     *
     * @return the global score
     */
    public int getGlobalScore() {
        return globalScore;
    }

    /**
     * Gets the kernels.
     *
     * @return the kernels
     */
    public HashMap getKernels() {
        return kernels;
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
     * Gets the solutionAttributes.
     *
     * @return the solutionAttributes
     */
    public HashMap getSolutionAttributes() {
        return solutionAttributes;
    }

    /**
     * Prints the profile.
     */
    public void printProfile() {
        Set keys = solutionAttributes.keySet();
        Iterator AttributesIterator = keys.iterator();
        while (AttributesIterator.hasNext()) {
            SolutionAttributes var = (SolutionAttributes) AttributesIterator.next();
            System.out.println(var.getName() + " : " + var.getValue());
        }
        Set keySet = kernels.keySet();
        Iterator kernelIterator = keySet.iterator();
        while (kernelIterator.hasNext()) {
            String name = (String) kernelIterator.next();
            System.out.println(name);
            Kernel kernel = (Kernel) kernels.get(name);
            HashMap kVars = kernel.getVariables();
            Set kVarsKeys = kVars.keySet();
            Iterator kVarsKeysIterator = kVarsKeys.iterator();
            while (kVarsKeysIterator.hasNext()) {
                SolutionAttributes var = (SolutionAttributes) kVars.get(kVarsKeysIterator.next());
                System.out.println("   " + var.getName() + " : "
                        + var.getValue());
            }
        }
    }

    /**
     * Sets the global score.
     *
     * @param globalScore the new global score
     */
    public void setGlobalScore(int globalScore) {
        this.globalScore = globalScore;
    }

    /**
     * Convert string to document.
     *
     * @param string the string
     * @return the document
     */
    public Document convertStringToDocument(String string) {
        try {
            StringReader stringReader = new StringReader(string);
            return new SAXBuilder().build(stringReader);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the profile.
     *
     * @param file the file
     * @return the profile
     */
    public Profile getProfile(File file) {
        Profile profile = new Profile(file);
        try {
            Document XmlDoc = new SAXBuilder().build(file);

            Element root = XmlDoc.getRootElement();
            Element profileNode = root.getChild("profile", root.getNamespace());
            Iterator iterator = profileNode.getChildren().iterator();
            int i = 0;
            while (iterator.hasNext()) {
                Element hint = (Element) iterator.next();
                if (hint.getName().equalsIgnoreCase("variable")) {
                    String name = hint.getChildText("name");
                    String type = hint.getChildText("type");

                    String temp = hint.getChildText("lbound");
                    Double dub = new Double(temp);
                    double lbound = dub.doubleValue();

                    temp = hint.getChildText("ubound");
                    dub = new Double(temp);
                    double ubound = dub.doubleValue();

                    temp = hint.getChildText("granularity");
                    dub = new Double(temp);
                    double granularity = dub.doubleValue();

                    temp = hint.getChildText("rateOfEvolution");
                    dub = new Double(temp);
                    double rateOfEvolution = dub.doubleValue();

                    temp = hint.getChildText("value");
                    dub = new Double(temp);
                    double value = dub.doubleValue();

                    String dfault = hint.getChildText("default");
                    String flag = hint.getChildText("flag");
                    String unit = hint.getChildText("unit");

                    SolutionAttributes variable = new SolutionAttributes(name, type,
                            lbound, ubound, granularity, rateOfEvolution, value, dfault, flag, unit);
                    profile.addVariable(variable);
                } else if (hint.getName().equalsIgnoreCase("kernel")) {

                    Iterator it = hint.getChildren().iterator();
                    Element nm = (Element) it.next();
                    String kernelName = nm.getText();
                    HashMap vars = new HashMap();
                    while (it.hasNext()) {
                        Element hintt = (Element) it.next();
                        String name = hintt.getChildText("name");
                        String type = hintt.getChildText("type");

                        String temp = hintt.getChildText("lbound");
                        Double dub = new Double(temp);
                        double lbound = dub.doubleValue();

                        temp = hintt.getChildText("ubound");
                        dub = new Double(temp);
                        double ubound = dub.doubleValue();

                        temp = hintt.getChildText("granularity");
                        dub = new Double(temp);
                        double granularity = dub.doubleValue();

                        temp = hintt.getChildText("rateOfEvolution");
                        dub = new Double(temp);
                        double rateOfEvolution = dub.doubleValue();

                        temp = hintt.getChildText("value");
                        dub = new Double(temp);
                        double value = dub.doubleValue();

                        String dfault = hintt.getChildText("default");
                        String flag = hintt.getChildText("flag");
                        String unit = hintt.getChildText("unit");

                        SolutionAttributes variable = new SolutionAttributes(name, type,
                                lbound, ubound, granularity, rateOfEvolution, value, dfault,
                                flag, unit);
                        vars.put(name, variable);
                    }
                    Kernel kernel = new Kernel(kernelName, vars);
                    profile.addKernel(kernel);
                }
            }
        } catch (Exception pce) {
            pce.printStackTrace();
        }
        return profile;
    }

    /**
     * Sets the profile.
     *
     * @param profile the profile
     * @return true, if successful
     */
    public boolean setProfile() {

        try {

            Document XmlDoc = new SAXBuilder().build(this.getFile());
            Element root = XmlDoc.getRootElement();
            Element profileNode = root.getChild("profile", root.getNamespace());
            List children = profileNode.getChildren();
            Iterator iterator = children.iterator();
            HashMap mySolutionAttributes = this.getSolutionAttributes();
            HashMap kernels = this.getKernels();
            Set enuK = this.getKernels().keySet();

            while (iterator.hasNext()) {

                Element hint = (Element) iterator.next();

                if (hint.getName().equalsIgnoreCase("variable")) {

                  //  System.out.println("\n Profile variable \n");
                    Element elem = hint.getChild("name");
                    SolutionAttributes var = (SolutionAttributes) mySolutionAttributes.get(elem.getValue());
                    elem.setText(var.getName());

                    elem = hint.getChild("type");
                    //  System.out.println("var.getType() = " + var.getType());
                    elem.setText(var.getType());

                    elem = hint.getChild("lbound");
                    Double dub = new Double(var.getLbound());
                    //  System.out.println("var.getLbound() = " + var.getLbound());
                    elem.setText(dub.toString());

                    elem = hint.getChild("ubound");
                    dub = new Double(var.getUbound());
                    // System.out.println("var.getUbound() = " + var.getUbound());
                    elem.setText(dub.toString());

                    elem = hint.getChild("granularity");
                    dub = new Double(var.getGranularity());
                    // System.out.println("var.getGranularity() = " + var.getGranularity());
                    elem.setText(dub.toString());

                    elem = hint.getChild("rateOfEvolution");
                    dub = new Double(var.getRateOfEvolution());
                    //  System.out.println("var.getRateOfEvolution() = " + var.getRateOfEvolution());
                    elem.setText(dub.toString());

                    elem = hint.getChild("value");
                    dub = new Double(var.getValue());
                    //  System.out.println("var.getValue() = " + var.getValue());
                    elem.setText(dub.toString());

                    elem = hint.getChild("default");
                    //   System.out.println("var.getDfault() = " + var.getDfault());
                    elem.setText(var.getDfault());

                    elem = hint.getChild("flag");
                    //  System.out.println("var.getFlag() = " + var.getFlag());
                    elem.setText(var.getFlag());

                    elem = hint.getChild("unit");
                    //  System.out.println("var.getUnit() = " + var.getUnit());
                    elem.setText(var.getUnit());

                } else if (hint.getName().equalsIgnoreCase("kernel")) {

                    Iterator it = hint.getChildren().iterator();
                    Element nm = (Element) it.next();

                    String kernelName = nm.getText();

                    Kernel kern = (Kernel) kernels.get(kernelName);
                    HashMap vars = kern.getVariables();
                    Collection coll = vars.values();
                    Iterator enu3 = coll.iterator();

                    while (it.hasNext()) {
                        Element hintt = (Element) it.next();
                        SolutionAttributes varb = (SolutionAttributes) enu3.next();

                        Element elem = hintt.getChild("name");
                        elem.setText(varb.getName());

                        elem = hintt.getChild("type");
                        elem.setText(varb.getType());

                        elem = hintt.getChild("lbound");
                        Double dub = new Double(varb.getLbound());
                        elem.setText(dub.toString());

                        elem = hintt.getChild("ubound");
                        dub = new Double(varb.getUbound());
                        elem.setText(dub.toString());

                        elem = hintt.getChild("granularity");
                        dub = new Double(varb.getGranularity());
                        elem.setText(dub.toString());

                        elem = hintt.getChild("rateOfEvolution");
                        dub = new Double(varb.getRateOfEvolution());
                        elem.setText(dub.toString());

                        elem = hintt.getChild("value");
                        dub = new Double(varb.getValue());
                        elem.setText(dub.toString());

                        elem = hintt.getChild("default");
                        elem.setText(varb.getDfault());

                        elem = hintt.getChild("flag");
                        elem.setText(varb.getFlag());

                        elem = hintt.getChild("unit");
                        elem.setText(varb.getUnit());
                    }
                }
            }
            Element scoreNode = root.getChild("globalscore",
                    root.getNamespace());
            Element elemi = scoreNode.getChild("value");
            elemi.setText("0");

            //System.out.println("[SetProfile] Writing score " + 0 + " to profile: " + profile.getFile().toString());
            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(XmlDoc);

            BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFile().getAbsolutePath()));
            writer.write(xmlString);
            writer.close();

        } catch (JDOMException | IOException pce) {
            System.out.println(pce.getMessage());
        }
        return true;
    }

    /**
     * Sets the value of a given variable.
     *
     * @param profile the profile
     * @param varname name of the variable
     * @param newValue value to be updated
     */
    /* added by Jim novemeber 2012 to make setProfileScore more generic */
    public void setProfileVariableValue(String varname, double newValue) {
        HashMap elements = new HashMap();
        try {
            Document XmlDoc = new SAXBuilder().build(this.file);

            Element root = XmlDoc.getRootElement();
            Element graph = root.getChild(varname, root.getNamespace());
            if (graph != null) {
                Element value = graph.getChild("value");
                Double dubi = new Double(newValue);
                value.setText(dubi.toString());
            } else {
                System.out.println("couldn;t find child with name " + varname);
            }

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(XmlDoc);

            BufferedWriter writer = new BufferedWriter(new FileWriter(this.file.getAbsolutePath()));
            writer.write(xmlString);
            writer.close();
        } catch (Exception pce) {
            pce.printStackTrace();
        }
    }

    // used for if the Profile is to have a hard copy of itself in a specified location on disk
    public boolean writeProfileToFile(String outputPath) {
        
        /* apply changes to solution attribute values in memory to their hard copy file before 
        copying that file to the location specified as "outputPath"
        */
        this.setProfile();
        
        String copy = "";
        File file = this.getFile();
        BufferedWriter writer;
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            Document XmlDoc = new SAXBuilder().build(file);
            copy = outputter.outputString(XmlDoc);
            writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(copy);
            writer.close();
        } catch (Exception pce) {
            pce.printStackTrace();
        }
        return true; // copy;
    }
}