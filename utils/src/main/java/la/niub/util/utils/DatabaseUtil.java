
package la.niub.util.utils;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DatabaseUtil {

    public static class Param {

        public static final String NOT_NULL = "NOT NULL";
        public static final String DEFAULT = "DEFAULT";
        public static final String PRIMARY_KEY = "PRIMARY KEY";
        public static final String AUTOINCREMENT = "AUTOINCREMENT";
        private static final String DEFAULT_FORMAT = "DEFAULT %s";

        public static final String defaultValue(String defaultValue) {
            return String.format(DEFAULT_FORMAT, defaultValue);
        }

    }

    public static class DataType {
        /**
         * 数据表中的常用属性值
         */
        public static final String BIGINT = "BIGINT";
        public static final String BLOB = "BLOB";
        public static final String BOOLEAN = "BOOLEAN";
        public static final String INTEGER = "INTEGER";
        public static final String TEXT = "TEXT";
        public static final String DOUBLE = "DOUBLE";

    }

    /**
     * 创建数据库表
     * 
     * @param db
     * @param table
     * @param map 存放字段名，和字段属性
     */
    public static class TableSqlBuilder {

        /**
         * 创建表sql语句
         */
        private static final String CREATE_TABLE_FORMAT = "CREATE TABLE IF NOT EXISTS %s (%s)";

        private final String mTableName;
        private final StringBuilder mBuilder;

        public TableSqlBuilder(String tableName) {
            mTableName = tableName;
            mBuilder = new StringBuilder();
        }

        public TableSqlBuilder addColumn(String columnName, String dataType, String... params) {
            if (mBuilder.length() > 0) {
                mBuilder.append(',');
            }
            mBuilder.append(columnName);
            mBuilder.append(' ');
            mBuilder.append(dataType);
            if (params.length > 0) {
                mBuilder.append(' ');
                for (String param : params) {
                    mBuilder.append(param);
                    mBuilder.append(' ');
                }
            }
            return this;
        }

        public String buildSql() {
            String text = mBuilder.toString();
            if (TextUtils.isEmpty(text)) {
                throw new IllegalArgumentException("can you add column?");
            }
            return String.format(CREATE_TABLE_FORMAT, mTableName, text);
        }
    }

    /**
     * Add a column to a table using ALTER TABLE.
     * 
     * @param dbTable name of the table
     * @param columnName name of the column to add
     * @param columnDefinition SQL for the column definition
     */
    public static String addColumnSql(String dbTable, String columnName,
            String columnDefinition) {
        return "ALTER TABLE " + dbTable + " ADD COLUMN " + columnName + " " + columnDefinition;
    }

    public static String dropTableSql(String dbTable) {
        return "DROP TABLE IF EXISTS " + dbTable;
    }

    public static String fullColumns(String table, String columnName) {
        return String.format("%s.%s", table, columnName);
    }

    /**
     * 判断某张表是否存在
     * 
     * @param tabName 表名
     * @return
     */
    public static boolean isTableExist(SQLiteDatabase db, String tableName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='"
                    + tableName.trim() + "'";
            Cursor cursor = db.rawQuery(sql, null);
            if (null != cursor) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * 判断某张表中是否存在某字段(注，该方法无法判断表是否存在，因此应与isTableExist一起使用)
     * 
     * @param tabName 表名
     * @param columnName 列名
     * @return
     */
    public boolean isColumnExist(SQLiteDatabase db, String tableName, String columnName) {
        boolean result = false;
        if (tableName == null) {
            return false;
        }
        try {
            String sql = "select count(1) as c from sqlite_master where type ='table' and name ='"
                    + tableName.trim() + "' and sql like '%" + columnName.trim() + "%'";
            Cursor cursor = db.rawQuery(sql, null);
            if (null != cursor) {
                if (cursor.moveToNext()) {
                    int count = cursor.getInt(0);
                    if (count > 0) {
                        result = true;
                    }
                }
                cursor.close();
            }
        } catch (Exception e) {
        }
        return result;
    }
}
