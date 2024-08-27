package com.example.myapplication.utils;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataUtil {
    public static String extractMacAddress(String input) {
        if (input == null || input.trim().isEmpty()) {
            return null; // 输入字符串为空或只包含空白字符
        }

        // 匹配MAC地址的正则表达式
        String macAddressRegex = "([0-9A-Fa-f]{2}:){5}[0-9A-Fa-f]{2}";

        // 使用正则表达式提取MAC地址
        Pattern pattern = Pattern.compile(macAddressRegex);
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group();
        }

        return null; // 没有找到匹配的MAC地址
    }

    public static byte[] generateRandomByteArray(int length) {
        // 创建一个长度为length的byte数组
        byte[] randomBytes = new byte[length];

        // 创建一个Random对象
        Random random = new Random();

        // 使用Random对象填充byte数组
        random.nextBytes(randomBytes);

        // 返回生成的随机字节数组
        return randomBytes;
    }

    /**
     * 生成四位长度的随机码
     * @param 
     * @return
     */
    public static String generateRandomCode() {
        // 生成一个包含所有可能字符的字符串
        String characters = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // 生成一个四位数的字符数组
        char[] code = new char[4];

        Random random = new Random();
        // 循环四次，每次从字符串中随机选择一个字符
        for (int i = 0; i < 4; i++) {
            int index = random.nextInt(characters.length());
            code[i] = characters.charAt(index);
        }

        // 将字符数组转换为字符串
        return new String(code);
    }

    // 使用正则表达式校验IPv4地址
    public static boolean isValidIP(String ip) {
        String ipPattern = "^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\\." +
                "(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$";

        Pattern pattern = Pattern.compile(ipPattern);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }

}
