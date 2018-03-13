package httpmapper.builder;

public class TestBuilder {


    private String orderId;
    private String orderStatusCode;
    private String name;
    private String appid;

    public static TestBuilderBuilder newBuilder(){
        return new TestBuilderBuilder();
    }



    private TestBuilder(String name) {
        this.orderId = orderId;
        this.orderStatusCode = orderStatusCode;
        this.name = name;
        this.appid = appid;
    }

    private static class TestBuilderBuilder{

        private String orderId;
        private String orderStatusCode;
        private String name;
        private String appid;

        public TestBuilderBuilder() {
        }

        public void buildOrder(String orderId,String orderStatusCode){
            new TestBuilderBuilder();
        }

        public TestBuilder  buildCard(String name){
            return new TestBuilder(name);
        }

    }

    public static void main(String[] args) {
        TestBuilder testBuilder = TestBuilder.newBuilder().buildCard("zhang");
    }

}
