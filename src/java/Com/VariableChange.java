/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Src.Controller;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.apache.log4j.Logger;

/**
 *
 * @author kieran
 */
public class VariableChange extends HttpServlet {
private static final Logger logger = Logger.getLogger(VariableChange.class);
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
        Controller controller = (Controller) session.getAttribute("controller");
            Gson gson = new Gson();
            HashMap variableData = gson.fromJson(request.getParameter("vars"), HashMap.class);

            
            Set keySet = variableData.keySet();
            Iterator iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String next = (String) iterator.next();

                switch (next) {
                    case "ProfileNum":
                        int profileCount = Integer.parseInt((String) variableData.get(next));
                        logger.info(next + " has been found inside VariableChange Servlet with value: " + profileCount);
                        controller.setNoOfProfiles(profileCount);
                        System.out.println("Number of Profiles successfully changed!");
                        break;
                    default:
                        logger.error("failed to change a variable within VariableChange Servlet");
                        throw new AssertionError();
                }
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
