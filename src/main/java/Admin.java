

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/Admin")
public class Admin extends HttpServlet {
	Connection con;
	private static final long serialVersionUID = 1L;
       
    public Admin() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
        	checkAccess(out,request);
        } catch (Exception e) {  out.println(e); }
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
	 	HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try {
            String operacia = request.getParameter("operacia");
		 	con = (Connection) session.getAttribute("spojenie");
            if (operacia.equals("showorders")) {
            	showOrdersAdmin(out,request);
            }
            if (operacia.equals("showusers")) {
            	showUsersAdmin(out,request);
            }
            if (operacia.equals("deleteorder")) {
            	deleteOrder(request.getParameter("order_id"));
            	showOrdersAdmin(out,request);
            }
            if (operacia.equals("changestate")) {
            	changeState(request.getParameter("order_id"), request.getParameter("stav"));
            	showOrdersAdmin(out,request);
            }
            if(operacia.equals("makeadmin")) {
            	setAdmin(request.getParameter("user_id"),Boolean.parseBoolean(request.getParameter("isadmin")));
            	showUsersAdmin(out,request);
            }

            
        } catch (Exception e) {  out.println(e); }
	}
	
	protected void checkAccess(PrintWriter out, HttpServletRequest request) {
	 	HttpSession session = request.getSession();
	 	con = (Connection) session.getAttribute("spojenie");
	 	if(session.getAttribute("admin") == null)
	 		accessDenied(out);
	 	else if((boolean) session.getAttribute("admin"))
	 		showOrdersAdmin(out,request);
	 	else
	 		accessDenied(out);
	}
	
	protected void accessDenied(PrintWriter out) {
		out.print("<h1 style='color: red; text-align: center;'>Access denied!");
		out.print("<div><a href='Main_Servlet'>Main Page</a></div></h1>");
	}
	protected void navigation(PrintWriter out, HttpServletRequest request) {
		out.print("<html><head>"
				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
				+ "<link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\">"
				+ "	<title>Admin Page</title>"
				+ "</head>");
		out.print("<div class=topnav>"
				+ "	   <form class=nav action=Admin method=post>"
				+ "		<input type=hidden name=operacia value=showorders><input type=submit value=Orders>"
				+ "	   </form>"
				+ "	   <form class=nav action=Admin method=post>"
				+ "		<input type=hidden name=operacia value=showusers><input type=submit value=Users>"
				+ "	   </form>"
				+ "  <div class=topnav-right>"
				+ "	   <form class=nav action=Main_Servlet method=post>"
				+ "		<input type=hidden name=operacia value=logout><input type=submit value=Logout></form>"
				+ "  </div>"
				+ "</div>");
		out.print("</html>");
	}
	protected void showOrdersAdmin(PrintWriter out, HttpServletRequest request) {
		navigation(out,request);
        try {
			Statement stmt = con.createStatement();
	        ResultSet rs_order = stmt.executeQuery("SELECT * FROM obj_zoznam INNER JOIN users ON obj_zoznam.ID_pouzivatela = users.id");
	        out.print("<table>");
	        while(rs_order.next()) {
	        	out.print("<tr><td>"+rs_order.getString("obj_zoznam.obj_cislo")+"</td>");
	        	out.print("<td>"+rs_order.getString("obj_zoznam.datum_objednavky")+"</td>");
	        	out.print("<td>"+rs_order.getString("users.meno")+" "+rs_order.getString("users.priezvisko")+"</td>");
	        	out.print("<td>"+rs_order.getString("obj_zoznam.stav")+"</td>"
	        			+ "<td><form action=Admin method=post><input type=hidden name=operacia value=changestate><input type=hidden name=order_id value="+rs_order.getString("obj_zoznam.id")+">"
	        			+ "<select style='margin-left: 80px; margin-right:20px;' name=stav>"
	        			+ "<option value=Paid>Paid</option>"
	        			+ "<option value=Processed>Processed</option>"
	        			+ "<option value=Sent>Sent</option>"
	        			+ "</select><input type=submit value=Change></form></td>");
	        	out.print("<td><form action=Admin method=post><input type=hidden name=operacia value=deleteorder><input type=hidden name=order_id value="+rs_order.getString("obj_zoznam.id")+"><input type=submit value=Delete></form></td></tr>");
	        }
	        out.print("</table>");
	        rs_order.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	protected void showUsersAdmin(PrintWriter out, HttpServletRequest request) {
		navigation(out,request);
        try {
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM users");
	        out.print("<table>");
	        while(rs.next()) {
	        	out.print("<tr><td>"+rs.getString("meno")+"</td>");
	        	out.print("<td>"+rs.getString("priezvisko")+"</td>");
	        	out.print("<td>"+rs.getString("login")+"</td>");
	        	out.print("<td>"+rs.getString("adresa")+"</td>");
	        	out.print("<td>"+rs.getString("zlava")+"</td>");
	        	if(rs.getBoolean("je_admin"))
	        		out.print("<td>ADMIN</td>");
	        	else
	        		out.print("<td>user</td>");
	        	out.print("<td><form action=Admin method=post>"
	        			+ "<input type=hidden name=operacia value=makeadmin>"
	        			+ "<input type=hidden name=user_id value="+rs.getString("id")+">"
	        			+ "<input type=hidden name=isadmin value="+rs.getBoolean("je_admin")+">"
	        			+ "<input type=submit value='Make Admin'></form></td>");
	        	out.print("</tr>");
	        }
	        out.print("</table>");
	        rs.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	protected void deleteOrder(String order_id) {
		try {
			Statement stmt = con.createStatement();
	        stmt.executeUpdate("DELETE FROM obj_polozky WHERE ID_objednavky="+order_id);
	        stmt.executeUpdate("DELETE FROM obj_zoznam WHERE id="+order_id);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	protected void changeState(String order_id, String new_state) {
		try {
			Statement stmt = con.createStatement();
	        stmt.executeUpdate("UPDATE obj_zoznam SET stav='"+new_state+"' WHERE id="+order_id);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	protected void setAdmin(String user_id, boolean isadmin) {
		try {
			isadmin = !isadmin;
			Statement stmt = con.createStatement();
	        stmt.executeUpdate("UPDATE users SET je_admin="+isadmin+" WHERE id="+user_id);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
