package com.AllinProgram;

import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SQL处理器
 *
 * @author AllinProgram
 * @since 2022-10-28 11:13 星期五
 */
@Slf4j
class SQLHandler {

    public static void vs(String dbFlagA, List<String> DDLAList, String dbFlagB, List<String> DDLBList) {

        Map<String/*tableName*/, Map<String/*filedName*/, String/*filedType*/>> databaseA = parseReflect(DDLAList);
        Map<String/*tableName*/, Map<String/*filedName*/, String/*filedType*/>> databaseB = parseReflect(DDLBList);

        // 开始比较差异
        Set<String> notExistTable = new HashSet<>();
        Set<String> notExistFiled = new HashSet<>();
        databaseA.forEach((dtTable, dtFiled) -> databaseB.forEach((ptTable, ptFiled) -> {
            if (!databaseA.containsKey(ptTable)) {
                notExistTable.add(dbFlagA + "不存在表" + ptTable);
            }
            if (!databaseB.containsKey(dtTable)) {
                notExistTable.add(dbFlagB + "不存在表" + dtTable);
            }

            // 同表比较
            if (dtTable.equals(ptTable)) {
                dtFiled.forEach((dtFiledName, dtFiledType) -> ptFiled.forEach((ptFiledName, ptFiledType) -> {
                    if (!dtFiled.containsKey(ptFiledName)) {
//                        notExistFiled.add(dbFlagA + "表 " + dtTable + "不存在字段" + ptFiledName + ptFiledType);
                        notExistFiled.add(formatMsg(dbFlagA, dtTable, ptFiledName, ptFiledType));
                    }
                    if (!ptFiled.containsKey(dtFiledName)) {
//                        notExistFiled.add(dbFlagB + "表 " + ptTable + "不存在字段" + dtFiledName + dtFiledType);
                        notExistFiled.add(formatMsg(dbFlagB, ptTable, dtFiledName, dtFiledType));
                    }
                    // 同字段比较
                    if (dtFiledName.equals(ptFiledName) && !dtFiledType.equals(ptFiledType)) {
                        // log.info("表名：{}, 字段名：{}，dt类型：{}， pt类型：{}", dtTable, ptFiledName, dtFiledType, ptFiledType);
                        log.error(String.format("表名：%-30s字段名：%-30s%s类型：%-30s%s类型：%-30s", dtTable, ptFiledName, dbFlagA, dtFiledType, dbFlagB, ptFiledType));
                    }
                }));
            }
        }));
        log.error("不存在的表：{}", notExistTable);
        log.error("不存在的字段：{}", notExistFiled);
    }

    private static String formatMsg(String env, String table, String filedName, String filedDataType) {
        return String.format("%-30s环境表：%-30s字段名：%-30s类型：%-30s", env, table, filedName, filedDataType);
    }

    /**
     * 从数据库获取所有的SQL
     */
    static String getSql(String url, String username, String password) throws SQLException, ClassNotFoundException {
        Class.forName("com.mysql.jdbc.Driver");
        Connection conn = DriverManager.getConnection(url, username, password);
        Statement stmt = conn.createStatement();
        ResultSet showTables = stmt.executeQuery("show tables;");

        List<String> tableNameList = new ArrayList<>();
        while (showTables.next()) {
            tableNameList.add(showTables.getString("Tables_in_" + url.substring(url.lastIndexOf("/") + 1)));
        }

        StringBuilder createTables = new StringBuilder();
        for (String tableName : tableNameList) {
            ResultSet showCreateTable;
            try {
                showCreateTable = stmt.executeQuery("show create table " + tableName);
            } catch (SQLException e) {
                // 表不存在就跳过
                if (e.getMessage().contains("doesn't exist")) {
                    log.error(e.getMessage());
                    continue;
                }
                throw e;
            }

            while (showCreateTable.next()) {
                // 拿到sql后，删除OB不可读字符，并增加分隔符
                String createTable = showCreateTable.getString("Create Table").replaceAll("BLOCK_SIZE.*?GLOBAL", "") + "\n\n ------------ \n\n";
                createTables.append(createTable);
            }
        }
        // Files.writeString(Paths.get(filePath), createTables, StandardOpenOption.TRUNCATE_EXISTING);
        return createTables.toString();
    }

    private static Map<String/*tableName*/, Map<String/*filedName*/, String/*filedType*/>> parseReflect(List<String> sqlList) {
        Map<String/*tableName*/, Map<String/*filedName*/, String/*filedType*/>> rst = new HashMap<>();
        for (String sql : sqlList) {
            CreateTable createTable;
            try {
                createTable = (CreateTable) CCJSqlParserUtil.parse(sql);
            } catch (JSQLParserException e) {
                log.error("无法解析的sql: {}", sql);
                throw new RuntimeException(e);
            }
            rst.put(createTable.getTable().getName(),
                    createTable.getColumnDefinitions().stream()
                            .collect(Collectors.toMap(
                                    ColumnDefinition::getColumnName,
                                    column -> column.getColDataType().toString())));
        }
        return rst;
    }
}