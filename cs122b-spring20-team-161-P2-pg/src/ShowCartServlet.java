import com.google.gson.JsonArray;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author jianyang
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-24
 */

@WebServlet(name = "ShowCartServlet",urlPatterns = "/api/showCart")
public class ShowCartServlet extends HttpServlet {

    public void  doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        JsonArray cartList = (JsonArray) request.getSession().getAttribute("cartList");
        System.out.println("show cartList");
        System.out.println(cartList.toString());
        out.write(cartList.toString());
    }

}
