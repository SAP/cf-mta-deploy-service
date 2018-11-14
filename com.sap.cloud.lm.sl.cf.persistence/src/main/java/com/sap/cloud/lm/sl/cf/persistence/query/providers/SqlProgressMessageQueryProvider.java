package com.sap.cloud.lm.sl.cf.persistence.query.providers;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.sap.cloud.lm.sl.cf.persistence.DataSourceWithDialect;
import com.sap.cloud.lm.sl.cf.persistence.dialects.DataSourceDialect;
import com.sap.cloud.lm.sl.cf.persistence.model.ProgressMessage;
import com.sap.cloud.lm.sl.cf.persistence.model.ProgressMessage.ProgressMessageType;
import com.sap.cloud.lm.sl.cf.persistence.query.SqlQuery;
import com.sap.cloud.lm.sl.cf.persistence.util.JdbcUtil;

public class SqlProgressMessageQueryProvider {

    private static final String COLUMN_NAME_ID = "ID";
    private static final String COLUMN_NAME_PROCESS_ID = "PROCESS_ID";
    private static final String COLUMN_NAME_TASK_ID = "TASK_ID";
    private static final String COLUMN_NAME_TASK_EXECUTION_ID = "TASK_EXECUTION_ID";
    private static final String COLUMN_NAME_TYPE = "TYPE";
    private static final String COLUMN_NAME_TEXT = "TEXT";
    private static final String COLUMN_NAME_TIMESTAMP = "TIMESTAMP";

    private static final String SELECT_MESSAGES_BY_PROCESS_ID = "SELECT ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP FROM %s WHERE PROCESS_ID=? ORDER BY ID";
    private static final String SELECT_MESSAGES_BY_PROCESS_AND_TASK_ID_AND_TASK_EXECUTION_ID = "SELECT ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP FROM %s WHERE PROCESS_ID=? AND TASK_ID=? AND TASK_EXECUTION_ID=? ORDER BY ID";
    private static final String SELECT_MESSAGES_BY_PROCESS_AND_TASK_ID = "SELECT TASK_EXECUTION_ID FROM %s WHERE PROCESS_ID=? AND TASK_ID=? ORDER BY ID DESC";
    private static final String SELECT_SPECIFIC_MESSAGE = "SELECT ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP FROM %s WHERE PROCESS_ID=? AND TASK_ID=? AND TASK_EXECUTION_ID=? AND TYPE=? ORDER BY ID";
    private static final String SELECT_MESSAGES_BY_PROCESS_ID_AND_TYPE = "SELECT ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP FROM %s WHERE PROCESS_ID=? AND TYPE=? ORDER BY ID";
    private static final String SELECT_ALL_MESSAGES = "SELECT ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP FROM %s ORDER BY ID";
    private static final String INSERT_MESSAGE = "INSERT INTO %s (ID, PROCESS_ID, TASK_ID, TASK_EXECUTION_ID, TYPE, TEXT, TIMESTAMP) VALUES(%s, ?, ?, ?, ?, ?, ?)";
    private static final String DELETE_MESSAGES_BY_PROCESS_AND_TASK_ID = "DELETE FROM %s WHERE PROCESS_ID=? AND TASK_ID=? AND TASK_EXECUTION_ID=?";
    private static final String DELETE_MESSAGES_BY_PROCESS_ID = "DELETE FROM %s WHERE PROCESS_ID=?";
    private static final String DELETE_MESSAGES_BY_PROCESS_ID_AND_TASK_ID = "DELETE FROM %s WHERE PROCESS_ID=? AND TASK_ID=?";
    private static final String DELETE_MESSAGES_OLDER_THAN = "DELETE FROM %s WHERE TIMESTAMP < ?";
    private static final String UPDATE_MESSAGE_BY_ID = "UPDATE %s SET TEXT=?, TIMESTAMP=? WHERE ID=?";

    private static final String ID_SEQ_NAME = "ID_SEQ";

    private final String tableName;
    private final DataSourceWithDialect dataSourceWithDialect;

    public SqlProgressMessageQueryProvider(String tableName, DataSourceWithDialect dataSourceWithDialect) {
        this.tableName = tableName;
        this.dataSourceWithDialect = dataSourceWithDialect;
    }

    public SqlQuery<Boolean> getAddQuery(final ProgressMessage message) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(INSERT_MESSAGE, tableName));
                statement.setString(1, message.getProcessId());
                statement.setString(2, message.getTaskId());
                statement.setString(3, message.getTaskExecutionId());
                statement.setString(4, message.getType()
                    .name());
                statement.setString(5, message.getText());
                statement.setTimestamp(6, new Timestamp(message.getTimestamp()
                    .getTime()));
                int rowsInserted = statement.executeUpdate();
                return rowsInserted > 0;
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<Boolean> getUpdateQuery(final long existingId, final ProgressMessage newMessage) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(UPDATE_MESSAGE_BY_ID, tableName));
                statement.setString(1, newMessage.getText());
                statement.setTimestamp(2, new Timestamp(newMessage.getTimestamp()
                    .getTime()));
                statement.setLong(3, existingId);
                int rowsUpdated = statement.executeUpdate();
                return rowsUpdated == 1;
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<Integer> getRemoveByProcessIdQuery(final String processId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(DELETE_MESSAGES_BY_PROCESS_ID, tableName));
                statement.setString(1, processId);
                return statement.executeUpdate();
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<Integer> getRemoveByProcessIdTaskIdAndTaskExecutionIdQuery(final String processId, final String taskId,
        final String taskExecutionId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(DELETE_MESSAGES_BY_PROCESS_AND_TASK_ID, tableName));
                statement.setString(1, processId);
                statement.setString(2, taskId);
                statement.setString(3, taskExecutionId);
                return statement.executeUpdate();
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<Integer> getRemoveOlderThanQuery(Date timestamp) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(DELETE_MESSAGES_OLDER_THAN, tableName));
                statement.setTimestamp(1, new Timestamp(timestamp.getTime()));
                return statement.executeUpdate();
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<Integer> getRemoveByProcessInstanceIdAndTaskIdQuery(final String processId, String taskId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement(getQuery(DELETE_MESSAGES_BY_PROCESS_ID_AND_TASK_ID, tableName));
                statement.setString(1, processId);
                statement.setString(2, taskId);
                return statement.executeUpdate();
            } finally {
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<List<ProgressMessage>> getFindAllQuery() {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                List<ProgressMessage> messages = new ArrayList<>();
                statement = connection.prepareStatement(getQuery(SELECT_ALL_MESSAGES, tableName));
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    messages.add(getMessage(resultSet));
                }
                return messages;
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<List<ProgressMessage>> getFindByProcessIdQuery(final String processId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                List<ProgressMessage> messages = new ArrayList<>();
                statement = connection.prepareStatement(getQuery(SELECT_MESSAGES_BY_PROCESS_ID, tableName));
                statement.setString(1, processId);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    messages.add(getMessage(resultSet));
                }
                return messages;
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<List<ProgressMessage>> getFindByProcessIdTaskIdAndTaskExecutionIdQuery(final String processId, final String taskId,
        final String taskExecutionId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                List<ProgressMessage> messages = new ArrayList<>();
                statement = connection.prepareStatement(getQuery(SELECT_MESSAGES_BY_PROCESS_AND_TASK_ID_AND_TASK_EXECUTION_ID, tableName));
                statement.setString(1, processId);
                statement.setString(2, taskId);
                statement.setString(3, taskExecutionId);
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    messages.add(getMessage(resultSet));
                }
                return messages;
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<String> getFindLastTaskExecutionIdQuery(final String processId, final String taskId) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                statement = connection.prepareStatement(getQuery(SELECT_MESSAGES_BY_PROCESS_AND_TASK_ID, tableName));
                statement.setString(1, processId);
                statement.setString(2, taskId);
                resultSet = statement.executeQuery();
                if (resultSet.next()) {
                    return resultSet.getString(COLUMN_NAME_TASK_EXECUTION_ID);
                }
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
            return null;
        };
    }

    public SqlQuery<List<ProgressMessage>> getFindByProcessIdTaskIdTaskExecutionIdAndTypeQuery(final String processId, final String taskId,
        final String taskExecutionId, final ProgressMessageType type) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                List<ProgressMessage> messages = new ArrayList<>();
                statement = connection.prepareStatement(getQuery(SELECT_SPECIFIC_MESSAGE, tableName));
                statement.setString(1, processId);
                statement.setString(2, taskId);
                statement.setString(3, taskExecutionId);
                statement.setString(4, type.name());
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    messages.add(getMessage(resultSet));
                }
                return messages;
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    public SqlQuery<List<ProgressMessage>> getFindByProcessidAndTypeQuery(final String processInstanceId, final ProgressMessageType type) {
        return (Connection connection) -> {
            PreparedStatement statement = null;
            ResultSet resultSet = null;
            try {
                List<ProgressMessage> messages = new ArrayList<>();
                statement = connection.prepareStatement(getQuery(SELECT_MESSAGES_BY_PROCESS_ID_AND_TYPE, tableName));
                statement.setString(1, processInstanceId);
                statement.setString(2, type.name());
                resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    messages.add(getMessage(resultSet));
                }
                return messages;
            } finally {
                JdbcUtil.closeQuietly(resultSet);
                JdbcUtil.closeQuietly(statement);
            }
        };
    }

    private ProgressMessage getMessage(ResultSet resultSet) throws SQLException {
        ProgressMessage message = new ProgressMessage();
        message.setId(resultSet.getLong(COLUMN_NAME_ID));
        message.setProcessId(resultSet.getString(COLUMN_NAME_PROCESS_ID));
        message.setTaskId(resultSet.getString(COLUMN_NAME_TASK_ID));
        message.setTaskExecutionId(resultSet.getString(COLUMN_NAME_TASK_EXECUTION_ID));
        message.setText(resultSet.getString(COLUMN_NAME_TEXT));
        message.setType(ProgressMessageType.valueOf(resultSet.getString(COLUMN_NAME_TYPE)));
        Timestamp dbTimestamp = resultSet.getTimestamp(COLUMN_NAME_TIMESTAMP);
        Date timestamp = (dbTimestamp == null) ? new Date() : new Date(dbTimestamp.getTime());
        message.setTimestamp(timestamp);
        return message;
    }

    private String getQuery(String statementTemplate, String tableName) {
        return String.format(statementTemplate, tableName, getDataSourceDialect().getSequenceNextValueSyntax(ID_SEQ_NAME));
    }

    protected DataSourceDialect getDataSourceDialect() {
        return dataSourceWithDialect.getDataSourceDialect();
    }
}
