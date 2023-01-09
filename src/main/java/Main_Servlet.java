import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/Main_Servlet")
public class Main_Servlet extends HttpServlet {
	Connection con = null;
	Guard g;
	PrintWriter out;
	String errorMessage = "";
	private static final long serialVersionUID = 1L;
        

    public Main_Servlet() {
        super();
    }

	public void init(ServletConfig config) throws ServletException {
		   super.init();
		   try {
		      Class.forName("com.mysql.cj.jdbc.Driver");
		  } catch (Exception e) {  errorMessage = e.getMessage();   }
		}
	public void destroy() {
		super.destroy();
		try {
			con.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
	 	HttpSession session = request.getSession();
		PrintWriter out = response.getWriter();
		connect(request);
		if(!loggedIn(request))
			loginForm(out);
		else
		 	if(session.getAttribute("admin") != null && (boolean) session.getAttribute("admin"))
  				response.sendRedirect("Admin");
		 	else
		 		showShop(out,request);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
    		connect(request);
            String operacia = request.getParameter("operacia");
            if (badConnection(out) || badOperation(operacia, out)) return;
            if (operacia.equals("login")) 
            	validateLogin(out, request, response);
            
            if(operacia.equals("logout")) {
            	logOut(out, request); return;}
            
            if(operacia.equals("addtocart")) { 
            	int price = Integer.parseInt(request.getParameter("pocet"))*Integer.parseInt(request.getParameter("price"));
            	addToCart(request, request.getParameter("product_id"), Integer.parseInt(request.getParameter("pocet")),price);
            	showShop(out,request);
            }
            if(operacia.equals("showorders")) {
            	showOrders(out,request);
            }
            if(operacia.equals("showorderproducts")) {
            	showOrders(out,request);
            	getOrderProduct(out,request.getParameter("order_id"));
            }
            
            
        } catch (Exception e) {  out.println(e); }
	}
	private boolean badOperation(String operacia, PrintWriter out) {
		   if (operacia == null) {
		       return true;
		   }
		   return false;
	}
	 private boolean badConnection(PrintWriter out) {
		    if (errorMessage.length() > 0) {
		        out.println(errorMessage);
		        return true;
		    }
		    return false;
		}

	protected void connect(HttpServletRequest request) {
		con = joinSession(request);
	}
	protected boolean loggedIn(HttpServletRequest request) {
	    HttpSession session = request.getSession();
		if(session.getAttribute("ID")!=null)
		return true;
		return false;
	}
	
	protected void logOut(PrintWriter out, HttpServletRequest request) {
	   HttpSession session = request.getSession();
	   session.invalidate();
	   loginForm(out);
	}
	
	protected Connection joinSession(HttpServletRequest request) {
	    try { 
	     HttpSession session = request.getSession();
	     Connection con = (Connection)session.getAttribute("spojenie");  
	      if (con == null) { 
			con = DriverManager.getConnection("jdbc:mysql://localhost/obchod", "root", "");
			session.setAttribute("spojenie", con);
			g = new Guard(con);
	      } 
	      return con; 
	    } catch(Exception e) {errorMessage = e.getMessage();}
		return con;
    }
	    
	protected void navbar(PrintWriter out, HttpServletRequest request) {
	    HttpSession session = request.getSession();
	    String meno = (String) session.getAttribute("meno");
	    String priezvisko = (String) session.getAttribute("priezvisko");
		out.print("<html><head>"  
				+ "<link rel=\"stylesheet\" href=\"styles.css\" type=\"text/css\">"
				+ "</head>");
		out.print("<div class=topnav>" 
				+ "  <a href='Main_Servlet'>Home</a>"
				+ "	   <form class=nav action=Main_Servlet method=post>"
				+ "		<input type=hidden name=operacia value=showorders><input type=submit value=Orders></form>"
				+ "  <div class=topnav-right>"
				+ "    <a>"+meno+" "+priezvisko+"</a>"
				+ "    <a class=clickable href='Kosik'>Cart</a>"
				+ "	   <form class=nav action=Main_Servlet method=post>"
				+ "		<input type=hidden name=operacia value=logout><input type=submit value=Logout></form>"
				+ "  </div>"
				+ "</div>");
		out.print("</html>");
	}
	
	protected void validateLogin(PrintWriter out, HttpServletRequest request, HttpServletResponse response) {
	    try {
	        String login = request.getParameter("login");
	        String password = request.getParameter("password");
	        Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM users "+
	                " WHERE login = '"+login+"' AND passwd = '"+password+"'");
	        HttpSession session = request.getSession();
	        if (rs.next()) { 
	          rs = stmt.executeQuery("SELECT * FROM users WHERE login = '"+login+"'"); 
	          rs.next(); 
	          session.setAttribute("ID", rs.getInt("id"));
	          session.setAttribute("meno", rs.getString("meno"));
	          session.setAttribute("priezvisko", rs.getString("priezvisko"));
			  session.setMaxInactiveInterval(900);
	          if(!rs.getBoolean("je_admin"))
	        	  showShop(out,request);
	          else {
      				response.sendRedirect("Admin");
      				session.setAttribute("admin", rs.getBoolean("je_admin"));
	          }
	        } else {
	        	loginForm(out);
	        	out.print("<div style=position:relative;color:red;text-align:center>Incorrect name or password!</div>");
	        	session.invalidate();
	        }
	        rs.close();
	        stmt.close();
	       } catch (Exception ex) { out.println(ex.getMessage()); }
	}
	
	protected void loginForm(PrintWriter out) {
		out.print("<html>"
				+ "<head>"
				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
				+ "<script src=\"//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js\"></script>"
				+ "<script src=\"//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>"
				+ "	<title>Login Page</title>"
				+ "	<link href=\"//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css\" rel=\"stylesheet\" id=\"bootstrap-css\">\r\n"
				+ "	<link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.3.1/css/all.css\" integrity=\"sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU\" crossorigin=\"anonymous\">\r\n"
				+ "	<link rel=\"stylesheet\" type=\"text/css\" href=\"styles2.css\">\r\n"
				+ "</head>");
		
		out.print("<body>"
				+ "<div class=\"container\">"
				+ "	<div class=\"d-flex justify-content-center h-100\">"
				+ "		<div class=\"card\">"
				+ "			<div class=\"card-header\">"
				+ "				<h3>Sign In</h3>"
				+ "			</div>"
				+ "			<div class=\"card-body\">"
				+ "				<form action=Main_Servlet method=post>"
				+ "				<input type=hidden name=operacia value=login>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-user\"></i></span>"
				+ "						</div>"
				+ "						<input type=text class=form-control placeholder=username name=login required>"
				+ "					</div>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-key\"></i></span>"
				+ "						</div>"
				+ "						<input type=password class=form-control placeholder=password name=password required>"
				+ "					</div>"
				+ "					<div class=\"form-group\">"
				+ "						<input type=submit value=\"Login\" class=\"btn float-right login_btn\">\r\n"
				+ "					</div>"
				+ "				</form>"
				+ "			</div>\r\n"
				+ "			<div class=\"card-footer\">"
				+ "				<div class=\"d-flex justify-content-center links\">"
				+ "					Don't have an account?"
				+ "				</div>\r\n"
				+ "				<div class=\"d-flex justify-content-center links\">"
				+ "	  				<a href=Registration>Register now!</a>"
				+ "				</div>"
				+ "			</div>"
				+ "		</div>"
				+ "	</div>"
				+ "</div>" 
				+ "</body>");
		out.print("</html>");
	}
	
	 protected int getUserID(HttpServletRequest request) {
		 HttpSession session = request.getSession();
		 Integer id = (Integer) (session.getAttribute("ID"));
		 if(id == null)
			 id=0;
		 return id;
	 }
	
	protected void showShop(PrintWriter out, HttpServletRequest request) {
		navbar(out,request);
        try {
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM sklad");
	        out.print("<div class=parent>");
	        while(rs.next()) {
	        	out.print("<head>"
	    				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
	        			+ "<title>Main Shop</title>"
	    				+"</head>"
	        			+ "  <div class='card child'>"
	        			+ "  <img src='"+rs.getString("obrazok")+"' alt='"+rs.getString("nazov")+"' style=width:100%;>"
	        			+ "  	<p class=title>"+rs.getString("nazov")+"</p>"
	        			+ "  	<p class=price>"+rs.getString("cena")+" €</p>"
	        			+ "		<form action=Main_Servlet method=post><input type=hidden name=operacia value=addtocart><input type=hidden name=price value="+rs.getString("cena")+"><input type=hidden name=product_id value="+rs.getString("id")+">"
	        			+ "		<input style='width:15%; margin-right:10px;' type=number name=pocet value=1 min=1 max="+rs.getInt("ks")+"><input type=submit value='Add to cart'></form>"
	        			+ "  </div>");
	        }
	        out.print("</div>");
		} catch (SQLException e) {
			e.printStackTrace();
		}   	 
	}
	
	protected void showOrders(PrintWriter out, HttpServletRequest request) {
		navbar(out,request);
        try {
			Statement stmt = con.createStatement();
	        ResultSet rs_order = stmt.executeQuery("SELECT * FROM obj_zoznam WHERE ID_pouzivatela="+getUserID(request));
	        out.print("<table>"); 
	        while(rs_order.next()) {
	        	out.print("<head>"
	    				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
	        			+ "<title>Orders</title>"
	    				+"</head>");
	        	out.print("<tr><td>"+rs_order.getString("obj_zoznam.obj_cislo")+"</td>");
	        	out.print("<td>"+rs_order.getString("obj_zoznam.datum_objednavky")+"</td>");
	        	out.print("<td>"+rs_order.getString("obj_zoznam.suma")+"€</td>");
	        	out.print("<td>"+rs_order.getString("obj_zoznam.stav")+"</td>");
	        	out.print("<td><form class=nav action=Main_Servlet method=post><input type=hidden name=operacia value=showorderproducts><input type=hidden name=order_id value="+rs_order.getString("id")+">"
	        			+ "<input type=submit value=Items></form></td></tr>");
	        }
	        out.print("</table>");
	        rs_order.close();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	protected void getOrderProduct(PrintWriter out, String id) {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs_products = stmt.executeQuery("SELECT * FROM obj_polozky INNER JOIN sklad ON obj_polozky.ID_tovaru = sklad.id WHERE obj_polozky.ID_objednavky="+id);
			out.print("<table style='border:1px solid black;'>");
	    	while(rs_products.next()) {
	        	out.print("<tr><td>"+rs_products.getString("obj_polozky.ks")+"x "+rs_products.getString("sklad.nazov")+"<td></tr>");
	    	}
	        out.print("</table>");
	    	rs_products.close(); 
		} catch (SQLException e) {
			e.printStackTrace();
		} 
	}
	
	protected void addToCart(HttpServletRequest request, String product_id, int amount, int price) {
		try {
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM kosik WHERE ID_pouzivatela="+getUserID(request)+" AND ID_tovaru="+product_id);
	        if(!rs.next()) {
	        	stmt.executeUpdate("INSERT INTO kosik(`ID_pouzivatela`, `ID_tovaru`, `cena`, `ks`) VALUES("+getUserID(request)+","+product_id+","+price+","+amount+")");
	        }
	        else {
	        	stmt.executeUpdate("UPDATE kosik SET cena="+(rs.getInt("cena")+price)+", ks="+(rs.getInt("ks")+amount)+" WHERE ID_pouzivatela="+getUserID(request)+" AND ID_tovaru="+product_id+"");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
