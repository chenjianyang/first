import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-movie"
@WebServlet(name = "SingleMovie", urlPatterns = "/api/single-movie")
public class SingleMovie extends HttpServlet {
    private static final long serialVersionUID = 3L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     *      response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String Movieid = request.getParameter("id");

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        try {
            // Get a connection from dataSource
            Connection dbcon = dataSource.getConnection();

            // Construct a query with parameter represented by "?"
            String query = "select movies.id, movies.price, movies.title, movies.year, movies.director, group_concat(distinct genres.name SEPARATOR ',') as genres , ratings.rating from movies, genres, genres_in_movies, ratings where movies.id = ratings.movieId and movies.id = genres_in_movies.movieId and genres_in_movies.genreId = genres.id and movies.id = ?";

            //String query2 = "SELECT stars.name, stars.id from stars, movies, stars_in_movies where stars.id = stars_in_movies.starId and stars_in_movies.movieId = movieId and stars_in_movies.movieId = ? group by starId";

            String query2 = "select count(*) as count, stars.id, stars.name from stars_in_movies, stars where stars.id = stars_in_movies.starId AND (stars.id = ANY(select stars.id from movies, stars, stars_in_movies where stars.id = stars_in_movies.starId AND stars_in_movies.movieId = movies.id AND movies.id =?)) group by stars.id order by count DESC,name ASC";

            // Declare our statement
            PreparedStatement statement = dbcon.prepareStatement(query);

            // Set the parameter represented by "?" in the query to the id we get from url,
            // num 1 indicates the first "?" in the query
            statement.setString(1, Movieid);

            // Perform the query
            ResultSet rs = statement.executeQuery();

            JsonArray jsonArray = new JsonArray();

            // Assign static data
            while (rs.next()) {

                ArrayList list3 = new ArrayList();
                String gen1 = "";
                String gen2 = "";
                String gen3 = "";


                String movieId = rs.getString("id");
                String movieTitle = rs.getString("title");
                String movieYear = rs.getString("year");
                String movieDirector = rs.getString("director");
                String movieGenres = rs.getString("genres");
                String movieRating = rs.getString("rating");
                String price = rs.getString("price");


                String tempGen = movieGenres;
                String[] split = tempGen.split(",");
                for (String tempGen2 : split) {
                    list3.add(tempGen2);
                }

                //insert gens
                if(list3.size() == 0)
                {
                }
                else if(list3.size() == 1)
                {
                    gen1 = gen1 + list3.get(0);
                }
                else if(list3.size() == 2)
                {
                    gen1 = gen1 + list3.get(0);
                    gen2 = gen2 + list3.get(1);
                }
                else if(list3.size() == 3)
                {
                    gen1 = gen1 + list3.get(0);
                    gen2 = gen2 + list3.get(1);
                    gen3 = gen3 + list3.get(2);
                }






                // Create a JsonObject based on the data we retrieve from rs

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("movieId", movieId);
                jsonObject.addProperty("movieTitle", movieTitle);
                jsonObject.addProperty("movieYear", movieYear);
                jsonObject.addProperty("movieDirector", movieDirector);
                jsonObject.addProperty("gen1", gen1);
                jsonObject.addProperty("gen2", gen2);
                jsonObject.addProperty("gen3", gen3);
                jsonObject.addProperty("movieRating", movieRating);
                jsonObject.addProperty("price", price);

                jsonArray.add(jsonObject);
            }

            //start 2nd query
            PreparedStatement statement2 = dbcon.prepareStatement(query2);
            statement2.setString(1, Movieid);
            ResultSet rs2 = statement2.executeQuery();

            while (rs2.next()) {
                String starName = rs2.getString("name");
                String starID = rs2.getString("id");
                String count = rs2.getString("count");

                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("starName", starName);
                jsonObject.addProperty("starID", starID);
                jsonObject.addProperty("count", count);


                jsonArray.add(jsonObject);

            }



            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            rs2.close();
            statement.close();
            statement2.close();
            dbcon.close();
        } catch (Exception e) {
            // write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // set reponse status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        out.close();

    }

}
