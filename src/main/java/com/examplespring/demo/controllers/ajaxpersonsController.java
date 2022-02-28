package com.examplespring.demo.controllers;

import com.examplespring.demo.models.persons;
import com.examplespring.demo.repo.PersonsRepositiry;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
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

            //private static final String SQL_DELETE_PROFILE = "delete from profiles where id = :id";
            //MapSqlParameterSource params = new MapSqlParameterSource();
            //params.addValue("firstName", firstName);
            /**
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.jdbc.Driver");
            dataSource.setUsername("root");
            dataSource.setUrl("jdbc:mysql://${MYSQL_HOST:localhost}:3306/test_utf8");
            dataSource.setPassword("12345678");

            JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
            jdbcTemplate.update("INSERT INTO persons (firstName, LastName, city, dataR) VALUES (\"" + firstName + "\", \"" + lastName + "\", \"" + city + "\", \"" + dataR + "\");");
*/
            resp.getWriter().write("insert ok");

            /*
            try {
                String ret, ret2;
                ret = mysqlQuery("INSERT INTO persons (firstName, LastName, city, dataR) VALUES (\"" + firstName + "\", \"" + lastName + "\", \"" + city + "\", \"" + dataR + "\");");

                ret = mysqlQueryR("SELECT MAX(id) AS insert_id FROM persons;", "insert_id");
                ret2 = "{" +
                        "\"insert_id\": \"" + ret + "\"" +
                        "}";
                resp.getWriter().write(ret2);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }*/

        }

        resp.getWriter().write("POST");
    }
}
