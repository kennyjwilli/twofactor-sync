package kennyjwilli.twofactorsync;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kenny on 10/17/16.
 */

public class Util {
    public static String[] defaultMatches = {"code", "pin"};
    public static int minCodeLength = 4;
    public static final String TAG = "twofactor-sync";

    /**
     * @param message A message
     * @param strMatches A list of strings to match against
     * @return True when message contains a string in strMatches
     */
    public static boolean hasVerificationCode(String message, String[] strMatches) {
        boolean r = false;
        for (String matcher : strMatches) {
            if (message.toLowerCase().contains(matcher.toLowerCase())) {
                r = true;
            }
        }
        return r;
    }

    public static boolean hasNumber(String s) {
        return s.matches(".*\\d+.*");
    }

    /**
     * @param list A list of chars
     * @return String constructed from the characters in list
     */
    private static String charsToString(List<Character> list) {
        StringBuilder builder = new StringBuilder(list.size());
        for (Character ch : list) {
            builder.append(ch);
        }
        return builder.toString();
    }

    /**
     * Returns the code or null by looping through all the characters in msg
     *
     * @param msg The message to check
     * @return A string that is probably the authentication code
     */
    public static String findBestCode(String msg) {
        final byte IN_STRING = 0;
        final byte IN_NUMBER = 1;
        byte mode = IN_STRING;
        List<Character> codeChars = new ArrayList<>();
        String code = null;

        for (int i = 0; i < msg.length(); i++) {
            char c = msg.charAt(i);
            switch (mode) {
                case IN_STRING:
                    if (Character.isDigit(c)) {
                        mode = IN_NUMBER;
                        codeChars.add(c);
                    }
                    break;
                case IN_NUMBER:
                    if (Character.isDigit(c)) {
                        codeChars.add(c);
                        if (codeChars.size() >= minCodeLength) {
                            code = charsToString(codeChars);
                        }
                    } else {
                        mode = IN_STRING;
                        codeChars.clear();
                    }
                    break;
            }
        }
        return code;
    }

    /**
     * Copies s to the clipboard
     * @param ctx App context
     * @param s The string the be copied
     */
    public static void copyToClipboard(Context ctx, String s) {
        ClipboardManager clipboard = (ClipboardManager) ctx.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText("Two factor code", s);
        clipboard.setPrimaryClip(clip);
    }
}
