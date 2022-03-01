package com.examplespring.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

@Service
public class DaoService {

    private final JdbcTemplate template;

    public int getCount() {
        return template.queryForObject(
                "SELECT COUNT(*) FROM test_jdbc.public.user", Integer.class);
    }

    public int getSqlQuery(String SQLquery) {
        return template.update(SQLquery);
    }

    public SqlRowSet getSqlQueryR(String SQLquery) {
        return template.queryForRowSet(SQLquery);
    }

    @Autowired
    public DaoService(DataSource dataSource) {
        this.template = new JdbcTemplate(dataSource);
    }
}
