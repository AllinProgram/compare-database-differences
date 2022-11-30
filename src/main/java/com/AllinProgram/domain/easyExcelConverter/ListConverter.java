package com.AllinProgram.domain.easyExcelConverter;

import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.converters.WriteConverterContext;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.data.WriteCellData;

import java.util.List;

/**
 * 令EasyExcel支持List<String>转换
 *
 * @author AllinProgram
 * @since 2022-11-30 20:09 Wednesday
 */
public class ListConverter implements Converter<List<String>> {

    @Override
    public Class<?> supportJavaTypeKey() {
        return String.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public WriteCellData<?> convertToExcelData(WriteConverterContext<List<String>> context) {
        return new WriteCellData<>(context.getValue().toString());
    }
}
