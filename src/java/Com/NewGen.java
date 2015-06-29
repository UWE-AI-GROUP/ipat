/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Src.Controller;
import java.io.IOException;
import java.util.HashMap;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.util.Iterator;
import java.util.Set;
import org.apache.log4j.Logger;

// TODO javadoc

/**
 *
 * @author kieran
 */
public class NewGen extends HttpServlet {
 private static final Logger logger = Logger.getLogger(NewGen.class);


    /**
     * Handles the HTTP <code>POST</code> method for the next generation
     * request button in the web view. 
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
            logger.error("request cannot be processed, no session.");
        } else {
            Controller controller = (Controller) session.getAttribute("Controller");
            Gson gson = new Gson();
            JsonObject data = gson.fromJson(request.getParameter("data"), JsonObject.class);
            HashMap vars = gson.fromJson( data.get("vars").getAsJsonObject(), HashMap.class);
            HashMap scores = gson.fromJson( data.get("scores").getAsJsonObject(), HashMap.class);
            
            Set keySet = vars.keySet();
            Iterator iterator = keySet.iterator();
            while (iterator.hasNext()) {
                String next = (String) iterator.next();

                switch (next) {
                    case "ProfileNum":
                        int profileCount = Integer.parseInt((String) vars.get(next));
                        logger.info(next + " has been found inside VariableChange Servlet with value: " + profileCount);
                        
                        session.setAttribute("profileCount", profileCount);
                        break;
                    default:
                        logger.error("failed to change variable " +next+ " within VariableChange Servlet");
                        throw new AssertionError();
                }
            }
            
            int profileCount = Integer.parseInt( (String) vars.get("ProfileNum"));
            HashMap HTML_Strings = controller.mainloop(scores, profileCount);
            String json = new Gson().toJson(HTML_Strings);
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
