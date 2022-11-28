package com.AllinProgram.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

import java.util.List;

/**
 * 同字段（基于名称）的差异数据
 *
 * @author AllinProgram
 * @since 2022-11-27 20:48 星期日
 */
@Getter
@Setter
@AllArgsConstructor
public class DiffColumn {

    private String columnName;

    private List<DiffColumnType> diffColumnTypeList;

    private ColumnDefinition columnDefinitionA;

    private ColumnDefinition columnDefinitionB;

    private String helpfulMessage;

    /**
     * 字段差异类型
     */
    public enum DiffColumnType {
        DATA_TYPE,
        SPECIFICITY,
        NOT_EXIST
    }
}
