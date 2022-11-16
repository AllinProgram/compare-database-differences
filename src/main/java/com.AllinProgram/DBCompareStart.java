package com.AllinProgram;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.sql.SQLException;
import java.util.List;

import static com.AllinProgram.SQLHandler.getSql;
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
        vs(databaseA.dbFlag, parseDDLList(databaseA), databaseB.dbFlag, parseDDLList(databaseB));
    }

    /**
     * 通过数据库链接获取DDL
     */
    private List<String> parseDDLList(DBConfig dbConfig) throws SQLException, ClassNotFoundException {
        return StringUtils.isBlank(dbConfig.getDdlFile())
                ? FileHandler.readContentBySeparator(getSql(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword()))
                : FileHandler.readContentBySeparator(Path.of(dbConfig.getDdlFile()));
    }

    @Getter
    @AllArgsConstructor
    public static class DBConfig {
        /**
         * 标识用于区分数据库，必填
         */
        private String dbFlag;
        private String url;
        private String username;
        private String password;
        /**
         * 对于本地无法连接的数据库，你可以将DDL语句放在一个文件中，并将地址放在这里，否则不要填写。
         */
        private String ddlFile;
    }
}
