package com.AllinProgram.domain.easyExcelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/**
 * 令EasyExcel支持CreateTable转换
 *
 * @author AllinProgram
 * @since 2022-11-30 20:07 Wednesday
 */
public class CreateTableConverter implements Converter<CreateTable> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<CreateTable> context) {
        return new WriteCellData<>(String.valueOf(context.getValue()));
    }
}
