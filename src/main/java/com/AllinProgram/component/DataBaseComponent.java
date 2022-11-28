package com.AllinProgram.component;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 数据库相关操作
 *
 * @author AllinProgram
 * @since 2022-11-28 16:43 星期一
 */
@Slf4j
public class DataBaseComponent {

    public static List<CreateTable> queryCreateTableList(String url, String username, String password) {
        Connection connection = connect(url, username, password);
        List<String> tableNameList = showTables(connection, url);
        return showCreateTableList(connection, tableNameList);
    }

    private static List<String> showTables(Connection connection, String url) {
        List<String> tableNameList = new ArrayList<>();
        try {
            ResultSet showTables = connection.createStatement().executeQuery("show tables;");

            while (showTables.next()) {
                String databaseName = url.substring(url.lastIndexOf("/") + 1);
                // 结果集列名格式为Tables_in_[databaseName]
                String tableName = showTables.getString("Tables_in_" + databaseName);
                tableNameList.add(tableName);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return tableNameList;
    }

    private static List<CreateTable> showCreateTableList(Connection connection, List<String> tableNames) {
        List<CreateTable> createTableList = new ArrayList<>();
        for (String tableName : tableNames) {
            try {
                ResultSet showCreateTableResult = connection.createStatement().executeQuery("show create table " + tableName);

                // 根据表名查询得到建表语句
                while (showCreateTableResult.next()) {
                    // 拿到sql后，删除OB不可读字符，并增加分隔符
                    String createTableSql = showCreateTableResult.getString("Create Table").replaceAll("BLOCK_SIZE.*?GLOBAL", "");
                    try {
                        createTableList.add((CreateTable) CCJSqlParserUtil.parse(createTableSql));
                    } catch (JSQLParserException e) {
                        log.error("无法解析的sql: {}", createTableSql);
                        throw new RuntimeException(e);
                    }
                }
            } catch (SQLException e) {
                // 分库分表场景下可能提示表不存在，直接跳过即可
                if (e.getMessage().contains("doesn't exist")) {
                    log.error(e.getMessage());
                    continue;
                }
                throw new RuntimeException(e);
            }
        }
        return createTableList;
    }

    private static Connection connect(String url, String username, String password) {
        try {
            return DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
