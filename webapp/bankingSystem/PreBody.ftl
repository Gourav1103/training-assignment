<html>
  <head>
    <title>${layoutSettings.companyName}</title>
    <meta name="viewport" content="width=device-width, user-scalable=no"/>
    <#if webSiteFaviconContent?has_content>
      <link rel="shortcut icon" href="">
    </#if>
    <#list layoutSettings.styleSheets as styleSheet>
      <link rel="stylesheet" href="${StringUtil.wrapString(styleSheet)}" type="text/css"/>
    </#list>
     <script src="/bankingSystem/js/employeeData.js"></script>
     <script src="/bankingSystem/js/createParty.js"></script>
     <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
     <script src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.11.0/umd/popper.min.js"></script>
    <script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>
    <script src="/bankingSystem/js/bootstrap.bundle.min.js"></script>
  </head>
  <body >
    <nav class="navbar navbar-expand-md navbar-light" style="background-color: rgb(201, 76, 76); padding:10px; ">
   <a class="navbar-brand" style="color:white" href="<@ofbizUrl>bank</@ofbizUrl>">AXIS</a>
         <div class="collapse navbar-collapse">
             <ul class="navbar-nav">
                  <li class="active nav-item" ><a class="nav-link" href="<@ofbizUrl>bank</@ofbizUrl>" ">Home</a></li>
                  <li class="nav-item "><a class="nav-link" href="<@ofbizUrl>employees</@ofbizUrl>" >Employees</a></li>
                  <li class="nav-item "><a class="nav-link" href="<@ofbizUrl>customers</@ofbizUrl>" >Customers</a></li>
                </ul>
          </div>
    </nav>
