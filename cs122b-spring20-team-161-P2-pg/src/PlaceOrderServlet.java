import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author jianyang chen
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-26
 */

@WebServlet(name = "PlaceOrderServlet",urlPatterns = "/api/PlaceOrder")
public class PlaceOrderServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String CreditCard = request.getParameter("CreditCard");
        String Expiration = request.getParameter("Expiration");
        String FirstName = request.getParameter("FirstName");
        String LastName = request.getParameter("LastName");
        try {
            Connection dbcon = dataSource.getConnection();

            JsonObject responseJsonObject = new JsonObject();
            if (CreditCard.equals("") || Expiration.equals("")||FirstName.equals("")||LastName.equals("")) {
                responseJsonObject.addProperty("status", "fail");
                responseJsonObject.addProperty("message", "any input can not be empty");
            } else {
                String query = "select id from creditcards as c where c.id= ? and c.expiration=?";
                PreparedStatement statement = dbcon.prepareStatement(query);
                statement.setString(1, CreditCard);
                statement.setString(2, Expiration);
                ResultSet result = statement.executeQuery();
                if (result.next()) //which means the username exist and the password match
                {


                    responseJsonObject.addProperty("status", "success");
                    responseJsonObject.addProperty("message", "success");
                } else {
                    responseJsonObject.addProperty("status", "fail");
                    responseJsonObject.addProperty("message", "incorrect CreditCard or incorrect Expiration");
                }
            }
            response.getWriter().write(responseJsonObject.toString());
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

}

