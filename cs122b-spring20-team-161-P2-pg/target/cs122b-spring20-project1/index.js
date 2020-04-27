/**
 * This example is following frontend and backend separation.
 *
 * Before this .js is loaded, the html skeleton is created.
 *
 * This .js performs two steps:
 *      1. Use jQuery to talk to backend API to get the json data.
 *      2. Populate the data to correct html elements.
 */


/**
 * Handles the data returned by the API, read the jsonObject and populate data into html elements
 * @param resultData jsonObject
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

function handleListResult(resultData) {
    console.log("handleListResult: populating star table from resultData");


    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

    for (let i = 0; i < Math.min(105, resultData.length); i++) {


        var tempG1 = resultData[i]["gen1"];
        var tempG2 = resultData[i]["gen2"];
        var tempG3 = resultData[i]["gen3"];

        if(tempG3 != "")
            tempG2 = tempG2 + ',';
        if(tempG2 != "")
            tempG1 = tempG1 + ',';
        let rowHTML = "";
        rowHTML += "<tr>";
        rowHTML += "<th>"
            + '<a href="single-movie.html?id=' + resultData[i]['movie_id'] + '">' + resultData[i]["movie_title"] + '</a>'+ "</th>";
        rowHTML += "<th>" + resultData[i]["movie_year"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_director"] + "</th>";
        rowHTML += "<th>"
            + '<a href="index.html?mode=2&browse=' + resultData[i]['gen1'] + '&sort=1&num=25' + '">' + tempG1 + '</a>'
            + '<a href="index.html?mode=2&browse=' + resultData[i]['gen2'] + '&sort=1&num=25' + '">' + tempG2 + '</a>'
            + '<a href="index.html?mode=2&browse=' + resultData[i]['gen3'] + '&sort=1&num=25' + '">' + tempG3 + '</a>'
            + "</th>";
        rowHTML += "<th>"
            + '<a href="single-star.html?id=' + resultData[i]['ID1'] + '">' + resultData[i]["star1"] + '</a>'
            + '<a href="single-star.html?id=' + resultData[i]['ID2'] + '">' + resultData[i]["star2"] + '</a>'
            + '<a href="single-star.html?id=' + resultData[i]['ID3'] + '">' + resultData[i]["star3"] + '</a>'
            + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML +="<th><input id="+resultData[i]["movie_id"]+" type='button' " +
            "value='add cart' onclick='add_cart(this)'></th>"
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}

function add_cart(node) {
    var tr = $(node).parent().parent();
    var tb = $(tr).parent();
    var id  = $(node).attr("id");
    $.post("api/cart",{"id":id},function(data){
        if(data){
            alert("add cart success");
        }else{
            alert("add cart fail");
        }
    });

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

let mode = getParameterByName('mode');
let browse = getParameterByName('browse');
let sort = getParameterByName('sort');
let page = getParameterByName('page');
let num = getParameterByName('num');







// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/movie?mode=" + mode + "&browse=" + browse + "&sort=" + sort + "&page=" + page + "&num=" + num, // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});