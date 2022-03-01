package com.examplespring.demo.controllers;

import com.examplespring.demo.DaoService;
import com.examplespring.demo.models.persons;
import com.examplespring.demo.repo.PersonsRepositiry;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
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
        resp.setHeader("Content-Type", "application/json; charset=utf-8");

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
            String city = obj.getString("city");
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

            serviceDB.getSqlQuery("INSERT INTO persons (firstName, LastName, city, dataR) " +
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
            String firstName = obj.getString("firstName");
            String lastName = obj.getString("lastName");
            String city = obj.getString("city");
            String dataR = obj.getString("dataR");

            String return_id;

            serviceDB.getSqlQuery("UPDATE persons SET " +
                    "firstName = \"" + firstName + "\"," +
                    "lastName = \"" + lastName + "\"," +
                    "city = \"" + city + "\"," +
                    "dataR = \"" + dataR + "\"" +
                    " WHERE id = \"" + id + "\";");

            // проверяем сохранились ли изменения
            SqlRowSet resultQuery = serviceDB.getSqlQueryR("SELECT id FROM persons WHERE " +
                    "firstName = \"" + firstName + "\" AND " +
                    "lastName = \"" + lastName + "\" AND " +
                    "city = \"" + city + "\" AND " +
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
