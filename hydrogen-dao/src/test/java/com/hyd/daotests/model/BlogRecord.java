package com.hyd.daotests.model;

import java.util.Date;

public record BlogRecord(
    Long id, String title, String content, Date createTime
) {

}
