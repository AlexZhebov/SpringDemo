package com.examplespring.demo.controllers;

import com.examplespring.demo.DaoService;
import com.examplespring.demo.repo.PersonsRepositiry;
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

public class ajaxpersonsController extends HttpServlet {

    private String script;
    private int i;

    @Autowired
    private PersonsRepositiry personsRepositiry;
    @Autowired
    DaoService serviceDB;
    /**
     * формирование json c данными с таблицы
     */
    @Override
    @GetMapping("/showdb")
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {


        //resp.setHeader("Access-Control-Allow-Origin", "*");
        //resp.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
        //resp.setHeader("Access-Control-Max-Age", "3600");
        //resp.setHeader("Access-Control-Allow-Headers", "x-requested-with");

        resp.setHeader("Content-Type", "application/json; charset=utf-8");

        String findQuery = "";
        //проверяем есть ли текст поиска
        if (!(req.getParameter("findtext") == null)) {
            findQuery = " AND (persons.firstName like '%" + req.getParameter("findtext") + "%'" +
                    "OR persons.lastName like '%" + req.getParameter("findtext") + "%'" +
                    "OR city.cityname like '%" + req.getParameter("findtext") + "%'" +
                    ") ";
        }

        String start = "0";
        //проверяем есть ли параметр start
        if (!(req.getParameter("start") == null)) {
            start = req.getParameter("start");
        }

        String limit = "10";
        //проверяем есть ли параметр limit
        if (!(req.getParameter("limit") == null)) {
            limit = req.getParameter("limit");
        }

        // вычисляем количество записей в таблице с условием поиска
        String totalcount = "0";
        SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT COUNT(persons.id) AS totalcount" +
                " FROM persons, city " +
                "WHERE persons.id_city = city.id " + findQuery + ";");
        if (resultQuery.next()) {
            totalcount = resultQuery.getString("totalcount");
        }

        String returnData = "\n { \n\"total\": \"" + totalcount + "\",     \n\"persons\": \n[";
        int i = 0;
        // считываем текущие значение
        resultQuery = serviceDB.getSqlQueryR("SELECT persons.*, city.cityname" +
                " FROM persons, city " +
                "WHERE persons.id_city = city.id " + findQuery + "" +
                "LIMIT " + start + ", " + limit + ";");
        while (resultQuery.next()) {
            if (i > 0) {returnData = returnData + ",\n";}
            returnData = returnData + " {\n" +
                    "  \"id\": \"" + resultQuery.getString("id") + "\",\n" +
                    "  \"firstName\": \"" + resultQuery.getString("firstname") + "\",\n" +
                    "  \"lastName\": \"" + resultQuery.getString("lastname") + "\",\n" +
                    "  \"id_city\": \"" + resultQuery.getString("id_city") + "\",\n" +
                    "  \"city\": \"" + resultQuery.getString("cityname") + "\",\n" +
                    "  \"dataR\": \"" + resultQuery.getString("dataR") + "\"\n" +
                    "  }";
            i++;
        }
        returnData = returnData + "\n]\n}";

        resp.getWriter().write(returnData);

        /**
        Iterable<persons> persons = personsRepositiry.findAll();
        script = "[\n";
        i = 0;
        persons.forEach(e -> {
            if (i > 0) {script = script + ", \n";}
            script = script + "  {\n" +
                    "  \"id\": \"" + e.getId() + "\",\n" +
                    "  \"firstName\": \"" + e.getFirstname() + "\",\n" +
                    "  \"lastName\": \"" + e.getLastname() + "\",\n" +
                    "  \"city\": \"" + e.getCity() + "\",\n" +
                    "  \"dataR\": \"" + e.getDatar() + "\"\n" +
                    "  }";
            i = i + 1;
        });
        script = script + "\n]\n";

        resp.getWriter().write(script);
         */
    }

    @Override
    @PostMapping("/showdb")
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setHeader("Content-Type", "application/json; charset=utf-8");

        // Проверяем если есть параметр ?persons=add то выполняем запрос на добавление
        if (req.getParameter("persons").equals("add")) {

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
            String firstName = obj.getString("firstName");
            String lastName = obj.getString("lastName");
            String city = obj.getString("id_city");
            String dataR = obj.getString("dataR");

            String return_id = "";

            /** первоначальный доступ к БД
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUsername("root");
            dataSource.setUrl("jdbc:mysql://localhost:3306/test_utf8");
            dataSource.setPassword("12345678");
            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
             jdbcTemplate.update("INSERT INTO persons (firstName, LastName, city, dataR) VALUES (\"" + firstName + "\", \"" + lastName + "\", \"" + city + "\", \"" + dataR + "\");");
             SqlRowSet resultQuery = jdbcTemplate.queryForRowSet("SELECT MAX(id) AS insert_id FROM persons;");
             */

            serviceDB.getSqlQuery("INSERT INTO persons (firstName, LastName, id_city, dataR) " +
                    "VALUES (\"" + firstName + "\", \"" + lastName + "\", \"" + city + "\", " +
                    "\"" + dataR + "\");");

            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT MAX(id) AS insert_id FROM persons;");
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

        // Проверяем если есть параметр ?persons=delete то выполняем запрос на удаление
        if (req.getParameter("persons").equals("delete")) {

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

            serviceDB.getSqlQuery("DELETE FROM persons WHERE id = \"" + id + "\";");

            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT id FROM persons " +
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

        // Проверяем если есть параметр ?persons=edit то выполняем запрос на добавление
        if (req.getParameter("persons").equals("edit")) {

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
            String firstName = "";
            String lastName = "";
            String city = "";
            String dataR = "";

            // считываем текущие значение
            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT * FROM persons WHERE " +
                    "id = \"" + id + "\";");
            if (resultQuery.next()) {
                firstName = resultQuery.getString("firstName");
                lastName = resultQuery.getString("lastName");
                city = resultQuery.getString("id_city");
                dataR = resultQuery.getString("dataR");
            }

            //проверяем какие пришли измененные поля
            if (!obj.isNull("firstName")) {
                firstName = obj.getString("firstName");
            }
            if (!obj.isNull("lastName")) {
                lastName = obj.getString("lastName");
            }
            if (!obj.isNull("id_city")) {
                city = obj.getString("id_city");
            }
            if (!obj.isNull("dataR")) {
                dataR = obj.getString("dataR");
            }

            String return_id;

            serviceDB.getSqlQuery("UPDATE persons SET " +
                    "firstName = \"" + firstName + "\"," +
                    "lastName = \"" + lastName + "\"," +
                    "id_city = \"" + city + "\"," +
                    "dataR = \"" + dataR + "\"" +
                    " WHERE id = \"" + id + "\";");

            // проверяем сохранились ли изменения
            resultQuery = serviceDB.getSqlQueryR("SELECT * FROM persons WHERE " +
                    "firstName = \"" + firstName + "\" AND " +
                    "lastName = \"" + lastName + "\" AND " +
                    "id_city = \"" + city + "\" AND " +
                    "dataR = \"" + dataR + "\" AND " +
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
