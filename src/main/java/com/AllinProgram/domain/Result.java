package com.AllinProgram.domain;

import lombok.Getter;
import lombok.Setter;
import net.sf.jsqlparser.statement.create.table.CreateTable;

import java.util.List;

/**
 * 程序运行数据
 *
 * @author AllinProgram
 * @since 2022-11-27 20:45 星期日
 */
@Getter
@Setter
public class Result {

    private List<CreateTable> createTableSqlA;

    private List<CreateTable> createTableSqlB;

    private List<DiffColumn> diffColumnList;

    private List<DiffTable> diffTableList;
}
