import java.security.MessageDigest;
public abstract class Message implements DES,RSA  {


    public String m1(String ID_c, String ID_tgs, String Ts1,String IPs,String IPr) {//C->AS发起请求
        String message = ID_c + ID_tgs +Ts1;
        String field="010000";
        message=field+"000001"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m1_d(String data){
        String info[]=new String[3];
        info[0]=data.substring(0,8);//IDc
        info[1]=data.substring(8,16);//IDtgs
        info[2]=data.substring(16);//TS1
        return info;
    }

    public String m2(String ID_tgs, String Ts2, String lifetime, String Kc_tgs, String TGT,String IDc,String IPs,String IPr) {//AS->C回复
        String message = ID_tgs +Ts2+DemitoBin(lifetime)+Kc_tgs+TGT;
        message=DES.encode(IDc,message);
        String test =DES.decode(IDc,message);
        String field="010100";
        message=field+"000010"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;

        return message;
    }
    public String[] m2_d(String data,String IDc){
        String info[]=new String[5];
        String decoded=DES.decode(IDc,data);
        info[0]=decoded.substring(0,8);//IDtgs
        info[1]=decoded.substring(8,22);//TS2
        info[2]=BinToDemi(decoded.substring(22,30));//LT1
        info[3]=decoded.substring(30,38);//Kc_tgs
        info[4]=decoded.substring(38);//TGT

        return info;
    }
    public String TGT(String Kc_tgs,String ID_c,String AD_c,String ID_tgs, String Ts2,String LT1,int Pk_tgs,int n) {
        String message = RSA.encode((Kc_tgs+ID_c +AD_c+ID_tgs +Ts2+DemitoBin(LT1)),Pk_tgs,n);
        return message;
    }
    public String[] TGT_d(String data,int sk,int n){//RSA TGSPk公钥加密
        String info[]=new String[6];
        data=RSA.decode(data,sk,n);
        info[0]=data.substring(0,8);//Kc_tgs
        info[1]=data.substring(8,16);//IDc
        info[2]=data.substring(16,28);//ADc
        info[3]=data.substring(28,36);//IDtgs
        info[4]=data.substring(36,50);//TS2
        info[5]=BinToDemi(data.substring(50));//LT1
        return info;
    }

    public String m3(String ID_v, String TGT, String ID_c, String AD_c, String TS_3,String Kc_tgs,String IPs,String IPr) {//C->TGS发起请求
        String m=ID_c+AD_c+TS_3;
        String encoded = DES.encode(Kc_tgs,m);//encode
        String message = ID_v + TGT +encoded;
        String field="010000";
        message=field+"000011"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m3_d(String data,String Kc_tgs){
        String info[]=new String[5];
        info[0]=data.substring(0,8);//IDv
        info[1]=data.substring(8,936);//TGT
        String encoded=data.substring(936);
        String decodes=DES.decode(Kc_tgs,encoded);
        info[2]=decodes.substring(0,8);//IDc
        info[3]=data.substring(8,16);//ADc
        info[4]=data.substring(16,30);//TS3
        return info;
    }

    public String m4(String Kc_v, String ID_v, String TS4,String ST,String Kc_tgs,String IPs,String IPr) {//TGS->C 回复
        String message = DES.encode(Kc_tgs,Kc_v+ID_v+TS4+ST);
        String field="010100";
        message=field+"000100"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m4_d(String data,String Kc_tgs){
        String info[]=new String[4];
        data=DES.decode(Kc_tgs,data);
        info[0]=data.substring(0,8);//Kc_v
        info[1]=data.substring(8,16);//IDv
        info[2]=data.substring(16,30);//TS4
        info[3]=data.substring(30);//ST
        return info;
    }

    public String ST(String Kc_v, String ID_c, String AD_c, String IDv, String TS4, String lifetime2) {
        String message = DES.encode(IDv,(Kc_v+ID_c+AD_c+IDv+TS4+DemitoBin(lifetime2)));
 //      message=DES.decode(IDv,message);
        return message;
    }
    public String[] ST_d(String data,String Kc_v){
        String info[]=new String[6];
        data=DES.decode(Kc_v,data);
        info[0]=data.substring(0,8);//Kc_v
        info[1]=data.substring(8,16);//IDc
        info[2]=data.substring(16,28);//ADc
        info[3]=data.substring(28,36);//IDv
        info[4]=data.substring(36,50);//TS4
        info[5]=BinToDemi(data.substring(50));//LT2
        return info;
    }

    public String m5(String ST, String IDc, String ADc,String TS5,String Kc_v,String IPs,String IPr) {//C->S发起请求
        String encoded = DES.encode(Kc_v,(IDc+ADc+TS5));
        String message = (ST +encoded);
        String field="010000";
        message=field+"000001"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m5_d(String data,String Kc_v){
        String info[]=new String[4];
        info[0]=data.substring(0,512);//ST
        String decoded=DES.decode(Kc_v,data.substring(512));
        info[1]=decoded.substring(0,8);//IDc
        info[2]=decoded.substring(8,20);//ADc
        info[3]=decoded.substring(20,34);//TS5
        return info;
    }

    public String m6(String TS5,String Kc_v,String IPs,String IPr) {//S->C回复
        String message=DES.encode(Kc_v,(TS5));//
        String field="010100";
        message=field+"000110"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m6_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//TS5
        return info;
    }

    public String m7(String ID_c,String K_c,int pk,int n,String IPs,String IPr){//C->A提交注册请求
        String message=RSA.encode((ID_c+K_c),pk,n);
        String field="010100";
        message=field+"000111"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m7_d(String data,int sk,int n){
        String info[]=new String[2];
        data=RSA.decode(data,sk,n);
        info[0]=data.substring(0,8);//IDc
        info[1]=data.substring(8,16);//Kc
        return info;
    }

    public String m8(Boolean FB,int sk,int n,String IPs,String IPr){//AS反馈
        String message;
        if(FB){
            message=RSA.encode("11",sk,n);
        }else {
            message=RSA.encode("00",sk,n);
        }
        String field="010100";
        message=field+"001000"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message ;//
    }
    public String[] m8_d(String data,int k,int n){
        String info[]=new String[1];
        info[0]=RSA.decode(data,k,n);//Sys info
        return info;
    }

    public String m9(String Kc_v,String IPs,String IPr){//刷新目录
        String message=DES.encode(Kc_v,"11");
        String field="010100";
        message=field+"001001"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m9_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//Sys order
        return info;
    }

    public String m10(String n,String name,String Kc_v,String IPs,String IPr){//S->C返回文件目录数据
        String message =DES.encode(Kc_v,DemitoBin(n)+name);
        String field="010000";
        message=field+"001010"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m10_d(String data,String Kc_v){
        String info[]=new String[2];
        String decoded=DES.decode(Kc_v,data);
        info[0]=BinToDemi(decoded.substring(0,8));// num
        info[1]=decoded.substring(8);//name星号隔开，需拆分
        return info;
    }

    public String m11(String name,String sum,String n,String file,String Kc_v,String IPs,String IPr){//C->S上传文件
        String message =DES.encode(Kc_v,(name+DemitoBin(sum)+DemitoBin(n)+file));
        String field="010000";
        message=field+"001011"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m11_d(String data,String Kc_v){
        String info[]=new String[4];
        String decoded=DES.decode(Kc_v,data);
        info[0]=decoded.substring(0,8);//name
        info[1]=BinToDemi(decoded.substring(8,16));//num
        info[2]=BinToDemi(decoded.substring(16,24));//seq
        info[3]=decoded.substring(24);//file
        return info;
    }

    public String m12(String order,String send,String k,String IPs,String IPr){//ack
        String message =DES.encode(k,(order+send));
        String field="010000";

        message=field+"001100"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m12_d(String data,String K){
        String info[]=new String[2];
        String decoded=DES.decode(K,data);
        info[0]=decoded.substring(0,2);//re
        info[1]=decoded.substring(2,4);//re
        return info;
    }

    public String m13(String filename,String Kc_v,String IPs,String IPr){//C->S发送下载请求
        String message =DES.encode(Kc_v,filename);
        String field="010000";
        message=field+"001101"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m13_d(String data,String kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(kc_v,data);//file name
        return info;
    }

    public String m14(String filename,String sum,String num,String file,String Kc_v,String IPs,String IPr){//S->C发送指定文件
        String message =DES.encode(Kc_v,(filename+DemitoBin(String.valueOf(sum))+DemitoBin(String.valueOf(num))+file));
        String field="010000";
        message=field+"001110"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m14_d(String data,String Kc_v){
        String info[]=new String[4];
        String decoded=DES.decode(Kc_v,data);
        info[0]=decoded.substring(0,8);//file name
        info[1]=BinToDemi(decoded.substring(8,16));//num
        info[2]=BinToDemi(decoded.substring(16,24));// seq
        info[3]=decoded.substring(24);//file
        return info;
    }

    public String m15(String IDc,String Kc,String lTS,int pk,int n,String IPs,String IPr){//AS->TGS 同步注册信息
        String message =RSA.encode((IDc+Kc+lTS),pk,n);
        String field="010000";

        message=field+"001111"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m15_d(String data,int k,int n){
        String info[]=new String[3];
        String decoded=RSA.decode(data,k,n);
        info[0]=decoded.substring(0,8);//IDc
        info[1]=decoded.substring(8,16);//Kc
        info[2]=decoded.substring(16);//LTS
        return info;
    }

    public String m16(String IDc,String TS4,int pk,int n,String IPs,String IPr){//TGS->AS 同步时间戳
        String message =RSA.encode((IDc+TS4),pk,n);
        String field="010000";
        message=field+"010000"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m16_d(String data,int k,int n){
        String info[]=new String[2];
        String decoded=RSA.decode(data,k,n);
        info[0]=decoded.substring(0,8);//IDc
        info[1]=decoded.substring(8);//TS4
        return info;
    }

    public String m17(String sys_m,int pk,int n,String IPs,String IPr){//AS->C错误信息反馈
        String message =RSA.encode(sys_m,pk,n);
        String field="010000";

        message=field+"010001"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m17_d(String data,int k,int n){
        String info[]=new String[1];
        info[0]=RSA.decode(data,k,n);//
        return info;
    }

    public String m18(String sys_m,int pk,int n,String IPs,String IPr){//TGS->C错误信息
        String message =RSA.encode(sys_m,pk,n);
        String field="010000";

        message=field+"010010"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m18_d(String data,int k,int n){
        String info[]=new String[1];
        info[0]=RSA.decode(data,k,n);//
        return info;
    }

    public String m19(String sys_m,String Kc_v,String IPs,String IPr){//S->C错误信息
        String message =DES.encode(Kc_v,sys_m);
        String field="010000";
        message=field+"010011"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m19_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//
        return info;
    }

    public String m20(String filemane,String Kc_v,String IPs,String IPr){//C->S上传请求
        String message =DES.encode(Kc_v,filemane);
        String field="010000";

        message=field+"010100"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m20_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//File name
        return info;
    }

    public String m21(String filename,String Kc_v,String IPs,String IPr){//C->S 删除指定文件
        String message =DES.encode(Kc_v,filename);
        String field="010000";
        message=field+"010101"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m21_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//File name
        return info;
    }

    public String m22(String order_fb,String delete_fb,String k,String IPs,String IPr){//S->C返回删除结果
        String message =DES.encode(k,(order_fb+delete_fb));
        String field="010000";

        message=field+"010110"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m22_d(String data,String K){
        String info[]=new String[2];
        String decoded=DES.decode(K,data);
        info[0]=decoded.substring(0,2);//
        info[1]=decoded.substring(2);//
        return info;
    }

    public String m23a(String state,String IDc,int pk,int n,String IPs,String IPr){//C->AS 发送离线请求
        String message =RSA.encode(state+IDc,pk,n);
        String field="010000";
        message=field+"000000"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m23a_d(String data,int sk,int n){
        String info[]=new String[2];
        String decoded=RSA.decode(data,sk,n);
        info[0]=decoded.substring(0,2);//status
        info[1]=decoded.substring(2);//IDc
        return info;
    }

    public String m23s(String state,String IDc,String Kc_v,String IPs,String IPr){//C->S 发送离线请求
        String message =DES.encode(Kc_v,(state+IDc));
        String field="010000";
        message=field+"000000"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m23s_d(String data,String Kc_v){
        String info[]=new String[2];
        String decoded=DES.decode(Kc_v,data);
        info[0]=decoded.substring(0,2);//status
        info[1]=decoded.substring(2);//IDc
        return info;
    }


    public String m24(String offl_fb,String Kc_v,String IPs,String IPr){//S->C离线反馈
        String message =DES.encode(Kc_v,offl_fb);
        String field="010000";
        message=field+"111111"+info_integ(message,IPs,IPr)+message;
        String ver= md5_encryption(message);
        message=message+ver;
        return message;
    }
    public String[] m24_d(String data,String Kc_v){
        String info[]=new String[1];
        info[0]=DES.decode(Kc_v,data);//sys_info
        return info;
    }

    public String md5_encryption(String plainText) {
        String re_md5 = new String();
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(plainText.getBytes());
            byte b[] = md.digest();

            int i;

            StringBuffer buf = new StringBuffer("");
            for (int offset = 0; offset < b.length; offset++) {
                i = b[offset];
                if (i < 0)
                    i += 256;
                if (i < 16)
                    buf.append("0");
                buf.append(Integer.toHexString(i));
            }

            re_md5 = buf.toString();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return re_md5;
    }

    public boolean verify_m(String message){
        String encrypted=md5_encryption(message.substring(0,(message.length()-32)));
        if (encrypted.equals(message.substring(message.length()-32))){
            return true;
        }else {
            return false;
        }
    }
    public String fill(String str,int n){//补位
        int i=n-str.length();
        while(i>0){
            str="0"+str;
            i--;
        }
        return str;
    }

    public String[] Divide(String message){
        message=message.substring(0,(message.length()-32));
        String filed=message.substring(0,6);//
        String type=BinToDemi(message.substring(6,12));
        String IPs=BinToIp(message.substring(12,44));
        String IPr=BinToIp(message.substring(44,76));
        String len=BinToDemi(message.substring(76,92));
        String retain=message.substring(92,100);
        String data=message.substring(100);
        String divided[] = {filed,type,IPs,IPr,len,retain,data};
        return divided;
    }
    public String IpToBin(String IP){//IP十进制转化成二进制
        String Bin="";
        for(int i=0;i<4;i++){
            Bin+=fill(Integer.toBinaryString(Integer.parseInt((IP.substring(i*3,(i+1)*3)))),8);
        }
        return Bin;
    }
    public String BinToIp(String Bin){//二进制转化为十进制IP
        String IP="";
        int b;
        for(int i=0;i<4;i++){
            b=Integer.parseInt(Bin.substring(i*8,(i+1)*8),2);
            IP+=b;
            if(i!=3){
                IP+=".";
            }
        }

        return IP;
    }
    public String DemitoBin(String str){//十进制转二进制
        String bin="",temp;

        temp=fill(Integer.toBinaryString(Integer.parseInt(str)),8);
        bin +=temp;

        return bin;
    }
    public String BinToDemi(String Bin){//二进制转十进制
        String demi="",temp;
        Bin=fill(Bin,8);
        for(int i=0;i<Bin.length()/8;i++){
            demi +=Integer.parseInt(Bin.substring(i*8,(i+1)*8),2);;
        }
        return demi;
    }
    public String StrToBin(String str){
        char[] strChar=str.toCharArray();
        String result="",temp;
        for(int i=0;i<strChar.length;i++){
            temp=String.format("%08d",Integer.parseInt(Integer.toBinaryString(strChar[i])));
            result +=temp;
        }
        return result;
    }

    public String BinToStr(String Bin){
        String s="";
        for (int j=0;j<Bin.length()/8;j++) {
            String t1 = Bin.substring(j * 8, (j+1) * 8 );
            int temp = Integer.valueOf(t1, 2);
            s = s + (char) temp;
        }
        return s;
    }
    public String info_integ(String mes,String IPs,String IPr){
        String IPsb,IPrb,len;
        String retain="00000000";
        int n=mes.length();
        len=fill(Integer.toBinaryString(n),16);
        IPsb=IpToBin(IPs);
        IPrb=IpToBin(IPr);
        return IPsb+IPrb+len+retain;
    }
}
