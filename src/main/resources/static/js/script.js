// Function to open the sidebar
function openNav() {
    document.getElementById("mySidebar").style.width = "250px"; // Set sidebar width to 250px
    document.getElementById("main").style.marginLeft = "250px"; // Adjust main content margin
    showContent(); // Show content when sidebar is opened
}

// Function to close the sidebar
function closeNav() {
    document.getElementById("mySidebar").style.width = "0"; // Set sidebar width to 0
    document.getElementById("main").style.marginLeft = "0"; // Reset main content margin
    hideContent(); // Hide content when sidebar is closed
}

// Function to show content
function showContent() {
    var contentItems = document.querySelectorAll(".item"); // Select all items with class "item"
    for (var i = 0; i < contentItems.length; i++) {
        contentItems[i].style.display = "block"; // Set display property to "block"
    }
}

// Function to hide content
function hideContent() {
    var contentItems = document.querySelectorAll(".item"); // Select all items with class "item"
    for (var i = 0; i < contentItems.length; i++) {
        contentItems[i].style.display = "none"; // Set display property to "none"
    }
}
 const search = () => {
    let query = $("#search-input").val();

    if (query == '') {
        $(".search-result").hide();
    } else {
        let url = `http://localhost:8081/search/${query}`;
        fetch(url)
            .then((response) => {
                return response.json();
            })
            .then((data) => {
               
                let text = `<div class='list-group'>`; // Declare text variable here
                data.forEach((contact) => {
                    text += `<a href='/user/${contact.cId}/contact/' class='list-group-item list-group-action'>${contact.name}</a>`; // Fix concatenation
                });
                text += `</div>`;
               // Log text to see the constructed HTML
                // Assuming you want to append the HTML to some container with class "search-result"
                $(".search-result").html(text); // Use .html() to replace the contents
            });
        console.log(query);
        $(".search-result").show();
    }
};
