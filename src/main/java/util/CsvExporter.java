package util;

import java.io.FileWriter;
import java.util.List;

public class CsvExporter {

    public static void write(String path, List<String[]> rows) throws Exception {
        try (FileWriter fw = new FileWriter(path, false)) {

            for (String[] row : rows) {
                fw.write(escapeRow(row));
                fw.write("\n");
            }
        }
    }

    private static String escapeRow(String[] row) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < row.length; i++) {
            sb.append(escapeField(row[i]));
            if (i < row.length - 1) sb.append(",");
        }

        return sb.toString();
    }

    private static String escapeField(String field) {
        if (field == null) return "";

        boolean containsComma = field.contains(",");
        boolean containsQuote = field.contains("\"");


        if (containsQuote) {
            field = field.replace("\"", "\"\"");
        }

        if (containsComma || containsQuote) {
            return "\"" + field + "\"";
        }

        return field;
    }
}
