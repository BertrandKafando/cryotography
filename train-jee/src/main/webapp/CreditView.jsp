<%--
  Created by IntelliJ IDEA.
  User: kafan
  Date: 16/02/2023
  Time: 10:34
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@page import="es.kf.trainjee.web.CreditModel" %>
<%
    CreditModel creditModel = (CreditModel) request.getAttribute("model");
%>
<html>
<head>
    <title>Simulateur Credit</title>
</head>
<body>
Test Credit
<form action="credits" method="post">
    <label for="montant">Montant</label>
    <input type="text" name="montant" id="montant" value="<%=creditModel.getMontant()%>">
    <label for="duree">Durée</label>
    <input type="text" name="duree" id="duree" value="<%=creditModel.getDuree()%>">
    <label for="taux">Taux</label>
    <input type="text" name="taux" id="taux" value="<%=creditModel.getTaux()%>" >
    <input type="submit" value="Calculer">
</form>
<div>
    <h2>Resultat</h2>
    <p>Montant : <%= creditModel.getMontant() %></p>
    <p>Durée : <%= creditModel.getDuree() %></p>
    <p>Taux : <%= creditModel.getTaux() %></p>%
    <p>Montant mensuel : <%= creditModel.getMensualite() %></p>
</div>
</body>
</html>
