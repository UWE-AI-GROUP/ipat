/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Src;

import java.io.File;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 *
 * @author kieran
 */
public class Utils {

    /**
     * The RAN d_ max.
     */
    private static int RAND_MAX = 32767;

    /**
     * Gets the gauss n01 double.
     *
     * @return the double
     */
    public static double GetGaussN01Double() {
        Random r = new Random();
        return r.nextGaussian();
    }

    /**
     * Gets the rand double01.
     *
     * @return the double
     */
    public static double GetRandDouble01() {
        Random r = new Random();
        //return ( (double)Math.random() - 1.0 ) / ( (double)r.nextDouble() );
        return r.nextDouble();
    }

    /**
     * Gets the rand int in range.
     *
     * @param b1 First boundary
     * @param b2 Second boundary
     * @return the int
     */
    public static int GetRandIntInRange(int b1, int b2) {
        return ((int) ((b2 - b1 + 1) * GetRandDouble01())) + b1;
    }

    /**
     * Returns if both {@link Collection Collections} contains the same elements, in the same quantities, regardless of order and collection type.
     * <p>
     * Empty collections and {@code null} are regarded as equal.
     * @param <T> type of first collection
     * @param col1 first collection
     * @param col2 second collection to compare to first
     * @return true or false
     */
    public static <T> boolean haveSameElements(Collection<T> col1, Collection<T> col2) {
        if (col1 == col2) {
            return true;
        }
        if (col1 == null) {
            return col2.isEmpty();
        }
        if (col2 == null) {
            return col1.isEmpty();
        }
        if (col1.size() != col2.size()) {
            return false;
        }

        // Helper class, so we don't have to do a whole lot of autoboxing
        class Count {

            // Initialize as 1, as we would increment it anyway
            public int count = 1;
        }
        final Map<T, Count> counts = new HashMap<>();
        for (final T item : col1) {
            final Count count = counts.get(item);
            if (count != null) {
                count.count++;
            } else {
                counts.put(item, new Count());
            }
        }
        for (final T item : col2) {
            final Count count = counts.get(item);
            if (count == null || count.count == 0) {
                return false;
            }
            count.count--;
        }
        for (final Count count : counts.values()) {
            if (count.count != 0) {
                return false;
            }
        }
        return true;
    }

    /**
     * The DB l_ max.
     */
    private double DBL_MAX = 10.0;

    /**
     * The IN t_ max.
     */
    private int INT_MAX = 10;

    /**
     * The IN t_ min.
     */
    private int INT_MIN = 0;

    /**
     * Abs.
     *
     * @param x the x
     * @return the double
     */
    double Abs(double x) {
        if (x < 0.0) {
            return -x;
        } else {
            return x;
        }
    }

    /**
     * Abs.
     *
     * @param n the n
     * @return the int
     */
    int Abs(int n) {
        if (n < 0) {
            return -n;
        } else {
            return n;
        }
    }

    /**
     * Gets the closest even number.
     *
     * @param x the x
     * @return the int
     */
    int GetClosestEvenNumber(double x) {
        return 2 * Round(x / 2.0);
    }

    /**
     * Gets the closest multiple of4.
     *
     * @param x the x
     * @return the int
     */
    int GetClosestMultipleOf4(double x) {
        return 4 * Round(x / 4.0);
    }

    /**
     * Gets the closest multiple of4.
     *
     * @param n the n
     * @return the int
     */
    int GetClosestMultipleOf4(int n) {
        return 4 * Round(n / 4.0);
    }

    /**
     * Gets the closest odd number.
     *
     * @param x the x
     * @return the int
     */
    int GetClosestOddNumber(double x) {
        return 2 * Round((x + 1.0) / 2.0) - 1;
    }

    /**
     * Gets the even rand int in range.
     *
     * @param b1 First boundary
     * @param b2 Second boundary
     * @return the int
     */
    int GetEvenRandIntInRange(int b1, int b2) {
        int bMin, bMax;
        bMin = Min(b1, b2);
        bMax = Max(b1, b2);

        if ((bMin == bMax) && (!IsEven(bMin))) {
            //ErrorLog err; err.WriteErrorLog("[GetEvenRandIntInRange] Error: two boundaries are equal and odd; cannot find even number between them");
            //exit(EXIT_FAILURE);
        }

        if (!IsEven(bMin)) {
            bMin++;
        }
        if (!IsEven(bMax)) {
            bMax--;
        }

        int n = GetRandIntInRange(bMin / 2, bMax / 2);

        return 2 * n;
    }

    /////////////////////////////////////////////////////////////////////////////
    // MIN and MAX, keep inside interval
    /**
     * Gets the odd rand int in range.
     *
     * @param b1 First boundary
     * @param b2 Second boundary
     * @return the int
     */
    int GetOddRandIntInRange(int b1, int b2) {
        int bMin, bMax;
        bMin = Min(b1, b2);
        bMax = Max(b1, b2);

        if ((bMin == bMax) && IsEven(bMin)) {
            //ErrorLog err; err.WriteErrorLog("[GetOddRandIntInRange] Error: two boundaries are equal and even; cannot find odd number between them");
            //exit(EXIT_FAILURE);
        }

        if (IsEven(bMin)) {
            bMin++;
        }
        if (IsEven(bMax)) {
            bMax--;
        }

        int n = GetRandIntInRange((bMin - 1) / 2, (bMax - 1) / 2);

        return 2 * n + 1;
    }

    /**
     * Gets the one of these.
     *
     * @param a the a
     * @param b the b
     * @return the double
     */
    double GetOneOfThese(double a, double b) {
        if (GetRandBool()) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Gets the one of these.
     *
     * @param a the a
     * @param b the b
     * @return the int
     */
    int GetOneOfThese(int a, int b) {
        if (GetRandBool()) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Gets the rand bool.
     *
     * @return true, if successful
     */
    public boolean GetRandBool() {
        if (GetRandDouble01() < 0.5) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the rnd nb from gaussian distr01.
     *
     * @return the double
     */
    double GetRndNbFromGaussianDistr01() {
        double x1, x2, w;
        do {
            x1 = 2.0 * GetRandDouble01() - 1.0;
            x2 = 2.0 * GetRandDouble01() - 1.0;
            w = x1 * x1 + x2 * x2;
        } while (w >= 1.0);

        w = Math.sqrt((-2.0 * Math.log(w)) / w);
        return x1 * w; // or x2 * w
    }

    // ? correct ?
    /**
     * Gets the rnd nb from gaussian distr mean sigma.
     *
     * @param mean the mean
     * @param sigma the sigma
     * @return the double
     */
    double GetRndNbFromGaussianDistrMeanSigma(double mean, double sigma) {
        return GetRndNbFromGaussianDistr01() * sigma + mean;
    }

    /**
     * Int to string filled with0.
     *
     * @param n Integer to include in the string
     * @param sizeToReach Minimal number of characters the resulting string will
     * contain
     * @return the string
     */
    String IntToStringFilledWith0(int n, int sizeToReach) {
		//ostringstream stringStream;
        //stringStream << setw(sizeToReach) << setfill('0') << n;

        //return stringStream.str();
        return "";
    }

    /**
     * Checks if is even.
     *
     * @param n Integer for which the even test will be conducted
     * @return true, if successful
     */
    public boolean IsEven(int n) {
        //		return (2 * ((int)(n / 2)) == n);
        return (2 * ((int) Math.floor(n / 2.0 + 0.5)) == n);
    }

    /**
     * Checks if is multiple of4.
     *
     * @param n Integer for which the test will be conducted
     * @return true, if successful
     */
    boolean IsMultipleOf4(int n) {
        return (4 * Round(n / 4.0)) == n;
    }

    /**
     * Keep argt1 between argt2 and argt3.
     *
     * @param var "Double" to test and eventually modify
     * @param bound1 First boundary of the interval
     * @param bound2 Second boundary of the interval
     */
    void KeepArgt1BetweenArgt2AndArgt3(double var, double bound1, double bound2) {
        double boundMin = Min(bound1, bound2);
        double boundMax = Max(bound1, bound2);

        if (var < boundMin) {
            var = boundMin;
        } else if (var > boundMax) {
            var = boundMax;
        }

        return;
    }

    /**
     * Keep argt1 between argt2 and argt3.
     *
     * @param var Integer to test and eventually modify
     * @param bound1 First boundary of the interval
     * @param bound2 Second boundary of the interval
     */
    void KeepArgt1BetweenArgt2AndArgt3(int var, int bound1, int bound2) {
        int boundMin = Min(bound1, bound2);
        int boundMax = Max(bound1, bound2);

        if (var < boundMin) {
            var = boundMin;
        } else if (var > boundMax) {
            var = boundMax;
        }

        return;
    }

    /**
     * Keep inside01.
     *
     * @param x the x
     * @return the double
     */
    double KeepInside01(double x) {
        if (x < 0.0) {
            return 0.0;
        } else if (x > 1.0) {
            return 1.0;
        } else {
            return x;
        }
    }

    /**
     * Max.
     *
     * @param a the a
     * @param b the b
     * @return the double
     */
    double Max(double a, double b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    /////////////////////////////////////////////////////////////////////////////
    // STRINGS
    /**
     * Max.
     *
     * @param a the a
     * @param b the b
     * @return the int
     */
    int Max(int a, int b) {
        if (a > b) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Max array1 d.
     *
     * @param array1D Pointer to int data (one dimension array)
     * @param arraySize Size of the data
     * @return the int
     */
    int MaxArray1D(int[] array1D, int arraySize) {
        int max = INT_MIN;
        for (int i = 0; i < arraySize; i++) {
            if (array1D[i] > max) {
                max = array1D[i];
            }
        }
        return max;
    }

    /**
     * Max array2 d.
     *
     * @param array2D Pointer to pointer to double data (two dimension array)
     * @param width Width of array
     * @param height Height of array
     * @return the double
     */
    double MaxArray2D(double[][] array2D, int width, int height) {
        double max = -DBL_MAX;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array2D[i][j] > max) {
                    max = array2D[i][j];
                }
            }
        }
        return max;
    }

    /**
     * Max array2 d.
     *
     * @param array2D Pointer to pointer to int data (two dimension array)
     * @param width Width of array
     * @param height Height of array
     * @return the int
     */
    int MaxArray2D(int[][] array2D, int width, int height) {
        int max = INT_MIN;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array2D[i][j] > max) {
                    max = array2D[i][j];
                }
            }
        }
        return max;
    }

    /**
     * Min.
     *
     * @param a the a
     * @param b the b
     * @return the double
     */
    double Min(double a, double b) {
        if (a < b) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Min.
     *
     * @param a the a
     * @param b the b
     * @return the int
     */
    public int Min(int a, int b) {
        if (a < b) {
            return a;
        } else {
            return b;
        }
    }

    /**
     * Min array1 d.
     *
     * @param array1D Pointer to int data (one dimension array)
     * @param arraySize Size of the data
     * @return the int
     */
    int MinArray1D(int[] array1D, int arraySize) {
        int min = INT_MAX;
        for (int i = 0; i < arraySize; i++) {
            if (array1D[i] < min) {
                min = array1D[i];
            }
        }
        return min;
    }

    /**
     * Min array2 d.
     *
     * @param array2D Pointer to pointer to double data (two dimension array)
     * @param width Width of array
     * @param height Height of array
     * @return the double
     */
    double MinArray2D(double[][] array2D, int width, int height) {
        double min = DBL_MAX;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array2D[i][j] < min) {
                    min = array2D[i][j];
                }
            }
        }
        return min;
    }

    /**
     * Min array2 d.
     *
     * @param array2D Pointer to pointer to int data (two dimension array)
     * @param width Width of array
     * @param height Height of array
     * @return the int
     */
    int MinArray2D(int[][] array2D, int width, int height) {
        int min = INT_MAX;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (array2D[i][j] < min) {
                    min = array2D[i][j];
                }
            }
        }
        return min;
    }

    /**
     * Real to string filled with0.
     *
     * @param x Real number to include in the string
     * @param sizeToReach Minimal number of characters the resulting string will
     * contain
     * @return the string
     */
    String RealToStringFilledWith0(double x, int sizeToReach) {
        /*ostringstream stringStream;
         stringStream << setw(sizeToReach) << setfill('0') << x;
         return stringStream.str();
         */
        return "";
    }

    /**
     * Round.
     *
     * @param x the x
     * @return the int
     */
    int Round(double x) {
        return (int) Math.floor(x + 0.5);
    }

    /**
     * Sq.
     *
     * @param a the a
     * @return the double
     */
    double Sq(double a) {
        return a * a;
    }

    /**
     * Sq.
     *
     * @param a the a
     * @return the int
     */
    /*
     std::string GetFileNameFromPath( std::string pathName)
     {
     int posiNameStart1 = (int) pathName.find_last_of("\\");
     int posiNameStart2 = (int) pathName.find_last_of("//");
     int posiNameStart = max(posiNameStart1, posiNameStart2);
     if(posiNameStart == std::string::npos)
     {
     //ErrorLog err; err.WriteErrorLog("[GetFileNameFromPath] Character '\' not found in path name");
     //exit(EXIT_FAILURE);
     return pathName;
     }
     else
     {
     return pathName.substr(posiNameStart + 1);
     }
     }
     /**
     * @param pathName		Name of a path (or any string containing a "\")
     */
    /*
     std::string GetRidOfWordAfterLastBackslash( std::string pathName)
     {
     int posiNameStart = (int) pathName.find_last_of("\\");
     if(posiNameStart == std::string::npos)
     {
     //ErrorLog err; err.WriteErrorLog("[GetRidOfWordAfterLastBackslash] Character '\' not found in path name");
     //exit(EXIT_FAILURE);
     return pathName;
     }
     else
     {
     return pathName.substr(0, posiNameStart);
     }
     }
     /**
     * @param pathName		Name of a path (or any string containing a "\")
     * @param n				Number of times last word will be erased
     */
    /*
     std::string GetRidOfWordAfterLastBackslash_nTimes(std::string pathName, unsigned int n)
     {
     for(unsigned int i = 0; i < n; i++)
     {
     if(pathName.empty())
     {
     return "";
     }
     else
     {
     pathName = GetRidOfWordAfterLastBackslash(pathName);
     }
     }
     return pathName;
     }
     /**
     * @param fileName		Name of a file including extension (or any string containing a ".")
     */
    /*
     std::string GetRidOfExtension( std::string fileName)
     {
     int posiNameEnd = (int) fileName.find_last_of(".");
     if(posiNameEnd == std::string::npos)
     {
     return fileName;
     }
     else
     {
     return fileName.substr(0, posiNameEnd);
     }
     }
     */
	/////////////////////////////////////////////////////////////////////////////
    // VECTORS
	/*
     int Sum(Vector<Integer> v)
     {
     int total = 0;
     for(int i = 0; i < v.size(); i++)
     total += ((Integer)v.get(i)).intValue();
     return total;
     }
     double Sum(Vector<Double> v)
     {
     double total = 0.0;
     for(int i = 0; i < v.size(); i++)
     total += ((Double)v.get(i)).doubleValue();
     return total;
     }
     double Ave( std::vector<int> &v)
     {
     if(v.size() != 0)
     return ((double)(Sum(v))) / (double)(v.size());
     else
     return 0.0;
     }
     double Ave( std::vector<double> &v)
     {
     if(v.size() != 0)
     return Sum(v) / (double)(v.size());
     else
     return 0.0;
     }
     double StdDev( std::vector<double> &v)
     {
     if(v.size() != 0)
     {
     double mean = Ave(v);
     double temp = 0.0;
     for(unsigned int i = 0; i < v.size(); i++)
     {
     temp += Sq(v[i] - mean);
     }
     return sqrt( temp / (double)(v.size()) );
     }
     else
     return 0.0;
     }
     */
    /////////////////////////////////////////////////////////////////////////////
    // MATH
    int Sq(int a) {
        return a * a;
    }

	/////////////////////////////////////////////////////////////////////////////
    // TIME
	/*
     string GetTimeStampYYYYMMDDHHMMSS()
     {
     ostringstream stringStream;
     SYSTEMTIME st;
     GetSystemTime(&st);
     stringStream << st.wYear;
     stringStream << setw(2) << setfill('0') << st.wMonth;
     stringStream << setw(2) << setfill('0') << st.wDay;
     stringStream << setw(2) << setfill('0') << st.wHour;
     stringStream << setw(2) << setfill('0') << st.wMinute;
     stringStream << setw(2) << setfill('0') << st.wSecond;
     return stringStream.str();
     }
     /**
     * @param pathName_source	Path amd name of the file to copy
     * @param pathName_dest		Path (including file name) where to create the copy
     * @return					Whether the copy succeded or failed
     */
    /*
     bool CopyTextFile( std::string pathName_source,  std::string pathName_dest)
     {
     ifstream srceFileStream;
     srceFileStream.open(pathName_source.c_str());
     if (! srceFileStream.is_open())
     {
     ErrorLog err; err.WriteErrorLog("[CopyTextFile] ERROR: unable to open source file");
     return false;
     }
     ofstream destFileStream;
     destFileStream.open(pathName_dest.c_str());
     if (! destFileStream.is_open())
     {
     ErrorLog err; err.WriteErrorLog("[CopyTextFile] ERROR: unable to open destination file");
     return false;
     }
     return true;
     }*/
    //        // finally update profile names by incrementing the generation count in each name and write them to file for safe keeping
    //         for (int i = 0; i < nextGen.size(); i++)
    //          {
    //
    //            try {
    //                String profileName = nextGen.get(i).getName(); // should be of form "gen_x-profile_y.xml"
    //                System.out.println("ProfileName = " + profileName);
    //                //get base name for profile
    //                String profile = profileName.substring(profileName.indexOf('-') , profileName.lastIndexOf('_')+1); // should be of form "profile_"
    //                System.out.println("Profile = " + profile);
    //                //get generation and increment it
    //                int generation = Integer.parseInt(profileName.substring((profileName.indexOf('_') + 1), profileName.indexOf('-')));
    //                generation++;
    //
    //                //build string holding new name - note that we hav not kept traack of the profile indices
    //                String outProfileName = "gen_" + generation + profile + i + ".xml";
    //                System.out.println("outprofilename = " + outProfileName);
    //
    //                // set name in profile to match new name
    //                nextGen.get(i).setName(outProfileName);
    //
    //                // write out the profile to file for safe keeping
    //                //build the path by fetching the session details from the controller and adding generaios + this file name
    //                String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
    //
    //                //write to file
    //                nextGen.get(i).writeProfileToFile(outProfilePath);
    //                File thisfile = new File(outProfilePath);
    //                nextGen.get(i).setFile(thisfile);
    //
    //            } catch (StringIndexOutOfBoundsException ex) {
    //                System.out.println("The profile names do not follow the correct convention to be processed."
    //                        + "/nLook within the Profiles Folder, and ensure the names appear as: gen_0-Profile_x.xml");
    //                System.out.println(ex.getMessage());
    //            }
    //        }
    //System.out.println("changed names and saved files");
    //finally write all ofthe profiles to file for safe keeping
    //for(int toSave=0; toSave < howMany;toSave++)
    //{
    //  String outProfileName= nextGen.get(toSave).getName();
    // String outProfilePath = Controller.outputFolder.getAbsolutePath() + "/generations/" + outProfileName;
    //System.out.println("saving next gen profile to file: " + outProfileName);
    // nextGen.get(toSave).writeProfileToFile(outProfilePath);
    //}
    //}
    // TODO read in the global scores from the profile.xml files
    /**
     *
     * @param file
     * @return
     */
    public static Profile getProfileFromFile(File file) {
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
                    IpatVariable variable = new IpatVariable(name, type, lbound, ubound, granularity, rateOfEvolution, value, dfault, flag, unit);
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
                        IpatVariable variable = new IpatVariable(name, type, lbound, ubound, granularity, rateOfEvolution, value, dfault, flag, unit);
                        vars.put(name, variable);
                    }
                    Kernel kernel = new Kernel(kernelName, vars);
                    profile.addKernel(kernel);
                } else if (hint.getName().equalsIgnoreCase("interaction")) {
                }
            }
        } catch (Exception pce) {
            pce.printStackTrace();
        }
        return profile;
    }
}
