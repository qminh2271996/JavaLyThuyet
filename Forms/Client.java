/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Forms;

import java.io.DataInputStream;
import java.io.PrintStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client implements Runnable {

  // TAO SOCKET 
  private static Socket clientSocket = null;
  // NGÕ RA Stream
  private static PrintStream os = null;
  // NGO VAO STREAM
  private static DataInputStream is = null;

  private static BufferedReader inputLine = null;
  private static boolean closed = false;
  
  public static void main(String[] args) {

    // port mặc dinh
    int portNumber = 8888;
    // sử dụng local host
    String host = "127.0.0.1";

    if (args.length < 2) {
      System.out
          .println("Usage: java MultiThreadChatClient <host> <portNumber>\n"
              + "Now using host=" + host + ", portNumber=" + portNumber);
    } else {
      host = args[0];
      portNumber = Integer.valueOf(args[1]).intValue();
    }

    /*
     * mở socket kết nối tới port và host, mở input và output Stream.
     */
    try {
      clientSocket = new Socket(host, portNumber);
      inputLine = new BufferedReader(new InputStreamReader(System.in));
      os = new PrintStream(clientSocket.getOutputStream());
      is = new DataInputStream(clientSocket.getInputStream());
    } catch (UnknownHostException e) {
      System.err.println("khong nhan biet duoc host:  " + host);
    } catch (IOException e) {
      System.err.println("khong the ket noi toi host: "
          + host);
    }

    /*
     * nếu quá trình kết nối, khởi tạo hoàn thành thì đọc/ghi dữ diệu bắt đầu
     * lúc này socket đã được mở.
     */
    if (clientSocket != null && os != null && is != null) {
      try {

        /* Create a thread to read from the server. */
        new Thread(new Client()).start();
        while (!closed) {
          os.println(inputLine.readLine().trim());
        }
        /*
         * Close the output stream, close the input stream, close the socket.
         */
        os.close();
        is.close();
        clientSocket.close();
      } catch (IOException e) {
        System.err.println("IOException:  " + e);
      }
    }
  }

  /*
   *tạo một thread để đọc dữ liệu từ server
   * 
   * @see java.lang.Runnable#run()
   */
  public void run() {
    /*
     * Keep on reading from the socket till we receive "Bye" from the
     * server. Once we received that then we want to break.
     */
    String responseLine;
    try {
      while ((responseLine = is.readLine()) != null) {
        System.out.println(responseLine);
        if (responseLine.indexOf("*** tam biet") != -1)
          break;
      }
      closed = true;
    } catch (IOException e) {
      System.err.println("IOException:  " + e);
    }
  }
}
