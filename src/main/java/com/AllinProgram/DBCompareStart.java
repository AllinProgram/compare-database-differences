package com.AllinProgram;

import com.AllinProgram.component.DiffCompareComponent;
import com.AllinProgram.component.ResultHandleComponent;
import com.AllinProgram.component.SqlHandleComponent;
import com.AllinProgram.domain.Result;
import com.AllinProgram.util.FileHandler;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 快速比对不同环境之间的库表差异
 *
 * @author AllinProgram
 * @since 2022-10-28 11:04 星期五
 */
@Slf4j
public class DBCompareStart {

    public DBCompareStart(DBConfig databaseA, DBConfig databaseB) {
        Result result = buildDataDomain(databaseA, databaseB);
        DiffCompareComponent diffCompareComponent = new DiffCompareComponent();
        diffCompareComponent.vs(result);
        new ResultHandleComponent().printDiffInfo(result);
    }

    /**
     * 构建数据模型，为后续数据操作做准备
     */
    private Result buildDataDomain(DBConfig databaseA, DBConfig databaseB) {
        Result result = new Result();
        SqlHandleComponent sqlHandleComponent = new SqlHandleComponent();
        result.setCreateTableSqlA(sqlHandleComponent.parseDDLData(databaseA));
        result.setCreateTableSqlB(sqlHandleComponent.parseDDLData(databaseB));
        return result;
    }

    @Getter
    public static class DBConfig {

        public DBConfig(String envName, String url, String username, String password) {
            this.envName = envName;
            this.url = url;
            this.username = username;
            this.password = password;
        }

        public DBConfig(String envName, String filePath) {
            this.envName = envName;
            this.filePath = filePath;
        }

        /**
         * 环境名称，不可为空
         */
        private final String envName;
        /**
         * 连接串
         */
        private String url;
        /**
         * 用户
         */
        private String username;
        /**
         * 密码
         */
        private String password;
        /**
         * 对于本地无法连接的数据库，你可以将建表语句保存为文件，并将文件地址传入，否则不要填写。
         * 对于多个SQL请使用分隔符进行分隔内容。
         * <p>
         * {@link FileHandler#SQL_SEPARATOR}
         */
        private String filePath;
    }
}
