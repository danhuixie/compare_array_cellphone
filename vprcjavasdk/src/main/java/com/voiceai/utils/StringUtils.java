package com.voiceai.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串操作工具类
 *
 * @author dongyangyang
 * @Date 2016/12/28 23:12
 * @Version 1.0
 */
public class StringUtils {

    /**
     * 　　DNA分析 　　拼字检查 　　语音辨识 　　抄袭侦测
     *
     * @createTime 2012-1-12
     */
    public static double levenshtein(String str1, String str2) {
        //计算两个字符串的长度。
        int len1 = str1.length();
        int len2 = str2.length();
        //建立上面说的数组，比字符长度大一个空间
        int[][] dif = new int[len1 + 1][len2 + 1];
        //赋初值，步骤B。
        for (int a = 0; a <= len1; a++) {
            dif[a][0] = a;
        }
        for (int a = 0; a <= len2; a++) {
            dif[0][a] = a;
        }
        //计算两个字符是否一样，计算左上的值
        int temp;
        for (int i = 1; i <= len1; i++) {
            for (int j = 1; j <= len2; j++) {
                if (str1.charAt(i - 1) == str2.charAt(j - 1)) {
                    temp = 0;
                } else {
                    temp = 1;
                }
                //取三个值中最小的
                dif[i][j] = min(dif[i - 1][j - 1] + temp, dif[i][j - 1] + 1,
                        dif[i - 1][j] + 1);
            }
        }
        System.out.println("字符串\"" + str1 + "\"与\"" + str2 + "\"的比较");
        //取数组右下角的值，同样不同位置代表不同字符串的比较
        System.out.println("差异步骤：" + dif[len1][len2]);
        //计算相似度
        double similarity = 1 - (float) dif[len1][len2] / Math.max(str1.length(), str2.length());
        return similarity;
    }

    //得到最小值
    private static int min(int... is) {
        int min = Integer.MAX_VALUE;
        for (int i : is) {
            if (min > i) {
                min = i;
            }
        }
        return min;
    }

    /**
     * 首字母变小写
     *
     * @param str
     * @return
     */
    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] += ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    public static void main(String[] a) {
        String i = "woshi发货淡蓝色的建安费";
        String re = setHtmlimage(3, "gg", i);
        re = setHtmlimage(5, "gg", re);
        System.out.println(re);
    }

    public static String[] spiltsp(String string, String regex) {
        String[] re = string.split(regex);
        String[] groups = new String[re.length];
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        for (int i = 0; matcher.find(); i++) {
            groups[i] = matcher.group();
        }
        for (int i = 0; i < re.length; i++) {
            re[i] += (groups[i] == null ? "" : groups[i]);
        }
        return re;
    }

    public static String setHtmlimage(int star, String image, String text) {
        StringBuilder re = new StringBuilder();
        String regex = "<img src.*?>";
        String[] oop = text.split(regex);
        String[] oop2 = StringUtils.spiltsp(text, regex);
        int i = 0, p = star;
        for (int j = 0; j < oop.length; j++) {
            if (p > oop[j].length()) {
                i++;
                p = p - oop[j].length();
            } else {
                break;
            }
        }
        StringBuilder re2 = new StringBuilder(oop2[i]);
        re2.insert(p, "<img src='" + image + "'/> ");
        oop2[i] = re2.toString();
        for (int j = 0; j < oop2.length; j++) {
            re.append(oop2[j]);
        }
        return re.toString();
    }

    /**
     * 首字母变大写
     *
     * @param str
     * @return
     */
    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] -= ('a' - 'A');
            return new String(arr);
        }
        return str;
    }

    /**
     * 判断是否为空
     *
     * @param str
     * @return
     */
    public static boolean isEmpty(final String str) {
        return (str == null) || (str.length() == 0);
    }

    /**
     * 判断是否不为空
     *
     * @param str
     * @return
     */
    public static boolean isNotEmpty(final String str) {
        return !isEmpty(str);
    }

    /**
     * 判断是否空白
     *
     * @param str
     * @return
     */
    public static boolean isBlank(final String str) {
        int strLen;
        if ((str == null) || ((strLen = str.length()) == 0))
            return true;
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断是否不是空白
     *
     * @param str
     * @return
     */
    public static boolean isNotBlank(final String str) {
        return !isBlank(str);
    }

    /**
     * 判断多个字符串全部是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isAllEmpty(String... strings) {
        if (strings == null) {
            return true;
        }
        for (String str : strings) {
            if (isNotEmpty(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断多个字符串其中任意一个是否为空
     *
     * @param strings
     * @return
     */
    public static boolean isHasEmpty(String... strings) {
        if (strings == null) {
            return true;
        }
        for (String str : strings) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }

    /**
     * checkValue为 null 或者为 "" 时返回 defaultValue
     *
     * @param checkValue
     * @param defaultValue
     * @return
     */
    public static String isEmpty(String checkValue, String defaultValue) {
        return isEmpty(checkValue) ? defaultValue : checkValue;
    }

    /**
     * 字符串不为 null 而且不为 "" 并且等于other
     *
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEmptyAndEquelsOther(String str, String other) {
        if (isEmpty(str)) {
            return false;
        }
        return str.equals(other);
    }

    public static boolean isNumeric(Object obj) {
        if (obj == null) {
            return false;
        } else {
            char[] chars = obj.toString().toCharArray();
            int length = chars.length;
            if (length < 1) {
                return false;
            } else {
                int i = 0;
                if (length > 1 && chars[0] == '-') {
                    i = 1;
                }

                while(i < length) {
                    if (!Character.isDigit(chars[i])) {
                        return false;
                    }

                    ++i;
                }

                return true;
            }
        }
    }

    public static boolean areNotEmpty(String... values) {
        boolean result = true;
        if (values != null && values.length != 0) {
            String[] var2 = values;
            int var3 = values.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String value = var2[var4];
                result &= !isEmpty(value);
            }
        } else {
            result = false;
        }

        return result;
    }

    public static String unicodeToChinese(String unicode) {
        StringBuilder out = new StringBuilder();
        if (!isEmpty(unicode)) {
            for(int i = 0; i < unicode.length(); ++i) {
                out.append(unicode.charAt(i));
            }
        }

        return out.toString();
    }

    public static String stripNonValidXMLCharacters(String input) {
        if (input != null && !"".equals(input)) {
            StringBuilder out = new StringBuilder();

            for(int i = 0; i < input.length(); ++i) {
                char current = input.charAt(i);
                if (current == '\t' || current == '\n' || current == '\r' || current >= ' ' && current <= '\ud7ff' || current >= '\ue000' && current <= '�' || current >= 65536 && current <= 1114111) {
                    out.append(current);
                }
            }

            return out.toString();
        } else {
            return "";
        }
    }

    public static String FilterNull(Object o) {
        return o != null && !"null".equals(o.toString()) ? o.toString().trim() : "";
    }

    public static boolean isEmpty(Object o) {
        if (o == null) {
            return true;
        } else {
            return "".equals(FilterNull(o.toString()));
        }
    }

    public static boolean isNotEmpty(Object o) {
        if (o == null) {
            return false;
        } else {
            return !"".equals(FilterNull(o.toString()));
        }
    }

    public static boolean isNum(Object o) {
        try {
            new BigDecimal(o.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public static boolean isNumeric(String str) {
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

    public static boolean isLong(Object o) {
        try {
            new Long(o.toString());
            return true;
        } catch (Exception var2) {
            return false;
        }
    }

    public static Long toLong(Object o) {
        return isLong(o) ? new Long(o.toString()) : 0L;
    }

    public static int toInt(Object o) {
        return isNum(o) ? new Integer(o.toString()) : 0;
    }

    public static String holdmaxlength(Object o) {
        int maxlength = 50;
        return o == null ? "" : subStringByByte(o, maxlength);
    }

    public static String holdmaxlength(Object o, int maxlength) {
        maxlength = maxlength <= 0 ? 50 : maxlength;
        return o == null ? "" : subStringByByte(o, maxlength);
    }

    public static String subStringByByte(Object o, int len) {
        if (o == null) {
            return "";
        } else {
            String str = o.toString();
            String result = null;
            if (str != null) {
                byte[] a = str.getBytes();
                if (a.length <= len) {
                    result = str;
                } else if (len > 0) {
                    result = new String(a, 0, len);
                    int length = result.length();
                    if (str.charAt(length - 1) != result.charAt(length - 1)) {
                        if (length < 2) {
                            result = null;
                        } else {
                            result = result.substring(0, length - 1);
                        }
                    }
                }
            }

            return result;
        }
    }

    public static String comma_add(String commaexpress, String newelement) {
        return comma_rect(FilterNull(commaexpress) + "," + FilterNull(newelement));
    }

    public static String comma_del(String commaexpress, String delelement) {
        if (commaexpress != null && delelement != null && !commaexpress.trim().equals(delelement.trim())) {
            String[] deletelist = delelement.split(",");
            String result = commaexpress;
            String[] var4 = deletelist;
            int var5 = deletelist.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String delstr = var4[var6];
                result = comma_delone(result, delstr);
            }

            return result;
        } else {
            return "";
        }
    }

    public static String comma_delone(String commaexpress, String delelement) {
        if (commaexpress != null && delelement != null && !commaexpress.trim().equals(delelement.trim())) {
            String[] strlist = commaexpress.split(",");
            StringBuffer result = new StringBuffer();
            String[] var4 = strlist;
            int var5 = strlist.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String str = var4[var6];
                if (!str.trim().equals(delelement.trim()) && !"".equals(str.trim())) {
                    result.append(str.trim() + ",");
                }
            }

            return result.toString().substring(0, result.length() - 1 > 0 ? result.length() - 1 : 0);
        } else {
            return "";
        }
    }

    public static boolean comma_contains(String commaexpress, String element) {
        boolean flag = false;
        commaexpress = FilterNull(commaexpress);
        element = FilterNull(element);
        if (!"".equals(commaexpress) && !"".equals(element)) {
            String[] strlist = commaexpress.split(",");
            String[] var4 = strlist;
            int var5 = strlist.length;

            for(int var6 = 0; var6 < var5; ++var6) {
                String str = var4[var6];
                if (str.trim().equals(element.trim())) {
                    flag = true;
                    break;
                }
            }
        }

        return flag;
    }

    public static String comma_intersect(String commaexpressA, String commaexpressB) {
        commaexpressA = FilterNull(commaexpressA);
        commaexpressB = FilterNull(commaexpressB);
        StringBuffer result = new StringBuffer();
        String[] strlistA = commaexpressA.split(",");
        String[] strlistB = commaexpressB.split(",");
        String[] var5 = strlistA;
        int var6 = strlistA.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            String boA = var5[var7];
            String[] var9 = strlistB;
            int var10 = strlistB.length;

            for(int var11 = 0; var11 < var10; ++var11) {
                String boB = var9[var11];
                if (boA.trim().equals(boB.trim())) {
                    result.append(boA.trim() + ",");
                }
            }
        }

        return comma_rect(result.toString());
    }

    public static String comma_rect(String commaexpress) {
        commaexpress = FilterNull(commaexpress);
        String[] strlist = commaexpress.split(",");
        StringBuffer result = new StringBuffer();
        String[] var3 = strlist;
        int var4 = strlist.length;

        for(int var5 = 0; var5 < var4; ++var5) {
            String str = var3[var5];
            if (!"".equals(str.trim()) && !("," + result.toString() + ",").contains("," + str + ",") && !"null".equals(str)) {
                result.append(str.trim() + ",");
            }
        }

        return result.toString().substring(0, result.length() - 1 > 0 ? result.length() - 1 : 0);
    }

    public static String comma_reverse(String commaexpress) {
        commaexpress = FilterNull(commaexpress);
        String[] ids = commaexpress.split(",");
        StringBuffer str = new StringBuffer();

        for(int i = ids.length - 1; i >= 0; --i) {
            str.append(ids[i] + ",");
        }

        return comma_rect(str.toString());
    }

    public static String comma_first(String commaexpress) {
        commaexpress = FilterNull(commaexpress);
        String[] ids = commaexpress.split(",");
        System.out.println("length:" + ids.length);
        return ids != null && ids.length > 0 ? ids[0] : null;
    }

    public static String comma_last(String commaexpress) {
        commaexpress = FilterNull(commaexpress);
        String[] ids = commaexpress.split(",");
        return ids != null && ids.length > 0 ? ids[ids.length - 1] : null;
    }

    public static String replace(String strData, String regex, String replacement) {
        return strData == null ? "" : strData.replaceAll(regex, replacement);
    }

    public static String String2HTML(String strData) {
        if (strData != null && !"".equals(strData)) {
            strData = replace(strData, "&", "&amp;");
            strData = replace(strData, "<", "&lt;");
            strData = replace(strData, ">", "&gt;");
            strData = replace(strData, "\"", "&quot;");
            return strData;
        } else {
            return "";
        }
    }

    public static String getexceptionInfo(Exception e) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        try {
            e.printStackTrace(new PrintStream(baos));
        } finally {
            try {
                baos.close();
            } catch (IOException var8) {
                var8.printStackTrace();
            }

        }

        return baos.toString();
    }

    public static String regex(String str) {
        Pattern pattern = Pattern.compile("[0-9-:/ ]");
        char[] array = str.toCharArray();

        for(int i = 0; i < array.length; ++i) {
            Matcher matcher = pattern.matcher(String.valueOf(array[i]));
            if (!matcher.matches()) {
                str = str.replace(String.valueOf(array[i]), "");
            }
        }

        return str;
    }

    public static String comma_insert(String commaexpress, String newelement, int index) {
        int length = commaexpress.length();
        if (index > length) {
            index = length;
        } else if (index < 0) {
            index = 0;
        }

        String result = commaexpress.substring(0, index) + newelement + commaexpress.substring(index, commaexpress.length());
        return result;
    }

    public static String changeDirection(String strDir) {
        String s = "/";
        String a = "\\";
        if (strDir != null && !" ".equals(strDir) && strDir.contains(s)) {
            strDir = strDir.replace(s, a);
        }

        return strDir;
    }

    public static String trim(String s) {
        int i = s.length();
        int j = 0;
        int k = 0;

        char[] arrayOfChar;
        for(arrayOfChar = s.toCharArray(); j < i && arrayOfChar[k + j] <= ' '; ++j) {
            ;
        }

        while(j < i && arrayOfChar[k + i - 1] <= ' ') {
            --i;
        }

        return j <= 0 && i >= s.length() ? s : s.substring(j, i);
    }

    public static String getBrackets(String str) {
        int a = str.indexOf("{");
        int c = str.indexOf("}");
        return a >= 0 && c >= 0 & c > a ? str.substring(a + 1, c) : str;
    }

    public static String commaToVerti(String str) {
        return str != null && !"".equals(str) && str.contains(",") ? str.replaceAll(",", "|") : str;
    }

    public static String extractBlank(String name) {
        return name != null && !"".equals(name) ? name.replaceAll(" +", "") : name;
    }

    public static String ConvertStr(String str) {
        return str != null && !"null".equals(str) ? str.trim() : "";
    }

    /**
     * 字符串不为 null 而且不为 "" 并且不等于other
     *
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEmptyAndNotEquelsOther(String str, String... other) {
        if (isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < other.length; i++) {
            if (str.equals(other[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 字符串不等于other
     *
     * @param str
     * @param other
     * @return
     */
    public static boolean isNotEquelsOther(String str, String... other) {
        for (int i = 0; i < other.length; i++) {
            if (other[i].equals(str)) {
                return false;
            }
        }
        return true;
    }

    /**
     * 判断字符串不为空
     *
     * @param strings
     * @return
     */
    public static boolean isNotEmpty(String... strings) {
        if (strings == null) {
            return false;
        }
        for (String str : strings) {
            if (str == null || "".equals(str.trim())) {
                return false;
            }
        }
        return true;
    }

    /**
     * 比较字符相等
     *
     * @param value
     * @param equals
     * @return
     */
    public static boolean equals(String value, String equals) {
        if (isAllEmpty(value, equals)) {
            return true;
        }
        return value.equals(equals);
    }

    /**
     * 比较字符串不相等
     *
     * @param value
     * @param equals
     * @return
     */
    public static boolean isNotEquals(String value, String equals) {
        return !equals(value, equals);
    }

    public static String[] split(String content, String separatorChars) {
        return splitWorker(content, separatorChars, -1, false);
    }

    public static String[] split(String str, String separatorChars, int max) {
        return splitWorker(str, separatorChars, max, false);
    }

    public static final String[] EMPTY_STRING_ARRAY = new String[0];

    private static String[] splitWorker(String str, String separatorChars, int max, boolean preserveAllTokens) {
        if (str == null) {
            return null;
        }
        int len = str.length();
        if (len == 0) {
            return EMPTY_STRING_ARRAY;
        }
        List<String> list = new ArrayList<String>();
        int sizePlus1 = 1;
        int i = 0, start = 0;
        boolean match = false;
        boolean lastMatch = false;
        if (separatorChars == null) {
            while (i < len) {
                if (Character.isWhitespace(str.charAt(i))) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else if (separatorChars.length() == 1) {
            char sep = separatorChars.charAt(0);
            while (i < len) {
                if (str.charAt(i) == sep) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        } else {
            while (i < len) {
                if (separatorChars.indexOf(str.charAt(i)) >= 0) {
                    if (match || preserveAllTokens) {
                        lastMatch = true;
                        if (sizePlus1++ == max) {
                            i = len;
                            lastMatch = false;
                        }
                        list.add(str.substring(start, i));
                        match = false;
                    }
                    start = ++i;
                    continue;
                }
                lastMatch = false;
                match = true;
                i++;
            }
        }
        if (match || (preserveAllTokens && lastMatch)) {
            list.add(str.substring(start, i));
        }
        return (String[]) list.toArray(EMPTY_STRING_ARRAY);
    }

    /**
     * 消除转义字符
     *
     * @param str
     * @return
     */
    public static String escapeXML(String str) {
        if (str == null)
            return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); ++i) {
            char c = str.charAt(i);
            switch (c) {
                case '\u00FF':
                case '\u0024':
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
                    if (c >= '\u0000' && c <= '\u001F')
                        break;
                    if (c >= '\uE000' && c <= '\uF8FF')
                        break;
                    if (c >= '\uFFF0' && c <= '\uFFFF')
                        break;
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    /**
     * 将字符串中特定模式的字符转换成map中对应的值
     *
     * @param s   需要转换的字符串
     * @param map 转换所需的键值对集合
     * @return 转换后的字符串
     */
    public static String replace(String s, Map<String, Object> map) {
        StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
        int cursor = 0;
        for (int start, end; (start = s.indexOf("${", cursor)) != -1 && (end = s.indexOf("}", start)) != -1; ) {
            ret.append(s.substring(cursor, start)).append(map.get(s.substring(start + 2, end)));
            cursor = end + 1;
        }
        ret.append(s.substring(cursor, s.length()));
        return ret.toString();
    }

    public static String replace(String s, Object... objs) {
        if (objs == null || objs.length == 0)
            return s;
        if (s.indexOf("{}") == -1)
            return s;
        StringBuilder ret = new StringBuilder((int) (s.length() * 1.5));
        int cursor = 0;
        int index = 0;
        for (int start; (start = s.indexOf("{}", cursor)) != -1; ) {
            ret.append(s.substring(cursor, start));
            if (index < objs.length)
                ret.append(objs[index]);
            else
                ret.append("{}");
            cursor = start + 2;
            index++;
        }
        ret.append(s.substring(cursor, s.length()));
        return ret.toString();
    }

    /**
     * 字符串格式化工具,参数必须以{0}之类的样式标示出来.大括号中的数字从0开始。
     *
     * @param source 源字符串
     * @param params 需要替换的参数列表,写入时会调用每个参数的toString().
     * @return 替换完成的字符串。如果原始字符串为空或者参数为空那么将直接返回原始字符串。
     */
    public static String replaceArgs(String source, Object... params) {
        if (params == null || params.length == 0 || source == null || source.isEmpty()) {
            return source;
        }
        StringBuilder buff = new StringBuilder(source);
        StringBuilder temp = new StringBuilder();
        int startIndex = 0;
        int endIndex = 0;
        String param = null;
        for (int count = 0; count < params.length; count++) {
            if (params[count] == null) {
                param = null;
            } else {
                param = params[count].toString();
            }

            temp.delete(0, temp.length());
            temp.append("{");
            temp.append(count);
            temp.append("}");
            while (true) {
                startIndex = buff.indexOf(temp.toString(), endIndex);
                if (startIndex == -1) {
                    break;
                }
                endIndex = startIndex + temp.length();

                buff.replace(startIndex, endIndex, param == null ? "" : param);
            }
            startIndex = 0;
            endIndex = 0;
        }
        return buff.toString();
    }

    public static String substringBefore(final String s, final String separator) {
        if (isEmpty(s) || separator == null) {
            return s;
        }
        if (separator.isEmpty()) {
            return "";
        }
        final int pos = s.indexOf(separator);
        if (pos < 0) {
            return s;
        }
        return s.substring(0, pos);
    }

    public static String substringBetween(final String str, final String open, final String close) {
        if (str == null || open == null || close == null) {
            return null;
        }
        final int start = str.indexOf(open);
        if (start != -1) {
            final int end = str.indexOf(close, start + open.length());
            if (end != -1) {
                return str.substring(start + open.length(), end);
            }
        }
        return null;
    }

    public static String substringAfter(final String str, final String separator) {
        if (isEmpty(str)) {
            return str;
        }
        if (separator == null) {
            return "";
        }
        final int pos = str.indexOf(separator);
        if (pos == -1) {
            return "";
        }
        return str.substring(pos + separator.length());
    }

    /**
     * 转换为字节数组
     *
     * @param
     * @return
     */
    public static String toString(byte[] bytes) {
        try {
            return new String(bytes, "utf-8");
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    /**
     * 转换为字节数组
     *
     * @param str
     * @return
     */
    public static byte[] getBytes(String str) {
        if (str != null) {
            try {
                return str.getBytes("utf-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        } else {
            return null;
        }
    }


}