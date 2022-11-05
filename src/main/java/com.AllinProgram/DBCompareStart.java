package com.AllinProgram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.sql.SQLException;

import static com.AllinProgram.SQLHandler.vs;

/**
 * 快速比对不同环境之间的库表差异
 *
 * @author AllinProgram
 * @since 2022-10-28 11:04 星期五
 */
@Slf4j
public class DBCompareStart {

    public DBCompareStart(DBConfig databaseA, DBConfig databaseB) throws SQLException, ClassNotFoundException {
        vs(databaseA.dbFlag, SQLHandler.parseDDLList(databaseA.getUrl(), databaseA.getUsername(), databaseA.getPassword()),
                databaseB.dbFlag, SQLHandler.parseDDLList(databaseB.getUrl(), databaseB.getUsername(), databaseB.getPassword()));
    }

    @Getter
    @AllArgsConstructor
    public static class DBConfig {
        /**
         * 标识用于区分数据库
         */
        private String dbFlag;
        private String url;
        private String username;
        private String password;
    }
}
