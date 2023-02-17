<%--
  Created by IntelliJ IDEA.
  User: kafan
  Date: 17/02/2023
  Time: 09:08
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Signer un document</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0-alpha1/dist/css/bootstrap.min.css" rel="stylesheet" integrity="sha384-GLhlTQ8iRABdZLl6O3oVMWSktQOp6b7In1Zl3/Jr59b6EGGoI1aFkw7cmDA6j6gD" crossorigin="anonymous">
</head>
<body>
<div class="container">
<h2  class="text-center mb-2" >Signer un document</h2>

    <!-- Content here -->
    <form method="post" action="sign" enctype="multipart/form-data">
        <input class="form-control" type="file" name="pdf"/>
        <label class="form-label" for="reason">Raison</label>
        <input class="form-control" type="text" name="reason" id="reason"/>
        <label class="form-label" for="location">Lieu</label>
        <input class="form-control" type="text" name="location" id="location" />
        <input class="btn btn-primary mt-2" type="submit" value="Signer"/>
    </form>
</div>


</body>
</html>
