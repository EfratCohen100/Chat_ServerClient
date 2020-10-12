package Gui;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import Gui.ClientGui;
 


public class Client
{
	private static ClientHandler clntHndler;
    private static Socket socket;
    private static BufferedWriter bw; 
    private static BufferedReader br;
    private static ClientGui m_cg;
 
    
    
    public Client(ClientGui cg)
    {
    	
    	m_cg = cg;
    	
    }
    
    static private String m_Msg="";
    
  
    /**
     *
     * @param the function get message of the client 
     * @return get String of the message 
     */
    public static String GuiUpdateMessage(String msg)
    {
    	 m_Msg+=msg+"\n";
    	    
    	m_cg.UpdateMessage(m_Msg);
    	
    	
    	return m_Msg;
    	
    }

    
    /**
     * the function close the connection between clients to server
     */
    public static void Close()
    {
    	  try {
    		 
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  try {
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	  try {
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    
    
    /**
     * @param the function performing connect between server and client
     */
    public static void Connect()
    {
    	 /****************** BufferedWriter    ****************/
    	 String host = "localhost";
         int port = 81;//25000;
         InetAddress address = null;
		try {
			address = InetAddress.getByName(host);
		} catch (UnknownHostException e1) {
			         e1.printStackTrace();
		}
         try {
			socket = new Socket(address, port);
		} catch (IOException e) {
          			e.printStackTrace();
		}

         //Send the message to the server
         OutputStream os = null;
		try {
			os = socket.getOutputStream();
		} catch (IOException e) {
			e.printStackTrace();
		}
         OutputStreamWriter osw = new OutputStreamWriter(os);
         bw = new BufferedWriter(osw);
         
        /****************** BufferedReader    ****************/
         //Get the return message from the server
         InputStream is = null;
		try {
			is = socket.getInputStream();
			
		} catch (IOException e) {
         			e.printStackTrace();
		}
         InputStreamReader isr = new InputStreamReader(is);
         br = new BufferedReader(isr);
         clntHndler = new ClientHandler(br);
         clntHndler.start();
    }
    
    /**
     * @param the function send massage to server
     */
    public static void Send()
    {
        try
        {
           
            SendMessageToServer();
           // GetMessageFromServer();
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
        }
        finally
        {
            //Closing the socket
            try
            {
                //socket.close();
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
	
	private static void SendMessageToServer() throws IOException {
		String clientMsg = "Hello from client:\n";
		bw.write(clientMsg);
		bw.flush();//clear buffer and send the rest
		System.out.println("Message sent to the server : "+clientMsg);
	}
	
	 /**
	 * the server get the message from the client 
     * @param the function get massage
     */
	public static void SendToServer(String Msg) {
		try 
		{
			String clientMsg = Msg+"\n";
						
			bw.write(clientMsg);
		
			bw.flush();//clear buffer and send the rest
			System.out.println("Message sent to the server : "+clientMsg);
			//GetMessageFromServer();
		} 
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	private static class ClientHandler extends Thread {
	  
	    private BufferedReader br;
	  

	    public ClientHandler(BufferedReader in) {
	        this.br = in;
	        
	        
	    }
	    
	    public void run() {
	    	try {
	     
	         
	        String message;
	        while ((message = br.readLine()) != null) 
	        {
	        	if ("bye".equals(message)) {
	        		GuiUpdateMessage("bye");
                    break;
	        	}
				GuiUpdateMessage(message);
				System.out.println("Message received from the server : " +message);
	        }
	        //br.close();
	        Close();
	    	}
	        catch (IOException e)
	    	{
					// TODO Auto-generated catch block
					e.printStackTrace();
			}
	    }
	}
}