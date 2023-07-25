package it.cgmconsulting.myblog.service;

import org.springframework.scheduling.annotation.Scheduled;

public class ScheduledService {

    @Scheduled(cron = "0 0 1 1 * *") // "0 30 9 7 * *" ogni mese al settimo giorno alle 9 e 30
    //@Scheduled(fixedRate = 1 , timeUnit = TimeUnit.DAYS) //Va a schedulare ad intervalli programmati, in millesecondi li conta
    public void generatedReportAuthorRating(){
        System.out.println("SCHEDULAZIONE MENSILE PER AUTHOR RATING");
        //la schedulazione parte ogni primo dle mese
        //Il report deve restituire tutti gli author che hanno scritto qualcosa nel periodo preso in considerazione
        //e scrivere nel DB questa 'classifica'

        //ETL -> Extract , Transform, Load
        //Extract: Recupore dei dati per creare una lista di ReportAuthorRating
        //Transform: elaborare questi dati (qualora servisse)
        //Load: caricarli da qualche parte (la tabella report:author:reporting nel nostro caso)

    }
}
