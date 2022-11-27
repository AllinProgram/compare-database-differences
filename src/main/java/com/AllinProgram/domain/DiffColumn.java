package com.AllinProgram.domain;

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
public class DiffColumn {

    private String columnName;

    private List<DiffType> diffTypeList;

    private ColumnDefinition columnDefinitionA;

    private ColumnDefinition columnDefinitionB;

    private String helpfulMessage;

    /**
     * 字段差异类型
     */
    public enum DiffType {
        DATA_TYPE,
        SPECIFICITY
    }
}
