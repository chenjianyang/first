import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "subtractQuantity",urlPatterns = "/api/subtractQuantity")
public class subtractQuantity extends HttpServlet {

    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("a111aa");
        PrintWriter out = response.getWriter();
        String id = request.getParameter("id");
        System.out.println(id);
        HttpSession session = request.getSession();
        JsonArray cartList = (JsonArray) session.getAttribute("cartList");
        Iterator itCart= cartList.iterator();
        JsonObject theOneinCart=null;
        while(itCart.hasNext()) {
            JsonObject thisOne=(JsonObject) itCart.next();

            if((thisOne.get("movie_id").toString()).replaceAll("\"","").equals(id))
            {

                theOneinCart=thisOne;

                break;
            }
        }
        int  number1 = 0;
        int  number2 = 0;
        number2=Integer.valueOf(theOneinCart.get("quantity").toString());
        if(number2!=1) {
            JsonObject temp = new JsonObject();
            temp.addProperty("movie_id", theOneinCart.get("movie_id").toString().replaceAll("\"", ""));
            temp.addProperty("movie_title", theOneinCart.get("movie_title").toString().replaceAll("\"", ""));
            temp.addProperty("movie_year", theOneinCart.get("movie_year").toString().replaceAll("\"", ""));
            temp.addProperty("movie_director", theOneinCart.get("movie_director").toString().replaceAll("\"", ""));
            //temp.addProperty("movie_genres", theOneinCart.get("movie_genres").toString().replaceAll("\"", ""));
            temp.addProperty("movie_rating", theOneinCart.get("movie_rating").toString().replaceAll("\"", ""));

            number1 = Integer.valueOf(theOneinCart.get("price").toString());
            temp.addProperty("price", number1);

            temp.addProperty("quantity", number2 - 1);

            cartList.remove(theOneinCart);
            cartList.add(temp);

            System.out.println(theOneinCart.get("quantity").toString());
            System.out.println(temp.get("quantity").toString());
            System.out.println(cartList.toString());
            out.write(temp.toString());
        }
        else {
            System.out.println("fail to subtract");
            out.write(false+"");
        }
        out.close();
    }

}
