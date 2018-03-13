package httpmapper.autocloseable;

public class TestAutoCloseAble {

    public static void main(String[] args) {
        try{
            MyResource mr = new MyResource();
            mr.doSomething();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }

}
