package es.kf.trainjee.web;

import es.kf.trainjee.metier.CreditSimul;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

/*
* deployer le servlet  : soit annotation soit web.xml
* */
@WebServlet(name = "credits", value = "/credits")
public class ControllerServlet extends HttpServlet {
    private CreditSimul metier;

    // initialisation du servlet

    public void init() throws ServletException {
        metier = new CreditSimul();
    }


    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setAttribute("model", new CreditModel());
        req.getRequestDispatcher("CreditView.jsp").forward(req, resp);
    }

    protected  void  doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double montant = Double.parseDouble(req.getParameter("montant"));
        double taux = Double.parseDouble(req.getParameter("taux"));
        int duree = Integer.parseInt(req.getParameter("duree"));
        double mensualite = metier.calculMensualite(montant, taux, duree);
        CreditModel model = new CreditModel(montant, taux, duree, mensualite);
        req.setAttribute("model", model);
        req.getRequestDispatcher("CreditView.jsp").forward(req, resp);
    }


    public void destroy() {
    }
}
