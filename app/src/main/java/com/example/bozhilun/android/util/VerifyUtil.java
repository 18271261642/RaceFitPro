package com.example.bozhilun.android.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.afa.tourism.greendao.gen.StepBeanDao;
import com.example.bozhilun.android.MyApp;
import com.example.bozhilun.android.R;
import com.example.bozhilun.android.bean.MessageEvent;
import com.example.bozhilun.android.bean.StepBean;
import com.example.bozhilun.android.bleutil.MyCommandManager;
import com.example.bozhilun.android.siswatch.utils.WatchUtils;
import com.suchengkeji.android.w30sblelibrary.utils.SharedPreferencesUtils;

import org.apache.commons.lang.StringUtils;
import org.greenrobot.eventbus.EventBus;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by thinkpad on 2016/6/28.
 */
public class VerifyUtil {
    //判断语言
    public static boolean isZh(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    //手机号格式验证
    public static boolean VerificationPhone(String phone) {
        Pattern pattern = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(170)|(18[0-9]))\\d{8}$");
        Matcher matcher = pattern.matcher(phone);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    //字符串去掉空格
    public static String clearStr(String str) {
        return str.replaceAll("\\s*", "");
    }

    //中文
    public static boolean isChinse(String str) {
        Pattern pattern = Pattern
                .compile("^[\\u4E00-\\u9FA5\\uF900-\\uFA2D]+$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    //邮箱
    public static boolean checkEmail(String str) {
        Pattern pattern = Pattern
                .compile("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    // 身份证正则表达式
    public static boolean checkIdNo(String str) {
        Pattern pattern = Pattern
                .compile("^(\\d{14}|\\d{17})(\\d|[xX])$");
        Matcher matcher = pattern.matcher(str);
        if (matcher.matches()) {
            return true;
        } else {
            return false;
        }
    }

    //根据身份证号码判断性别
    public static String getSex(String value) {
        value = value.trim();
        if (value == null || (value.length() != 15 && value.length() != 18)) {
            return "";
        }
        if (value.length() == 18) {
            String lastValue = value.substring(16, 17);
            int sex = Integer.parseInt(lastValue) % 2;
            return sex == 0 ? "女" : "男";
        } else if (value.length() == 15) {
            String lastValue = value.substring(13, 14);
            int sex = Integer.parseInt(lastValue) % 2;
            return sex == 0 ? "女" : "男";
        } else {
            return "";
        }
    }

    //根据
    public static boolean getIsChinesEnglish(Context context) {
        Locale locale = context.getResources().getConfiguration().locale;
        String language = locale.getLanguage();
        if (language.endsWith("zh"))
            return true;
        else
            return false;
    }

    //根据身份证号码获得年龄
    public static int getAge(String identification) {
        int age = 0;
        GregorianCalendar currentDay;
        GregorianCalendar beforeDay;
        // 生日日期
        Date birthday = null;
        // 生日日期字符
        String birthdayStr;
        // 生日那年
        int birthYear;
        // 今年
        int currentYear;
        if (identification.length() == 18) {
            birthdayStr = identification.substring(6, 14);
            try {
                birthday = new SimpleDateFormat("yyyyMMdd")
                        .parse(birthdayStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            beforeDay = new GregorianCalendar();
            beforeDay.setTime(birthday);
            birthYear = beforeDay.get(Calendar.YEAR);
            currentDay = new GregorianCalendar();
            currentDay.setTime(new Date());
            currentYear = currentDay.get(Calendar.YEAR);
            age = currentYear - birthYear;
        } else if (identification.length() == 15) {
            birthdayStr = "19" + identification.substring(6, 12);
            try {
                birthday = new SimpleDateFormat("yyyyMMdd")
                        .parse(birthdayStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            beforeDay = new GregorianCalendar();
            beforeDay.setTime(birthday);
            birthYear = beforeDay.get(Calendar.YEAR);

            currentDay = new GregorianCalendar();
            currentDay.setTime(new Date());
            currentYear = currentDay.get(Calendar.YEAR);
            age = currentYear - birthYear;
        }
        return age;
    }

    public static String getBir(String IdNo) {
        String result = "";
        if (IdNo.length() == 18) {
            result = IdNo.substring(6, 14);
        }
        if (IdNo.length() == 15) {
            result = "19" + IdNo.substring(6, 12);
        }
        result = result.substring(0, 4) + "-" + result.substring(4, 6) + "-" + result.substring(6, 8);
        return result;
    }

    //是否是数字
    public static boolean checkNumber(String value) {
        String regex = "^(-?[1-9]\\d*\\.?\\d*)|(-?0\\.\\d*[1-9])|(-?[0])|(-?[0]\\.\\d*)$";
        return value.matches(regex);
    }

    public static boolean isMobile(String number) {
        String num = "[1][358]\\d{9}";//"[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
        return number.matches(num);
    }

    //保留2位小数
    public static double roundTo2(double value) {
        return Math.round(value * 100) / 100.0;
    }

    //获取中文长度
    public static int getWordCount(String s) {
        int length = 0;
        for (int i = 0; i < s.length(); i++) {
            int ascii = Character.codePointAt(s, i);
            if (ascii >= 0 && ascii <= 255)
                length++;
            else
                length += 2;

        }
        return length;
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static String getDatetoString(Date date) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        return myFormatter.format(date);
    }

    /**
     * 得到二个日期间的间隔天数
     */
    public static Long getTwoDay(String sj1, String sj2) {
        SimpleDateFormat myFormatter = new SimpleDateFormat("yyyy-MM-dd");
        long day = 0;
        try {
            Date date = myFormatter.parse(sj1);
            Date mydate = myFormatter.parse(sj2);
            day = (date.getTime() - mydate.getTime()) / (24 * 60 * 60 * 1000);
        } catch (Exception e) {
            return 0l;
        }
        return day;
    }

    //用星号代替手机号码中间字符
    public static String encryptPhone(String phone) {
        String encryptionPhone = phone;
        if (!TextUtils.isEmpty(phone) && phone.length() > 7) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < phone.length(); i++) {
                char c = phone.charAt(i);
                if (i >= 3 && i <= 7) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            encryptionPhone = sb.toString();
        }
        return encryptionPhone;
    }

    //用星号代替身份证号码中间字符
    public static String encryptIdNo(String IdNo) {
        String encryptionIdNo = IdNo;
        if (!TextUtils.isEmpty(IdNo) && IdNo.length() > 14) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < IdNo.length(); i++) {
                char c = IdNo.charAt(i);
                if (i >= 3 && i <= 14) {
                    sb.append('*');
                } else {
                    sb.append(c);
                }
            }
            encryptionIdNo = sb.toString();
        }
        return encryptionIdNo;
    }

}
