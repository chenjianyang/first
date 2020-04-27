/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs three steps:
 *      1. Get parameter from request URL so it know which id to look for
 *      2. Use jQuery to talk to backend API to get the json data.
 *      3. Populate the data to correct html elements.
 */


/**
 * Retrieve parameter from request URL, matching by parameter name
 * @param target String
 * @returns {*}
 */
function getParameterByName(target) {
    // Get request URL
    let url = window.location.href;
    // Encode target parameter name to url encoding
    target = target.replace(/[\[\]]/g, "\\$&");

    // Ues regular expression to find matched parameter value
    let regex = new RegExp("[?&]" + target + "(=([^&#]*)|&|#|$)"),
        results = regex.exec(url);
    if (!results) return null;
    if (!results[2]) return '';

    // Return the decoded parameter value
    return decodeURIComponent(results[2].replace(/\+/g, " "));
}

/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
 */

function handleResult(resultData) {

    console.log("handleResult: populating star info from resultData");

    // populate the star info h3
    // find the empty h3 body by id "smovie_info"
    let starInfoElement = jQuery("#smovie_info");

    var tempG1 = resultData[0]["gen1"];
    var tempG2 = resultData[0]["gen2"];
    var tempG3 = resultData[0]["gen3"];

    if(tempG3 != "")
        tempG2 = tempG2 + ',';
    if(tempG2 != "")
        tempG1 = tempG1 + ',';

    // -------------------------------------------------------------
    starInfoElement.append("<p>Movie Title: " + resultData[0]["movieTitle"] + "</p>" +
        "<p><font size=5>Year: " + resultData[0]["movieYear"] + "</font></p>" +
        "<p><font size=5>Director: " + resultData[0]["movieDirector"] + "</font></p>"
        + "<p><font size=5>Genres: "
        + '<a href="index.html?mode=2&browse=' + resultData[0]['gen1'] + '&sort=1&num=25' + '">' + tempG1 + '</a>'
        + '<a href="index.html?mode=2&browse=' + resultData[0]['gen2'] + '&sort=1&num=25' + '">' + tempG2 + '</a>'
        + '<a href="index.html?mode=2&browse=' + resultData[0]['gen3'] + '&sort=1&num=25' + '">' + tempG3 + '</a>'
        + "</font></p>"
        + "<p><font size=5>Rating: " + resultData[0]["movieRating"] + "</font></p>"
    +"<p><font size=5>Price: " + resultData[0]["price"] + "</font></p>");

    console.log("handleResult: populating movie table from resultData");

    // Populate the star table
    // Find the empty table body by id "movie_table_body"
    let movieTableBodyElement = jQuery("#smovie_table_body");

    // Concatenate the html tags with resultData jsonObject to create table rows
    for (let i = 0; i < Math.min(99, resultData.length); i++) {

        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"
            + '<a href="single-star.html?id=' + resultData[1+i]['starID'] + '">' + resultData[1+ i]["starName"] + '</a>'
            + "</th>";
        rowHTML += "<th>" + resultData[1+i]['count'] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        movieTableBodyElement.append(rowHTML);
    }
}

/**
 * Once this .js is loaded, following scripts will be executed by the browser\
 */

// Get id from URL
let movieId = getParameterByName('id');


// Makes the HTTP GET request and registers on success callback function handleResult
jQuery.ajax({
    dataType: "json",  // Setting return data type
    method: "GET",// Setting request method
    url: "api/single-movie?id=" + movieId, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleResult(resultData) // Setting callback function to handle data returned successfully by the SingleStarServlet
});