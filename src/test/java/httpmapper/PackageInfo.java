package httpmapper;


import httpmapper.mapper.TestMapper;
import mrchenli.utils.MapperRequestKeyUtil;

public class PackageInfo {

    public static void main(String[] args) {
        System.out.println("========"+ MapperRequestKeyUtil.getKey(TestMapper.class.getMethods()[0]));
    }

}
