package com.AllinProgram;

import com.AllinProgram.component.DataBaseComponent;
import com.AllinProgram.domain.Result;
import com.AllinProgram.util.FileHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
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

    public static void vs(String envA, String envB, Result result) {

        Map<String/*tableName*/, List<ColumnDefinition>> databaseA = parseReflect(result.getCreateTableSqlA());
        Map<String/*tableName*/, List<ColumnDefinition>> databaseB = parseReflect(result.getCreateTableSqlB());

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
                        if (!aTableColumnNameList.contains(bColumnName)) {
                            notExistColumn.add(formatMsg(envA, aTable, bColumn));
                        }
                        if (!bTableColumnNameList.contains(aColumnName)) {
                            notExistColumn.add(formatMsg(envB, bTable, aColumn));
                        }

                        /*2. 同字段比较差异*/
                        List<String> msgList = validateColumn(aColumn, bColumn);
                        if (msgList != null && msgList.size() != 0) {
                            System.out.printf("表名：%-30s字段名：%-30s%s：%-80s%s：%-80s区别：%s%n", aTable, aColumnName, envA, aColumn, envB, bColumn, msgList);
                        }
                    });
                });
            }
        }));
        System.out.println();
        System.out.printf("不存在的表：%s", notExistTable);
        System.out.println("\n");
        System.out.printf("不存在的字段：%s", notExistColumn);
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

    static List<CreateTable> parseDDLList(DBCompareStart.DBConfig dbConfig) {
        return StringUtils.isBlank(dbConfig.getFilePath())
                ? DataBaseComponent.queryCreateTableList(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())
                : parseCreateTableFromFile(dbConfig.getFilePath());
    }

    private static List<CreateTable> parseCreateTableFromFile(String filePath) {
        return FileHandler.readContentBySeparator(Path.of(filePath)).stream()
                .map(sql -> {
                    try {
                        return (CreateTable) CCJSqlParserUtil.parse(sql);
                    } catch (JSQLParserException e) {
                        log.error("该SQL文本无法解析：{}", sql);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }


    private static Map<String/*tableName*/, List<ColumnDefinition>/*columnList*/> parseReflect(List<CreateTable> createTableList) {
        return createTableList.stream()
                .collect(
                        Collectors.toMap(
                                createTable -> createTable.getTable().getName(),
                                CreateTable::getColumnDefinitions));
    }
}