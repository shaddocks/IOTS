
public class test extends Message {
    public static void main(String[] args) {
        String s = "abcdefghijk";
        String AD = "192168007007";
        String ID1 = "ID000001";
        String ID2 = "ID000002";
        String k = "12345678";
        String TS = "20071212060606";
        String LT = "10";
        String order="or";
        String warning="wa";
        String num="20";
        String seq="66";
        int rec[] = RSA.rsa();
        int sen[] = RSA.rsa();
        test t = new test();

        String mes = t.m24(order,k,AD,AD);
        System.out.println(mes);
        System.out.println(t.verify_m(mes));

        System.out.println("------------------");
        String inf[] = t.Divide(mes);
        String data[] = t.m24_d(inf[6],k);
        for (int i = 0; i < inf.length; i++) {
            System.out.println(inf[i]);
        }

        System.out.println("------------------");
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i]);
        }
        System.out.println("------------------");




    }
}
