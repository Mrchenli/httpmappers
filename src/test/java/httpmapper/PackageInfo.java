package httpmapper;


public class PackageInfo {


    public String longestPalindrome(String s) {
        char[] cs = s.toCharArray();
        int[] indes = new int[128];
        int from=0;
        int to=0;
        int maxSize=0;
        for(int i=0;i<cs.length;i++){
            if(indes[cs[i]]==0){
                indes[cs[i]]=i+1;//indes +1
            }else if(indes[cs[i]]>0){
                int tmp = i-indes[cs[i]]+1;
                if(maxSize>tmp){
                    indes[cs[i]]=i+1;
                }else{
                    if((i<cs.length-1)&&(cs[i]==cs[i+1])){
                        continue;
                    }else{
                        maxSize=tmp;
                        from=indes[cs[i]]-1;
                        to = i;
                        indes[cs[i]]=i+1;
                    }
                }
            }
        }
        return s.substring(from,to+1);
    }
    public static void main(String[] args) {
        PackageInfo packageInfo = new PackageInfo();
        String s = packageInfo.longestPalindrome("cbbd");
        System.out.println("s==>"+s);
    }

}
