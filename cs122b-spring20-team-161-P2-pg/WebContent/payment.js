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
let payment_form = $("#payment_form");
function handleListResult(resultData) {
    console.log("handleListResult: populating star table from resultData");

    // Populate the star table
    // Find the empty table body by id "star_table_body"
    let totalLocation = jQuery("#totalLocation");

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
        totalLocation.append(rowHTML);

}


/**
 * Once this .js is loaded, following scripts will be executed by the browser
 */

// Makes the HTTP GET request and registers on success callback function handleStarResult
jQuery.ajax({
    dataType: "json", // Setting return data type
    method: "GET", // Setting request method
    url: "api/payment", // Setting request url, which is mapped by StarsServlet in Stars.java
    success: (resultData) => handleListResult(resultData)
    // Setting callback function to handle data returned successfully by the StarsServlet
});





/**
 * Handle the data returned by LoginServlet
 * @param resultDataString jsonObject
 */
function handleLoginResult(resultDataString) {
    let resultDataJson = JSON.parse(resultDataString);

    console.log("handle login response");
    console.log(resultDataJson);
    console.log(resultDataJson["status"]);

    // If login succeeds, it will redirect the user to index.html
    if (resultDataJson["status"] === "success") {
        window.location.replace("PlacedOrder.html");
    } else {
        // If login fails, the web page will display
        // error messages on <div> with id "login_error_message"
        console.log("show error message");
        console.log(resultDataJson["message"]);
        $("#login_error_message").text(resultDataJson["message"]);
    }
}

/**
 * Submit the form content with POST method
 * @param formSubmitEvent
 */
function submitPaymentForm(formSubmitEvent) {
    console.log("submit login form");
    /**
     * When users click the submit button, the browser will not direct
     * users to the url defined in HTML form. Instead, it will call this
     * event handler when the event is triggered.
     */
    formSubmitEvent.preventDefault();

    $.ajax(
        "api/PlaceOrder", {
            method: "POST",
            // Serialize the login form to the data sent by POST request
            data: payment_form.serialize(),
            success: handleLoginResult
        }
    );
}

// Bind the submit action of the form to a handler function
payment_form.submit(submitPaymentForm);

