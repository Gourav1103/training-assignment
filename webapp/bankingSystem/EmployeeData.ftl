<div style=" padding:30px;">
   <div class="basic-div container" style="display:flex; ">
      <div style="width:50%;">
         <h3>
         Basic information
         <h3>
         <div>
            <label class="basic-label">Name:
            <label>
               <p class="basic-p" id="name">${person.personalTitle}. ${person.firstName} ${person.middleName?default("")} ${person.lastName}</p>
         </div>
         <div> <label class="basic-label">Gender:<label>
         <#assign gender=person.gender>
         <#if gender=="M">
         <p class="basic-p" id="personGender">Male</p>
         <#else>
         <p class="basic-p" id="personGender">Female</p>
         </#if>
         </div>
         <#assign employeeS=person.employmentStatusEnumId>
         <#if employeeS??>
         <div> <label class="basic-label">Employee-Status:<label>
         <#assign employee=person.employmentStatusEnumId>
         <#if employee=="EMPS_FULLTIME">
         <p class="basic-p" id="personEmployee">Full-time Employed</p>
         <#elseif employee=="EMPS_PARTTIME">
         <p class="basic-p" id="personEmployee">Part-time Employed</p>
         </#if>
         </div>
         </#if>
      </div>
      <div style="width:50%;"> <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#exampleModalLong"> EDIT </button> <!-- Modal -->
      <div class="modal fade" id="exampleModalLong" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
      <div class="modal-dialog" role="document">
      <div class="modal-content">
      <div class="modal-header">
      <h5 class="modal-title" id="exampleModalLongTitle">Edit Employee Data</h5> <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button>
      </div>
      <div class="modal-body form" style="display:grid;">
      <div> <label for="personTitle">Person Title</label> <select id="personTitle">
      <option value="Mr">Mr</option>
      <option value="Mrs">Mrs</option>
      <option value="Miss">Miss</option>
      </select> </div>
      <div> <label for="firstName">First Name</label> <input type="name" placeholder=${person.firstName} id="firstName"> </div>
      <div> <label for="middleName">Middle Name</label> <input type="name" placeholder=${person.middleName?default("NA")} id="middleName"> </div>
      <div> <label for="lastName">Last Name</label> <input type="name" placeholder=${person.lastName} id="lastName"> </div>
      <div> <label for="gender">Gender</label> <select id="gender">
      <option value="M">Male</option>
      <option value="F">Female</option>
      </select> </div>
      <div> <label for="employeeStatus">Employee Status</label> <select id="employeeStatus">
      <option value="EMPS_FULLTIME">Full-Time Employed</option>
      <option value="EMPS_PARTTIME">Part-Time Employed</option>
      </select> </div>
      </div>
      <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-value=${person.partyId} id="personDescription">Save changes</button> </div>
      </div>
      </div>
      </div>
      </div>
   </div>
</div>
<div class="basic-div container" style="display:grid;">
   <div class="container" style="display:flex;">
      <div style="width:80%">
         <h3>
         Contact Info
         <h3>
      </div>
      <div style="width:20%;">
         <div class="dropdown">
            <button class="btn btn-secondary dropdown-toggle" type="button" id="dropdownMenuButton" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false"> Operations </button>
            <div class="dropdown-menu" aria-labelledby="dropdownMenuButton"> <button type="button" class="btn btn-primary dropdown-item" data-toggle="modal" data-target="#editEmail"> Edit Email </button> <button type="button" class="btn btn-primary dropdown-item" data-toggle="modal" data-target="#editPostalAddress"> Edit Address </button> <button type="button" class="btn btn-primary dropdown-item" data-toggle="modal" data-target="#editPhone"> Edit Phone </button> </div>
         </div>
      </div>
   </div>
   <div class="container" style="display:flex; padding:20px;">
      <div class="container basic-child-div" style="width:28%">
         <h4>Email Info</h4>
         <div style:"margin-top:5px">
            <label for="emailContact" style="font-size:15px; font-style:bold;">Purposes</label>
            <select id="emailContact" name="emailContact" style="font-size:12px;">
               <option value="PRIMARY_EMAIL" style="font-size:12px;"> Primary Email</option>
               <option value="OTHER_EMAIL" style="font-size:12px;">Other Email</option>
            </select>
         </div>
         <div class="container" style="display:grid;">
            <div style="margin-left:auto;"> <button data-val="${primaryEmailContactId?default("NA")}" data-id=${person.partyId?default("NA")} style="display:none;" id="priEmailDelete">delete</button> <button data-val="${otherEmailContactId?default("NA")}" data-id=${person.partyId?default("NA")} style="display:none;" id="otherEmailDelete">delete</button> </div>
            <div>
               <div id="primaryEmailDiv" style="margin-top:5px;display: none;">
                  <label class="basic-label">Email:
                  <label>
                     <p class="basic-p" id="primaryEmail">${emailPrimaryId?default("NA")}</p>
               </div>
               <div id="otherEmailDiv" style="margin-top:5px;display: none;"> <label class="basic-label">Email:<label>
               <p class="basic-p" id="otherEmail">${emailOtherId?default("NA")}</p>
               </div>
            </div>
         </div>
      </div>
      <div class="container basic-child-div" style="width:40%">
      <h4>Address Info</h4>
      <#if postalAddress??>
      <div> <label class="basic-label">Address:<label>
      <p class="basic-p" id="Address">${postalAddress.houseNumber?default("")}, ${postalAddress.address1?default("NA")}, ${postalAddress.city?default("")}</p>
      </div>
      <div> <label class="basic-label">PostalCode:<label>
      <p class="basic-p" id="Postal">${postalAddress.postalCode?default("NA")}</p>
      </div>
      <#else>
      <div> <label class="basic-label">Address:<label>
      <p class="basic-p" id="Address">NA</p>
      </div>
      <div> <label class="basic-label">PostalCode:<label>
      <p class="basic-p" id="Postal">NA</p>
      </div>
      </#if>
      </div>
      <div class="container basic-child-div" style="width:28%">
      <h4>Phone Info</h4>
      <#if telecomNumber??>
      <div> <label class="basic-label">Phone:<label>
      <p class="basic-p" id="Phone">${telecomNumber.countryCode?default("")} ${telecomNumber.contactNumber?default("NA")}</p>
      </div>
      <#else>
      <div> <label class="basic-label">Phone:<label>
      <p class="basic-p" id="Phone">NA</p>
      </div>
      </#if>
      </div>
   </div>
</div>
</div> <!-- Modal -->
<div class="modal fade" id="editEmail" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
<div class="modal-dialog" role="document">
<div class="modal-content">
<div class="modal-header">
<h5 class="modal-title" id="exampleModalLongTitle">Edit Employee Email</h5> <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button>
</div>
<div class="modal-body form" style="display:grid;">
<div id="pEmail" style="display:none;"> <label for="email">Email</label> <input type="email" placeholder="${emailPrimaryId?default(" NA")}" id="primaryemail"> </div>
<div id="oEmail" style="display:none;"> <label for="email">Email</label> <input type="email" placeholder="${emailOtherId?default(" NA")}" id="otheremail"> </div>
</div>
<div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-primary-value=${primaryEmailContactId?default("NA")} data-other-value=${otherEmailContactId?default("NA")} data-id=${person.partyId} id="personEmail">Save changes</button> </div>
</div>
</div>
</div> <!-- Modal -->
<div class="modal fade" id="editPostalAddress" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLongTitle">Edit Employee Address</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button>
         </div>
         <#if postalAddress??>
         <div class="modal-body form" style="display:grid;">
            <div> <label for="house">House Number</label> <input type="name" placeholder="${postalAddress.houseNumber?default(" NA")}" id="house"> </div>
            <div> <label for="address1">Address1</label> <input type="name" placeholder="${postalAddress.address1?default(" NA")}" id="address1"> </div>
            <div> <label for="city">City</label> <input type="name" placeholder="${postalAddress.city?default(" NA")}" id="city"> </div>
            <div> <label for="address2">Address2</label> <input type="name" placeholder="${postalAddress.address2?default(" NA")}" id="address2"> </div>
            <div> <label for="postal">Postal code</label> <input type="name" placeholder="${postalAddress.postalCode?default(" NA")}" id="postal"> </div>
         </div>
         <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-value=${postalAddress.contactMechId?default("NA")} data-id=${person.partyId} id="personAddress">Save changes</button> </div>
         <#else>
         <div class="modal-body form" style="display:grid;">
            <div> <label for="house">House Number</label> <input type="name" placeholder="NA" id="house"> </div>
            <div> <label for="address1">Address1</label> <input type="name" placeholder="NA" id="address1"> </div>
            <div> <label for="city">City</label> <input type="name" placeholder="NA" id="city"> </div>
            <div> <label for="address2">Address2</label> <input type="name" placeholder="NA" id="address2"> </div>
            <div> <label for="postal">Postal code</label> <input type="name" placeholder="NA" id="postal"> </div>
         </div>
         <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-value="NA" data-id=${person.partyId} id="personAddress">Save changes</button> </div>
         </#if>
      </div>
   </div>
</div>
<div class="modal fade" id="editPhone" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
   <div class="modal-dialog" role="document">
      <div class="modal-content">
         <div class="modal-header">
            <h5 class="modal-title" id="exampleModalLongTitle">Edit Employee Phone</h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close"> <span aria-hidden="true">&times;</span> </button>
         </div>
         <div class="modal-body form" style="display:grid;">
            <#if telecomNumber??>
            <div> <label for="phone">Phone Number</label> <input type="name" placeholder="${telecomNumber.contactNumber?default("")}" id="phone"> </div>
         </div>
         <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-value=${telecomNumber.contactMechId?default("NA")} data-id=${person.partyId} id="personPhone">Save changes</button> </div>
         <#else>
         <div> <label for="phone">Phone Number</label> <input type="name" placeholder="NA" id="phone"> </div>
      </div>
      <div class="modal-footer"> <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button> <button type="button" class="btn btn-primary" data-value="NA" data-id=${person.partyId} id="personPhone">Save changes</button> </div>
      </#if>
   </div>
</div>
</div>
</div>
</div>
</div>