package cn.hyrkg.fastspigot3.spigot.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
    /**
     * 浏览所有包含的文件。如果传入文件，返回自己，如果传入文件夹，返回文件夹中的文件。
     */
    public static List<File> flatFile(File file) {
        List<File> result = new ArrayList<>();

        if (file == null || !file.exists()) {
            return result;
        }

        if (file.isFile()) {
            // 如果是文件，返回包含该文件的列表
            result.add(file);
        } else if (file.isDirectory()) {
            // 如果是文件夹，递归遍历所有子文件和子文件夹
            File[] files = file.listFiles();
            if (files != null) {
                for (File subFile : files) {
                    // 递归调用，获取所有子文件
                    result.addAll(flatFile(subFile));
                }
            }
        }

        return result;
    }

    public static List<File> sortFilesByLeadingNumber(List<File> files) {
        files.sort((f1, f2) -> {
            String name1 = f1.getName();
            String name2 = f2.getName();

            // 提取文件名开头的数字
            Integer num1 = extractLeadingNumber(name1);
            Integer num2 = extractLeadingNumber(name2);

            // 如果两个文件都有开头数字，按数字排序
            if (num1 != null && num2 != null) {
                return num1.compareTo(num2);
            }
            // 如果只有第一个文件有数字，第一个文件排在前面
            else if (num1 != null && num2 == null) {
                return -1;
            }
            // 如果只有第二个文件有数字，第二个文件排在前面
            else if (num1 == null && num2 != null) {
                return 1;
            }
            // 如果都没有数字，按文件名字母顺序排序
            else {
                return name1.compareTo(name2);
            }
        });
        return files;
    }

    /**
     * 提取文件名开头的数字
     *
     * @param fileName 文件名
     * @return 开头的数字，如果没有则返回null
     */
    public static Integer extractLeadingNumber(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return null;
        }

        StringBuilder numberStr = new StringBuilder();
        for (char c : fileName.toCharArray()) {
            if (Character.isDigit(c)) {
                numberStr.append(c);
            } else {
                break;
            }
        }

        if (numberStr.length() > 0) {
            try {
                return Integer.parseInt(numberStr.toString());
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }
}
