package com.AllinProgram.domain.easyExcelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;

/**
 * 令EasyExcel支持ColumnDefinition转换
 *
 * @author AllinProgram
 * @since 2022-11-30 20:00 Wednesday
 */

public class ColumnDefinitionConverter implements Converter<ColumnDefinition> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<ColumnDefinition> context){
        return new WriteCellData<>(String.valueOf(context.getValue()));
    }
}
