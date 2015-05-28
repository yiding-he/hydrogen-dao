package com.hyd.daotest.bugs;

import com.hyd.dao.SQL;

import java.util.Date;

/**
 * (description)
 * created at 2015/5/28
 *
 * @author Yiding
 */
public class ExtraParameterBug {

    public static void main(String[] args) {
        SQL.Select select = SQL.Select("TRADE_ID").From("UCR_CRM1.TF_BH_TRADE");
        select = select.Where("ACCEPT_DATE>?", new Date());
        select = select.OrderBy("ACCEPT_DATE");

        System.out.println(select.toCommand());
        System.out.println(select.toCommand());


        SQL.Update update = SQL.Update("t").Set("name=?", "name").Where("id=?", "id");
        System.out.println(update.toCommand());
        System.out.println(update.toCommand());


        SQL.Delete delete = SQL.Delete("t").Where("id=?", "id");
        System.out.println(delete.toCommand());
        System.out.println(delete.toCommand());


        SQL.Insert insert = SQL.Insert("t").Values("id", "id").Values("name", "name");
        System.out.println(insert.toCommand());
        System.out.println(insert.toCommand());
    }
}
