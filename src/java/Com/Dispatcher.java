/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Algorithms.Processor;
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
import com.google.gson.Gson;
import org.apache.commons.fileupload.FileItemIterator;
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
    String fileRepository;
    String contextPath;

    @Override
    public void init() throws ServletException {
       
        this.contextPath = getServletContext().getRealPath("/");
        logger.info("session context path = " + contextPath);
        this.fileRepository = contextPath + "/tempFileRepository/";
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

        File file;

        Boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            System.out.println("Error, system did not think there wa a multipart request.");
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Error, Please load welcome page and select the type of use case desired.");
        } 
        else {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            factory.setSizeThreshold(maxMemSize);
            // Location to save data that is larger than maxMemSize.
            File myRepository =new File(fileRepository);
            if (myRepository.mkdirs())
                       System.out.println("Created repository directory " + fileRepository);
            factory.setRepository(myRepository);
            String repname = factory.getRepository().getName();
            String reppath = factory.getRepository().getAbsolutePath();
                    System.out.println("factory repository is " + reppath + repname);

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
                        System.out.println("filename read is " + fileName);
                        //  String fieldName = fi.getFieldName();
                        //  String contentType = fi.getContentType();
                        //  boolean isInMemory = fi.isInMemory();
                        //  long sizeInBytes = fi.getSize();

                        // get specified paths in the web.xml, if they are left as null then set defaults  
                        String outputPath = getServletContext().getInitParameter("clientFolder");
                        // initialise the output path for the artifacts 
                        if (outputPath.equalsIgnoreCase("")) {
                            session.setAttribute("clientDataPath", contextPath + "/Client Data/");
                        } else {
                            session.setAttribute("clientDataPath", getServletContext().getInitParameter("clientFolder"));
                        }
                        // initialise the Paths for the hints and profiles to be read from based on use case
                        session.setAttribute("profilePath", contextPath + "/data/" + (String) session.getAttribute("usecase") + "/Profiles/");
                        session.setAttribute("hintsFile", contextPath + "/data/" + (String) session.getAttribute("usecase") + "/hints.xml");

                        File input = new File((String) session.getAttribute("clientDataPath") + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File((String) session.getAttribute("clientDataPath") + session.getId() + "/output/");
                        output.mkdirs();
                        session.setAttribute("inputFolder", (String) session.getAttribute("clientDataPath") + session.getId() + "/input/");
                        session.setAttribute("outputFolder", (String) session.getAttribute("clientDataPath") + session.getId() + "/output/");

                        if (fileName.lastIndexOf("\\") >= 0) {
                            file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")));
                        } else {
                            file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1));
                        }
                        fi.write(file);
                    }
                }
            } catch (FileUploadException ex) {
                logger.info(ex, ex);
            } catch (Exception ex) {
                logger.info(ex, ex);
            }

            logger.info("File(s) uploaded by user : "
                    + "\ninput path : " + (String) session.getAttribute("inputFolder")
                    + "\noutput path : " + (String) session.getAttribute("outputFolder")
                    + "\nprofile path : " + (String) session.getAttribute("profilePath")
                    + "\nhintsXML path : " + (String) session.getAttribute("hintsFile"));

            // TODO make the fileRepository Folder generic so it doesnt need to be changed
            // for each migration of the program to a different server
            File input = new File((String) session.getAttribute("inputFolder"));
            File output = new File((String) session.getAttribute("outputFolder"));
            File profile = new File((String) session.getAttribute("profilePath"));
            File hintsXML = new File((String) session.getAttribute("hintsFile"));

            Processor processor = (Processor) session.getAttribute("processor");
            Controller controller = new Controller(input, output, profile, hintsXML, processor);
            HashMap initialArtifacts = controller.initialArtifacts();
            session.setAttribute("Controller", controller);

            logger.info("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
                    + "Awaiting user to update parameters to generate next generation of results.\n");

            String json = new Gson().toJson(initialArtifacts);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(json);
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
