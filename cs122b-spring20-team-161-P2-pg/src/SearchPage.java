import javax.annotation.Resource;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * A servlet that takes input from a html <form> and talks to MySQL moviedb,
 * generates output as a html <table>
 */

// Declaring a WebServlet called SearchPage, which maps to url "/form"
@WebServlet(name = "SearchPage", urlPatterns = "/search")
public class SearchPage extends HttpServlet {
    private static final long serialVersionUID = 4L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;


    // Use http GET
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("text/html");    // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a instance of current session on the request
        HttpSession session = request.getSession(true);


        try {


            session.setAttribute("title", null);
            session.setAttribute("year", null);
            session.setAttribute("director", null);
            session.setAttribute("star", null);

            session.setAttribute("mode", null);
            session.setAttribute("browse", null);
            session.setAttribute("sort", null);
            session.setAttribute("pageNumber", 1);
            session.setAttribute("num", null);
            session.setAttribute("TotalPage", null);
            session.setAttribute("query", null);
            session.setAttribute("check", 1);

            // Create a new connection to database
            Connection dbCon = dataSource.getConnection();

            // Declare a new statement
            Statement statement = dbCon.createStatement();

            // Retrieve parameter "name" from the http request, which refers to the value of <input name="name"> in index.html
            String title = request.getParameter("title");
            String year = request.getParameter("year");
            String director = request.getParameter("director");
            String star = request.getParameter("star");




            if(title.compareTo("")!= 0)
            {
                session.setAttribute("title", title);

            }
            if(year.compareTo("")!= 0)
            {
                session.setAttribute("year", year);

            }
            if(director.compareTo("")!= 0)
            {
                session.setAttribute("director", director);

            }
            if(star.compareTo("")!= 0)
            {
                session.setAttribute("star", star);

            }

            //request.getRequestDispatcher("/index.html").forward(request,response);
            response.sendRedirect("index.html?mode=1&sort=1&num=25");


        } catch (Exception ex) {

            // Output Error Massage to html
            out.println(String.format("<html><head><title>MovieDBExample: Error</title></head>\n<body><p>SQL error in doGet: %s</p></body></html>", ex.getMessage()));
            return;
        }
        out.close();
    }

}
