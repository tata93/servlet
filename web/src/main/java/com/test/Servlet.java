package com.test;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.PrintWriter;
import java.io.IOException;
import java.rmi.server.ExportException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet
public class Servlet extends HttpServlet {

    private final JdbcTemplate jdbcTemplate;

    Servlet(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private void serviceInner(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String INN = request.getParameter("INN");
        String OGRN = request.getParameter("OGRN");
        String name = request.getParameter("name");
        String address = request.getParameter("address");
        String sql;
        if (INN != null && OGRN != null && name != null && address != null) {
            sql = "INSERT INTO ORG VALUES (null, " + INN + ", " + OGRN + ", " + name + ", " + address + ")";
            jdbcTemplate.update(sql);
        } else {
            response.sendRedirect("index.html");
        }
        response.setContentType("text/html;charset=UTF-8");
    }

    private void serviceSearch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        try {
            String searchInn = request.getParameter("searchInn");
            String searchOgrn = request.getParameter("searchOgrn");
            String searchName = request.getParameter("searchName");
            String searchAddress = request.getParameter("searchAddress");
            String sql = null;
            if (null == searchInn) {
                if (searchOgrn == null) {
                    if (searchName == null) {
                        if (searchAddress == null) {
                             response.sendRedirect("index.html");
                        } else {
                            sql = "SELECT * FROM ORG WHERE ADRESS = " + searchAddress;
                        }
                    } else {
                        sql = "SELECT * FROM ORG WHERE TITLE = " + searchName;
                    }
                } else {
                    sql = "SELECT * FROM ORG WHERE OGRN = " + searchOgrn;
                }
            } else {
                sql = "SELECT * FROM ORG WHERE INN = " + searchInn;
            }
            String json = new Gson().toJson(jdbcTemplate.queryForList(sql));
            response.setContentType("application/json;charset=UTF-8");
            out.print(json);
        } catch (ExportException e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("index.html").forward(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (request.getParameter("action").equals("search")) {
            serviceSearch(request, response);
        } else if (request.getParameter("action").equals("insert")) {
            serviceInner(request, response);
            response.sendRedirect("index.html");
        } else {
            response.sendRedirect("index.html");
        }
    }

}

