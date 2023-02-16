package es.kf.trainjee.metier;

public class CreditSimul {
    public  double  calculMensualite(double montant, double taux, int duree) {
        double mensualite = 0;
        double tauxMensuel = taux / 12;
        double tauxMensuelPourcent = tauxMensuel / 100;
        double dureeEnMois = duree * 12;
        double puissance = Math.pow(1 + tauxMensuelPourcent, dureeEnMois);
        mensualite = montant * (tauxMensuelPourcent * puissance) / (puissance - 1);
        return mensualite;
    }
}
