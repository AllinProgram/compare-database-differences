package com.AllinProgram;


import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件处理
 *
 * @author AllinProgram
 * @since 2022-10-28 11:13 星期五
 */
class FileHandler {

    /**
     * 请不要纠结这里的“------------”，这是分隔符，在获取sql的时候我使用了这个作为多个SQL之间的分隔符。
     */
    public static List<String> readContentBySeparator(String content) {
        // Files.readString(Paths.get(content)
        return Arrays.stream(content.split("------------"))
                .filter(StringUtils::isNotBlank)
                .collect(Collectors.toList());
    }
}
