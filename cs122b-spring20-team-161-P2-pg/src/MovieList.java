import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.annotation.Resource;
import javax.servlet.RequestDispatcher;
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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

// Declaring a WebServlet called StarsServlet, which maps to url "/api/stars"
@WebServlet(name = "MovieList", urlPatterns = "/api/movie")
public class MovieList extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.xml
    @Resource(name = "jdbc/moviedb")
    private DataSource dataSource;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        response.setContentType("application/json"); // Response mime type

        String mode = request.getParameter("mode");
        String temp_browse = request.getParameter("browse");
        String temp_sort = request.getParameter("sort");
        String page = request.getParameter("page");
        String temp_num = request.getParameter("num");



        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        HttpSession session = request.getSession(true);

        try {
            // Get a connection from dataSource

            Connection dbcon = dataSource.getConnection();

            // Declare our statement
            Statement statement = dbcon.createStatement();
            Statement statement2 = dbcon.createStatement();
            Statement statement3 = dbcon.createStatement();

            //get parameters
            ResultSet rs = null;
            ResultSet rs2 = null;
            ResultSet rs3 = null;
            JsonArray jsonArray = new JsonArray();
            Integer check = (Integer)session.getAttribute("check");
            if(check == null)
            {
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
            }

            // init total page count
            session.setAttribute("TotalPage", null);

            //save parameter to global
            //detect mode
            String temp_Mode = (String)session.getAttribute("mode");
            //a new action
            if(!mode.equals("3"))
                session.setAttribute("mode", mode);
            if (mode.equals("3"))
                mode = temp_Mode;

            if(!temp_sort.equals("null"))
                session.setAttribute("sort", temp_sort);
            if(!temp_num.equals("null"))
                session.setAttribute("num", temp_num);
            if(!temp_browse.equals("null"))
                session.setAttribute("browse", temp_browse);
            //assign value
            String sort = (String)session.getAttribute("sort");
            String num = (String)session.getAttribute("num");
            String browse = (String)session.getAttribute("browse");



            //search
            if(mode.compareTo("1") == 0)
            {
                String title = "";
                String year = "";
                String director = "";
                String star = "";

                //get search result
                title = (String)session.getAttribute("title");
                year = (String)session.getAttribute("year");
                director = (String)session.getAttribute("director");
                star = (String)session.getAttribute("star");


                session.setAttribute("browse", null);

                String query = null;
                //test
                if(title == null && year == null && director == null && star == null)
                {
                    query = "select * from topmovies";
                }
                // has title but not year
                else if((title != null || director != null) && year == null)
                {
                    if(director == null)
                        director = "";
                    if(title == null)
                        title = "";
                    if(star == null)
                    {
                        query = "select * from topmovies where topmovies.title like '%";
                        query = query + title + "%' AND topmovies.director like '%" + director + "%'";
                    }
                    else
                    {
                        query = "select topmovies.* from topmovies, stars, stars_in_movies where topmovies.title like '%" + title + "%' AND topmovies.director like '%" + director + "%' AND topmovies.id = stars_in_movies.movieId AND stars_in_movies.starId = stars.id AND stars.name like '%" + star + "%' group by topmovies.id";
                    }

                }
                //include year
                else if(year != null)
                {
                    if(director == null)
                        director = "";
                    if(title == null)
                        title = "";

                    //without stars
                    if(star == null)
                    {
                        query = "select * from topmovies where topmovies.title like '%";
                        query = query + title + "%' AND topmovies.director like '%" + director + "%'" + " AND year = '" + year + "'";
                    }
                    //with year and stars
                    else
                    {
                        query = "select topmovies.* from topmovies, stars, stars_in_movies where topmovies.title like '%";
                        query = query + title + "%' AND topmovies.director like '%" + director + "%' AND year = '" + year + "' AND topmovies.id = stars_in_movies.movieId and stars_in_movies.starId = stars.id AND stars.name like '%" + star + "%' group by topmovies.id";
                    }
                }

                //calculate total result and total page number
                String resultCount = "select count(*) as count from (" + query + ") as t";
                rs3 = statement3.executeQuery(resultCount);
                String tempRN = "";
                while (rs3.next())
                {
                    tempRN = rs3.getString("count");
                }

                int num_result = Integer.valueOf(tempRN);
                int Single_Page = Integer.valueOf(num);

                int Total_page = num_result / Single_Page;
                if( (num_result % Single_Page) != 0)
                    Total_page++;

                //save it to global
                session.setAttribute("TotalPage", Total_page);


                //--------------------------------------
                if(sort.compareTo("1") == 0)
                {
                    query = query + " order by topmovies.rating DESC, topmovies.title DESC";
                }
                else if(sort.compareTo("2") == 0)
                {
                    query = query + " order by topmovies.rating ASC, topmovies.title ASC";
                }
                else if(sort.compareTo("3") == 0)
                {
                    query = query + " order by topmovies.title DESC, topmovies.rating DESC";
                }
                else if(sort.compareTo("4") == 0)
                {
                    query = query + " order by topmovies.title ASC, topmovies.rating ASC";
                }

                if(num.compareTo("10") == 0)
                {
                    query = query + " limit 10";
                }
                else if(num.compareTo("25") == 0 )
                {
                    query = query + " limit 25";
                }
                else if(num.compareTo("50") == 0)
                {
                    query = query + " limit 50";
                }
                else if(num.compareTo("100") == 0)
                {
                    query = query + " limit 100";
                }

                //edit offset
                    Integer currentPage = (Integer)session.getAttribute("pageNumber");
                    //calculate correct current page
                    //prev
                    if(page.compareTo("1") == 0)
                    {
                        currentPage = currentPage - 1;
                        if (currentPage < 1)
                            currentPage = 1;
                    }
                    //next
                    else if(page.compareTo("2") == 0)
                    {
                        currentPage = currentPage + 1;
                        if(currentPage > Total_page)
                            currentPage = Total_page;
                    }

                    //update page number
                    session.setAttribute("pageNumber", currentPage);

                    //----------------

                    int offset = 0;
                    if(currentPage == 1)
                    {

                    }
                    else if(currentPage <= Total_page)
                    {
                        offset = (currentPage - 1) * Single_Page;
                        query = query + " offset " + offset;
                    }
                    else if(currentPage > Total_page)
                    {
                        offset = (Total_page - 1) * Single_Page;
                        query = query + " offset " + offset;
                    }


                // Perform the query
                rs = null;
                rs = statement.executeQuery(query);
                rs2 = null;


                // Iterate through each row of rs
                while (rs.next()) {
                    rs2 = null;
                    ArrayList list1 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    ArrayList list3 = new ArrayList();

                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genres = rs.getString("genres");
                    String movie_rating = rs.getString("rating");
                    String price = rs.getString("price");



                    //assign string
                    String star1 = "";
                    String star2 = "";
                    String star3 = "";
                    String ID1 = "";
                    String ID2 = "";
                    String ID3 = "";
                    String gen1 = "";
                    String gen2 = "";
                    String gen3 = "";

                    //split gen
                    String tempGen = movie_genres;
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

                    //All 3 stars
                    String query2 = "select count(*) as count, stars.id, stars.name from stars_in_movies, stars where stars.id = stars_in_movies.starId AND (stars.id = ANY(select stars.id from movies, stars, stars_in_movies where stars.id = stars_in_movies.starId AND stars_in_movies.movieId = movies.id AND movies.id = '";
                    query2 = query2 + movie_id + "')) group by stars.id order by count DESC,name ASC limit 3";

                    rs2 = statement2.executeQuery(query2);

                    // insert all stars to list
                    while (rs2.next())
                    {
                        String tempN = rs2.getString("name");
                        String tempID = rs2.getString("id");
                        list1.add(tempN);
                        list2.add(tempID);
                    }
                    //rs2 = null;

                    if(list2.size() == 0)
                    {
                    }
                    else if(list2.size() == 1)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                    }
                    else if(list2.size() == 2)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        star1 = star1 + ", ";
                    }
                    else if(list2.size() == 3)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        ID3 = ID3 + list2.get(2);
                        star3 = star3 + list1.get(2);
                        star1 = star1 + ", ";
                        star2 = star2 + ", ";
                    }

                    //-------------------
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    //jsonObject.addProperty("movie_genres", movie_genres);
                    jsonObject.addProperty("gen1", gen1);
                    jsonObject.addProperty("gen2", gen2);
                    jsonObject.addProperty("gen3", gen3);
                    jsonObject.addProperty("star1", star1);
                    jsonObject.addProperty("star2", star2);
                    jsonObject.addProperty("star3", star3);
                    jsonObject.addProperty("ID1", ID1);
                    jsonObject.addProperty("ID2", ID2);
                    jsonObject.addProperty("ID3", ID3);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("price", price);
                    jsonObject.addProperty("quantity", 1);
                    jsonArray.add(jsonObject);
                }
            }
            //browse
            if(mode.compareTo("2") == 0)
            {

                String query = null;
                //if a-z

                if(browse.length() == 1)
                {
                    query = "select * from topmovies where title like '" + browse + "%'";
                }
                //if *
                else if(browse.compareTo("test") == 0)
                {
                    query = "select * from topmovies where title regexp '^[^a-z0-9]'";
                }
                // by genres
                else if(browse.compareTo("Music") == 0)
                {
                    query="(select * from topmovies where topmovies.genres like '%music%' and topmovies.genres not like '%musical%') UNION ALL (select * from topmovies where topmovies.genres like '%music,musical%')";
                }
                else
                {
                    query = "select * from topmovies where topmovies.genres like '%" + browse + "%'";
                }

                //calculate total result and total page number
                String resultCount = "select count(*) as count from (" + query + ") as t";
                rs3 = statement3.executeQuery(resultCount);
                String tempRN = "";
                while (rs3.next())
                {
                    tempRN = rs3.getString("count");
                }

                int num_result = Integer.valueOf(tempRN);
                int Single_Page = Integer.valueOf(num);

                int Total_page = num_result / Single_Page;
                if( (num_result % Single_Page) != 0)
                    Total_page++;




                //--------------------------------------
                if(sort.compareTo("1") == 0)
                {
                    query = query + " order by topmovies.rating DESC, topmovies.title DESC";
                }
                else if(sort.compareTo("2") == 0)
                {
                    query = query + " order by topmovies.rating ASC, topmovies.title ASC";
                }
                else if(sort.compareTo("3") == 0)
                {
                    query = query + " order by topmovies.title DESC, topmovies.rating DESC";
                }
                else if(sort.compareTo("4") == 0)
                {
                    query = query + " order by topmovies.title ASC, topmovies.rating ASC";
                }

                if(num.compareTo("10") == 0)
                {
                    query = query + " limit 10";
                }
                else if(num.compareTo("25") == 0)
                {
                    query = query + " limit 25";
                }
                else if(num.compareTo("50") == 0)
                {
                    query = query + " limit 50";
                }
                else if(num.compareTo("100") == 0)
                {
                    query = query + " limit 100";
                }

                //edit offset
                Integer currentPage = (Integer)session.getAttribute("pageNumber");
                //calculate correct current page
                //prev
                if(page.compareTo("1") == 0)
                {
                    currentPage = currentPage - 1;
                    if (currentPage < 1)
                        currentPage = 1;
                }
                //next
                else if(page.compareTo("2") == 0)
                {
                    currentPage = currentPage + 1;
                    if(currentPage > Total_page)
                        currentPage = Total_page;
                }

                //update page number
                session.setAttribute("pageNumber", currentPage);

                //----------------
                int offset = 0;
                if(currentPage == 1)
                {

                }
                else if(currentPage <= Total_page)
                {
                    offset = (currentPage - 1) * Single_Page;
                    query = query + " offset " + offset;
                }
                else if(currentPage > Total_page)
                {
                    offset = (Total_page - 1) * Single_Page;
                    query = query + " offset " + offset;
                }



                rs = statement.executeQuery(query);
                rs2 = null;

                while (rs.next())
                {
                    rs2 = null;
                    ArrayList list1 = new ArrayList();
                    ArrayList list2 = new ArrayList();
                    ArrayList list3 = new ArrayList();

                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genres = rs.getString("genres");
                    String movie_rating = rs.getString("rating");
                    String price = rs.getString("price");



                    //assign string
                    String star1 = "";
                    String star2 = "";
                    String star3 = "";
                    String ID1 = "";
                    String ID2 = "";
                    String ID3 = "";
                    String gen1 = "";
                    String gen2 = "";
                    String gen3 = "";

                    //split gen
                    String tempGen = movie_genres.substring(0, movie_genres.length());
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

                    //All 3 stars
                    String query2 = "select count(*) as count, stars.id, stars.name from stars_in_movies, stars where stars.id = stars_in_movies.starId AND (stars.id = ANY(select stars.id from movies, stars, stars_in_movies where stars.id = stars_in_movies.starId AND stars_in_movies.movieId = movies.id AND movies.id = '";
                    query2 = query2 + movie_id + "')) group by stars.id order by count DESC,name ASC limit 3";

                    rs2 = statement2.executeQuery(query2);

                    // insert all stars to list
                    while (rs2.next())
                    {
                        String tempN = rs2.getString("name");
                        String tempID = rs2.getString("id");
                        list1.add(tempN);
                        list2.add(tempID);
                    }
                    //rs2 = null;

                    if(list2.size() == 0)
                    {
                    }
                    else if(list2.size() == 1)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                    }
                    else if(list2.size() == 2)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        star1 = star1 + ", ";
                    }
                    else if(list2.size() == 3)
                    {
                        ID1 = ID1 + list2.get(0);
                        star1 = star1 + list1.get(0);
                        ID2 = ID2 + list2.get(1);
                        star2 = star2 + list1.get(1);
                        ID3 = ID3 + list2.get(2);
                        star3 = star3 + list1.get(2);
                        star1 = star1 + ", ";
                        star2 = star2 + ", ";
                    }

                    //-------------------
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    //jsonObject.addProperty("movie_genres", movie_genres);
                    jsonObject.addProperty("gen1", gen1);
                    jsonObject.addProperty("gen2", gen2);
                    jsonObject.addProperty("gen3", gen3);
                    jsonObject.addProperty("star1", star1);
                    jsonObject.addProperty("star2", star2);
                    jsonObject.addProperty("star3", star3);
                    jsonObject.addProperty("ID1", ID1);
                    jsonObject.addProperty("ID2", ID2);
                    jsonObject.addProperty("ID3", ID3);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("price",Integer.valueOf(price));
                    jsonObject.addProperty("quantity",1);
                    jsonArray.add(jsonObject);
                }


            }


            session.setAttribute("movieList",jsonArray);
            // write JSON string to output
            out.write(jsonArray.toString());
            // set response status to 200 (OK)
            response.setStatus(200);

            rs.close();
            rs2.close();
            rs3.close();
            statement.close();
            statement2.close();
            statement3.close();
            dbcon.close();



            //response.setHeader("refresh", "3");
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