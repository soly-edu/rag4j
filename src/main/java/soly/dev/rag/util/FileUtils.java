package soly.dev.rag.util;

public class FileUtils {

    private FileUtils() {
        /* This utility class should not be instantiated */
    }


    public static String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf(".");
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex + 1).toLowerCase();
    }

    public static boolean isValidFileExtension(String fileExtension) {
        return switch (fileExtension) {
            case "doc", "txt", "md" -> true;
            default -> false;
        };
    }
}
