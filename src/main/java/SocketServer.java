import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
public class SocketServer {
    public static void main(String[] args) {
        InputStreamReader isr;
        BufferedReader br;
        OutputStreamWriter osw;
        BufferedWriter bw;
        String str;
        Scanner in = new Scanner(System.in);
        try {
            ServerSocket server = new ServerSocket(4444);// 在本机的4444端口开放Server
            Socket socket = server.accept();// 只要产生连接，socket便可以代表所连接的那个物体，同时这个server.accept()只有产生了连接才会进行下一步操作。
            System.out.println(socket.getInetAddress());// 输出连接者的IP。
            System.out.println("建立了一个连接！");
            while (true) {
                isr = new InputStreamReader(socket.getInputStream());
                br = new BufferedReader(isr);
                System.out.println(socket.getInetAddress() + ":" + br.readLine());
                osw = new OutputStreamWriter(socket.getOutputStream());
                bw = new BufferedWriter(osw);
                System.out.print("回复:");
                str = in.nextLine();
                bw.write(str + "\n");
                bw.flush();
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}