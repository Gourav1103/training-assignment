document.addEventListener("DOMContentLoaded", function () {
  // Event listener for the modal's "Save changes" button in the footer
  document.getElementById("personDescription")
    .addEventListener("click", function () {
      var partyId = document
        .getElementById("personDescription")
        .getAttribute("data-value");
      var personTitle = document.getElementById("personTitle").value;
      var firstName = document.getElementById("firstName").value;
      var lastName = document.getElementById("lastName").value;
      var middleName = document.getElementById("middleName").value;
      var gender = document.getElementById("gender").value;
      var employeeStatus = document.getElementById("employeeStatus").value;
      var xhr = new XMLHttpRequest();
      xhr.open(
        "POST",
        "https://localhost:8443/bankingSystem/control/editPersonData",
        true
      ); // Replace with your actual save endpoint URL
      xhr.setRequestHeader("Content-Type", "application/json");

      // Add any required request parameters or data

      var requestData;

      if (middleName == "NA") {
        requestData = {
          partyId: partyId,
          personalTitle: personTitle,
          firstName: firstName,
          lastName: lastName,
          gender: gender,
          employmentStatusEnumId: employeeStatus,
        };
      } else {
        requestData = {
          partyId: partyId,
          personalTitle: personTitle,
          firstName: firstName,
          lastName: lastName,
          middleName: middleName,
          gender: gender,
          employmentStatusEnumId: employeeStatus,
        };
      }

      xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          if (xhr.status === 200) {
            try {
              console.log("updated Successfully");
              // Request successful, handle the response if needed
              document.getElementById("exampleModalLong").style.display =
                "none";
              document.body.classList.remove("modal-open");
              document.querySelector(".modal-backdrop").style.display = "none";
              // Call the hideModal function to hide the modal

              window.location.reload();
            } catch (error) {
              console.error("Error parsing JSON:", error);
            }
          } else {
            // Request failed, handle errors
            console.error("Error:", xhr.status);
          }
        }
      };

      xhr.send(JSON.stringify(requestData));
    });

  //update person Email
  document.getElementById("personEmail")
  .addEventListener("click", function () {
    var primaryContactMechId = document
      .getElementById("personEmail")
      .getAttribute("data-primary-value");
    var otherContactMechId = document
      .getElementById("personEmail")
      .getAttribute("data-other-value");
    var partyId = document
      .getElementById("personEmail")
      .getAttribute("data-id");
    var primaryEmail = document.getElementById("primaryemail").value;
    var otherEmail = document.getElementById("otheremail").value;
    var purpose = document.getElementById("emailContact").value;

    var xhr = new XMLHttpRequest();

    xhr.open(
      "POST",
      "https://localhost:8443/bankingSystem/control/editPersonEmail",
      true
    ); // Replace with your actual save endpoint URL
    xhr.setRequestHeader("Content-Type", "application/json");
    var requestData;
    // Add any required request parameters or data
    if (purpose == "PRIMARY_EMAIL") {
      if (primaryContactMechId != "NA") {
        requestData = {
          contactMechId: primaryContactMechId,
          contactMechPurposeTypeId: purpose,
          emailAddress: primaryEmail,
          partyId: partyId,
        };
      } else {
        requestData = {
          contactMechPurposeTypeId: purpose,
          emailAddress: primaryEmail,
          partyId: partyId,
        };
      }
    } else {
      if (otherContactMechId != "NA") {
        requestData = {
          contactMechId: otherContactMechId,
          contactMechPurposeTypeId: purpose,
          emailAddress: otherEmail,
          partyId: partyId,
        };
      } else {
        requestData = {
          contactMechPurposeTypeId: purpose,
          emailAddress: otherEmail,
          partyId: partyId,
        };
      }
    }

    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === 200) {
          try {
            console.log("updated Successfully");
            //set the full name val id
            if (purpose == "PRIMARY_EMAIL") {
              document.getElementById("priEmailDelete").style.display = "block";
            } else {
              document.getElementById("otherEmailDelete").style.display =
                "block";
            }

            // Request successful, handle the response if needed
            document.getElementById("editEmail").style.display = "none";
            document.body.classList.remove("modal-open");
            document.querySelector(".modal-backdrop").style.display = "none";
            window.location.reload();
          } catch (error) {
            console.error("Error parsing JSON:", error);
          }
        } else {
          // Request failed, handle errors
          console.error("Error:", xhr.status);
        }
      }
    };

    xhr.send(JSON.stringify(requestData));
  });

  //update the Person Address
  document.getElementById("personAddress")
    .addEventListener("click", function () {
      var contactMechId = document
        .getElementById("personAddress")
        .getAttribute("data-value");
      var partyId = document
        .getElementById("personAddress")
        .getAttribute("data-id");
      var address1 = document.getElementById("address1").value;
      var address2 = document.getElementById("address2").value;
      var city = document.getElementById("city").value;
      var houseNo = document.getElementById("house").value;
      var postal = document.getElementById("postal").value;

      console.log(contactMechId);
      var xhr = new XMLHttpRequest();

      xhr.open(
        "POST",
        "https://localhost:8443/bankingSystem/control/editPersonAddress",
        true
      );
      xhr.setRequestHeader("Content-Type", "application/json");
      var requestData;
      //check if address is present or not
      if (contactMechId != "NA") {
        requestData = {
          contactMechId: contactMechId,
          address1: address1,
          address: address2,
          city: city,
          contactMechPurposeTypeId: "PRIMARY_LOCATION",
          postalCode: postal,
          partyId: partyId,
          houseNumber: houseNo,
        };
      } else {
        requestData = {
          address1: address1,
          address: address2,
          city: city,
          contactMechPurposeTypeId: "PRIMARY_LOCATION",
          postalCode: postal,
          partyId: partyId,
          houseNumber: houseNo,
        };
      }

      xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          if (xhr.status === 200) {
            try {
              console.log("updated Successfully");
              document.getElementById("editPostalAddress").style.display = "none";
              document.body.classList.remove("modal-open");
              document.querySelector(".modal-backdrop").style.display = "none";
              window.location.reload();
            } catch (error) {
              console.error("Error parsing JSON:", error);
            }
          } else {
            // Request failed, handle errors
            console.error("Error:", xhr.status);
          }
        }
      };
      xhr.send(JSON.stringify(requestData));
    });

  //update the persons telecom Number
  document.getElementById("personPhone")
  .addEventListener("click", function () {
    var contactMechId = document
      .getElementById("personPhone")
      .getAttribute("data-value");
    var partyId = document
      .getElementById("personPhone")
      .getAttribute("data-id");
    var phone = document.getElementById("phone").value;

    console.log(contactMechId);
    var xhr = new XMLHttpRequest();

    xhr.open(
      "POST",
      "https://localhost:8443/bankingSystem/control/editPersonPhone",
      true
    );
    xhr.setRequestHeader("Content-Type", "application/json");
    var requestData;
    //check phone is present or not
    if (contactMechId != "NA") {
      requestData = {
        contactMechId: contactMechId,
        contactMechPurposeTypeId: "PRIMARY_PHONE",
        contactNumber: phone,
        partyId: partyId,
        countryCode: "+91",
      };
    } else {
      requestData = {
        contactMechPurposeTypeId: "PRIMARY_PHONE",
        contactNumber: phone,
        partyId: partyId,
        countryCode: "+91",
      };
    }

    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === 200) {
          try {
            console.log("updated Successfully");
            document.getElementById("editPhone").style.display = "none";
            document.body.classList.remove("modal-open");
            document.querySelector(".modal-backdrop").style.display = "none";
            window.location.reload();
          } catch (error) {
            console.error("Error parsing JSON:", error);
          }
        } else {
          // Request failed, handle errors
          console.error("Error:", xhr.status);
        }
      }
    };
    xhr.send(JSON.stringify(requestData));
  });

  //deleted email address
  document.getElementById("priEmailDelete")
    .addEventListener("click", function () {
      var contactMechId = document
        .getElementById("priEmailDelete")
        .getAttribute("data-val");
      var partyId = document
        .getElementById("priEmailDelete")
        .getAttribute("data-id");
      var purpose = document.getElementById("emailContact").value;
      console.log(contactMechId + " " + purpose);

      var xhr = new XMLHttpRequest();

      xhr.open(
        "POST",
        "https://localhost:8443/bankingSystem/control/deleteContact",
        true
      );
      xhr.setRequestHeader("Content-Type", "application/json");
      var requestData = {
        contactMechId: contactMechId,
        partyId: partyId,
      };

      xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          if (xhr.status === 200) {
            try {
              console.log("deleted Successfully");
              //set the full name val id
              document.getElementById("priEmailDelete").style.display = "none";
              window.location.reload();
            } catch (error) {
              console.error("Error parsing JSON:", error);
            }
          } else {
            // Request failed, handle errors
            console.error("Error:", xhr.status);
          }
        }
      };
      xhr.send(JSON.stringify(requestData));
    });

  //deleted email address
  document.getElementById("otherEmailDelete")
    .addEventListener("click", function () {
      var contactMechId = document
        .getElementById("otherEmailDelete")
        .getAttribute("data-val");
      var partyId = document
        .getElementById("otherEmailDelete")
        .getAttribute("data-id");
      var purpose = document.getElementById("emailContact").value;
      console.log(contactMechId + " " + purpose);

      var xhr = new XMLHttpRequest();

      xhr.open(
        "POST",
        "https://localhost:8443/bankingSystem/control/deleteContact",
        true
      ); // Replace with your actual save endpoint URL
      xhr.setRequestHeader("Content-Type", "application/json");

      // Add any required request parameters or data
      var requestData = {
        contactMechId: contactMechId,
        partyId: partyId,
      };

      xhr.onreadystatechange = function () {
        if (xhr.readyState === XMLHttpRequest.DONE) {
          if (xhr.status === 200) {
            try {
              console.log("deleted Successfully");
              document.getElementById("otherEmailDelete").style.display = "none";
              window.location.reload();
            } catch (error) {
              console.error("Error parsing JSON:", error);
            }
          } else {
            // Request failed, handle errors
            console.error("Error:", xhr.status);
          }
        }
      };
      xhr.send(JSON.stringify(requestData));
    });

  function handleDropdownChange() {
    var selectedValue = document.getElementById("emailContact").value;
    var primaryContactMechId = document
      .getElementById("priEmailDelete")
      .getAttribute("data-val");
    var otherContactMechId = document
      .getElementById("otherEmailDelete")
      .getAttribute("data-val");
    // Show or hide divs based on the selected value
    if (selectedValue === "PRIMARY_EMAIL") {
      document.getElementById("primaryEmailDiv").style.display = "block";
      document.getElementById("otherEmailDiv").style.display = "none";
      document.getElementById("otherEmailDelete").style.display = "none";
      document.getElementById("pEmail").style.display = "block";
      document.getElementById("oEmail").style.display = "none";
      var primaryContactMechId = document
        .getElementById("priEmailDelete")
        .getAttribute("data-val");
      if (primaryContactMechId != "NA") {
        document.getElementById("priEmailDelete").style.display = "block";
      } else {
        document.getElementById("priEmailDelete").style.display = "none";
      }
    } else if (selectedValue === "OTHER_EMAIL") {
      document.getElementById("primaryEmailDiv").style.display = "none";
      document.getElementById("otherEmailDiv").style.display = "block";
      document.getElementById("priEmailDelete").style.display = "none";
      document.getElementById("pEmail").style.display = "none";
      document.getElementById("oEmail").style.display = "block";

      if (otherContactMechId != "NA") {
        document.getElementById("otherEmailDelete").style.display = "block";
      }
    }
  }
  // Add event listener to the email dropdown
  document.getElementById("emailContact")
    .addEventListener("change", handleDropdownChange);

  // Call the function once on page load to set the initial state based on the default value
  handleDropdownChange();
});
