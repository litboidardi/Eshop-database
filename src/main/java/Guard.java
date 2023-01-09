import java.sql.Connection;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class Guard implements HttpSessionBindingListener {
	  Connection connection;
	  
	 public Guard(Connection c) {
	    connection = c; 
	 }

	 @Override
	 public void valueBound(HttpSessionBindingEvent event) {
		 
	 }

	 @Override
	 public void valueUnbound(HttpSessionBindingEvent event) {
	    try { 
	       if (connection != null) connection.close();
	    } catch (Exception e) { }
	  }            
	}

