import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.util.Arrays;

public class ImageLabeler {
    private static Socket clientSocket;
    private static DataOutputStream out;
    private static BufferedReader in;
    private static InputStream inStream;
    private static String ip;
    private static int port;

    public static void main(String args[]) throws UnsupportedEncodingException {
       ip = args[0];
       port = Integer.parseInt(args[1]);

       try{
           startConnection(ip,port);
           for(int i = 0 ; i < 4 ; i ++ )
           {
               sendIGETmessage();
               handleIGETmessage();
           }
           stopConnection();
       }catch (IOException e) {
           e.printStackTrace();
       }

    }

    public static void startConnection(String ip, int port) throws IOException {
        clientSocket = new Socket(ip, port);
        out = new DataOutputStream(clientSocket.getOutputStream());
        inStream = clientSocket.getInputStream();
        in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

        out.writeBytes("USER bilkentstu\r\n");
        if(!in.readLine().equals("OK"))
            stopConnection();
        out.writeBytes("PASS cs421f2019\r\n");
        if(!in.readLine().equals("OK"))
            stopConnection();
    }

    public static void sendIGETmessage() throws IOException {
        out.writeBytes("IGET\r\n");
    }

    public static void handleIGETmessage() throws IOException {
        String[] labelList = new String[3];
        for(int j = 0 ; j < 3 ; j++) {
            byte[] head = new byte[7];
            inStream.read(head);

            byte[] sizeBytes = new byte[4];
            sizeBytes[0]=0;
            for(int i = 0; i < 3; i ++)
            {
                sizeBytes[i+1] = head[i+4];
            }

            ByteBuffer wrapped = ByteBuffer.wrap(sizeBytes);
            int size = wrapped.getInt();
            System.out.println("Size " + size);
            String label = getLabel(size);
            labelList[j] = label;

            byte[] ImageData = new byte[size];
            inStream.read(ImageData);
            //ByteArrayInputStream bis = new ByteArrayInputStream(ImageData);
            //BufferedImage bImage2 = ImageIO.read(bis);
            //String pathname = "photo"+j+".jpg";
            //ImageIO.write(bImage2, "jpg", new File(pathname));
        }
        String labelMessage = String.format("ILBL %s,%s,%s\r\n", labelList[0],labelList[1],labelList[2]);
        //String labelMessage ="ILBL " + labelList[0] + "," + labelList[1] + "," + labelList[2] + "\r\n";
        System.out.println(labelMessage);
        out.writeBytes(labelMessage);
        String response = in.readLine();
        System.out.println(response);

    }

    public static String getLabel(int imageSize)
    {
        switch (imageSize) {
            case 75537 :
            case 48224 :
            case 21383 :
                return "bear";
            case 65530 :
            case 16803 :
            case 72825 :
                return "cat";
            case 38146 :
            case 58300 :
            case 56439 :
                return "dog";
            case 19017 :
            case 19041 :
            case 18505 :
                return "shark";
            default :
                return "";
        }
    }

    public static  void stopConnection() throws IOException {
        out.writeBytes("EXIT\r\n");
        in.close();
        out.close();
        clientSocket.close();
    }
}
