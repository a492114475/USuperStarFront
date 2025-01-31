package com.zjut.azure2;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.sql.Connection;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.File;

/**
 * Servlet implementation class login
 */
@WebServlet("/login")
public class login extends HttpServlet {
	private static final long serialVersionUID = 1L;
	Connection con = null;

	/**
	 * 初始化连接数据库
	 * 
	 * @see HttpServlet#HttpServlet()
	 */
	public void init() {
		try {
			Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
			String url = "jdbc:sqlserver://127.0.0.1:1433;databaseName=USuperStar";
			con = (Connection) DriverManager.getConnection(url, "sa", "a123123");
//			System.out.println("good!");
		} catch (ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/**
	 * 实现自动登录 自动登录保存一小时
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		//System.out.println(1);
		String value1 = "", value2 = "";
		Cookie cookie = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (int i = 0; i < cookies.length; i++) {
				cookie = cookies[i];
				if (cookie.getName().equals("num"))
					value1 = cookie.getValue();
				if (cookie.getName().equals("password"))
					value2 = cookie.getValue();
			}
			String sql = "SELECT * FROM Operator WHERE Onum=?";
			PreparedStatement pStatement;
			if (!value1.equals("")) {
				try {
					pStatement = con.prepareStatement(sql);
					pStatement.setString(1, value1);
					ResultSet resultSet = pStatement.executeQuery();
					Operator operator = new Operator();
					ArrayList<Operator> aList = new ArrayList<>();
					while (resultSet.next()) {

						operator.setNum(resultSet.getString("Onum"));
						operator.setName(resultSet.getString("Oname"));
						operator.setEmail(resultSet.getString("Oemail").trim());
						operator.setSex(resultSet.getString("Osex"));
						operator.setPassword(resultSet.getString("Opassword").trim());
						aList.add(operator);
					}

					if (!aList.isEmpty() && value2.equals(operator.getPassword())) {

						request.getSession().setAttribute("Operator", operator);
						RequestDispatcher rDispatcher = request.getRequestDispatcher("/welcome.jsp");
						rDispatcher.forward(request, response);
					} else {
						RequestDispatcher rDispatcher = request.getRequestDispatcher("/index.jsp");
						rDispatcher.forward(request, response);
					}

				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		String num = request.getParameter("num");
		String password = request.getParameter("password");
		ArrayList<Operator> aList = new ArrayList<>();
		String sql = "SELECT * FROM Operator WHERE Onum=?";
		try {

			PreparedStatement pStatement = con.prepareStatement(sql);
			pStatement.setString(1, num);
			ResultSet resultSet = pStatement.executeQuery();

			// System.out.println(password);

			Operator operator = new Operator();
			while (resultSet.next()) {


				operator.setNum(resultSet.getString("Onum"));
				operator.setName(resultSet.getString("Oname"));
				operator.setEmail(resultSet.getString("Oemail").trim());
				operator.setSex(resultSet.getString("Osex"));
				operator.setPassword(resultSet.getString("Opassword").trim());
				aList.add(operator);
			}

			if (!aList.isEmpty() && password.equals(operator.getPassword())) {
				//request.getParameter("login-save");
				if (request.getParameter("login-save") != null && request.getParameter("login-save").equals("on")) {
					Cookie numCookie = new Cookie("num", num);
					Cookie passwordCookie = new Cookie("password", password);
					Cookie nameCookie=new Cookie("name", operator.getName().trim());
					numCookie.setMaxAge(60 * 60);//保存一小时
					passwordCookie.setMaxAge(60 * 60);
					nameCookie.setMaxAge(60*60);
					response.addCookie(nameCookie);
					response.addCookie(numCookie);
					response.addCookie(passwordCookie);
				}

				
				
				request.getSession().setAttribute("Operator", operator);
				RequestDispatcher rDispatcher = request.getRequestDispatcher("/welcome.jsp");
				rDispatcher.forward(request, response);
			} else {
				
				response.sendRedirect("error.jsp");
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
