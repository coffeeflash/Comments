package ch.tobisyurt.comments.service;

import org.apache.commons.text.StringEscapeUtils;

public class SecuritiyUtil {

    public static String cleanIt(String arg0) {
        return StringEscapeUtils.escapeHtml4(arg0);
    }
}
