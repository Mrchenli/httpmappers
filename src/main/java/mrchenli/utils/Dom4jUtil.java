package mrchenli.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dom4jUtil {

    private static Logger LOGGER = LoggerFactory.getLogger(Dom4jUtil.class);
    private static final String xmlDefineReg = "^<\\?xml.*\\?>";

    public static  <T> List<T> parseList(List<Node> nodes ,Class<T> type,boolean isMap) throws IllegalAccessException, InstantiationException {
        List<T> list = new ArrayList<>();
        for (Node nd:nodes) {
            T lo;
            if(isMap){
                lo = (T) parseMap(nd,type);
            }else{
                lo = parseObject(nd,type,null);
            }
            list.add(lo);
        }
        return list;
    }

    public static Map<String,String> parseMap(Node node,Class type){
        Field[] fields = type.getDeclaredFields();
        Map<String,String> map = new HashMap();
        for (Field f: fields) {
            String key = f.getName();
            String value = node.valueOf(key);
            map.put(key,value);
        }
        return map;
    }


    public static <T> T parseObject(Node node, Class<T> type){
        return parseObject(node,type,null);
    }
    /**
     *
     * @param node 节点
     * @param type 目标对象
     * @param geneField 目标对象上的泛型
     * @param <T>
     * @return
     */
    public static <T> T parseObject(Node node, Class<T> type,Class geneField){
        try {
            T o = type.newInstance();
            Field[] fields = type.getDeclaredFields();
            for (Field f:fields) {
                Class clzz =null;
                //判断是否是泛型属性
                boolean isGenericField= f.getGenericType().getTypeName().length()==1;
                if(isGenericField){
                    clzz = geneField;
                }else{
                    clzz = f.getType();
                }
                String name = upperCase(f.getName());
                if(Map.class.isAssignableFrom(clzz)){//parseMap//
                    if(f.isAnnotationPresent(XmlORM.class)){
                        XmlORM xmlPath= f.getAnnotation(XmlORM.class);
                        Node mapNode = node.selectSingleNode(name);
                        f.set(o,parseMap(mapNode,xmlPath.value()));
                    }
                }else if(List.class.isAssignableFrom(clzz)){//parseList todo 目前不存在list嵌套的情况
                    ParameterizedType pt = (ParameterizedType) f.getGenericType();
                    Class ft = (Class) pt.getActualTypeArguments()[0];
                    Node lnode = node.selectSingleNode(name);
                    String path = upperCase(ft.getSimpleName());

                    List<Node> nodes = lnode.selectNodes(path);
                    List list = new ArrayList();
                    if(Map.class.isAssignableFrom(ft)){
                        if(f.isAnnotationPresent(XmlORM.class)){
                            Class lt = f.getAnnotation(XmlORM.class).value();
                            list = parseList(nodes,lt,true);
                        }
                    }else{
                        list = parseList(nodes,ft,false);
                    }
                    f.set(o,list);
                }else if(String.class.isAssignableFrom(clzz)){//parseString
                    String value = node.valueOf(name);
                    f.setAccessible(true);
                    f.set(o,value);
                    f.setAccessible(false);
                }else{//parseObject
                    Class tmpGen = f.getGenericType() instanceof ParameterizedType? (Class) ((ParameterizedType) f.getGenericType()).getActualTypeArguments()[0] :null;
                    Object po = parseObject(node.selectSingleNode(name),clzz,tmpGen);
                    f.setAccessible(true);
                    f.set(o,po);
                    f.setAccessible(false);
                }
            }
            return o;
        }catch (Exception e){
            LOGGER.info("xml response handler error e==>{}",e);
        }
        return null;
    }


    public static String upperCase(String str) {
        char[] ch = str.toCharArray();
        if (ch[0] >= 'a' && ch[0] <= 'z') {
            ch[0] = (char) (ch[0] - 32);
        }
        return new String(ch);
    }

    public static <T> T parseResult(String res ,Class<T> type) throws DocumentException {
        return parseGennericResult(res,type,null);
    }

    public static <T> T parseGennericResult(String res ,Class<T> type,Class genericField)
            throws DocumentException {
        res = res.replaceAll(xmlDefineReg,"").trim();
        Document document = DocumentHelper.parseText(res);
        Node node = document.getRootElement();
        return parseObject(node,type,genericField);
    }

}
