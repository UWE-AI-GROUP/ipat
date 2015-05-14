/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Src.Artifact;
import Src.Controller;
import Src.Interaction;
import Src.Profile;
import com.google.gson.Gson;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author kieran
 */
public class newGenRequest extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet newGenRequest</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet newGenRequest at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
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
            System.out.println("Error, next generation button pressed before upload of input files.");
        } else {
            // get hint (interaction) values allocated by the user :
            Gson gson = new Gson();
            HashMap data = gson.fromJson(request.getParameter("data"), HashMap.class);
            Controller controller = (Controller) session.getAttribute("Controller");
           Interaction interaction = new Interaction();
           interaction.updateProfileHints(data, controller);
            controller.mainloop();
            
            Artifact[] results = controller.processedArtifacts;
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

//             //TESTING : check for profile global scores have been reset to 5
//                Profile[] prof = controller.currentGenerationOfProfiles;
//                for (Profile prof1 : prof) {
//                    System.out.println(prof1.getName() + "   :  " + prof1.getGlobalScore());
//            }
            String json = new Gson().toJson(HM);
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
