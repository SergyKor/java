package com.example.anketajavapro;

import java.io.*;
import java.util.ArrayList;

import jakarta.servlet.http.*;

@SuppressWarnings("serial")
public class Questionnaire extends HttpServlet {

   // static final String TEMPLATE = "<html>" +
     //       "<head><title>TEST</title></head>" +
       //     "<body><h1>%s</body><html>";


    private String firstName;
    private String lastName;
    private String country;
    private String age;
    private String gender;
    private String[] questions = new String[5];
    private ArrayList list = new ArrayList<>();





    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        questions[0] = "First name";
        questions[1] = "Last name";
        questions[2] = "Country";
        questions[3] = "Age";
        questions[4] = "Gender";
        firstName = request.getParameter("firstName");
        lastName = request.getParameter("lastName");
        country = request.getParameter("chooseCountry");
        age = request.getParameter("chooseAge");
        gender = request.getParameter("chooseGender");

        list.add(firstName);
        list.add(lastName);
        list.add(country);
        list.add(age);
        list.add(gender);

      PrintWriter print = response.getWriter();
      print.println("<html><head><title>Answers</title></head>");
      print.println("<bode><table border =2\">");
      for (int i = 0; i < questions.length; i++){
          print.println("<tr><td>" + questions[i]+"</td>");
          print.println("<td>"+ list.get(i)+"</td></tr>");
      }
      print.println("</table><br>");
      print.println("</body></html>");



    }


}