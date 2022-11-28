package com.AllinProgram.component;

import com.AllinProgram.domain.DiffColumn;
import com.AllinProgram.domain.DiffTable;
import com.AllinProgram.domain.Result;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.*;
import java.util.stream.Collectors;

import static com.AllinProgram.domain.DiffTable.buildDiffTable;

/**
 * 比对差异
 *
 * @author AllinProgram
 * @since 2022-11-28 19:56 星期一
 */
public class DiffCompareComponent {
    public void vs(Result result) {

        Map<String/*tableName*/, List<ColumnDefinition>> databaseA = parseReflect(result.getCreateTableSqlA());
        Map<String/*tableName*/, List<ColumnDefinition>> databaseB = parseReflect(result.getCreateTableSqlB());

        // 开始比较差异
        databaseA.forEach((aTableName, aColumnList) -> databaseB.forEach((bTableName, bColumnList) -> {
            if (!databaseA.containsKey(bTableName)) {
                buildDiffTable(DiffTable.DiffTableType.NOT_EXIST, result, bTableName);
            }
            if (!databaseB.containsKey(aTableName)) {
                buildDiffTable(DiffTable.DiffTableType.NOT_EXIST, result, aTableName);
            }

            // 同表比较
            if (aTableName.equals(bTableName)) {
                aColumnList.forEach(aColumn -> {
                    List<String> aTableColumnNameList = aColumnList.stream().map(ColumnDefinition::getColumnName).collect(Collectors.toList());
                    List<String> bTableColumnNameList = bColumnList.stream().map(ColumnDefinition::getColumnName).collect(Collectors.toList());
                    bColumnList.forEach(bColumn -> {
                        String aColumnName = aColumn.getColumnName();
                        String bColumnName = bColumn.getColumnName();

                        /*1. 找到同名表中却不存在的字段*/
                        if (!aTableColumnNameList.contains(bColumnName)) {
                            result.getDiffColumnList().add(new DiffColumn(
                                    bColumnName,
                                    List.of(DiffColumn.DiffColumnType.NOT_EXIST),
                                    aColumn, bColumn, null
                            ));
                        }
                        if (!bTableColumnNameList.contains(aColumnName)) {
                            result.getDiffColumnList().add(new DiffColumn(
                                    aColumnName,
                                    List.of(DiffColumn.DiffColumnType.NOT_EXIST),
                                    aColumn, bColumn, null
                            ));
                        }

                        /*2. 同字段比较差异*/
                        List<DiffColumn.DiffColumnType> diffColumnTypeList = validateColumn(aColumn, bColumn);
                        if (diffColumnTypeList != null && diffColumnTypeList.size() != 0) {
                            result.getDiffColumnList().add(new DiffColumn(
                                    aColumnName, diffColumnTypeList, aColumn, bColumn, null));
                        }
                    });
                });
            }
        }));
    }

    /**
     * 比对同字段ColumnDefinition对象，并将具体差异返回
     *
     * @return 具体不同
     */
    private List<DiffColumn.DiffColumnType> validateColumn(ColumnDefinition columnA, ColumnDefinition columnB) {
        String aColumnName = columnA.getColumnName();
        String bColumnName = columnB.getColumnName();
        if (!aColumnName.equals(bColumnName)) {
            return null;
        }

        List<DiffColumn.DiffColumnType> diffColumnTypeList = new ArrayList<>();
        if (!columnA.getColDataType().toString().equals(columnB.getColDataType().toString())) {
            diffColumnTypeList.add(DiffColumn.DiffColumnType.DATA_TYPE);
        }

        List<String> aColumnSpecs = columnA.getColumnSpecs();
        List<String> bColumnSpecs = columnB.getColumnSpecs();
        if (!new HashSet<>(aColumnSpecs).equals(new HashSet<>(bColumnSpecs))) {
            diffColumnTypeList.add(DiffColumn.DiffColumnType.SPECIFICITY);
        }
        return diffColumnTypeList;
    }

    private String formatMsg(String env, String table, ColumnDefinition column) {
        return String.format("%-30s环境表：%-30s字段：%-50s", env, table, column);
    }

    private Map<String/*tableName*/, List<ColumnDefinition>/*columnList*/> parseReflect(List<CreateTable> createTableList) {
        return createTableList.stream()
                .collect(
                        Collectors.toMap(
                                createTable -> createTable.getTable().getName(),
                                CreateTable::getColumnDefinitions));
    }
}
