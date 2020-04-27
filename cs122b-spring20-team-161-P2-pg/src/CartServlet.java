import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {


        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        System.out.println(id);

        HttpSession session = request.getSession();

        JsonArray cartList = (JsonArray) session.getAttribute("cartList");
        if(cartList == null){
            cartList = new JsonArray();
            session.setAttribute("cartList",cartList);
        }

        JsonArray movieList=(JsonArray)session.getAttribute("movieList");

        Iterator newIt= movieList.iterator();
        JsonObject newjson=null;


        boolean exist=false;
        while(newIt.hasNext()) {
            JsonObject it=(JsonObject) newIt.next();

            if((it.get("movie_id").toString()).replaceAll("\"","").equals(id))
            {

                    newjson=it;

                    break;
            }
        }



        Iterator itCart= cartList.iterator();
        JsonObject theOneinCart=null;
        while(itCart.hasNext()) {
            JsonObject thisOne=(JsonObject) itCart.next();

            if((thisOne.get("movie_id").toString()).replaceAll("\"","").equals(id))
            {

                theOneinCart=thisOne;
                exist=true;
                break;
            }
        }


        PrintWriter out = response.getWriter();
        if(exist==true)
        {

            int  number1 = 0;
            int  number2 = 0;
            JsonObject temp=new JsonObject();
            temp.addProperty("movie_id", theOneinCart.get("movie_id").toString().replaceAll("\"",""));
            temp.addProperty("movie_title", theOneinCart.get("movie_title").toString().replaceAll("\"",""));
            temp.addProperty("movie_year", theOneinCart.get("movie_year").toString().replaceAll("\"",""));
            temp.addProperty("movie_director", theOneinCart.get("movie_director").toString().replaceAll("\"",""));
            //temp.addProperty("movie_genres", theOneinCart.get("movie_genres").toString().replaceAll("\"",""));
            temp.addProperty("movie_rating", theOneinCart.get("movie_rating").toString().replaceAll("\"",""));

            number1=Integer.valueOf(theOneinCart.get("price").toString().replaceAll("\"",""));
            temp.addProperty("price",number1);
            number2=Integer.valueOf(theOneinCart.get("quantity").toString());
            temp.addProperty("quantity",number2+1);
            cartList.remove(theOneinCart);
            cartList.add(temp);

            out.write(temp.toString());

        }
        else
        {
            cartList.add(newjson);

            out.write(newjson.toString());

        }

        out.close();








        //  response.getWriter().write(String.join(",", previousItems));
    }
}
