package com.AllinProgram.component;

import com.AllinProgram.DBCompareStart;
import com.AllinProgram.util.FileHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import org.apache.commons.lang3.StringUtils;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL处理
 *
 * @author AllinProgram
 * @since 2022-11-28 19:49 星期一
 */
@Slf4j
public class SqlHandleComponent {

    public List<CreateTable> parseDDLData(DBCompareStart.DBConfig dbConfig) {
        return StringUtils.isBlank(dbConfig.getFilePath())
                ? new DataBaseComponent().queryCreateTableList(dbConfig.getUrl(), dbConfig.getUsername(), dbConfig.getPassword())
                : parseCreateTableFromFile(dbConfig.getFilePath());
    }

    private List<CreateTable> parseCreateTableFromFile(String filePath) {
        return FileHandler.readContentBySeparator(Path.of(filePath)).stream()
                .map(sql -> {
                    try {
                        return (CreateTable) CCJSqlParserUtil.parse(sql);
                    } catch (JSQLParserException e) {
                        log.error("该SQL文本无法解析：{}", sql);
                        throw new RuntimeException(e);
                    }
                }).collect(Collectors.toList());
    }
}
