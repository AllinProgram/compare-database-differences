package com.AllinProgram.component;

import com.AllinProgram.domain.DiffColumn;
import com.AllinProgram.domain.DiffTable;
import com.AllinProgram.domain.Result;
import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.write.metadata.WriteSheet;

/**
 * 数据处理组件
 *
 * @author AllinProgram
 * @since 2022-11-29 20:56 星期二
 */
public class ResultHandleComponent {

    public void buildDataToExcel(Result result) {
        String fileName = "数据库差异文件" + System.currentTimeMillis() + ".xlsx";
        try (ExcelWriter excelWriter = EasyExcel.write(fileName).build()) {
            WriteSheet writeTableSheet = EasyExcel.writerSheet("表差异").head(DiffTable.class).build();
            excelWriter.write(result.getDiffTableList(), writeTableSheet);

            WriteSheet writeColumnSheet = EasyExcel.writerSheet("列差异").head(DiffColumn.class).build();
            excelWriter.write(result.getDiffColumnList(), writeColumnSheet);
        }
    }
}
