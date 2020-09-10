import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class Client {
    public static void main(String[] args) {
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter bw;
        String str;
        Scanner in = new Scanner(System.in);
        try {
            Socket socket = new Socket("192.168.43.3", 8888);
//     System.out.println(socket.getInetAddress());// 输出连接者的IP。
            System.out.println("成功连接服务器");
                osw = new OutputStreamWriter(socket.getOutputStream());
                bw = new BufferedWriter(osw);
                System.out.print("回复:");
                str = in.nextLine();
                bw.write(str + "\n");
                bw.flush();
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                System.out.println(socket.getInetAddress() + ":" + br.readLine());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
