import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Random;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "ShowPlacedOrder",urlPatterns = "/api/ShowPlacedOrder")
public class ShowPlacedOrder extends HttpServlet {
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("application/json");
        HttpSession session = request.getSession();
        Date date=new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateString = formatter.format(date);
        JsonArray cartList = (JsonArray) session.getAttribute("cartList");
        JsonObject thisOne = null;
        try {
            Connection moviedb = dataSource.getConnection();

            Iterator itCart = cartList.iterator();
            while (itCart.hasNext()) {

                thisOne = (JsonObject) itCart.next();


                String movieId = thisOne.get("movie_id").toString().replaceAll("\"","");
                System.out.println(movieId);

                //cartList.remove(thisOne);
                String quantity = thisOne.get("quantity").toString();
                System.out.println(quantity);
                String query = "insert into sales(customerId,movieId,saleDate,quantity) values(?,?,?,?)";
                PreparedStatement statement = moviedb.prepareStatement(query);


                statement.setInt(1, 99999);
                statement.setString(2, movieId);
                statement.setString(3, dateString);
                statement.setInt(4, Integer.valueOf(quantity));


                int num = statement.executeUpdate();
                System.out.println(movieId);
                System.out.println(quantity);

                //  }


            }
        }catch(SQLException throwables){
                throwables.printStackTrace();

            }


            PrintWriter out = response.getWriter();
            Random newRandom = new Random();
            int num = newRandom.nextInt(9999999);
            String str = String.valueOf(num);

            Iterator it = cartList.iterator();
            System.out.println(str);
            while (it.hasNext()) {
                if (it.next() != null) {
                    it.remove();
                }
                //cartList.remove(thisOne);
            }

            out.write(str);
            System.out.println(str);
            out.close();


        }
    }

