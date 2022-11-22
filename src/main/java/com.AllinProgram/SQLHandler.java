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

    public static void vs(String envA, List<String> DDLAList, String envB, List<String> DDLBList) {

        Map<String/*tableName*/, List<ColumnDefinition>> databaseA = parseReflect(DDLAList);
        Map<String/*tableName*/, List<ColumnDefinition>> databaseB = parseReflect(DDLBList);

        // 开始比较差异
        Set<String> notExistTable = new HashSet<>();
        Set<String> notExistColumn = new HashSet<>();
        databaseA.forEach((aTable, aColumnList) -> databaseB.forEach((bTable, bColumnList) -> {
            if (!databaseA.containsKey(bTable)) {
                notExistTable.add(envA + "不存在表" + bTable);
            }
            if (!databaseB.containsKey(aTable)) {
                notExistTable.add(envB + "不存在表" + aTable);
            }

            // 同表比较
            if (aTable.equals(bTable)) {
                aColumnList.forEach(aColumn -> {
                    List<String> aTableColumnNameList = aColumnList.stream().map(ColumnDefinition::getColumnName).collect(Collectors.toList());
                    List<String> bTableColumnNameList = bColumnList.stream().map(ColumnDefinition::getColumnName).collect(Collectors.toList());
                    bColumnList.forEach(bColumn -> {
                        String aColumnName = aColumn.getColumnName();
                        String bColumnName = bColumn.getColumnName();

                        /*1. 找到同名表中却不存在的字段*/
                        if (!aTableColumnNameList.contains(aColumnName)) {
                            notExistColumn.add(formatMsg(envA, aTable, bColumn));
                        }
                        if (!bTableColumnNameList.contains(bColumnName)) {
                            notExistColumn.add(formatMsg(envB, bTable, aColumn));
                        }

                        /*2. 同字段比较差异*/
                        List<String> msgList = validateColumn(aColumn, bColumn);
                        if (msgList != null && msgList.size() != 0) {
                            log.error(String.format("表名：%-30s字段名：%-30s%s：%-80s%s：%-80s区别：%s", aTable, aColumnName, envA, aColumn, envB, bColumn, msgList));
                        }
                    });
                });
            }
        }));
        log.error("不存在的表：{}", notExistTable);
        log.error("不存在的字段：{}", notExistColumn);
    }

    /**
     * 比对同字段ColumnDefinition对象，并将具体差异返回
     *
     * @return 具体不同
     */
    private static List<String> validateColumn(ColumnDefinition columnA, ColumnDefinition columnB) {
        String aColumnName = columnA.getColumnName();
        String bColumnName = columnB.getColumnName();
        if (!aColumnName.equals(bColumnName)) {
            return null;
        }

        List<String> msgList = new ArrayList<>();
        if (!columnA.getColDataType().toString().equals(columnB.getColDataType().toString())) {
            msgList.add("字段类型不一致");
        }

        List<String> aColumnSpecs = columnA.getColumnSpecs();
        List<String> bColumnSpecs = columnB.getColumnSpecs();
        if (!new HashSet<>(aColumnSpecs).equals(new HashSet<>(bColumnSpecs))) {
            msgList.add("字段规格不一致");
        }
        return msgList;
    }

    private static String formatMsg(String env, String table, ColumnDefinition column) {
        return String.format("%-30s环境表：%-30s字段：%-50s", env, table, column);
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

    private static Map<String/*tableName*/, List<ColumnDefinition>/*columns*/> parseReflect(List<String> sqlList) {
        Map<String, List<ColumnDefinition>> rst = new HashMap<>();
        for (String sql : sqlList) {
            CreateTable createTable;
            try {
                createTable = (CreateTable) CCJSqlParserUtil.parse(sql);
            } catch (JSQLParserException e) {
                log.error("无法解析的sql: {}", sql);
                throw new RuntimeException(e);
            }
            rst.put(createTable.getTable().getName(), createTable.getColumnDefinitions());
        }
        return rst;
    }
}