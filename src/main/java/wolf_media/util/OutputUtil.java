package main.java.wolf_media.util;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;

/**
 * Output utility for printing ResultSets as tables
 * 
 * @author John Fagan
 *
 */
public class OutputUtil {
    
    enum Align {
        LEFT,
        RIGHT
    }
    
    private static String leftPad(String str, int totalLength, char padCharacter) {
        if (str == null) {
            return "";
        }
        if (str.length() >= totalLength) {
            return str;
        }
        StringBuilder sb = new StringBuilder();
        int padSize = totalLength - str.length();
        for (int i = 0; i < padSize; i++) {
            sb.append(padCharacter);
        }
        sb.append(str);
        return sb.toString();
    }
    
    private static String leftPad(String str, int totalLength) {
        return leftPad(str, totalLength, ' ');
    }
    
    private static String rightPad(String str, int totalLength, char padCharacter) {
        if (str == null) {
            return "";
        }
        if (str.length() >= totalLength) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int padSize = totalLength - str.length();
        for (int i = 0; i < padSize; i++) {
            sb.append(padCharacter);
        }
        return sb.toString();
    }
    
    private static String rightPad(String str, int totalLength) {
        return rightPad(str, totalLength, ' ');
    }
    
    private static ArrayList<Integer> getColumnWidths(ArrayList<ArrayList<String>> columns, ArrayList<String> columnHeaders) throws Exception {
        if (columns.size() != columnHeaders.size()) {
            throw new Exception("Columns and column headers count must be equal");
        }
        ArrayList<Integer> columnWidths = new ArrayList<Integer>();
        for (int i = 0; i < columns.size(); i++) {
            int maxColWidth = columnHeaders.get(i).length();
            ArrayList<String> column = columns.get(i);
            for (int j = 0; j < column.size(); j++) {
                String colStr = column.get(j);
                if (colStr == null) {
                    colStr = "";
                }
                int curColWidth = 0;
                if (colStr != null) {
                    curColWidth = column.get(j).length();
                }
                if (curColWidth > maxColWidth) {
                    maxColWidth = curColWidth;
                }
            }
            columnWidths.add(i, maxColWidth);
        }
        return columnWidths;
    }
    
    private static String getHorizontalBorder(ArrayList<Integer> columnWidths) {
        if (columnWidths.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("+");
        for (int i = 0; i < columnWidths.size(); i++) {
            int cellSize = columnWidths.get(i);
            for (int j = 0; j < cellSize + 2; j++) {
                sb.append('-');
            }
            sb.append('+');
        }
        return sb.toString();
    }
    
    private static String getHeaderString(ArrayList<String> columnHeaders, ArrayList<Integer> columnWidths) throws Exception {
        if (columnHeaders.size() != columnWidths.size()) {
            throw new Exception("Column headers and column widths count must be equal");
        }
        if (columnHeaders.size() <= 0) {
            return "";
        }
        StringBuilder sb = new StringBuilder("|");
        for (int i = 0; i < columnHeaders.size(); i++) {
            String colName = columnHeaders.get(i);
            if (colName == null) {
                colName = "";
            }
            int colWidth = columnWidths.get(i);
            sb.append(' ');
            sb.append(rightPad(colName, colWidth));
            sb.append(" |");
        }
        return sb.toString();
    }
    
    private static ArrayList<ArrayList<String>> getColumnData(ResultSet resultSet) throws SQLException {
        ResultSetMetaData metadata = resultSet.getMetaData();
        int columnCount = metadata.getColumnCount();
        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
        for (int i = 0; i < columnCount; i++) {
            output.add(new ArrayList<String>());
        }
        while (resultSet.next()) {
            for (int i = 0; i < columnCount; i++) {
                String colValue = resultSet.getString(i + 1);
                if (colValue == null) {
                    colValue = "";
                }
                output.get(i).add(colValue);
            }
        }
        return output;
    }
    
    private static ArrayList<String> getColumnHeaders(ResultSetMetaData metadata) throws SQLException {
        int colCount = metadata.getColumnCount();
        ArrayList<String> output = new ArrayList<String>(colCount);
        for (int i = 1; i <= colCount; i++) {
            output.add(metadata.getColumnName(i));
        }
        return output;
    }
    
    private static ArrayList<Align> getColumnAlignments(ResultSetMetaData metadata) throws SQLException {
        int colCount = metadata.getColumnCount();
        ArrayList<Align> output = new ArrayList<Align>(colCount);
        for (int i = 1; i <= colCount; i++) {
            int type = metadata.getColumnType(i);
            switch (type) {
            case Types.BIGINT:
            case Types.BIT:
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.REAL:
            case Types.SMALLINT:
            case Types.TINYINT:
                output.add(Align.RIGHT);
                break;
            default:
                output.add(Align.LEFT);
                break;
            }
        }
        return output;
    }
    
    public static void printResultSet(ResultSet resultSet) throws Exception {
        ResultSetMetaData metadata = resultSet.getMetaData();
        ArrayList<String> colHeaders = getColumnHeaders(metadata);
        ArrayList<ArrayList<String>> colData = getColumnData(resultSet);
        ArrayList<Align> colAligns = getColumnAlignments(metadata);
        ArrayList<Integer> columnWidths = getColumnWidths(colData, colHeaders);
        
        String horizontalBorder = getHorizontalBorder(columnWidths);
        String header = getHeaderString(colHeaders, columnWidths);
        StringBuilder output = new StringBuilder();
        output.append(horizontalBorder);
        output.append('\n');
        output.append(header);
        output.append('\n');
        output.append(horizontalBorder);
        output.append('\n');
        int tableLength = colData.get(0).size();
        for (int rowIndex = 0; rowIndex < tableLength; rowIndex++) {
            output.append('|');
            for (int colIndex = 0; colIndex < colData.size(); colIndex++) {
                String colValue = colData.get(colIndex).get(rowIndex);
                if (colValue == null) {
                    colValue = "";
                }
                int colWidth = columnWidths.get(colIndex);
                Align colAlign = colAligns.get(colIndex);
                output.append(' ');
                if (colAlign == Align.RIGHT) {
                    output.append(leftPad(colValue, colWidth));
                } else {
                    output.append(rightPad(colValue, colWidth));
                }
                output.append(" |");
            }
            output.append('\n');
        }
        output.append(horizontalBorder);
        System.out.println(output.toString());
    }
}
