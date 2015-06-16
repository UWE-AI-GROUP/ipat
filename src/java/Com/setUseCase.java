/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Com;

import Algorithms.CSSProcessor;
import Algorithms.Processor;
import Algorithms.UMLProcessor;
import java.io.IOException;
import java.util.Date;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author kieran
 */
public class setUseCase extends HttpServlet {

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
        
        // set up the type of processor selected
            String data = request.getParameter("usecase");
            Processor processor;
            switch (data) {
                case "CSS Evolution":
                    System.out.println("CSS");
                    session.setAttribute("usecase", data);
                    processor = new CSSProcessor();
                    break;
                case "UML Evolution":
                    System.out.println("UML");
                    session.setAttribute("usecase", data);
                    processor = new UMLProcessor();
                    break;
                default:
                    System.out.println("Assertion error, value: " + data);
                    throw new AssertionError();
        
        }
            session.setAttribute("processor", processor);
            RequestDispatcher dispatcher = request.getRequestDispatcher("main.jsp");
            dispatcher.forward(request, response);
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
