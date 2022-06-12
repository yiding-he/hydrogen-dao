package com.hyd.dao.src.models;

import java.util.Date;

public record BlogRecord(
    Long id, String title, String content, Date createTime
) {

}
