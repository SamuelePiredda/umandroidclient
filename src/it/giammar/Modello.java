package it.giammar;

import it.giammar.pratomodel.QueryReply.Database;

public class Modello {
	private String bancaDati;
    private String descrizione;
    private int tipo;

    public Modello(String bancaDati, String percorso) {
        this.bancaDati = bancaDati;
        this.descrizione = percorso;
        this.tipo = 1;
    }

    public Modello(String bancaDati, String percorso, int tipo) {
        this.bancaDati = bancaDati;
        this.descrizione = percorso;
        this.tipo = tipo;
    }
    
    public String getBancaDati() {
        return bancaDati;
    }

    public void setBancaDati(String bancaDati) {
        this.bancaDati = bancaDati;
    }

    public String getDescrizione() {
        return descrizione;
    }

    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    
    public int getTipo() {
        return tipo;
    }

    public void setDescrizione(int tipo) {
        this.tipo = tipo;
    }


}
