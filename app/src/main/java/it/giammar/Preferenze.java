package it.giammar;

import android.widget.CheckBox;
import android.widget.EditText;

public class Preferenze {
  private long id;
  private String utente;
  private String password;
  private String host;
  private String port;
  private String attPort;
  private int useSSL;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public long getuseSSL() {
	    return useSSL;
  }

  public void setuseSSL(int useSSL) {
    this.useSSL = useSSL;
  }

  public String getUtente() {
    return utente;
  }

  public void setUtente(String utente) {
    this.utente = utente;
  }
  
  public String getPassword() {
	    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public String getHost() {
	    return host;
  }

  public void setHost(String host) {
	  this.host = host;
  }
  
  public String getPort() {
	    return port;
  }

  public void setPort(String port) {
		this.port = port;
  }
  
  public String getAttport() {
	    return attPort;
  }

  public void setattPort(String attPort) {
		this.attPort = attPort;
  }

  // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
    return host;
  }
} 