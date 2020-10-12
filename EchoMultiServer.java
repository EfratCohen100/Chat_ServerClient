package Gui;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

//import SeverClient.ServerGUI;
public class EchoMultiServer extends Thread{
	static public ServerGui m_sg;
	boolean bDowork = true;
	 private PrintWriter serverBW = null;
	ArrayList<EchoClientHandler> alEchoClients = new ArrayList<>();
	static private String m_Msg = "";
	public EchoMultiServer(int port,ServerGui serverGui)
	{
		  try {
			  EchoMultiServer.m_sg = serverGui;
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
    private ServerSocket serverSocket;
 
    
    /**
     * @param the function get massage
     */
   public static void GuiUpdateMessage(String msg)
   {
	   m_Msg+=msg+"\n";
	   m_sg.UpdateMessage(m_Msg);
   }
    public void run() 
    {
 		  
	   while (bDowork)
	   {
		  try {
			    Socket clntSocket = serverSocket.accept();
			   if(!bDowork)
 			    	break;
 		        if(clntSocket.isConnected())
 		        	alEchoClients.add(new EchoClientHandler(clntSocket))  ;
 		            int len = alEchoClients.size();
 		            alEchoClients.get(len-1).start();
			}
			catch (SocketException exc) {
			    // SocketException is thrown when serverSocket is closed.
			} catch (IOException e) {
				// TODO Auto-generated catch block
				//serverSocket.close();
				e.printStackTrace();
			}
			finally {
				
			    // You don't have to close the socket, because in order to
			    // reach this block, another thread had to close the socket.

			   // serverSocket.close();
			}
		  }    
	}
    
    /**
     * The function is responsible for stopping the server 
     * from sending messages to clients
     */
    public void stopServer() {
        try {
        	  bDowork = false;
			  serverSocket.close();
			  for (EchoClientHandler echoClientHandler : alEchoClients) {
				  echoClientHandler.Stop();
			}
			 
		    } 
           catch (IOException e)
           {
			// TODO Auto-generated catch block
			e.printStackTrace();
		   }
    }
    
    
    private static class EchoClientHandler extends Thread {
        private Socket clientSocket;
        private PrintWriter mineBW = null;
        private PrintWriter[] outArr = new PrintWriter[1];
        private BufferedReader in;
        static Map<String,PrintWriter>map= new HashMap<String,PrintWriter>();
       
        public EchoClientHandler(Socket socket) {
            this.clientSocket = socket;
               
        }
        public void Stop()
        {
        	mineBW.write("disconnect");
        	mineBW.flush();
        }
        public void run() {
        	try {
            mineBW = outArr[0] = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(
              new InputStreamReader(clientSocket.getInputStream()));
             
            String inputLine;
            while ( (inputLine = in.readLine()) != null) 
            {
            	String[]inputLineParts = inputLine.split(":");
            	String sFromName = inputLineParts[0].trim();
            	inputLine = inputLineParts[1].trim();
            	String sPrefix =sFromName+" : ";
            	if(inputLine.toLowerCase().trim().startsWith("connect"))//connect,name
            	{
            		String msg =  "Client "+sFromName+" was connected!";
            		outArr[0] = RegisterClient(sFromName);
            		EchoMultiServer.GuiUpdateMessage(msg);
            		outArr[0].println(msg);
            	}
            	else	 
            	 if(inputLine.trim().toLowerCase().startsWith("set_msg_all"))
            	 {
            		 String[] ClientNames = GetAllClientNames(inputLine); 
            		 PrintWriter[] prwArr;
            		 prwArr = GetAllClientOut(inputLine);
            		  String msg = GetClinetMessage(inputLine);
            		  if(ClientNames.length>0 && prwArr.length >0)
             		  {
             			 int len = ClientNames.length;
                		 for (int idx=0;idx<len;idx++ ) 
                		 {
        		           PrintWriter prw  = prwArr[idx];
        		           prw.println(sPrefix+msg);
                		 }
            			 EchoMultiServer.GuiUpdateMessage(sPrefix+ msg+"to all recepient was sent!");
					  }
            		  outArr[0].println("\n");
            	 }
            	 else
            	 if(inputLine.trim().toLowerCase().startsWith("set_msg"))//set_msg,name
            	 {
            		String[] msg =new String[1];
            		String clientName = GetClinetName(inputLine);
            		boolean bsuccess = GetClientOutAndMessage(inputLine,outArr,msg);
            		if(bsuccess)
            		{
            			 outArr[0].println(sPrefix+msg[0]);
            			 EchoMultiServer.GuiUpdateMessage(sPrefix+msg[0]+" to "+clientName+" was sent!");
            		}
            		else
            			outArr[0].println("\n");
            	 }
            	 else
                	 if(inputLine.trim().toLowerCase().startsWith("get_users"))
                	 {
                		 String[] ClientNames = GetAllClientNames(inputLine);           
                		 boolean ans = GetClientOut(inputLine,outArr);
                		 if(ClientNames.length>0 && ans == true)
                		 {
                			 int len = ClientNames.length;
                			 String sAllClientsNames = "";
                    		 for (int idx=0;idx<len;idx++ ) 
                    		 {
                    			 sAllClientsNames+=ClientNames[idx]+" ";
							 } 
                    		 outArr[0].println(sAllClientsNames);
                		     EchoMultiServer.GuiUpdateMessage(sPrefix+"get all clients names was sent!");
                		 }
                		 else
                	         outArr[0].println("\n");
                	 }
	            	 else
		                  if (inputLine.trim().toLowerCase().startsWith("disconnect"))
		                  {
		                    String[] sParts = inputLine.split(",");  
		                	EchoMultiServer.GuiUpdateMessage("bye to : "+sParts[1]);  
		                    outArr[0].println("bye");
		                    break;
		                  }
	                  else {
			            	EchoMultiServer.GuiUpdateMessage("Unknown message");
			                outArr[0].println("Unknown message");
	                  }
            
            }
           
            
			in.close();
            outArr[0].close();
            clientSocket.close();
        	
        	}
            catch (IOException e)
        	{
   				// TODO Auto-generated catch block
   				e.printStackTrace();
   			}
    }
   
        /**
         * The function check if we send message by Receiving a request line of 
         * name of client, mane of command and sender- Check integrity
         * @param the function get message of the client 
         * @return check if send message between clients 
         */
	private boolean GetClientOutAndMessage(String sInput, PrintWriter[] pwOut, String[] msgOut) {
		boolean ans = false;
		msgOut[0] = GetClinetMessage(sInput);
		String ClientName = GetClinetName(sInput);
		pwOut[0]= null;
		if(map.containsKey(ClientName))
			pwOut[0] =	map.get(ClientName);
		if(pwOut[0]!=null && msgOut[0]!="")
			ans = true;
    	return ans;
		}

	 /**
	 * the server get all clients name 
     * @param the function get line of massage and get all clients name
     * that connected to the server
     * @return arrays of strings 
     */
	private String[] GetAllClientNames(String inputLine)
	{
		if(map.isEmpty())
			return null;
		else
		{
			  Set<String> setCol = map.keySet();
			  String[] setArr =new String[setCol.size()];
			  setCol.toArray(setArr);
			  return  setArr;
		}
	}

	
	 /**
	 * After that server get all clients name that connected 
	 * to the server, The server sends the list of contacts to the client that asked form him   
     * @param the function get message 
     * @return arrays of printWriter 
     */
	private PrintWriter[] GetAllClientOut(String inputLine) {
		if(map.isEmpty())
				return null;
			else
			{
				Collection<PrintWriter> col = map.values();
				PrintWriter[] colArr =new PrintWriter[col.size()];
				col.toArray(colArr);
				  return  colArr;
			}	
		}

	 /**
	 * When the server wants to send the message 
	 * back to the client it checks that the client is indeed connected
     * @param the function get message
     * @return boolean true/ false if the client exist in the list connected 
     */	
	private boolean GetClientOut(String sInput, PrintWriter[] pwOut) 
	{
		boolean ans = false;
		String ClientName = GetClinetName(sInput);
		pwOut[0]= null;
		if(map.containsKey(ClientName))
			pwOut[0] =	map.get(ClientName);
		if(pwOut[0]!=null )
			ans = true;
    	return ans;
	}	
	
	
	 /**
	 * The server accepts the request that the client sent to it
     * @param the function get message - line of message 
     * @return the request that client send to server
     */
    private String GetClinetMessage(String sInput) {
    	String[] msgParts = null;
    	if(sInput.contains(","))
    	{
    	 msgParts = sInput.split(",");
    	if(msgParts.length>2)
        	return msgParts[2];
        }
    	return null;
	}

    
    /**
     * The server receives the request from the client 
     * and extracts the user name that client want to send him the message
     * @param the function get message
     * @return get name of client
     */
	private String GetClinetName(String sInput)//2
    {
    	String[] msgParts = null;
    	if(sInput.contains(","))
    	{
    	 msgParts = sInput.split(",");
    	if(msgParts.length>1)
        	return msgParts[1];
        }
    	return "";
    }
	private PrintWriter  RegisterClient(String inputLine) {
		String clntName = "";
		clntName = inputLine;//GetClinetName(inputLine);
		map.put(clntName,outArr[0]);
		return outArr[0];
	}
}
}