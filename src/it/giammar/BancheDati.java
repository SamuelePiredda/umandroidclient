package it.giammar;

import android.widget.CheckBox;
import android.widget.EditText;

public class BancheDati {
  private long id;
  private String nome;
  private int enabled;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }
  
  public long getEnabled() {
	    return enabled;
  }

  public void setEnabled(int enabled) {
    this.enabled = enabled;
  }

  public String getNome() {
    return nome;
  }

  public void setNome(String nome) {
    this.nome = nome;
  }
  
    // Will be used by the ArrayAdapter in the ListView
  @Override
  public String toString() {
    return nome;
  }
} 