package ext.com.wanli.util;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class StringUtil {

    /**
     * Judge if the given string is empty or not.
     * 
     * @param str
     *            the given string
     * @return is empty or not
     */
    public static boolean isEmpty(String str) {
        boolean isEmpty = false;
        if (str == null || str.trim().length() <= 0) {
            isEmpty = true;
        }
        return isEmpty;
    }

    /**
     * Get list from tokens, method <code>split()</code> is not used here for
     * the reason that the argument is <code>Regular Expression</code>.
     * 
     * @param src
     *            string source
     * @param split
     *            split
     * @return list of tokens
     * @see java.util.List
     */
    public static List<String> getListFromTokens(String src, String split) {
        List<String> tokenList = null;
        if (!isEmpty(src) && !isEmpty(split)) {
            String[] strArray = src.split(split);
            tokenList = Arrays.asList(strArray);
        }
        return tokenList;
    }

    public static List quoteStrList(List list) {
        List tmpList = list;
        list = new ArrayList();
        Iterator i = tmpList.iterator();
        while (i.hasNext()) {
            String str = (String) i.next();
            str = "'" + str + "'";
            list.add(str);
        }
        return list;
    }

    public static String join(List list, String delim) {
        if (list == null || list.size() < 1) {
            return null;
        }
        StringBuffer buf = new StringBuffer();
        Iterator i = list.iterator();
        while (i.hasNext()) {
            buf.append((String) i.next());
            if (i.hasNext()) {
                buf.append(delim);
            }
        }
        return buf.toString();
    }

    public static String[] split(String str, String delim) {
        String[] splitList = null;

        if (str == null) {
            return splitList;
        }

        if (delim == null || delim.length() == 0 || str.indexOf(delim) < 0) {
            splitList = new String[1];
            splitList[0] = str;
            return splitList;
        }

        int i = 0;
        String strTemp = str;
        while (strTemp != null && strTemp.indexOf(delim) >= 0) {
            i++;
            if (strTemp.length() < (strTemp.indexOf(delim) + delim.length())) {
                break;
            }
            strTemp = strTemp.substring(strTemp.indexOf(delim) + delim.length(), strTemp.length());
        }

        splitList = new String[i + 1];
        i = 0;
        strTemp = str;
        while (strTemp != null && strTemp.indexOf(delim) >= 0) {
            splitList[i++] = strTemp.substring(0, strTemp.indexOf(delim));
            if (strTemp.length() < (strTemp.indexOf(delim) + delim.length())) {
                break;
            }
            strTemp = strTemp.substring(strTemp.indexOf(delim) + delim.length(), strTemp.length());
        }

        if (strTemp != null && splitList[splitList.length - 1] == null) {
            splitList[splitList.length - 1] = strTemp;
        }

        return splitList;
    }

    public static String createBreaks(String input, int maxLength) {
        char chars[] = input.toCharArray();
        int len = chars.length;
        StringBuffer buf = new StringBuffer(len);
        int count = 0;
        int cur = 0;
        for (int i = 0; i < len; i++) {
            if (Character.isWhitespace(chars[i])) {
                count = 0;
            }
            if (count >= maxLength) {
                count = 0;
                buf.append(chars, cur, i - cur).append(" ");
                cur = i;
            }
            count++;
        }
        buf.append(chars, cur, len - cur);
        return buf.toString();
    }

    /**
     * Escape SQL tags, ' to ''; \ to \\.
     * 
     * @param input
     *            string to replace
     * @return string
     */
    public static String escapeSQLTags(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        StringBuffer buf = new StringBuffer();
        char ch = ' ';
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            if (ch == '\\') {
                buf.append("\\\\");
            } else if (ch == '\'') {
                buf.append("\'\'");
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Escape HTML tags.
     * 
     * @param input
     *            string to replace
     * @return string
     */
    public static String escapeHTMLTags(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        StringBuffer buf = new StringBuffer();
        char ch = ' ';
        for (int i = 0; i < input.length(); i++) {
            ch = input.charAt(i);
            if (ch == '<') {
                buf.append("&lt;");
            } else if (ch == '>') {
                buf.append("&gt;");
            } else if (ch == '&') {
                buf.append("&amp;");
            } else if (ch == '"') {
                buf.append("&quot;");
            } else {
                buf.append(ch);
            }
        }
        return buf.toString();
    }

    /**
     * Convert new lines, \n or \r\n to <BR />
     * .
     * 
     * @param input
     *            string to convert
     * @return string
     */
    public static String convertNewlines(String input) {
        input = replace(input, "\r\n", "\n");
        input = replace(input, "\n", "<BR/>");
        input = replace(input, "&", "&amp;");
        input = replace(input, "<", "&lt;");
        input = replace(input, ">", "&gt;");
        input = replace(input, "\"", "&quot;");
        input = replace(input, "\'", "&#39;");
        input = replace(input, "\t", "&nbsp; &nbsp; ");
        input = replace(input, "\r", "");
        input = replace(input, "\n\n", "<p></p>");
        input = replace(input, "\n", "<br />");
        return input;
    }

    /**
     * Convert new lines, \n or \r\n to <BR />
     * .
     * 
     * @param input
     *            string to convert
     * @return string
     */
    public static String convertNewlinesText(String input) {
        input = replace(input, "\n", "\r\n");
        input = replace(input, "<BR/>", "\r\n");
        input = replace(input, "<BR>", "\r\n");
        input = replace(input, "<br/>", "\r\n");
        input = replace(input, "<br>", "\r\n");
        return input;
    }

    public static String replace(String mainString, String oldString, String newString) {
        if (mainString == null) {
            return null;
        }
        int i = mainString.lastIndexOf(oldString);
        if (i < 0) {
            return mainString;
        }
        StringBuffer mainSb = new StringBuffer(mainString);
        while (i >= 0) {
            mainSb.replace(i, i + oldString.length(), newString);
            i = mainSb.toString().lastIndexOf(oldString, i - 1);
        }
        return mainSb.toString();
    }
    
    
    /**
     * Check a string null or blank.
     * 
     * @param param
     *            string to check
     * @return boolean
     */
    public static boolean nullOrBlank(String param) {
        return (param == null || param.trim().length() <= 0 || param.trim().equalsIgnoreCase("null")) ? true : false;
    }

    public static String notNull(String param) {
        return param == null ? "" : param.trim();
    }

    /**
     * Parse a string to int.
     * 
     * @param param
     *            string to parse
     * @return int value, on exception return 0.
     */
    public static int parseInt(String param) {
        int i = 0;
        try {
            i = Integer.parseInt(param);
        } catch (Exception e) {
            i = (int) parseFloat(param);
        }
        return i;
    }

    public static long parseLong(String param) {
        long l = 0;
        if (nullOrBlank(param)) {
            return l;
        }
        try {
            l = Long.parseLong(param);
        } catch (Exception e) {
            l = (long) parseDouble(param);
        }
        return l;
    }

    public static float parseFloat(String param) {
        float f = 0;
        try {
            f = Float.parseFloat(param);
        } catch (Exception e) {
            //
        }
        return f;
    }

    public static double parseDouble(String param) {
        double d = 0;
        try {
            d = Double.parseDouble(param);
        } catch (Exception e) {
            //
        }
        return d;
    }

    /**
     * Parse a string to boolean.
     * 
     * @param param
     *            string to parse
     * @return boolean value, if param begin with(1,y,Y,t,T) return true, on
     *         exception return false.
     */
    public static boolean parseBoolean(String param) {
        if (nullOrBlank(param)) {
            return false;
        }
        switch (param.charAt(0)) {
        case '1':
        case 'y':
        case 'Y':
        case 't':
        case 'T':
            return true;
        }
        return false;
    }

    /**
     * Parse a Object to String.
     * 
     * @param param
     *            string to parse
     * @return boolean value, if param begin with(1,y,Y,t,T) return true, on
     *         exception return false.
     */
    public static String parseString(Object param) {
        return parseString(param, Locale.CHINA);
    }

    /**
     * Parse a Object to String.
     * 
     * @param param
     *            string to parse
     * @return boolean value, if param begin with(1,y,Y,t,T) return true, on
     *         exception return false.
     */
    public static String parseString(Object obj, Locale locale) {
        if (obj == null || nullOrBlank(obj.toString())) {
            return "";
        }
        String str = "";
        NumberFormat numberformat = NumberFormat.getInstance(locale);
        if (obj instanceof Double) {
            str = numberformat.format(((Double) obj).doubleValue());
            str = replace(str, ",", "");
        } else if (obj instanceof Float) {
            str = numberformat.format(((Float) obj).floatValue());
            str = replace(str, ",", "");
        } else {
            str = obj.toString();
        }

        // 防止死循环
        int i = 0;
        while (str.startsWith("　") && i <= 1000) {
            i++;
            str = str.substring(str.indexOf("　") + ("　").length(), str.length());
        }

        // 防止死循环
        i = 0;
        while (str.endsWith("　") && i <= 1000) {
            i++;
            str = str.substring(0, str.lastIndexOf("　"));
        }

        str = str.trim();

        return str;
    }

    /**
     * Parse a Object to String.
     * 
     * @param param
     *            string to parse
     * @return boolean value, if param begin with(1,y,Y,t,T) return true, on
     *         exception return false.
     */
    public static String parseString(float obj, Locale locale) {
        NumberFormat numberformat = NumberFormat.getInstance(locale);
        return replace(numberformat.format(obj), ",", "");
    }

    /**
     * Parse a Object to String.
     * 
     * @param param
     *            string to parse
     * @return boolean value, if param begin with(1,y,Y,t,T) return true, on
     *         exception return false.
     */
    public static String parseString(double obj, Locale locale) {
        NumberFormat numberformat = NumberFormat.getInstance(locale);
        return replace(numberformat.format(obj), ",", "");
    }

    /**
     * Convert URL .
     * 
     * @param input
     *            string to convert
     * @return string
     */
    public static String convertURL(String input) {
        if (input == null || input.length() == 0) {
            return input;
        }
        StringBuffer buf = new StringBuffer(input.length() + 25);
        char chars[] = input.toCharArray();
        int len = input.length();
        int index = -1;
        int i = 0;
        int j = 0;
        int oldend = 0;
        while (++index < len) {
            char cur = chars[i = index];
            j = -1;
            if ((cur == 'f' && index < len - 6 && chars[++i] == 't' && chars[++i] == 'p' || cur == 'h' && (i = index) < len - 7 && chars[++i] == 't' && chars[++i] == 't' && chars[++i] == 'p'
                    && (chars[++i] == 's' || chars[--i] == 'p'))
                    && i < len - 4 && chars[++i] == ':' && chars[++i] == '/' && chars[++i] == '/') {
                j = ++i;
            }
            if (j > 0) {
                if (index == 0 || (cur = chars[index - 1]) != '\'' && cur != '"' && cur != '<' && cur != '=') {
                    cur = chars[j];
                    while (j < len) {
                        if (cur == ' ' || cur == '\t' || cur == '\'' || cur == '"' || cur == '<' || cur == '[' || cur == '\n' || cur == '\r' && j < len - 1 && chars[j + 1] == '\n') {
                            break;
                        }
                        if (++j < len) {
                            cur = chars[j];
                        }
                    }
                    cur = chars[j - 1];
                    if (cur == '.' || cur == ',' || cur == ')' || cur == ']') {
                        j--;
                    }
                    buf.append(chars, oldend, index - oldend);
                    buf.append("<a href=\"");
                    buf.append(chars, index, j - index);
                    buf.append('"');
                    buf.append(" target=\"_blank\"");
                    buf.append('>');
                    buf.append(chars, index, j - index);
                    buf.append("</a>");
                } else {
                    buf.append(chars, oldend, j - oldend);
                }
                oldend = index = j;
            } else if (cur == '[' && index < len - 6 && chars[i = index + 1] == 'u' && chars[++i] == 'r' && chars[++i] == 'l' && (chars[++i] == '=' || chars[i] == ' ')) {
                j = ++i;
                int u2;
                int u1 = u2 = input.indexOf("]", j);
                if (u1 > 0) {
                    u2 = input.indexOf("[/url]", u1 + 1);
                }
                if (u2 < 0) {
                    buf.append(chars, oldend, j - oldend);
                    oldend = j;
                } else {
                    buf.append(chars, oldend, index - oldend);
                    buf.append("<a href =\"");
                    String href = input.substring(j, u1).trim();
                    if (href.indexOf("javascript:") == -1 && href.indexOf("file:") == -1) {
                        buf.append(href);
                    }
                    buf.append("\" target=\"_blank");
                    buf.append("\">");
                    buf.append(input.substring(u1 + 1, u2).trim());
                    buf.append("</a>");
                    oldend = u2 + 6;
                }
                index = oldend;
            }
        }
        if (oldend < len) {
            buf.append(chars, oldend, len - oldend);
        }
        return buf.toString();
    }

    /**
     * Display a string in html page, call methods: escapeHTMLTags, convertURL,
     * convertNewlines.
     * 
     * @param input
     *            string to display
     * @return string
     */
    public static String dspHtml(String input) {
        String str = input;
        str = createBreaks(str, 80);
        str = escapeHTMLTags(str);
        str = convertURL(str);
        str = convertNewlines(str);
        return str;
    }

    /**
     * @param ss
     *            需要转换的字符串
     * @return return_type String
     */
    public static String toChinese(String ss) {
        return toChinese(ss, "ISO8859-1");
    }

    /**
     * @param ss
     *            需要转换的字符串
     * @param toByteStr
     *            需要转换的
     * @return return_type String
     */
    public static String toChinese(String ss, String toByteStr) {
        // 处理中文问题,实现编码转换
        if (ss != null && toByteStr != null) {
            try {
                String temp_p = ss;
                byte[] temp_t = temp_p.getBytes(toByteStr);
                ss = new String(temp_t);
            } catch (Exception e) {
                System.err.println("toChinese exception:" + e.getMessage());
                System.err.println("The String is:" + ss);
            }
        }
        return ss;
    }

    // 将型double数据转换为不待小数点得字符
    public static String caststring1(String tmpq) {
        String tmp1 = "";
        if (tmpq.indexOf("-") <= 0) {
            int tt = tmpq.indexOf(".");
            if (tt > 0) {
                tmp1 = tmpq.substring(0, tt);
            } else {
                tmp1 = tmpq;
            }
        } else {
            tmp1 = tmpq;
        }
        return tmp1;
    }

    /**
     * Description: 如果给定number的长度小于给定的nLength，就在number前面补零 Created on 2015-09-30
     * 
     * @param number
     *            键值
     * @param nLength
     *            需要设定的长度 return_type String
     */
    public static String formatNumber(int number, int nLength) {
        return formatNumber(number, nLength, '0');
    }

    /**
     * Description: 如果给定number的长度小于给定的nLength，就在number前面补给定的字符 Created on
     * 2015-09-30
     * 
     * @param number
     *            键值
     * @param nLength
     *            需要设定的长度
     * @param nChar
     *            补的字符 return_type String
     */
    public static String formatNumber(int number, int nLength, char nChar) {
        String s = String.valueOf(number);

        if (StringUtil.nullOrBlank(s)) {
            s = "";
        }

        StringBuffer buf = new StringBuffer();

        // 前面补给定的字符
        while ((nLength - s.length()) > 0) {
            buf.append(nChar);
            nLength--;
        }

        return (buf.toString() + s);
    }

    /**
     * Description: 如果给定number的长度小于给定的nLength，就在number前面补零 Created on 2015-09-30
     * 
     * @param number
     *            键值
     * @param nLength
     *            需要设定的长度 return_type String
     */
    public static String formatNumber(String s, int nLength) {
        return formatNumber(s, nLength, '0');
    }

    /**
     * Description: 如果给定number的长度小于给定的nLength，就在number前面补给定的字符 Created on
     * 2015-09-30
     * 
     * @param number
     *            键值
     * @param nLength
     *            需要设定的长度
     * @param nChar
     *            补的字符 return_type String
     */
    public static String formatNumber(String s, int nLength, char nChar) {
        if (StringUtil.nullOrBlank(s)) {
            s = "";
        }

        StringBuffer buf = new StringBuffer();

        // 前面补给定的字符
        while ((nLength - s.length()) > 0) {
            buf.append(nChar);
            nLength--;
        }

        return (buf.toString() + s);
    }

    public static String[] parseIntoArray(String s, String s1, int i) {
        String as[] = new String[i];
        int j = 0;
        for (int l = 0; l < as.length; l++) {
            int k = s.indexOf(s1, j);
            if (k == -1) {
                as[l] = null;
            } else {
                as[l] = s.substring(j, k);
            }
            j = k + s1.length();
        }

        return as;
    }

    /**
     * Description: 由于Java对处理浮点数，由于Java处理浮点数的性能不是很好，所以674报表中出现了浮点数超长的问题。 比如0.2 *
     * 0.3的结果有可能出现 0.0599999999999999999999999999 该函数专门处理浮点数的乘积,其中的scale可以指定
     * 返回的积的位数是各自浮点数位数的和 Created on 2015-10-14
     * 
     * @param multiplicandFloat
     *            被乘数
     * @param multiplicatorFloat
     *            乘数 return_type String
     */
    public static float multiplyFloat(float multiplicandFloat, float multiplicatorFloat) {
        int scale = getScale(multiplicandFloat) + getScale(multiplicatorFloat);

        return multiplyFloat(multiplicandFloat, multiplicatorFloat, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Description: 由于Java对处理浮点数，由于Java处理浮点数的性能不是很好，所以674报表中出现了浮点数超长的问题。 比如0.2 *
     * 0.3的结果有可能出现 0.0599999999999999999999999999 该函数专门处理浮点数的乘积,其中的scale可以指定
     * Created on 2015-10-14
     * 
     * @param multiplicandFloat
     *            被乘数
     * @param multiplicatorFloat
     *            乘数
     * @param scale
     *            浮点数的乘积需要的小数点后面的位数 return_type String
     */
    public static float multiplyFloat(float multiplicandFloat, float multiplicatorFloat, int scale) {
        return multiplyFloat(multiplicandFloat, multiplicatorFloat, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Description: 由于Java对处理浮点数，由于Java处理浮点数的性能不是很好，所以674报表中出现了浮点数超长的问题。 比如0.2 *
     * 0.3的结果有可能出现 0.0599999999999999999999999999 该函数专门处理浮点数的乘积
     * 其中的scale、roundingMode可以指定 Created on 2015-10-14
     * 
     * @param multiplicandFloat
     *            被乘数
     * @param multiplicatorFloat
     *            乘数
     * @param scale
     *            浮点数的乘积需要的小数点后面的位数
     * @param roundingMode
     *            处理小数点的方式 return_type String
     */
    public static float multiplyFloat(float multiplicandFloat, float multiplicatorFloat, int scale, int roundingMode) {
        return roundToTwoDecimals(multiplicandFloat * multiplicatorFloat, scale, roundingMode);
    }

    /**
     * Description: 获得小数点的位数，如果小数点后面的部分以零结尾，就把后面的零都去除 Created on 2015-10-14
     * 
     * @param floatValue
     *            return_type int
     */
    public static int getScale(float floatValue) {
        return getScale(floatValue, true);
    }

    /**
     * Description: 获得小数点的位数 Created on 2015-10-14
     * 
     * @param floatValue
     *            需要处理的浮点数
     * @param quChuLing
     *            如果小数点后面的部分以零结尾，是否把后面的零去除 return_type int
     */
    public static int getScale(float floatValue, boolean quChuLing) {
        try {
            String fStr = String.valueOf(floatValue);

            int indexOf = fStr.indexOf(".");
            if (indexOf >= 0) {
                String strss = fStr.substring(indexOf + 1, fStr.length());

                if (quChuLing) {
                    // 如果将后面的零去除
                    while (strss.endsWith("0")) {
                        strss = strss.substring(0, strss.length() - 1);
                    }
                }

                // 小数点位数
                return strss.length();
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Description: 获得小数点的位数，如果小数点后面的部分以零结尾，就把后面的零都去除 Created on 2015-10-14
     * 
     * @param doubleValue
     *            return_type int
     */
    public static int getScale(double doubleValue) {
        return getScale(doubleValue, true);
    }

    /**
     * Description: 获得小数点的位数 Created on 2015-10-14
     * 
     * @param doubleValue
     *            需要处理的 double 数
     * @param quChuLing
     *            如果小数点后面的部分以零结尾，是否把后面的零去除 return_type int
     */
    public static int getScale(double doubleValue, boolean quChuLing) {
        try {
            String fStr = String.valueOf(doubleValue);

            int indexOf = fStr.indexOf(".");
            if (indexOf >= 0) {
                String strss = fStr.substring(indexOf + 1, fStr.length());

                if (quChuLing) {
                    // 如果将后面的零去除
                    while (strss.endsWith("0")) {
                        strss = strss.substring(0, strss.length() - 1);
                    }
                }

                // 小数点位数
                return strss.length();
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * Description: 处理浮点数，默认若舍弃部分>=.5，则作 ROUND_UP ；否则，作 ROUND_DOWN 。 Created on
     * 2015-10-14
     * 
     * @param floatValue
     *            需要处理的浮点数
     * @param scale
     *            小数点后面的位数 return_type float
     */
    public static float roundToTwoDecimals(float floatValue, int scale) {
        return roundToTwoDecimals(floatValue, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Description: 处理双精度数，默认若舍弃部分>=.5，则作 ROUND_UP ；否则，作 ROUND_DOWN 。 Created on
     * 2015-11-19
     * 
     * @param doubleValue
     *            需要处理的双精度数
     * @param scale
     *            小数点后面的位数 return_type String
     */
    public static String roundToTwoDecimals(double doubleValue, int scale) {
        return roundToTwoDecimals(doubleValue, scale, BigDecimal.ROUND_HALF_UP);
    }

    /**
     * Description: 处理浮点数， Created on 2015-10-14
     * 
     * @param floatValue
     *            需要处理的浮点数
     * @param scale
     *            小数点后面的位数
     * @param roundingMode
     *            处理小数点的方式 return_type float
     */
    public static float roundToTwoDecimals(float floatValue, int scale, int roundingMode) {
        if (scale <= 0) {
            scale = 0;
        }

        BigDecimal bigdecimal = (new BigDecimal(floatValue)).setScale(scale, roundingMode);
        return bigdecimal.floatValue();

        /*
         * BigDecimal 的 变量索引 ROUND_CEILING 如果 BigDecimal 是正的，则做 ROUND_UP
         * 操作；如果为负，则做 ROUND_DOWN 操作。 ROUND_DOWN 从不在舍弃(即截断)的小数之前增加数字。 ROUND_FLOOR
         * 如果 BigDecimal 为正，则作 ROUND_UP ；如果为负，则作 ROUND_DOWN 。 ROUND_HALF_DOWN
         * 若舍弃部分> .5，则作 ROUND_UP；否则，作 ROUND_DOWN 。 ROUND_HALF_EVEN
         * 如果舍弃部分左边的数字为奇数，则作 ROUND_HALF_UP ；如果它为偶数，则作 ROUND_HALF_DOWN 。
         * ROUND_HALF_UP 若舍弃部分>=.5，则作 ROUND_UP ；否则，作 ROUND_DOWN 。
         * ROUND_UNNECESSARY 该“伪舍入模式”实际是指明所要求的操作必须是精确的，，因此不需要舍入操作。 ROUND_UP 总是在非
         * 0 舍弃小数(即截断)之前增加数字。
         */
    }

    /**
     * Description: 处理双精度数 Created on 2015-10-14
     * 
     * @param doubleValue
     *            需要处理的双精度数
     * @param scale
     *            小数点后面的位数
     * @param roundingMode
     *            处理小数点的方式 return_type String
     */
    public static String roundToTwoDecimals(double doubleValue, int scale, int roundingMode) {
        if (scale <= 0) {
            scale = 0;
        }

        BigDecimal bigdecimal = (new BigDecimal(doubleValue)).setScale(scale, roundingMode);
        return bigdecimal.toString();

        /*
         * BigDecimal 的 变量索引 ROUND_CEILING 如果 BigDecimal 是正的，则做 ROUND_UP
         * 操作；如果为负，则做 ROUND_DOWN 操作。 ROUND_DOWN 从不在舍弃(即截断)的小数之前增加数字。 ROUND_FLOOR
         * 如果 BigDecimal 为正，则作 ROUND_UP ；如果为负，则作 ROUND_DOWN 。 ROUND_HALF_DOWN
         * 若舍弃部分> .5，则作 ROUND_UP；否则，作 ROUND_DOWN 。 ROUND_HALF_EVEN
         * 如果舍弃部分左边的数字为奇数，则作 ROUND_HALF_UP ；如果它为偶数，则作 ROUND_HALF_DOWN 。
         * ROUND_HALF_UP 若舍弃部分>=.5，则作 ROUND_UP ；否则，作 ROUND_DOWN 。
         * ROUND_UNNECESSARY 该“伪舍入模式”实际是指明所要求的操作必须是精确的，，因此不需要舍入操作。 ROUND_UP 总是在非
         * 0 舍弃小数(即截断)之前增加数字。
         */
    }

    /**
     * Description: 获得 hashtable 的值 Created on 2006-07-31
     * 
     * @param hashtable
     * @param key
     *            return_type String
     */
    public static String getValueFromHashtable(Hashtable hashtable, String key) {
        if (hashtable == null) {
            return null;
        }

        if (nullOrBlank(key)) {
            return null;
        }

        String value = null;

        if (hashtable.containsKey(key)) {
            value = (String) hashtable.get(key);
        }

        if (value == null) {
            value = "";
        }

        return value;
    }

    /**
     * Description: 获得 hashtable 的值 Created on 2006-07-31
     * 
     * @param hashtable
     * @param key
     * @param defaultValue
     *            return_type String
     */
    public static String getValueFromHashtable(Hashtable hashtable, String key, String defaultValue) {
        if (hashtable == null) {
            return null;
        }

        if (nullOrBlank(key)) {
            return null;
        }

        String value = null;

        if (hashtable.containsKey(key)) {
            value = (String) hashtable.get(key);
        }

        if (value == null && defaultValue != null) {
            value = defaultValue;
        }

        return value;
    }
    
    
    public static String longToString(Long l){
    	if(l!=null){
    		return String.valueOf(l);
    	}
    	return "";
    }
    
    public static boolean isLong(String str){
        try{
            Long.parseLong(str);
            return true;
        }catch(NumberFormatException e){
          return false;
        }
    }
}
