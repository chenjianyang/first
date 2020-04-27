function handleListResult(resultData) {
    console.log("handleListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let OrderNumber = jQuery("#OrderNumber");

    // Iterate through resultData, no more than 10 entries
    // for (let i = 0; i < Math.min(10, resultData.length); i++) {
    //     let rowHTML = "";
    //     rowHTML += "<tr>";
    //     rowHTML +=
    //         "<th>" +
    //         // Add a link to single-star.html with id passed with GET url parameter
    //         '<a href="single-star.html?id=' + resultData[i]['movie_id'] + '">'
    //         + resultData[i]["movie_title"] +     // display star_name for the link text
    //         '</a>' +
    //         "</th>";
    //     rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
    //     rowHTML += "</tr>";
    //
    //
    //     starTableBodyElement.append(rowHTML);
    // }


    let rowHTML = "";
    rowHTML += "<p>";
    rowHTML+=resultData;
    rowHTML += "</p>";

    // Append the row created to the table body, which will refresh the page
    OrderNumber.append(rowHTML);

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/ShowPlacedOrder", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
});