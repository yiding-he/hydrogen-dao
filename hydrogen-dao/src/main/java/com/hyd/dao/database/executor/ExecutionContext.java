package com.hyd.dao.database.executor;

import lombok.Data;

@Data
public class ExecutionContext {

    private ExecuteMode executeMode = ExecuteMode.Batch;
}
