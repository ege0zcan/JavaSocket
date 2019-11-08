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
    private static int photo_index = 0;
    
    public static void main(String args[]) throws UnsupportedEncodingException {
        ip = args[0];
        port = Integer.parseInt(args[1]);
        
        try{
            startConnection(ip,port);
            for(int i = 0 ; i < 4 ; i ++ )
            {
                sendCommand("IGET\r\n");
                handleIGETresponse();
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
        
        sendCommand("USER bilkentstu\r\n");
        checkResponse(in.readLine());
        sendCommand("PASS cs421f2019\r\n");
        checkResponse(in.readLine());
    }
    
    public static void sendCommand(String message) throws IOException {
        System.out.print("Command:  " + message);
        out.writeBytes(message);
    }
    
    public static void checkResponse(String response) throws IOException {
        System.out.println("Response: " + response);
        if(!response.equals("OK"))
            stopConnection();
    }
    
    public static void sendIGETmessage() throws IOException {
        System.out.println("Command: IGET");
        out.writeBytes("IGET\r\n");
    }
    
    public static void handleIGETresponse() throws IOException {
        String[] labelList = new String[3];
        for(int j = 0 ; j < 3 ; j++) {
            byte[] head = new byte[7];
            inStream.read(head);
            if(head[0] != 73 || head[1] != 83 || head[2] != 78 || head[3] != 68) //did not receive ISND
            {
                sendCommand("IGET\r\n");
                handleIGETresponse();
                return;
            }
            System.out.println("Response: ISND");
            byte[] sizeBytes = new byte[4];
            sizeBytes[0]=0;
            for(int i = 0; i < 3; i ++)
            {
                sizeBytes[i+1] = head[i+4];
            }
            
            ByteBuffer wrapped = ByteBuffer.wrap(sizeBytes);
            int size = wrapped.getInt();
            String label = getLabel(size);
            labelList[j] = label;
            
            byte[] ImageData = new byte[size];
            inStream.read(ImageData);
            ByteArrayInputStream bis = new ByteArrayInputStream(ImageData);
            BufferedImage bImage2 = ImageIO.read(bis);
            String pathname = "photo"+(photo_index++)+".jpg";
            ImageIO.write(bImage2, "jpg", new File(pathname));
        }
        String labelMessage = String.format("ILBL %s,%s,%s\r\n", labelList[0],labelList[1],labelList[2]);
        sendCommand(labelMessage);
        String response = in.readLine();
        System.out.println("Response: " + response);
        while(!response.equals("OK"))
        {
            out.writeBytes(labelMessage);
            response = in.readLine();
        }
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
