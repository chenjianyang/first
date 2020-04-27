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
 * @author 扣人心弦的稳
 * @project cs122b-spring20-project2-login-cart-example
 * @create 2020-04-26
 */

@WebServlet(name = "PaymentServlet",urlPatterns = "/api/payment")
public class PaymentServlet extends HttpServlet {

    public void  doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        System.out.println("payment");
        PrintWriter out = response.getWriter();
        response.setContentType("application/json"); // Response mime type
        HttpSession session = request.getSession();

        JsonArray cartList = (JsonArray) session.getAttribute("cartList");
        Iterator newIt= cartList.iterator();
        int total=0;
        while(newIt.hasNext()) {
            JsonObject it=(JsonObject) newIt.next();
            int price=it.get("price").getAsInt();
            int quantity=it.get("quantity").getAsInt();
            total=total+price*quantity;
        }
        System.out.println(total);
        String str = String.valueOf(total);
        out.write(str);
        out.close();
    }

}

