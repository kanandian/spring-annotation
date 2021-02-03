package com.flyer.tx;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Random;
import java.util.UUID;

@Repository
public class UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    public void insertUser() {
        String sql = "insert into user (username, age) values(?, ?)";

        String userName = UUID.randomUUID().toString().substring(0, 5);
        Random random = new Random();
        int age = 20 + random.nextInt(10);
        jdbcTemplate.update(sql, userName, age);
    }
}
