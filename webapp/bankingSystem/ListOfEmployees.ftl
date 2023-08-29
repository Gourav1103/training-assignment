<h1 style="color:black; padding:20px;">Employees Data</h1>
<div style="padding:60px;">
   <div style="padding-bottom:20px;">
      <button type="button" class="btn btn-primary" data-toggle="modal" data-target="#createPerson">
      create
      </button>
   </div>
   <!-- Modal -->
   <div class="modal fade" id="createPerson" tabindex="-1" role="dialog" aria-labelledby="exampleModalLongTitle" aria-hidden="true">
      <div class="modal-dialog" role="document">
         <div class="modal-content">
            <div class="modal-header">
               <h5 class="modal-title" id="exampleModalLongTitle">Edit Employee Data</h5>
               <button type="button" class="close" data-dismiss="modal" aria-label="Close">
               <span aria-hidden="true">&times;</span>
               </button>
            </div>
            <div class="modal-body form" style="display:grid;">
               <div>
                  <label for="pTitle">Person Title</label>
                  <select id="pTitle">
                     <option value="Mr">Mr</option>
                     <option value="Mrs">Mrs</option>
                     <option value="Miss">Miss</option>
                  </select>
               </div>
               <div>
                  <label for="fName">First Name</label>
                  <input type="name"  id="fName">
               </div>
               <div>
                  <label for="mName">Middle Name</label>
                  <input type="name" id="mName">
               </div>
               <div>
                  <label for="lName">Last Name</label>
                  <input type="name"  id="lName">
               </div>
               <div>
                  <label for="gen">Gender</label>
                  <select id="gen">
                     <option value="M">Male</option>
                     <option value="F">Female</option>
                  </select>
               </div>
               <div>
                  <label for="eStatus">Employee Status</label>
                  <select id="eStatus">
                     <option value="EMPS_FULLTIME">Full-Time Employed</option>
                     <option value="EMPS_PARTTIME">Part-Time Employed</option>
                  </select>
               </div>
            </div>
            <div class="modal-footer">
               <button type="button" class="btn btn-secondary" data-dismiss="modal"  style="margin-right:auto;">Close</button>
               <button type="button" class="btn btn-primary"  id="personDes">Save changes</button>
            </div>
         </div>
      </div>
   </div>
   <table class="table table-bordered table-striped table-hover" >
      <thead>
         <tr>
            <th>${uiLabelMap.Details}</th>
            <th>${uiLabelMap.FirstName}</th>
            <th>${uiLabelMap.EmployeeGender}</th>
            <th>${uiLabelMap.EmployeeStatus}</th>
            <th>${uiLabelMap.DeleteEmployee}</th>
         </tr>
      </thead>
      <tbody>
         <#list employeesData as employee>
         <tr id="tr">
            <td class="list-group-item" ><a class="link-primary" href="<@ofbizUrl>employees/${employee.partyId}</@ofbizUrl>" style="color:black;">Details</a></td>
            <td>${employee.firstName?default("NA")}</td>
            <td>${employee.gender?default("NA")}</td>
            <td>${employee.employmentStatusEnumId}</td>
            <td><img src="https://cdn-icons-png.flaticon.com/512/3405/3405244.png" width="50"  id=${employee.partyId} data-val=${employee.partyId} /></td>
         </tr>
         </#list>
      </tbody>
   </table>

</div>