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

@WebServlet(name = "deleteCartServlet",urlPatterns = "/api/deleteCart")
public class deleteCart extends HttpServlet {

    public void  doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("id");
        HttpSession session = request.getSession();
        JsonArray cartList = (JsonArray) session.getAttribute("cartList");
        Iterator newIt = cartList.iterator();
        boolean flag = false;
        while (newIt.hasNext()) {
            JsonObject it = (JsonObject) newIt.next();

            if ((it.get("movie_id").toString()).replaceAll("\"", "").equals(id)) {
                cartList.remove(it);
                flag = true;
                break;
            }
        }
        PrintWriter out=response.getWriter();
        out.write(flag+"");
        out.close();
    }

}
