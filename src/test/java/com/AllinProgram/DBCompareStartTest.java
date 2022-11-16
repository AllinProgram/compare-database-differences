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
                new DBCompareStart.DBConfig("DT-Proposal", "jdbc:mysql://localhost:3306/t_ply_xxoo", "username", "password", null),
                new DBCompareStart.DBConfig("PROD-Proposal", null, null, null, "src\\main\\resources\\ddl.sql")
        );
    }
}