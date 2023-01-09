

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * Servlet implementation class Registration
 */
@WebServlet("/Registration")
public class Registration extends HttpServlet {
	private static final long serialVersionUID = 1L;
    Connection con;
    String errorMessage = "";
    Guard g;
    public Registration() {
        super();
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
		
		PrintWriter out = response.getWriter();
		connect(request);
		registrationForm(out);
	}


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("text/html;charset=UTF-8");
        request.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();
        try {
   		 	HttpSession session = request.getSession();
   		 	con = (Connection) session.getAttribute("spojenie");
            String operacia = request.getParameter("operacia");
            if (operacia.equals("registration")) 
        		if(ValidateRegistration(out,request))
        			response.sendRedirect("Main_Servlet");  
            
        } catch (Exception e) {  out.println(e); }
	}
	
	protected void connect(HttpServletRequest request) {
		con = joinSession(request);
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
	
	protected void registrationForm(PrintWriter out) {
		out.print("<html>"
				+ "<head>"
				+ "<link rel=\"icon\" type=\"image/x-icon\" href=\"https://seeklogo.com/images/S/shopify-logo-826A5C40EC-seeklogo.com.png\">"
				+ "<script src=\"//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/js/bootstrap.min.js\"></script>"
				+ "<script src=\"//cdnjs.cloudflare.com/ajax/libs/jquery/3.2.1/jquery.min.js\"></script>"
				+ "	<title>Registration Page</title>"
				+ "	<link href=\"//maxcdn.bootstrapcdn.com/bootstrap/4.1.1/css/bootstrap.min.css\" rel=\"stylesheet\" id=\"bootstrap-css\">\r\n"
				+ "	<link rel=\"stylesheet\" href=\"https://use.fontawesome.com/releases/v5.3.1/css/all.css\" integrity=\"sha384-mzrmE5qonljUremFsqc01SB46JvROS7bZs3IO2EmfFsd15uHvIt+Y8vEf7N7fWAU\" crossorigin=\"anonymous\">\r\n"
				+ "	<link rel=\"stylesheet\" type=\"text/css\" href=\"styles2.css\">\r\n"
				+ "</head>");
		out.print("<body>"
				+ "<div class=\"container\">"
				+ "	<div class=\"d-flex justify-content-center h-100\">"
				+ "		<div class=\"card\">"
				+ "			<div class=\"card-header\">"
				+ "				<h3>Register</h3>"
				+ "			</div>"
				+ "			<div class=\"card-body\">"
				+ "				<form action=Registration method=post>"
				+ "				<input type=hidden name=operacia value=registration>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-user\"></i></span>"
				+ "						</div>"
				+ "						<input type=text class=form-control placeholder=Name name=name required>"
				+ "					</div>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-user\"></i></span>"
				+ "						</div>"
				+ "						<input type=text class=form-control placeholder=Surname name=surname required>"
				+ "					</div>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-user\"></i></span>"
				+ "						</div>"
				+ "						<input type=text class=form-control placeholder=Adress name=address required>"
				+ "					</div>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-user\"></i></span>"
				+ "						</div>"
				+ "						<input type=email class=form-control placeholder=Email name=login required>"
				+ "					</div>"
				+ "					<div class=\"input-group form-group\">"
				+ "						<div class=\"input-group-prepend\">"
				+ "							<span class=\"input-group-text\"><i class=\"fas fa-key\"></i></span>"
				+ "						</div>"
				+ "						<input type=password class=form-control placeholder=password name=password required>"
				+ "					</div>"
				+ "					<div class=\"form-group\">"
				+ "						<input type=submit value=\"Register\" class=\"btn float-right login_btn\"></button>"
				+ "					</div>"
				+ "				</form>"
				+ "			</div>\r\n"
				+ "		</div>" 
				+ "	</div>"
				+ "</div>"
				+ "</body>");
		out.print("</html>");
	}
	protected boolean ValidateRegistration(PrintWriter out, HttpServletRequest request) {
        try {
        	HttpSession session = request.getSession();
        	Connection con = (Connection)session.getAttribute("spojenie");
			Statement stmt = con.createStatement();
	        ResultSet rs = stmt.executeQuery("SELECT * FROM users "+
	                " WHERE login = '"+request.getParameter("login")+"'");
	        if(!rs.next()) {
	        	stmt.executeUpdate("INSERT INTO `users`(login,passwd,meno,priezvisko,adresa,zlava,poznamky,je_admin) "
				+ "VALUES('"+request.getParameter("login")+"', '"+request.getParameter("password")+"', '"+request.getParameter("name")+"', '"+request.getParameter("surname")+"', '"+request.getParameter("address")+"',"
						+ "0,'new user',0)");
	        	return true;
	        }
	        else {
	        	registrationForm(out);
	        	out.println("<div style=position:relative;color:red;text-align:center>This user is already registered.</div>");
	        }
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

       
        
	}

}
