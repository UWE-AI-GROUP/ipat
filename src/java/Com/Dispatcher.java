/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Algorithms.CSSProcessor;
import Algorithms.Processor;
import Algorithms.UMLProcessor;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import Src.Controller;
import Src.Display;
import com.google.gson.Gson;
import javax.servlet.RequestDispatcher;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.log4j.Logger;


/**
 *
 * @author kieran
 */
public class Dispatcher extends HttpServlet {

    private static final Logger logger = Logger.getLogger(Dispatcher.class);

    int maxFileSize = 819600;
    int maxMemSize = 4096;
    File myRepository;
    String contextPath;

    @Override
    public void init(){
        this.contextPath = getServletContext().getRealPath("/");
        logger.info("session context path = " + contextPath);
        this.myRepository = new File(contextPath + "/tempFileRepository/");
        File logFile = new File(contextPath + "/log/log4j-IPAT.log");
        System.setProperty("rootPath", logFile.getAbsolutePath());
       
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(true);
        if (!session.isNew()) {
            session.invalidate();
            session = req.getSession(true);
        }
        
        
        
        Processor processor;
        String problemDataFolderName;
        // ### Add additional problem cases below ###
        switch (req.getServletPath()) {
            case "/UML":
                processor = new UMLProcessor();
                problemDataFolderName = "UML Evolution";
                break;
            case "/CSS":
                processor = new CSSProcessor();
                problemDataFolderName = "CSS Evolution";
                break;
            default:
                logger.info("Trouble with the instantiation of the Processor in Dispatchers doGet(). No case"
                        + " for this URL extension. Ensure case Strings in Dispatcher.doGet() exactly match those in web.xml");
                throw new AssertionError();
        }

        session.setAttribute("processor", processor);
        session.setAttribute("problemDataFolderName", problemDataFolderName);

        RequestDispatcher RD = req.getRequestDispatcher("main.jsp");
        RD.forward(req, resp);

    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        if (session == null) {

        }
        Processor processor = (Processor) session.getAttribute("processor");
        String problemDataFolderName = (String) session.getAttribute("problemDataFolderName");
        File inputFolder = null;
        File outputFolder = null;
        File profilePath = null;
        File hintsXML = null;
        String dataPath;

        File file;

        Boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            logger.info("Error, system did not think there was a multipart request.\n");
            return;
        }

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
        if (myRepository.mkdirs()) {
            logger.info("Created repository directory " + myRepository.getAbsolutePath()+"\n");
        }
        factory.setRepository(myRepository);
        logger.info("factory repository is " + factory.getRepository().getAbsolutePath()+"\n");

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);

        // Parse the request to get file items
        List<FileItem> fileItems;
        try {
            fileItems = upload.parseRequest(request);
            // Process the uploaded file items
            Iterator i = fileItems.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (!fi.isFormField()) {

                    // Get the uploaded file parameters
                    String fileName = fi.getName();
                    logger.info("filename read is " + fileName +"\n");
                    //  String fieldName = fi.getFieldName();
                    //  String contentType = fi.getContentType();
                    //  boolean isInMemory = fi.isInMemory();
                    //  long sizeInBytes = fi.getSize();

                    // get specified paths in the web.xml, if they are left as null then set defaults  
                    String path = getServletContext().getInitParameter("clientFolder");
                    // initialise the output path for the artifacts 
                    if (path.equalsIgnoreCase("")) {
                        dataPath = contextPath + "/Client Data/";
                    } else {
                        dataPath = getServletContext().getInitParameter("clientFolder");
                    }
                    // initialise the Paths for the hints and profiles to be read from based on use case
                    profilePath = new File(contextPath + "/data/" + problemDataFolderName + "/Profiles/");
                    hintsXML = new File(contextPath + "/data/" + problemDataFolderName + "/hints.xml");

                    File input = new File(dataPath + session.getId() + "/input/");
                    input.mkdirs();
                    File output = new File(dataPath + session.getId() + "/output/");
                    output.mkdirs();
                    inputFolder = new File(dataPath + session.getId() + "/input/");
                    outputFolder = new File(dataPath + session.getId() + "/output/");

                    if (fileName.lastIndexOf("\\") >= 0) {
                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")));
                    } else {
                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1));
                    }
                    fi.write(file);
                }
            }
        } catch (FileUploadException ex) {
            logger.info(ex +"\n", ex );
        } catch (Exception ex) {
            logger.info(ex +"\n", ex);
        }

        logger.info("File(s) uploaded by user : " +"\n"
                + "\ninput path : " + inputFolder.getAbsolutePath()+"\n"
                + "\noutput path : " + outputFolder.getAbsolutePath()+"\n"
                + "\nprofile path : " + profilePath.getAbsolutePath()+"\n"
                + "\nhintsXML path : " + hintsXML.getAbsolutePath()+"\n");

        if (inputFolder != null && outputFolder != null && profilePath != null && hintsXML != null) {

            Display webDisplay = new WebDisplay();
            Controller controller = new Controller(inputFolder, outputFolder, profilePath, hintsXML, processor, webDisplay);
            HashMap HTML_Strings = controller.initialisation();
            session.setAttribute("Controller", controller);

            logger.info("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
                    + "Awaiting user to update parameters to generate next generation of results.\n");

            String json = new Gson().toJson(HTML_Strings);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
        } else {
            logger.info("There was a fatal error in Dispatcher. Filepaths are not correctly instantiated."+"\n");
        }

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
