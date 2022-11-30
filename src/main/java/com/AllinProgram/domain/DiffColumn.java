package com.AllinProgram.domain;

import com.AllinProgram.domain.easyExcelConverter.ColumnDefinitionConverter;
import com.AllinProgram.domain.easyExcelConverter.ListConverter;
import com.alibaba.excel.annotation.ExcelProperty;
import lombok.*;
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
@EqualsAndHashCode
@AllArgsConstructor
public class DiffColumn {

    @ExcelProperty("列名")
    private String columnName;

    @ExcelProperty(value = "差异类型", converter = ListConverter.class)
    private List<DiffColumnType> diffColumnTypeList;

    @ExcelProperty(value = "环境A", converter = ColumnDefinitionConverter.class)
    private ColumnDefinition columnDefinitionA;

    @ExcelProperty(value = "环境B", converter = ColumnDefinitionConverter.class)
    private ColumnDefinition columnDefinitionB;

    @ExcelProperty("建议")
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
