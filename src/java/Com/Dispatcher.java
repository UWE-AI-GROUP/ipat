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
import java.util.ArrayList;
import org.apache.commons.io.FileUtils;

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
                    // TODO clear the client folder here
                   // FileUtils.deleteDirectory(new File("clientDataPath"));
                    if (fileName.lastIndexOf("\\") >= 0) {

                        File input = new File(clientDataPath + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File(clientDataPath + session.getId() + "/output/");
                        output.mkdirs();
                        session.setAttribute("inputFolder", clientDataPath + session.getId() + "/input/");
                        session.setAttribute("outputFolder", clientDataPath + session.getId() + "/output/");

                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/")));
                    } else {
                        File input = new File(clientDataPath + session.getId() + "/input/");
                        input.mkdirs();
                        File output = new File(clientDataPath + session.getId() + "/output/");
                        output.mkdirs();
                        session.setAttribute("inputFolder", clientDataPath + session.getId() + "/input/");
                        session.setAttribute("outputFolder", clientDataPath + session.getId() + "/output/");

                        file = new File(input.getAbsolutePath() + "/" + fileName.substring(fileName.lastIndexOf("/") + 1));
                    }
                    fi.write(file);
                }
            }
        } catch (Exception ex) {
            System.out.println("Failure: File Upload");
            System.out.println(ex);
            //TODO show error page for website
        }
        System.out.println("file uploaded" );
        // TODO make the fileRepository Folder generic so it doesnt need to be changed
        // for each migration of the program to a different server
        File input = new File((String) session.getAttribute("inputFolder"));
        File output = new File((String) session.getAttribute("outputFolder"));
        File profile = new File(getServletContext().getInitParameter("profileFolder"));

        System.out.println("folders created");
        // TODO synchronize controller
        Controller controller = new Controller(input, output, profile);
        //commented out code below looks older than what Kieran sent me on 14/5
//        controller.initialArtifacts();
//        session.setAttribute("Controller", controller);
//        Artifact[] results = controller.processedArtifacts;
//
//        
//        System.out.println("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
//                + "Awaiting user to update parameters to generate next generation of results.\n");
//
//        List<String> list = new ArrayList<String>();
//        for (Artifact result : results) {
//            //paths returned to view as "src" attributes for iframe table
//            //example :  Client%20Data/6328C0BCAA80D3244E0A66F77BBD47D1/output/gen_1-profile_1-HTMLPage2.html
//            list.add("Client%20Data/" + session.getId() + "/output/" + result.getFilename()); 
//        }
//        String json = new Gson().toJson(list);
//
//        response.setContentType("application/json");
//        response.setCharacterEncoding("UTF-8");
//        response.getWriter().write(json);

        
        //strt of code kieran sent me
        controller.initialArtifacts();
       Artifact[] results = controller.processedArtifacts;
       session.setAttribute("Controller", controller);
       HashMap<String, ArrayList<String>> HM = new HashMap();


       for (Artifact result : results) {
           // cut out the generation (gen_y)
           String name = result.getFilename().substring(result.getFilename().indexOf("-") + 1);
           // split the result into its profile_x  and  fileName
           String[] parts = name.split("-");
           String fileName = parts[1];
           String profileNum = parts[0].substring(parts[0].indexOf("_") + 1);

           // if the hashmap is empty add the first element to it
           if (HM.isEmpty()) {
               System.out.println("CREATING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
               ArrayList<String> imageList = new ArrayList<>();
               imageList.add("Client%20Data/" + session.getId() + "/output/" + result.getFile().getName());
               HM.put(profileNum, imageList);

               // if the hashmap is not empty 
           } else {
               // check if hashmap already has the profile with a result in it and add the current result to this list
               if (HM.containsKey(profileNum)) {
                   System.out.println("ADDING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
                   ArrayList<String> imageList = (ArrayList<String>) HM.get(profileNum);
                   imageList.add("Client%20Data/" + session.getId() + "/output/" + result.getFile().getName());
                   HM.put(profileNum, imageList);

                   // if there are no matches, add a new hashmap element with the current result placed into a new list 
               } else {
                   System.out.println("CREATING RESULT [" + fileName + "] TO [ " + profileNum + " ] ");
                   ArrayList<String> imageList = new ArrayList<>();
                   imageList.add("Client%20Data/" + session.getId() + "/output/" + result.getFile().getName());
                   HM.put(profileNum, imageList);
               }
           }
       }

//        // check for correct output for the view
//        Iterator<ArrayList<String>> iterator = HM.values().iterator();
//        int tempCount = 0;
//        while (iterator.hasNext()) {
//            ArrayList<String> next = iterator.next();
//            System.out.println("profile " + tempCount);
//            tempCount++;
//            for (String next1 : next) {
//                System.out.println(next1);
//            }
//        }

       System.out.println("Initialisation of profiles for session (" + session.getId() + ") is complete\n"
               + "Awaiting user to update parameters to generate next generation of results.\n");

       String json = new Gson().toJson(HM);
       response.setContentType("application/json");
       response.setCharacterEncoding("UTF-8");
       response.getWriter().write(json);
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
