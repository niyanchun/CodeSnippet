package com.niyanchun.language.regex;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Pattern;

/**
 * @description:
 * @author: NiYanchun
 * @version: 1.0
 * @create: 2019-05-16
 **/
public class Demo {

    public static void main(String[] args) throws Exception {

        String text = "this is a handsome boy,\nthat is a pretty giRl yep.";
        String text2 = "they are beautiful giRls";

        boolean find1 = Pattern.compile("\\bgirl\\b", Pattern.CASE_INSENSITIVE).matcher(text).find();
        boolean find2 = StringUtils.containsIgnoreCase(text, "girl");

        System.out.println(find1 + "," + find2);

        String result = text.replaceAll("(?i)giRl", "<em>giRl</em>");
        System.out.println(result);
        System.out.println(text2.replaceAll("\\b(?i)giRl\\b", "<em>giRls</em>"));
    }
}
