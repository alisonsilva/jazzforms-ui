package br.com.laminarsoft.jazzforms.ui.entidade;

import java.util.ArrayList;
import java.util.List;

public class UIUsuario implements Comparable<UIUsuario> {
	private String nome;
	private String uid;
	private String cn;
	private String dn;
	private String groupDn;
	private String senha;
	
	
	private List<String> papeis = new ArrayList<String>();
	
	@Override
	public int compareTo(UIUsuario o) {
		return nome.compareTo(o.getNome());
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
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

	public String getGroupDn() {
		return groupDn;
	}

	public void setGroupDn(String groupDn) {
		this.groupDn = groupDn;
	}

	public List<String> getPapeis() {
		return papeis;
	}

	public void setPapeis(List<String> papeis) {
		this.papeis = papeis;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}
	
	
}
