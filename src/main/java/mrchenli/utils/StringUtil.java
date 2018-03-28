package mrchenli.utils;

public  class StringUtil {

    public static boolean isEmpty(String str){
        return str==null ||str.trim().length()==0;
    }

    public static String upperFirst(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

}
