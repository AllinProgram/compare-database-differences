package com.AllinProgram.domain;

import com.AllinProgram.domain.easyExcelConverter.CreateTableConverter;
import com.AllinProgram.domain.easyExcelConverter.ListConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;

import static com.AllinProgram.domain.DiffTable.DiffTableType.NOT_EXIST;

/**
 * 同表（基于表名）的差异数据
 *
 * @author AllinProgram
 * @since 2022-11-27 20:53 星期日
 */
@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class DiffTable {

    @ExcelProperty("表名")
    private String tableName;

    @ExcelProperty(value = "差异类型", converter = ListConverter.class)
    private List<DiffTableType> diffTableTypeList;

    @ExcelProperty(value = "环境A", converter = CreateTableConverter.class)
    private CreateTable createTableA;

    @ExcelProperty(value = "环境B", converter = CreateTableConverter.class)
    private CreateTable createTableB;

    @ExcelProperty("建议")
    private String helpfulMessage;

    public static void buildDiffTable(DiffTableType diffTableType, Result result, String tableName) {
        switch (diffTableType) {
            case NOT_EXIST:
                result.getDiffTableList().add(new DiffTable(
                        tableName,
                        List.of(NOT_EXIST),
                        result.getCreateTableSqlA().stream()
                                .filter(createTable -> tableName.equals(createTable.getTable().getName()))
                                .findFirst().orElse(null),
                        result.getCreateTableSqlB().stream()
                                .filter(createTable -> tableName.equals(createTable.getTable().getName()))
                                .findFirst().orElse(null),
                        null
                ));
                break;
            case INDEX:
                break;
            case OPTION:
                break;
        }
    }

    /**
     * 表差异类型
     */
    public enum DiffTableType {
        INDEX,
        OPTION,
        NOT_EXIST,
    }
}
