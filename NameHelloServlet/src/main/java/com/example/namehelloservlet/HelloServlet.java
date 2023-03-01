package com.example.namehelloservlet;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;


   /*
    * POST /name HTTP/1.1
    * ....
    * name=111
    */
@SuppressWarnings("serial")
public class HelloServlet extends HttpServlet {
    static final String TEMPLATE = "<html>" +
            "<head><title>TEST</title></head>" +
            "<body><h1>%s</body><html>";
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException{
        String name = req.getParameter("name");

        resp.getWriter().println(String.format(TEMPLATE, "Hello"+name));


    }
}