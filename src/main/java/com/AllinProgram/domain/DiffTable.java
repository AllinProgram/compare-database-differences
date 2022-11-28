package com.AllinProgram.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
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
@AllArgsConstructor
public class DiffTable {

    private String tableName;

    private List<DiffTableType> diffTableTypeList;

    private CreateTable createTableA;

    private CreateTable createTableB;

    private String helpfulMessage;

    public static void buildDiffTable(DiffTableType diffTableType, Result result, String tableName) {
        switch (diffTableType) {
            case NOT_EXIST:
                result.getDiffTableList().add(new DiffTable(
                        tableName,
                        List.of(NOT_EXIST),
                        result.getCreateTableSqlA().stream()
                                .filter(createTable -> tableName.equals(createTable.getTable().getName()))
                                .findFirst().get(),
                        result.getCreateTableSqlB().stream()
                                .filter(createTable -> tableName.equals(createTable.getTable().getName()))
                                .findFirst().get(),
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
        NOT_EXIST
    }
}
