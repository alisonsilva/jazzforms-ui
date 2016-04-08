package br.com.laminarsoft.jazzforms.ui.entidade;

import java.util.ArrayList;
import java.util.List;

public class UIGrupo implements Comparable<UIGrupo> {
	private String nome;
	private String cn;
	private String dn;
	private String description;
	
	private List<UIUsuario> usuarios = new ArrayList<UIUsuario>();
	
	@Override
	public int compareTo(UIGrupo o) {
		return nome.compareTo(o.getNome());
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getCn() {
		return cn;
	}
	public void setCn(String cn) {
		this.cn = cn;
	}
	public String getDn() {
		return dn;
	}
	public void setDn(String dn) {
		this.dn = dn;
	}
	public List<UIUsuario> getUsuarios() {
		return usuarios;
	}
	public void setUsuarios(List<UIUsuario> usuarios) {
		this.usuarios = usuarios;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}

	
}
