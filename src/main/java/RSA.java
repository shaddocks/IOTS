import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Random;
public interface RSA {

    static String fill(String str){//补位
        int i=16-str.length();
        while(i!=0){
            str="0"+str;
            i--;
        }
        return str;
    }
    static String toBinary(String str){
        String result="",temp;
        for(int i=0;i<str.length();i++){
            temp=fill(Integer.toBinaryString(str.charAt(i)));
            result +=temp;
        }
        return result;
    }
    static String arraytoBinary(int[] str){
        //把数组转化为二进制字符串
        String result="",temp;
        for(int i=0;i<str.length;i++){
            temp=fill(Integer.toBinaryString(str[i]));
            result +=temp;
        }
        return result;
    }

    static String toString1(String str,int x){
        String s="";
        for (int j=0;j<str.length()/x;j++) {
            String t1 = str.substring(j * x, j * x + x);
            int temp = Integer.valueOf(t1, 2);
            s = s + (char) temp;
        }
        return s;
    }
    static String toString(int de[]){
        String bin="";
        for (int j=0;j<de.length;j++) {
            bin+=fill(Integer.toBinaryString(de[j]));
        }

        String s=toString1(bin,16);
        return s;
    }
    static int[] todemical(String bin){
        int de[]=new int[bin.length()/16];
        for (int j=0;j<de.length;j++) {
            de[j]=Integer.valueOf(bin.substring(j*16,j*16+16),2);
        }
        return de;
    }
    static int[] rsa(){// p q n pk sk
        int p,q,n;
        do{
            long l = System.currentTimeMillis();
            p = (int)( l % 100);
        }while (prime_check(p)!=true||p<20);
        do{
            long l = System.currentTimeMillis();
            q = (int)( l % 50);
        }while (prime_check(q)!=true||q<20||p==q);
        n=p*q;
        int pk=public_key(p,q);
        int sk=private_key(pk,p,q);
        int para[]={p,q,n,pk,sk};
        return para;
    }
    static boolean prime_check(int num){
        double max =Math.sqrt((double)num);
        for (int i=2;i<=max;i++){
            if(num%i==0){
                return false;
            }
        }
        return true;
    }

    static boolean relatively_prime(int n1,int n2){
        double max=Math.min(Math.sqrt((double)n1),Math.sqrt((double)n2));
        if(n1%n2==0||n2%n1==0){
            return false;
        }
        if(prime_check(n1)||prime_check(n2)){
            return true;
        }
        for (int i=2;i<max;i++){
            if(n1%i==0&&n2%i==0){
                return false;
            }
        }
        return true;
    }

    static int private_key(int e,int p,int q){//私钥
        int f = (p - 1) * (q - 1);
        for(int d = 1; d < f; d ++)    //找到逆元d
        {
            if((e * d )% f == 1)
                return d;
        }
        return 0;
    }
    static int public_key(int p,int q){
        int f=(p-1)*(q-1);
        int e;
        Random random = new Random();
        do{
            e = random.nextInt(f);
        }while (!relatively_prime(e,f));
        return e;
    }

    static String encode(String text, int pk, int n){
        String bin=toBinary(text);
        int de_m[]=todemical(bin);
        BigInteger N = new BigInteger(String.valueOf(n));
        for (int i=0;i<de_m.length;i++) {
            BigInteger M = new BigInteger(String.valueOf(de_m[i]));
            de_m[i] = M.pow(pk).remainder(N).intValue();
        }
          String c=arraytoBinary(de_m);
        return c;
    }

    static  String decode(String c, int sk, int n){
        int de_m[]=todemical(c);
        BigInteger N=new BigInteger(String.valueOf(n));
        for (int i=0;i<de_m.length;i++) {
            BigInteger C=new BigInteger(String.valueOf(de_m[i]));
            de_m[i]=C.pow(sk).remainder(N).intValue();
        }
        String m=arraytoBinary(de_m);//bin
        m=toString(todemical(m));
        return m;
    }

    static String Signature(String m,int sk,int n){
        String Encrypted =md5(m);//bin
        Encrypted= decode(Encrypted,sk,n);
        return Encrypted;
    }

    static boolean Verify(String s,String m,int pk,int n){
        String M= encode(s,pk,n);
        String h=md5(m);
//        System.out.println("Ve:"+M+" h(M)"+h);

        if(M.equals(h)){
            return true;
        }else {
            return false;
        }
    }
    static String md5(String m) {
        try {
            MessageDigest md = MessageDigest.getInstance("md5");
            byte[] bytes = md.digest(m.getBytes("UTF-8"));
            String str = Base64.getEncoder().encodeToString(bytes);
            str=toBinary(str);
            return str;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;// 发生异常返回空
    }



     static void main(String[] args) {

        int[] p=rsa();
         System.out.println("p="+p[0]+"  q="+p[1]);

         String mm="abc";
        int pk=RSA.public_key(p[0],p[1]);
        int sk=RSA.private_key(pk,p[0],p[1]);
        String c=RSA.encode(mm,pk,p[2]);
        String s=Signature(mm,sk,p[2]);
        System.out.println("pk="+pk+"  sk="+sk);
        System.out.println("ENcode=>688:"+c);
        System.out.println("Decode=>"+RSA.decode(c,sk,p[2]));
        System.out.println("sig"+s);
        System.out.println(Verify(s,mm,pk,p[2]));
    }
}

