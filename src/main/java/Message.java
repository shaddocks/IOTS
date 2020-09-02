
public abstract class Message implements DES,RSA  {
    String m1(String ID_c, String ID_tgs, String Ts1) {//C->AS发起请求
        String message = (ID_c + "*" + ID_tgs + "*" + Ts1);
        return message;
    }

    String m2(String ID_tgs, String Ts2, String lifetime, String Kc_tgs, String TGT,String IDc) {//AS->C回复
        String message = (ID_tgs +"*" +Ts2+"*"+ lifetime +"*"+ Kc_tgs+"*"+ TGT);
        message=DES.encode(IDc,message);
        return message;
    }

    String TGT(String ID_c, String ID_tgs, String Ts) {
        String message = (ID_c + "*" + ID_tgs + "*"+ Ts);
        return message;
    }

    String m3(String ID_v, String TGT, String ID_c, String AD_c, String TS_3,String Kc_tgs) {//C->TGS发起请求
        String m=ID_c+"*"+AD_c+"*"+TS_3;
        String encoded = DES.encode(Kc_tgs,m);//encode
        String message = (ID_v + "*" + TGT + "*" + encoded);
        return message;
    }

    String m4(String Kc_v, String ID_v, String TS4,String ST,String Kc_tgs) {//TGS->C 回复
        String message = DES.encode(Kc_tgs,(Kc_v+"*"+ID_v+"*"+TS4+"*"+ST));
        return message;
    }

    String ST(String Kc_v, String ID_c, String AD_c, String IDv, String TS4, String lifetime2) {
        String message = DES.encode(IDv,(Kc_v+"*"+ID_c+"*"+AD_c+"*"+IDv+"*"+TS4+"*"+lifetime2));
        return message;
    }

    String m5(String ST, String IDc, String ADc,String TS5,String Kc_v) {//C->S发起请求
        String encoded = DES.encode(Kc_v,(IDc+"*"+ADc+"*"+TS5));
        String message = (ST +"*"+ encoded);
        return message;
    }

    String m6(String TS5,String Kc_v) {//S->C回复
     String message=DES.encode(Kc_v,(TS5+1));//
        return message;
    }
    String m7(String ID_c,String K_c,int pk,int n){//C->A提交注册请求
        String message=RSA.encode((ID_c+"*"+K_c),pk,n);
        return message;
    }
    String m8(Boolean FB,int sk,int n){//AS反馈
        String message;
        if(FB){
            message=RSA.encode("11",sk,n);
            return message ;//11
        }else {
            message=RSA.encode("00",sk,n);
            return message ;//
        }
    }
    String m9(String Kc_v){//刷新目录
        String message=DES.encode(Kc_v,"11");
        return message;
    }

    String m10(int n,String name,String Kc_v){//S->C返回文件目录数据
        String message =DES.encode(Kc_v,(String.valueOf(n)+"*"+name));
        return message;
    }
    String m11(String name,int sum,int n,String file,String Kc_v){//C->S上传文件
        String message =DES.encode(Kc_v,(name+"*"+String.valueOf(sum)+"*"+String.valueOf(n)+"*"+file));
        return message;
    }
    String m12(String order,String send,String k){//ack
        String message =DES.encode(k,(order+"*"+send));
        return message;
    }
    String m13(String filename,String Kc_v){//C->S发送下载请求
        String message =DES.encode(Kc_v,filename);
        return message;
    }
    String m14(String filename,String piece,String num,String file,String Kc_v){//S->C发送指定文件
        String message =DES.encode(Kc_v,(filename+"*"+piece+"*"+num+"*"+file));
        return message;
    }
    String m15(String IDc,String Kc,String lTS,int pk,int n){//AS->TGS 同步注册信息
        String message =RSA.encode((IDc+"*"+Kc+"*"+lTS),pk,n);
        return message;
    }
    String m16(String IDc,String TS4,int pk,int n){//TGS->AS 同步时间戳
        String message =RSA.encode((IDc+"*"+TS4),pk,n);
        return message;
    }
    String m17(String sys_m,int pk,int n){//AS->C错误信息反馈
        String message =RSA.encode(sys_m,pk,n);
        return message;
    }
    String m18(String sys_m,int pk,int n){//TGS->C错误信息
        String message =RSA.encode(sys_m,pk,n);
        return message;
    }
    String m19(String sys_m,int pk,int n){//S->C错误信息
        String message =RSA.encode(sys_m,pk,n);
        return message;
    }
    String m20(String filemane,String Kc_v){//C->S上传请求
        String message =DES.encode(Kc_v,filemane);
        return message;
    }
    String m21(String filename,String Kc_v){//C->S 删除指定文件
        String message =DES.encode(Kc_v,filename);
        return message;
    }
    String m22(String order_fb,String delete_fb,String k){//S->C返回删除结果
        String message =DES.encode(k,(order_fb+"*"+delete_fb));
        return message;
    }
    String m23(String state,String IDc,String Kc_v){//C->S 发送离线请求
        String message =DES.encode(Kc_v,(state+"*"+IDc));
        return message;
    }
    String m24(String offl_fb,String Kc_v){//S->C离线反馈
        String message =DES.encode(Kc_v,offl_fb);
        return message;
    }
}
