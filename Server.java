import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.BufferedReader;
import java.io.Console;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.IOException;


import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;

public class Server {

    private static boolean patternCheck = false;
    private final static String HTTP_METHOD_GET = "GET";
    private final static String HTTP_METHOD_POST = "POST";
    public final static int DEFAULT_PORT = 80;
    private static File filename;
       
	public static void main(String[] args) {        
        try {
            while (patternCheck != true) {
                ServerSocket server = new ServerSocket(3001);
                
                System.out.println("Server listening to port " + 3001);
                Socket clientSocket  = server.accept();
                System.out.println("Server accepted connection");
                
                InputStream inputStream = clientSocket.getInputStream();
                OutputStream outputStream = clientSocket.getOutputStream();
                
                StringBuilder request = new StringBuilder();
                int data = inputStream.read();
                int counter = 0;
                
                while(data != -1) {
                    if(((char) data) == '\r' || ((char) data) == '\n') {
                        counter++;
                        if (counter == 4)
                            break;
                    } else {
                        counter = 0;
                    }
                    request.append((char) data);
                    data = inputStream.read();
                }
                
                System.out.println("\nRequest From client");
                System.out.println(request);
                
                
                //Regex pattern; separate entities grouped within parenthesis
                Pattern pattern = Pattern.compile("httpc(\\s+(get|post))((\\s+-v)?(\\s+-h\\s+([^\\s]+))?(\\s+-d\\s+('.+'))?(\\s+-f\\s+([^\\s]+))?)(\\s+'((http[s]?:\\/\\/www\\.|http[s]?:\\/\\/|www\\.)?([^\\/]+)(\\/.+)?)'*)");
        
                // Now create matcher object.
                Matcher m = pattern.matcher(request);

                System.out.println("x");

                if (m.find()) {
                    System.out.println("y");

                    patternCheck = true;
                    /*
                    * Group 2: Get or Post				m.group(2)
                    * Group 4: verbose -v				m.group(4)
                    * Group 5: header -h				m.group(5)
                    * Group 6: Header content 			m.group(6)
                    * Group 7: data -d					m.group(7)
                    * Group 8: Data content			m.group(8)
                    * Group 9: file -f					m.group(9)
                    * Group 10: File content			m.group(10)
                    * Group 12: URL					m.group(12)
                    * Group 14: Host					m.group(14)
                    * Group 15: Path					m.group(15)
                    */

                    String type = m.group(2);
                    System.out.println("DEBUG TYPE:" + type);

                    //Assign the path if not empty
                    String path = "";

                    if (m.group(15) != null) {
                        path = m.group(15).replaceAll("'", "").trim();
                        filename = new File(m.group(10));
                    }
                    
                    System.out.println("DEBUG PATH:" + path);

                    if (type.equals("GET")) {
                        get(path, filename);
                    } else if (type.equals("POST")) {
                        post(path, filename);
                    }       
                    
                System.out.println("DEBUG FILENAME:" + filename);
                clientSocket.close();
                server.close();
        }
        
    }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void get(String path, File filename){
            String body = "";
			String request = "";
		if (path == "" || path == null){
            body = "{\"A2\" : \"get request\"}";
			
			String response = 	"HTTP/1.0 200 ok\r\n"
							 	+ "Content-Length: " + body.length() + "\r\n"
							 	+ "Content-Disposition: inline"+ "\r\n"
							 	+ "Content-Disposition: attachment; filename=\"get.json\"" + "\r\n"
								+ "Content-Type: application/json\r\n\r\n"
			 					+ body;
			
			System.out.println("Response sent to client\n" + response);
        }else{
            
            try {  
                BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in.readLine()) != null) {
					String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

					String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
						StringBuilder.append(linesArray[i]+",");
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

                    } in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
        }			
    }

    public static void post(String path, File filename) {
        String body = "";
        String request = "";

        try {  
            	BufferedReader in = new BufferedReader(new FileReader(filename));
				String line = "";
				StringBuilder StringBuilder = new StringBuilder();

                while ((line = in .readLine()) != null) {
					String formattedLine = line.replaceAll("[\\{\\}]", "").replaceAll("\\s", "");

					String[] linesArray = formattedLine.split(",");
                    for (int i = 0; i < linesArray.length; i++) {
						StringBuilder.append(linesArray[i]+",");
					}				
					body = "{"+StringBuilder.toString().substring(0, StringBuilder.length() - 1)+"}";
					request = "POST /post?info=info HTTP/1.0\r\n"
							+ "Content-Type:application/json\r\n"
							+ "Content-Length: " + body.length() +"\r\n"
							+ "\r\n"
							+ body;

				} in .close();
			
        } catch (Exception e) {
            e.printStackTrace();
        }

       
    }

    private static void formattedOutputResponse(String response) {

       String[] responseFormatted = response.split("\n\n");

            for (int i = 1; i < responseFormatted.length; i++)
                System.out.println(responseFormatted[i]);
        
    }

}