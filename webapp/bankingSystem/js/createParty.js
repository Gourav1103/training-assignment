document.addEventListener("DOMContentLoaded", function () {
  const tableDeletes = document.querySelectorAll("img[id]");

  tableDeletes.forEach((row) => {
    row.addEventListener("click", function () {
      const shouldDelete = window.confirm(
        "Are you sure you want to delete Employee?"
      );
      if (shouldDelete) {
        var id = this.id;
        var partyIdTo = document.getElementById(id).getAttribute("data-val");
        var xhr = new XMLHttpRequest();
        xhr.open(
          "POST",
          "https://localhost:8443/bankingSystem/control/deletePerson",
          true
        );
        xhr.setRequestHeader("Content-Type", "application/json");
        var requestData = {
          partyIdTo: partyIdTo,
          roleTypeIdFrom: "ORGANIZATION_ROLE",
          roleTypeIdTo: "EMPLOYEE",
        };

        xhr.onreadystatechange = function () {
          if (xhr.readyState === XMLHttpRequest.DONE) {
            if (xhr.status === 200) {
              try {
                console.log("deleted Successfully");
                // Request successful, handle the response if needed
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
      }
    });
  });
  document.getElementById("personDes").addEventListener("click", function () {
    var personTitle = document.getElementById("pTitle").value;
    var firstName = document.getElementById("fName").value;
    var lastName = document.getElementById("lName").value;
    var middleName = document.getElementById("mName").value;
    var gen = document.getElementById("gen").value;
    var emp = document.getElementById("eStatus").value;
    var xhr = new XMLHttpRequest();
    xhr.open(
      "POST",
      "https://localhost:8443/bankingSystem/control/createsPerson",
      true
    );
    xhr.setRequestHeader("Content-Type", "application/json");

    var requestData;

    if (middleName == "NA") {
      requestData = {
        roleTypeId: "EMPLOYEE",
        personalTitle: personTitle,
        firstName: firstName,
        lastName: lastName,
        gender: gen,
        employmentStatusEnumId: emp,
      };
    } else {
      requestData = {
        roleTypeId: "EMPLOYEE",
        personalTitle: personTitle,
        firstName: firstName,
        lastName: lastName,
        middleName: middleName,
        gender: gen,
        employmentStatusEnumId: emp,
      };
    }

    xhr.onreadystatechange = function () {
      if (xhr.readyState === XMLHttpRequest.DONE) {
        if (xhr.status === 200) {
          try {
            console.log("created Successfully");
            // Request successful, handle the response if needed
            document.getElementById("createPerson").style.display = "none";
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
});
