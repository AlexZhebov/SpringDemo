package com.examplespring.demo.controllers;

import com.examplespring.demo.DaoService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;

@Controller
public class ajaxSprCityController extends HttpServlet {


    @Autowired
    DaoService serviceDB;

    /** обработка GET запросов
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    @GetMapping("/sprcity")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String returnData = "[\n";
        int i = 0;
        // считываем текущие значение
        SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT * FROM city;");
        while (resultQuery.next()) {
            if (i > 0) {returnData = returnData + ",\n";}
            returnData = returnData + "{\n   \"id\": \"" + resultQuery.getString("id") + "\",\n";
            returnData = returnData + "   \"cityname\": \"" + resultQuery.getString("cityname") + "\"\n}\n";
            i++;
        }
        returnData = returnData + "]";

        resp.getWriter().write(returnData);
    }

    /** Обработка POST запросов
     *
     * @param req
     * @param resp
     * @throws ServletException
     * @throws IOException
     */
    @Override
    @PostMapping("/sprcity")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json; charset=utf-8");

        // Проверяем если есть параметр ?city=add то выполняем запрос на добавление
        if (req.getParameter("city").equals("add")) {

            // считываем буфер POST в строку
            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = req.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) { /*report an error*/ }

            // преобразовываем строку в объект JSON
            JSONObject obj = new JSONObject(jb.toString());

            // получаем переменные
            String cityname = obj.getString("cityname");

            String return_id = "";

            serviceDB.getSqlQuery("INSERT INTO city (cityname) " +
                    "VALUES (\"" + cityname + "\");");

            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT MAX(id) AS insert_id FROM city;");
            return_id = "{" +
                    "\"insert_id\": \"-1\"" +
                    "}";
            while (resultQuery.next()) {
                return_id = "{" +
                        "\"insert_id\": \"" + resultQuery.getString("insert_id") + "\"" +
                        "}";
            }

            resp.getWriter().write(return_id);

        }

        // Проверяем если есть параметр ?city=delete то выполняем запрос на удаление
        if (req.getParameter("city").equals("delete")) {

            // считываем буфер POST в строку
            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = req.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) { /*report an error*/ }

            // преобразовываем строку в объект JSON
            JSONObject obj = new JSONObject(jb.toString());

            // получаем переменные
            String id = obj.getString("id");

            String return_id;

            serviceDB.getSqlQuery("DELETE FROM city WHERE id = \"" + id + "\";");

            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT id FROM city " +
                    "WHERE id = \"" + id + "\";");
            return_id = "{" +
                    "\"deleted_id\": \"-1\"" +
                    "}";
            if (!resultQuery.next()) {
                return_id = "{" +
                        "\"deleted_id\": \"" + id + "\"" +
                        "}";
            }

            resp.getWriter().write(return_id);

        }

        // Проверяем если есть параметр ?city=edit то выполняем запрос на добавление
        if (req.getParameter("city").equals("edit")) {

            // считываем буфер POST в строку
            StringBuffer jb = new StringBuffer();
            String line = null;
            try {
                BufferedReader reader = req.getReader();
                while ((line = reader.readLine()) != null)
                    jb.append(line);
            } catch (Exception e) { /*report an error*/ }

            // преобразовываем строку в объект JSON
            JSONObject obj = new JSONObject(jb.toString());

            // получаем переменные
            String id = obj.getString("id");
            String city = "";


            city = obj.getString("cityname");

            String return_id;

            serviceDB.getSqlQuery("UPDATE city SET " +
                    "cityname = \"" + city + "\"" +
                    " WHERE id = \"" + id + "\";");

            // проверяем сохранились ли изменения
            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT * FROM city WHERE " +
                    "cityname = \"" + city + "\" AND " +
                    "id = \"" + id + "\";");
            return_id = "{" +
                    "\"updated_id\": \"-1\"" +
                    "}";
            if (resultQuery.next()) {
                return_id = "{" +
                        "\"updated_id\": \"" + id + "\"" +
                        "}";
            }

            resp.getWriter().write(return_id);

        }
    }
}
