package com.openjfx.database.mysql;

import com.openjfx.database.model.TableColumnMeta;

import java.util.List;
import java.util.stream.Collectors;

/**
 * SQL statement processing auxiliary class
 *
 * @author yangkui
 * @since 1.0
 */
public class SQLHelper {
    /**
     * Escape MySQL keyword conflict
     *
     * @param field field
     * @return escape after field
     */
    public static String escapeMysqlField(String field) {
        var array = field.split("\\.");
        var stringBuffer = new StringBuilder();
        for (int i = 0; i < array.length; i++) {
            var s = array[i];
            stringBuffer.append("`").append(s).append("`");
            if (i != array.length - 1) {
                stringBuffer.append(".");
            }
        }
        return stringBuffer.toString();
    }

    public static List<TableColumnMeta> getPrimaryKey(List<TableColumnMeta> tableColumnMetas) {
        return tableColumnMetas.stream().filter(TableColumnMeta::getPrimaryKey).collect(Collectors.toList());
    }
}
