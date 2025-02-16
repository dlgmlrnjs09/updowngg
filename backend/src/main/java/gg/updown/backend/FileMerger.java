package gg.updown.backend;

import java.io.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileMerger {
    private static final Set<String> SUPPORTED_EXTENSIONS = new HashSet<>(Arrays.asList(
            "txt", "log", "md", "yml", "yaml", "properties", "conf", "config", "xml", "json",
            "java", "kt", "groovy", "scala", "html", "css", "js", "jsx", "ts", "tsx", "vue", "php",
            "sh", "bash", "py", "rb", "perl", "sql", "gradle", "maven", "gitignore", "env"
    ));

    public static void mergeFiles(String inputFolderPath, String outputFilePath) {
        try {
            BufferedWriter writer = Files.newBufferedWriter(
                    Paths.get(outputFilePath),
                    StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING
            );

            final int[] processedFiles = {0};
            final int[] skippedFiles = {0};

            Files.walk(Paths.get(inputFolderPath))
                    .filter(Files::isRegularFile)
                    .forEach(filePath -> {
                        try {
                            if (shouldProcessFile(filePath) && isTextFile(filePath)) {
                                writeFileContent(writer, filePath);
                                processedFiles[0]++;
                            } else {
                                skippedFiles[0]++;
                            }
                        } catch (IOException e) {
                            skippedFiles[0]++;
                        }
                    });

            writer.write("\n총계: " + processedFiles[0] + " 처리, " + skippedFiles[0] + " 제외");
            writer.close();

        } catch (IOException e) {
            System.err.println("오류 발생: " + e.getMessage());
        }
    }

    private static void writeFileContent(BufferedWriter writer, Path filePath) throws IOException {
        writer.write("\n@ " + filePath + "\n");
        List<String> lines = Files.readAllLines(filePath, StandardCharsets.UTF_8);
        boolean lastLineWasEmpty = false;
        StringBuilder currentLine = new StringBuilder();

        for (String line : lines) {
            String processedLine = minimizeLine(line);

            if (processedLine.isEmpty()) {
                if (!lastLineWasEmpty) {
                    writer.write("\n");
                    lastLineWasEmpty = true;
                }
            } else {
                if (currentLine.length() > 0 &&
                        !processedLine.startsWith("}") &&
                        !processedLine.startsWith(")") &&
                        !processedLine.startsWith(".") &&
                        !processedLine.startsWith(",")) {
                    currentLine.append(" ");
                }
                currentLine.append(processedLine);

                if (processedLine.endsWith(";") ||
                        processedLine.endsWith("{") ||
                        processedLine.endsWith("}") ||
                        processedLine.endsWith(")")) {
                    writer.write(currentLine.toString() + "\n");
                    currentLine.setLength(0);
                    lastLineWasEmpty = false;
                }
            }
        }

        if (currentLine.length() > 0) {
            writer.write(currentLine.toString() + "\n");
        }
    }

    private static String minimizeLine(String line) {
        // 1. 주석 라인 전체 제거
        if (line.trim().startsWith("//") ||
                line.trim().startsWith("/*") ||
                line.trim().startsWith("*") ||
                line.contains("*/")) {
            return "";
        }

        // 2. 인라인 주석 제거
        int commentIdx = line.indexOf("//");
        if (commentIdx >= 0) {
            line = line.substring(0, commentIdx);
        }

        // 3. 어노테이션은 보존
        if (line.trim().startsWith("@")) {
            return line.trim();
        }

        // 4. XML/HTML 주석 제거
        if (line.contains("<!--") || line.contains("-->")) {
            return "";
        }

        // 5. 모든 연속된 공백을 단일 공백으로
        line = line.replaceAll("\\s+", " ");

        // 6. 줄 시작과 끝의 공백 제거
        line = line.trim();

        // 7. 빈 줄 제거
        if (line.isEmpty()) {
            return "";
        }

        return line;
    }

    private static boolean shouldProcessFile(Path filePath) {
        String extension = getFileExtension(filePath);
        if (extension.isEmpty()) {
            String fileName = filePath.getFileName().toString();
            return SUPPORTED_EXTENSIONS.contains(fileName);
        }
        return SUPPORTED_EXTENSIONS.contains(extension.toLowerCase());
    }

    private static String getFileExtension(Path filePath) {
        String fileName = filePath.getFileName().toString();
        int lastDotIndex = fileName.lastIndexOf('.');
        return (lastDotIndex == -1) ? "" : fileName.substring(lastDotIndex + 1);
    }

    private static boolean isTextFile(Path filePath) {
        try {
            if (Files.size(filePath) > 10_000_000) return false;
            byte[] bytes = Files.readAllBytes(filePath);
            if (bytes.length == 0) return true;

            int nullCount = 0;
            int totalBytes = Math.min(8192, bytes.length);

            for (int i = 0; i < totalBytes; i++) {
                if (bytes[i] == 0x00) {
                    nullCount++;
                    if (nullCount > totalBytes * 0.05) return false;
                }
            }

            CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
            decoder.onMalformedInput(CodingErrorAction.REPORT);
            decoder.onUnmappableCharacter(CodingErrorAction.REPORT);

            ByteArrayInputStream input = new ByteArrayInputStream(bytes);
            InputStreamReader reader = new InputStreamReader(input, decoder);
            char[] buffer = new char[1024];
            while (reader.read(buffer) != -1) {}

            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static void main(String[] args) {
        String type = "backend";
        String inputFolder = "C:\\java_project\\updowngg\\" + type + "\\src";
        String outputFile = "C:\\Users\\leeheekwon\\Desktop\\merged\\" + type + "_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm")) + ".txt";
        mergeFiles(inputFolder, outputFile);
    }
}