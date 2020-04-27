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
function handleListResult(resultData) {
    console.log("handleListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let starTableBodyElement = jQuery("#star_table_body");

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

    for (let i = 0; i < Math.min(20, resultData.length); i++) {
        let rowHTML = "";
        rowHTML += "<tr>";
        //rowHTML += "<th>" + resultData[i]["movie"] + "</th>";
        rowHTML += "<th>" + resultData[i]["movie_title"] + "</th>";
        rowHTML += "<th>" + resultData[i]["price"] + "</th>";
       // rowHTML += "<th>" + resultData[i]["quantity"] + "</th>";
        rowHTML+="<th>" + resultData[i]["price"]*resultData[i]["quantity"] + "</th>";
        rowHTML+="<th><input id="+resultData[i]["movie_id"]+" type='button' value='+' onclick='add(this)'>   " +
            "<label>"+resultData[i]["quantity"]+"</label>  " +
            " <input id="+resultData[i]["movie_id"]+" type='button' value='-' onclick='dec(this)'></th>"
        rowHTML +="<th><input id="+resultData[i]["movie_id"]+" type='button' value='delete' onclick='del(this)'></th>"
        //rowHTML += "<th>" + resultData[i]["movie_rating"] + "</th>";
        rowHTML += "</tr>";

        // Append the row created to the table body, which will refresh the page
        starTableBodyElement.append(rowHTML);
    }
}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/showCart", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData) // Setting callback function to handle data returned successfully by the StarsServlet
});

function add(node) {
    var count = Number($(node).parent().find("label:eq(0)").html());
    count++;
    $(node).parent().find("label:eq(0)").html(count);
    var price = Number($(node).parent().parent().find("th:eq(1)").html());
    var totalPrice = price * count;
    $(node).parent().parent().find("th:eq(2)").html(totalPrice);
    var id  = $(node).attr("id");
    $.post("api/addQuantity",{"id":id},function(data){
        if(data){

            alert("add success");
        }else{
            alert("add fail");
        }
    });


}
function dec(node) {
    var count = Number($(node).parent().find("label:eq(0)").html());//blank means children
    if(count > 1){
        count--;
    }
    $(node).parent().find("label:eq(0)").html(count);
    var price = Number($(node).parent().parent().find("th:eq(1)").html());
    var totalPrice = price * count;
    $(node).parent().parent().find("th:eq(2)").html(totalPrice);
    var id  = $(node).attr("id");
    $.post("api/subtractQuantity",{"id":id},function(data){
        if(data!="false"){

            alert("subtract success");
        }else{
            alert("subtract fail");
        }
    });

}
function del(node) {
    var tr = $(node).parent().parent();
    var tb = $(tr).parent();
    var id  = $(node).attr("id");
    $.post("api/deleteCart",{"id":id},function(data){
        if(data){
        $(tr).remove();
        alert("delete success");
    }else{
        alert("delete fail");
    }
    });

}
