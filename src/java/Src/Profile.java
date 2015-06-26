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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.log4j.Logger;
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

    private static final Logger logger = Logger.getLogger(Profile.class);

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
     * The no of kernel profileLevelVariables.
     */
    private int noOfKernerlVariables;

    /**
     * The no of profile profileLevelVariables.
     */
    private int noOfProfileVariables;

    /**
     * The profileLevelVariables.
     */
    private HashMap profileLevelVariables;

    /**
     * Instantiates a new ipat profile.
     *
     * @param file the file
     */
    public Profile(File file) {
        profileLevelVariables = new HashMap();
        kernels = new HashMap();
        this.file = file;
        this.name = file.getName();
    }

    public void randomiseProfileVariableValues() {
        Collection<IpatVariable> collection = this.profileLevelVariables.values();
        for (IpatVariable SA : collection) {
            SA.randomiseValues();
           // logger.debug("new value for " + this.name + " PROFILE VARIABLE " + SA.getName() + " = " + SA.getValue() + "\n");
        }
    }

    // TODO randomise kernel Values
    public void randomiseKernelVariableValues() {
        Collection<Kernel> collection = this.kernels.values();
        for (Kernel k : collection) {
            k.randomiseValues();
           // logger.debug("new values for " + this.name + " KERNEL VARIABLE " + k.getName() + ":\n");
            HashMap variables = k.getVariables();
           Collection<IpatVariable> KernelCollection = variables.values();
            for (IpatVariable SA : KernelCollection) {
                // logger.debug(SA.getName() + " = " + SA.getValue());
            }
           // logger.debug("\n----------------------------------------------------------------------------\n");
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

    public void replaceKernel(Kernel kernel) {
        Kernel oldvalue = (Kernel) kernels.put(kernel.getName(), kernel);
        if (oldvalue == null) {
            logger.error("Error replacing kernel " + kernel.getName() + "in profile " + this.getName() + " Not previously present in profile");
        }
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
    public void addVariable(IpatVariable var) {
        profileLevelVariables.put(var.getName(), var);
    }

    public void removeVariable(String varname) {
        profileLevelVariables.remove(varname);
    }

    public void replaceVariable(IpatVariable var) {
        IpatVariable oldval = (IpatVariable) profileLevelVariables.put(var.getName(), var);
        if (oldval == null) {
            logger.error("error replacing profile variable " + var.getName() + " in profile " + this.getName() + " old value not found or null");
        }
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
     * Gets the profileLevelVariables.
     *
     * @return the profileLevelVariables
     */
    public HashMap getSolutionAttributes() {
        return profileLevelVariables;
    }

    /**
     * Prints the profile.
     */
    public void printProfile() {
        Set keys = profileLevelVariables.keySet();
        Iterator AttributesIterator = keys.iterator();
        while (AttributesIterator.hasNext()) {
            IpatVariable var = (IpatVariable) AttributesIterator.next();
            logger.info(var.getName() + " : " + var.getValue());
        }
        Set keySet = kernels.keySet();
        Iterator kernelIterator = keySet.iterator();
        while (kernelIterator.hasNext()) {
            String name = (String) kernelIterator.next();
            logger.debug(name);
            Kernel kernel = (Kernel) kernels.get(name);
            HashMap kVars = kernel.getVariables();
            Set kVarsKeys = kVars.keySet();
            Iterator kVarsKeysIterator = kVarsKeys.iterator();
            while (kVarsKeysIterator.hasNext()) {
                IpatVariable var = (IpatVariable) kVars.get(kVarsKeysIterator.next());
                logger.debug("   " + var.getName() + " : "
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

                    IpatVariable variable = new IpatVariable(name, type,
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

                        IpatVariable variable = new IpatVariable(name, type,
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
    public boolean writeToFile() {

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
                    IpatVariable var = (IpatVariable) mySolutionAttributes.get(elem.getValue());
                    elem.setText(var.getName());

                    elem = hint.getChild("type");
                    //  System.out.println("var.getType() = " + var.getType());
                    elem.setText(var.getType());

                    elem = hint.getChild("lbound");
                    Double dub = var.getLbound(); // changed from new Double
                    //  System.out.println("var.getLbound() = " + var.getLbound());
                    elem.setText(dub.toString());

                    elem = hint.getChild("ubound");
                    dub = var.getUbound();
                    // System.out.println("var.getUbound() = " + var.getUbound());
                    elem.setText(dub.toString());

                    elem = hint.getChild("granularity");
                    dub = var.getGranularity();
                    // System.out.println("var.getGranularity() = " + var.getGranularity());
                    elem.setText(dub.toString());

                    elem = hint.getChild("rateOfEvolution");
                    dub = var.getRateOfEvolution();
                    //  System.out.println("var.getRateOfEvolution() = " + var.getRateOfEvolution());
                    elem.setText(dub.toString());

                    elem = hint.getChild("value");
                    dub = var.getValue();
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
                        IpatVariable varb = (IpatVariable) enu3.next();

                        Element elem = hintt.getChild("name");
                        elem.setText(varb.getName());

                        elem = hintt.getChild("type");
                        elem.setText(varb.getType());

                        elem = hintt.getChild("lbound");
                        Double dub = varb.getLbound();
                        elem.setText(dub.toString());

                        elem = hintt.getChild("ubound");
                        dub = varb.getUbound();
                        elem.setText(dub.toString());

                        elem = hintt.getChild("granularity");
                        dub = varb.getGranularity();
                        elem.setText(dub.toString());

                        elem = hintt.getChild("rateOfEvolution");
                        dub = varb.getRateOfEvolution();
                        elem.setText(dub.toString());

                        elem = hintt.getChild("value");
                        dub = varb.getValue();
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

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.getFile().getAbsolutePath()))) {
                writer.write(xmlString);
            }

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
                Double dubi = newValue;
                value.setText(dubi.toString());
            } else {
                logger.error("couldn;t find child with name " + varname);
            }

            XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
            String xmlString = outputter.outputString(XmlDoc);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.file.getAbsolutePath()))) {
                writer.write(xmlString);
            }
        } catch (JDOMException | IOException pce) {
            logger.error(Arrays.toString(pce.getStackTrace()) + " in Profile");
        }
    }

    // used for if the Profile is to have a hard copy of itself in a specified location on disk
    public boolean copyToNewFile(String outputPath) {

        /* apply changes to solution attribute values in memory to their hard copy file before 
         copying that file to the location specified as "outputPath"
         */
        this.writeToFile();

        String copy;
        File fileCopy = this.getFile();
        BufferedWriter writer;
        XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
        try {
            Document XmlDoc = new SAXBuilder().build(fileCopy);
            copy = outputter.outputString(XmlDoc);
            writer = new BufferedWriter(new FileWriter(outputPath));
            writer.write(copy);
            writer.close();
        } catch (JDOMException | IOException pce) {
           logger.error(Arrays.toString(pce.getStackTrace()) + " in Profile");
        }
        return true; // copy;
    }
}
