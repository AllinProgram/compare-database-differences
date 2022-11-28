package com.AllinProgram;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;

/**
 * Test
 *
 * @author AllinProgram
 * @since 2022-11-16 9:57 星期三
 */
class DBCompareStartTest {

    @Test
    public void vsTest() throws SQLException, ClassNotFoundException {
        new DBCompareStart(
                new DBCompareStart.DBConfig("开发环境", "jdbc:mysql://localhost:3306/t_ply_xxoo", "username", "password"),
                new DBCompareStart.DBConfig("测试环境", "src\\main\\resources\\ddl.sql")
        );
    }
}