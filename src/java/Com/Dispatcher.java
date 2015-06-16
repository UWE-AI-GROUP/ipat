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
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.fileupload.FileUploadException;

/**
 *
 * @author kieran
 */
public class Dispatcher extends HttpServlet {

    int maxFileSize = 51200;
    int maxMemSize = 4096;
    String fileRepository;
    String contextPath;

    @Override
    public void init() throws ServletException {
        this.contextPath = getServletContext().getRealPath("/");
        this.fileRepository = contextPath + "/temp file repository/";
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
        String clientDataPath;
        
        String hintsFile = null;
        String profilePath = null;
        String inputFolder = null;
        String outputFolder = null;

        Boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (!isMultipart) {
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null) {
            System.out.println("Error, Please load welcome page and select the type of use case desired.");
        } else {

            DiskFileItemFactory factory = new DiskFileItemFactory();
            // maximum size that will be stored in memory
            factory.setSizeThreshold(maxMemSize);
            // Location to save data that is larger than maxMemSize.
            factory.setRepository(new File(fileRepository));

            // Create a new file upload handler
            ServletFileUpload upload = new ServletFileUpload(factory);
            // maximum file size to be uploaded.
            upload.setSizeMax(maxFileSize);

            // Parse the request to get file items
            List fileItems;
            try {
                fileItems = upload.parseRequest(request);
                // Process the uploaded file items
                Iterator i = fileItems.iterator();
                while (i.hasNext()) {
                    FileItem fi = (FileItem) i.next();
                    if (!fi.isFormField()) {
                        
                        // Get the uploaded file parameters
                        String fileName = fi.getName();
                        //  String fieldName = fi.getFieldName();
                        //  String contentType = fi.getContentType();
                        //  boolean isInMemory = fi.isInMemory();
                        //  long sizeInBytes = fi.getSize();

                        // get specified paths in the web.xml, if they are left as null then set defaults  
                        String outputPath = getServletContext().getInitParameter("clientFolder");
                        // initialise the output path for the artifacts 
                        if (outputPath.equalsIgnoreCase("")) {
                            clientDataPath = contextPath + "/Client Data/";
                        } else {
                            clientDataPath = getServletContext().getInitParameter("clientFolder");
                        }
                        // initialise the Paths for the hints and profiles to be read from based on use case
                        profilePath = contextPath + "/data/" + session.getAttribute("usecase") + "/Profiles/";
                        hintsFile = contextPath + "/data/" + session.getAttribute("usecase") + "/hints.xml";
                        
                        File input = new File(clientDataPath + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File(clientDataPath + session.getId() + "/output/");
                        output.mkdirs();
                        inputFolder = clientDataPath + session.getId() + "/input/";
                        outputFolder = clientDataPath + session.getId() + "/output/";

                        if (fileName.lastIndexOf("\\") >= 0) {
                            file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")));
                        } else {
                            file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1));
                        }
                        fi.write(file);
                    }
                }
            } catch (FileUploadException ex) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(Dispatcher.class.getName()).log(Level.SEVERE, null, ex);
            }

             System.out.println("input path : " + inputFolder);
             System.out.println("output path : " + outputFolder);
              System.out.println("profile path : " + profilePath);
               System.out.println("hintsXML path : " + hintsFile);
            
            System.out.println("file uploaded");
            // TODO make the fileRepository Folder generic so it doesnt need to be changed
            // for each migration of the program to a different server
            File input = new File(inputFolder);
            File output = new File(outputFolder);
            File profile = new File(profilePath);
            File hintsXML = new File(hintsFile);
            
            
            Processor processor = (Processor) session.getAttribute("processor");
            Controller controller = new Controller(input, output, profile, hintsXML, processor);
            HashMap initialArtifacts = controller.initialArtifacts();
            session.setAttribute("Controller", controller);

            System.out.println("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
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
