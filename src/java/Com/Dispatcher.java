/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Src.Artifact;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import Src.Controller;
import java.util.Enumeration;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;

/**
 *
 * @author kieran
 */
public class Dispatcher extends HttpServlet {

    int maxFileSize;
    int maxMemSize;
    String fileRepository;
    String webPath;
    

    public void init() throws ServletException {
        this.webPath = getServletConfig().getInitParameter("clientFolder");
        this.maxFileSize = Integer.parseInt(getServletConfig().getInitParameter("maxFileSize"));
        this.maxMemSize = Integer.parseInt(getServletConfig().getInitParameter("maxMemSize"));
        this.fileRepository = getServletConfig().getInitParameter("fileRepository");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
    
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
            return;
        }

        // Create a session object if it is already not  created.
        HttpSession session = request.getSession(true);
        // Get session creation time.
        Date createTime = new Date(session.getCreationTime());
        // Get last access time of this web page.
        Date lastAccessTime = new Date(session.getLastAccessedTime());

        String visitCountKey = new String("visitCount");
        String userIDKey = new String("userID");
        String userID = new String("ABCD");
        Integer visitCount = (Integer) session.getAttribute(visitCountKey);

        // Check if this is new comer on your web page.
        if (visitCount == null) {

            session.setAttribute(userIDKey, userID);
        } else {

            visitCount++;
            userID = (String) session.getAttribute(userIDKey);
        }
        session.setAttribute(visitCountKey, visitCount);

        DiskFileItemFactory factory = new DiskFileItemFactory();
        // maximum size that will be stored in memory
        factory.setSizeThreshold(maxMemSize);
        // Location to save data that is larger than maxMemSize.
        factory.setRepository(new File(fileRepository));

        // Create a new file upload handler
        ServletFileUpload upload = new ServletFileUpload(factory);
        // maximum file size to be uploaded.
        upload.setSizeMax(maxFileSize);

        try {
            // Parse the request to get file items
            List fileItems = upload.parseRequest(request);
            // Process the uploaded file items
            Iterator i = fileItems.iterator();
            while (i.hasNext()) {
                FileItem fi = (FileItem) i.next();
                if (!fi.isFormField()) {
                    // Get the uploaded file parameters
                    String fieldName = fi.getFieldName();
                    String fileName = fi.getName();
                    String contentType = fi.getContentType();
                    boolean isInMemory = fi.isInMemory();
                    long sizeInBytes = fi.getSize();
                    // Write the file to server in "/uploads/{sessionID}/"   
                    String clientDataPath = getServletContext().getInitParameter("clientFolder");
                
                    if (fileName.lastIndexOf("\\") >= 0) {
                       
                        File input = new File( clientDataPath + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File( clientDataPath + session.getId() + "/output/");
                        output.mkdirs();
                        session.setAttribute("inputFolder", clientDataPath + session.getId() + "/input/");
                        session.setAttribute("outputFolder", clientDataPath + session.getId() + "/output/");
  
                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")));
                    } else {
                         File input = new File( clientDataPath + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File( clientDataPath + session.getId() + "/output/");
                        output.mkdirs();
                        session.setAttribute("inputFolder", clientDataPath + session.getId() + "/input/");
                        session.setAttribute("outputFolder", clientDataPath + session.getId() + "/output/");
  
                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")+1));
                    }
                    fi.write(file);
                }
            }
        } catch (Exception ex) {
            System.out.println("Failure: File Upload");
            System.out.println(ex);
            //TODO show error page for website
        }
        
        // TODO make the fileRepository Folder generic so it doesnt need to be changed
        // for each migration of the program to a different server

        File input = new File((String) session.getAttribute("inputFolder"));
        File output = new File((String) session.getAttribute("outputFolder"));
        File profile = new File(getServletContext().getInitParameter("profileFolder"));
        
        // TODO synchronize controller
        Controller controller = new Controller(input, output, profile);
        controller.initialArtifacts();
        Artifact[] results = controller.processedArtifacts;
        System.out.println("RESULTS:");
        for (Artifact result : results) {
            System.out.println(result.getFile().getName());
        }
         session.setAttribute("Controller", controller);
        System.out.println("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
                + "Awaiting user to update parameters to generate next generation of results.\n");

       
        request.setAttribute("results", results);
       RequestDispatcher dispatch = request.getRequestDispatcher("main.jsp");
       response.setContentType("application/javascript");
       dispatch.forward(request, response);
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
