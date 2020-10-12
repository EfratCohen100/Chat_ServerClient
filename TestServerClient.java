package Gui;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TestServerClient 
{
	@Test
	public void givenClient_whenServerEchosMessage_thenCorrect() 
	{
	  String msg1= Client.GuiUpdateMessage("hello");
	  String msg2= Client.GuiUpdateMessage("World");
	  String msg3=  Client.GuiUpdateMessage("!");
	  String msg4= Client.GuiUpdateMessage("good bye");
	   
	  assertEquals("hello",msg1);
	  assertEquals("world", msg2);
	  assertEquals("!", msg3);
	  assertEquals("good bye", msg4);

	}
	
	@Test
	public void givenClient_whenServerResponds_thenCorrect() {
	    
	    Client.Connect();
	    String msg1 = Client.GuiUpdateMessage("hello");
	    String msg2 = Client.GuiUpdateMessage("world");
	    String terminate = Client.GuiUpdateMessage(".");
	     
	    assertEquals(msg1, "hello");
	    assertEquals(msg2, "world");
	    assertEquals(terminate, "bye");
	
	}
	   
	     
}
