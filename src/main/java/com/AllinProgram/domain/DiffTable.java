package com.AllinProgram.domain;

import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;

/**
 * 同表（基于表名）的差异数据
 *
 * @author AllinProgram
 * @since 2022-11-27 20:53 星期日
 */
@Getter
@Setter
public class DiffTable {

    private String tableName;

    private List<DiffType> diffTypeList;

    private CreateTable createTableA;

    private CreateTable createTableB;

    private String helpfulMessage;

    /**
     * 表差异类型
     */
    public enum DiffType {
        INDEX,
        TABLE_OPTION
    }
}
