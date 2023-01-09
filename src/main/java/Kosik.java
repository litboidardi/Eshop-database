

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet("/Kosik")
public class Kosik extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Connection con;
    public Kosik() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
		PrintWriter out = response.getWriter();
	    try {
   		 	HttpSession session = request.getSession();
   		 	con = (Connection) session.getAttribute("spojenie");
			if(session.getAttribute("ID")!=null && con != null)
				showCart(out,request);
			else
				response.sendRedirect("Main_Servlet");
	    } catch (Exception e) {  out.println(e); }

	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
            String operacia = request.getParameter("operacia");
            if (operacia.equals("removefromcart")) {
            	removeFromCart(out, request, request.getParameter("product_id"));
            	showCart(out,request);
            }
            if(operacia.equals("clearall")){
            	clearCart(getUserID(request));
            	showCart(out,request);
            }
            if(operacia.equals("makeorder")) {
            	makeOrder(out,request,request.getParameter("suma"));
			}

            
        } catch (Exception e) {  out.println(e); }
	}
	
	protected void navigation(PrintWriter out, HttpServletRequest request) {
	    HttpSession session = request.getSession();
	    String meno = (String) session.getAttribute("meno");
	    String priezvisko = (String) session.getAttribute("priezvisko");
		out.print("<html><head>"
				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
				+ "<link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\">"
				+ "	<title>Cart</title>"
				+ "</head>");
		out.print("<div class=topnav>"
				+ "  <a class=clickable href='Main_Servlet'>Home</a>"
				+ "	   <form class=nav action=Main_Servlet method=post>"
				+ "		<input type=hidden name=operacia value=showorders><input type=submit value=Orders></form>"
				+ "  <div class=topnav-right>"
				+ "    <a>"+meno+" "+priezvisko+"</a>"
				+ "    <a href='Kosik'>Cart</a>"
				+ "	   <form class=nav action=Main_Servlet method=post>"
				+ "		<input type=hidden name=operacia value=logout><input type=submit value=Logout></form>"
				+ "  </div>"
				+ "</div>");
		out.print("</html>");
	}
	protected int getUserID(HttpServletRequest request) {
		 HttpSession session = request.getSession();
		 Integer id = (Integer) (session.getAttribute("ID"));
		 if(id == null)
			 id=0;
		 return id; 
	}
	protected void showCart(PrintWriter out, HttpServletRequest request) {
		navigation(out, request);
		out.print("<div class=cart-body><div class=cart>"
				+ "<div class=Header>"
				+ " <h3 class=Heading>Cart</h3>"
				+ " <form class=nav action=Kosik method=post><input type=hidden name=operacia value=clearall><input type=submit class=Action value='Remove all'></form>"
				+ " </div>");
		
		try {
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM kosik "
	        		+ "INNER JOIN sklad ON kosik.ID_tovaru = sklad.id "
	        		+ "WHERE ID_pouzivatela="+getUserID(request));
	        double discount=0;
	        int sum_price = 0;
	        out.print("<div class=row><div class=column>");
	        while(rs.next()) {
	        	sum_price+=rs.getInt("kosik.cena");
	        	out.print("<div class=items>"
	        			+ " <div class=image-box>"
	        			+ " <img src='"+rs.getString("sklad.obrazok")+"' alt='"+rs.getString("nazov")+"' style={{ height=180px }} />"
	        			+ " </div>"
	        			+ " <div class=about>"
	        			+ " <h1 class=title>"+rs.getString("sklad.nazov")+"</h1>"
	        			+ " <h3 class=subtitle>"+rs.getString("kosik.ks")+"x</h3>"
	        			+ " </div>"
	        			+ " <div class=prices>"
	        			+ " <div class=amount>"+rs.getString("kosik.cena")+"€</div>"
	        			+ " <div class=remove>"
	        			+ " <form class=nav action=Kosik method=post><input type=hidden name=product_id value="+rs.getString("kosik.ID_tovaru")+"><input type=hidden name=operacia value=removefromcart><input type=submit class=Action value='Remove'></form>"
	        			+ " </div>"
	        			+ " </div>"
	        			+ " </div>");
	        }
	        rs.close();
	        rs = stmt.executeQuery("SELECT * FROM users WHERE id="+getUserID(request));
	        rs.next();
	        if(rs.getInt("users.zlava") != 0 && rs.getInt("users.zlava") > 0) {
				discount = Math.round((double)(sum_price)/100*rs.getInt("users.zlava"));
				sum_price -= discount;
	        }
	        out.print("<hr>"
	        		+ " <div class=checkout>"
	        		+ " <div class=total>"
	        		+ " <div>"
	        		+ " <div class=Subtotal>Total</div>"
	        		+ " <div class=discount>Discount "+rs.getInt("users.zlava")+"%</div>"
	        		+ " </div>"
	        		+ " <div class=total-amount>"+sum_price+"€</div>"
	        		+ " </div>"
	        		+ " <form action=Kosik method=post>"
					+ " <input type=hidden name=suma value="+sum_price+">"
					+ " <input type=hidden name=operacia value=makeorder>"
					+ " <input type=submit class=payment value='Order'></form>"
	        		+ " </div>");
	        out.print("</div></div>");
	        rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		out.print("</div>");
	}
	protected void removeFromCart(PrintWriter out,HttpServletRequest request, String product_id ) {
		try {
			Statement stmt = con.createStatement();
	        stmt.executeUpdate("DELETE FROM kosik WHERE ID_pouzivatela="+getUserID(request)+" AND ID_tovaru="+product_id);
		}catch (SQLException e) {
			e.printStackTrace();
		}
	}
	protected void makeOrder(PrintWriter out, HttpServletRequest request, String suma) {
		int user_id = getUserID(request);
		if (user_id == 0) 
			return;
		try {
			synchronized (this) {
				if (onStock(user_id) == null) {
					String CisloObj = newOrderNum(); 
					confirmOrder(user_id, CisloObj, suma);
					navigation(out, request);
					out.print("<h1 style='color: white; text-align: center;'>Your order will be processed as soon as possible.");
				}
				else {
					showCart(out,request);
					out.print("<h2 style='color:red;'>"+onStock(user_id)+"</h2>");
				}
		} 
		}catch(Exception e) {
			out.println(e);
		}
	}
	protected void confirmOrder(int user_id, String order_number,String suma) {
		try {
			Statement stmt = con.createStatement();
			Statement instmt = con.createStatement();
			stmt.executeUpdate("INSERT INTO obj_zoznam(obj_cislo, datum_objednavky, ID_pouzivatela, suma, stav) VALUES("+order_number+", NOW(), "+user_id+", "+suma+", 'being processed')");
			ResultSet rs = stmt.executeQuery("SELECT id FROM obj_zoznam WHERE obj_cislo="+order_number);
			rs.next(); int order_id = rs.getInt("id");
			stmt.executeUpdate("INSERT INTO obj_polozky(ID_objednavky, ID_tovaru, cena, ks) SELECT "+order_id+",kosik.ID_tovaru, kosik.cena, kosik.ks FROM kosik WHERE ID_pouzivatela="+user_id+"");
			rs = stmt.executeQuery("SELECT * FROM sklad INNER JOIN kosik ON sklad.ID = kosik.ID_tovaru WHERE kosik.ID_pouzivatela ="+user_id);
			while(rs.next()) {
				instmt.executeUpdate("UPDATE sklad SET ks="+(rs.getInt("sklad.ks")-rs.getInt("kosik.ks"))+" WHERE sklad.id="+rs.getInt("kosik.ID_tovaru"));
			}
			stmt.executeUpdate("DELETE FROM kosik WHERE ID_pouzivatela="+user_id);
			stmt.close();
			instmt.close();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	protected String onStock(int user_id) {
		try {
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM kosik INNER JOIN sklad ON kosik.ID_tovaru = sklad.id WHERE ID_pouzivatela="+user_id);
	        while(rs.next()) {
	        	if(rs.getInt("sklad.ks") < rs.getInt("kosik.ks"))
	        		return("Item "+rs.getString("sklad.nazov")+" is out of stock");
	        }
		}catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	protected void clearCart(int user_id) {
		Statement stmt;
		try {
			stmt = con.createStatement();
			stmt.executeUpdate("DELETE FROM kosik WHERE ID_pouzivatela="+user_id);
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}
	protected String newOrderNum() {
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");  
		LocalDateTime now = LocalDateTime.now();  
		return dtf.format(now); 
	}
	
}
