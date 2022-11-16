package com.AllinProgram;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件处理
 *
 * @author AllinProgram
 * @since 2022-10-28 11:13 星期五
 */
@Slf4j
class FileHandler {

    /**
     * 请不要纠结这里的“------------”，这是分隔符，在获取sql的时候我使用了这个作为多个SQL之间的分隔符。
     */
    public static List<String> readContentBySeparator(String content) {
        return Arrays.stream(content.split("------------"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }

    public static List<String> readContentBySeparator(Path filePath) {
        try {
            return Arrays.stream(Files.readString(filePath).split("------------"))
                    .filter(StringUtils::isNotBlank)
                    .map(sql -> sql.replaceAll("BLOCK_SIZE.*?GLOBAL", "")) // 干掉OB特有的字符
                    .collect(Collectors.toList());
        } catch (IOException e) {
            log.error("通过文件地址读取DDL数据失败");
            throw new RuntimeException(e);
        }
    }
}
