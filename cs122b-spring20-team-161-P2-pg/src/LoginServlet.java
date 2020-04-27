import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

@WebServlet(name = "LoginServlet", urlPatterns = "/api/login")
public class LoginServlet extends HttpServlet {


    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        JsonObject responseJsonObject = new JsonObject();

        PrintWriter out = response.getWriter();
        String username = null;
        String password = null;
        String FinalUsername = null;
        String FinalPW = null;


        try
        {
            Connection dbcon = dataSource.getConnection();
            Statement statement = dbcon.createStatement();
            Statement statement2 = dbcon.createStatement();
            ResultSet rs = null;
            ResultSet rs2 = null;

            username = request.getParameter("username");
            password = request.getParameter("password");

            if(username.equals("") || password.equals(""))
            {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "Please enter your Username/Password");
            }
            else
            {
                String query1 = "select customers.* from customers where email = '" + username + "'";
                String query2 = "select customers.* from customers where email = '" + username + "' AND password = '" + password + "'";

                //check username
                rs = statement.executeQuery(query1);
                if (rs.next())
                {
                    FinalUsername = rs.getString("email");
                }

                if(FinalUsername == null)
                {
                    // Login fail
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "user " + username + " doesn't exists");
                }
                //username is valid
                else
                {
                    //check password
                    rs2 = statement2.executeQuery(query2);
                    if (rs2.next())
                    {
                        FinalPW = rs2.getString("password");
                    }

                    if(FinalPW == null)
                    {
                        // Login fail
                        responseJsonObject.addProperty("status", "fail");
                        responseJsonObject.addProperty("message", "Your password is incorrect");
                    }
                    //success!
                    else
                    {
                        // Login success:
                        // set this user into the session
                        request.getSession().setAttribute("user", new User(username));

                        responseJsonObject.addProperty("status", "success");
                        responseJsonObject.addProperty("message", "success");
                    }

                }
            }



            rs.close();
            rs2.close();
            statement.close();
            statement2.close();
            dbcon.close();
        } catch (Exception e) {

        }







        out.write(responseJsonObject.toString());

        /* This example only allows username/password to be test/test
        /  in the real project, you should talk to the database to verify username/password
        */







    }
}
