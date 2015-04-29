/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.StringReader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 *
 * @author kieran
 */
public class Profile {
    
	/** The file. */
	private File file;
	
	/** The global score. */
	private int globalScore = 5;
	
	/** The kernels. */
	private Hashtable kernels;
	
	/** The name. */
	private String name;
	
	/** The no of kernels. */
	private int noOfKernels;
	
	/** The no of kernel solutionAttributes. */
	private int noOfKernerlVariables;
	
	/** The no of profile solutionAttributes. */
	private int noOfProfileVariables;
	
	/** The solutionAttributes. */
	private Hashtable solutionAttributes;

	/**
	 * Instantiates a new ipat profile.
	 *
	 * @param file the file
	 */
	public Profile(File file) {
		solutionAttributes = new Hashtable();
		kernels = new Hashtable();
		this.file = file;
		this.name = file.getName();
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
	 * Adds the varaiable.
	 *
	 * @param var the var
	 */
	public void addVariable(SolutionAttributes var) {
		solutionAttributes.put(var.getName(), var);
	}

	/**
	 * Gets the file.
	 *
	 * @return the file
	 */
	public File getFile() {
		return file;
	}
        
        public void setFile(File thisfile)
        {
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
	public Hashtable getKernels() {
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
	public Hashtable getSolutionAttributes() {
		return solutionAttributes;
	}

	/**
	 * Prints the profile.
	 */
	public void printProfile() {
		Enumeration enu = solutionAttributes.elements();
		while (enu.hasMoreElements()) {
			SolutionAttributes var = (SolutionAttributes) enu.nextElement();
			System.out.println(var.getName() + " : " + var.getValue());
		}
		enu = kernels.keys();
		while (enu.hasMoreElements()) {
			String name = (String) enu.nextElement();
			System.out.println(name);
			Kernel kernel = (Kernel) kernels.get(name);
			Hashtable kVars = kernel.getVariables();
			Enumeration enu2 = kVars.keys();
			while (enu2.hasMoreElements()) {
				SolutionAttributes var = (SolutionAttributes) kVars.get(enu2.nextElement());
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
	 * Copy profile.
	 *
	 * @param parent the parent
	 * @param outputPath the output path
	 * @return true, if successful
	 */
	public boolean writeProfileToFile(Profile parent, String outputPath) {
		System.out.println("Copy profile " + parent.getName() + " to " + outputPath);
		String copy = "";
		File file = parent.getFile();
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
		return true;// copy;
	}

	/**
	 * Gets the component.
	 *
	 * @param type the type
	 * @param value the value
	 * @return the component
	 */
	

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
				}

				else if (hint.getName().equalsIgnoreCase("kernel")) {

					Iterator it = hint.getChildren().iterator();
					Element nm = (Element) it.next();
					String kernelName = nm.getText();
					Hashtable vars = new Hashtable();
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
	public boolean setProfile(Profile profile) {
		try {
			Document XmlDoc = new SAXBuilder().build(profile.getFile());

			Element root = XmlDoc.getRootElement();
			Element profileNode = root.getChild("profile", root.getNamespace());
			Iterator iterator = profileNode.getChildren().iterator();

			Enumeration enu = profile.getSolutionAttributes().elements();
			Hashtable kernels = profile.getKernels();
			Enumeration enuK = profile.getKernels().keys();
			
			while (iterator.hasNext()) {

				Element hint = (Element) iterator.next();
				if (hint.getName().equalsIgnoreCase("variable")) {
					SolutionAttributes var = (SolutionAttributes) enu.nextElement();
					Element elem = hint.getChild("name");
					elem.setText(var.getName());

					elem = hint.getChild("type");
					elem.setText(var.getType());

					elem = hint.getChild("lbound");
					Double dub = new Double(var.getLbound());
					elem.setText(dub.toString());

					elem = hint.getChild("ubound");
					dub = new Double(var.getUbound());
					elem.setText(dub.toString());

					elem = hint.getChild("granularity");
					dub = new Double(var.getGranularity());
					elem.setText(dub.toString());
					
					elem = hint.getChild("rateOfEvolution");
					dub = new Double(var.getRateOfEvolution());
					elem.setText(dub.toString());

					elem = hint.getChild("value");
					dub = new Double(var.getValue());
					elem.setText(dub.toString());

					elem = hint.getChild("default");
					elem.setText(var.getDfault());

					elem = hint.getChild("flag");
					elem.setText(var.getFlag());
					
					elem = hint.getChild("unit");
					elem.setText(var.getUnit());
				}

				else if (hint.getName().equalsIgnoreCase("kernel")) {

					Iterator it = hint.getChildren().iterator();
					Element nm = (Element) it.next();
					
					String kernelName = nm.getText();
					
					Kernel kern = (Kernel) kernels.get(kernelName);
					Hashtable vars = kern.getVariables();
					Enumeration enu3 = vars.elements();
					
					while (it.hasNext()) {
						Element hintt = (Element) it.next();
						SolutionAttributes varb = (SolutionAttributes) enu3.nextElement();
						
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

			BufferedWriter writer = new BufferedWriter(new FileWriter(profile
					.getFile().getAbsolutePath()));
			writer.write(xmlString);
			writer.close();
		} catch (Exception pce) {
			pce.printStackTrace();
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
	public void setProfileVariableValue( String varname, double newValue) {
	Hashtable elements = new Hashtable();
	try {
		Document XmlDoc = new SAXBuilder().build(this.file);
	
		Element root = XmlDoc.getRootElement();
		Element graph = root.getChild(varname, root.getNamespace());
                if(graph != null)
                {
                    Element value = graph.getChild("value");
                    Double dubi = new Double(newValue);
		value.setText(dubi.toString());
                }
                else
                    System.out.println("couldn;t find child with name "+ varname);
		
	
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		String xmlString = outputter.outputString(XmlDoc);
	
		BufferedWriter writer = new BufferedWriter(new FileWriter(this.file.getAbsolutePath()));
		writer.write(xmlString);
		writer.close();
	} catch (Exception pce) {
		pce.printStackTrace();
	}
}

    public boolean writeProfileToFile(String outputPath) {
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