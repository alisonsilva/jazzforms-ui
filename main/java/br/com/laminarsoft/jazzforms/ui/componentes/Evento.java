package br.com.laminarsoft.jazzforms.ui.componentes;


public class Evento {
	private String nomeEvento;
	private String nomeImplementacaoEvento;
	private String nomeComponente;
	private String idTipoEvento;
	private String evtDescricao;
	private String evtAssinatura;
	
	public Evento(String nomeEvt, String nomeImplEvt, String nomeComp, String idTipoEvento, String evtDescricao, String evtAssinatura) {
		this.nomeEvento = new String(nomeEvt);
		this.nomeImplementacaoEvento = new String(nomeImplEvt);
		this.nomeComponente = new String(nomeComp);
		this.evtDescricao = new String(evtDescricao);
		this.evtAssinatura = new String(evtAssinatura);
		this.idTipoEvento = new String(idTipoEvento);
	}

	public String getNomeEvento() {
		return nomeEvento;
	}

	public String getNomeImplementacaoEvento() {
		return nomeImplementacaoEvento;
	}

	public String getNomeComponente() {
		return nomeComponente;
	}
	
	public String getEvtDescricao() {
		return evtDescricao;
	}
	
	public String getIdTipoEvento() {
		return idTipoEvento;
	}
	
	public String getEvtAssinatura() {
		return evtAssinatura;
	}
	
	public void setNomeEvento(String nomeEvento) {
		this.nomeEvento = nomeEvento;
	}
	
	public void setNomeImplementacaoEvento(String nomeImp) {
		this.nomeImplementacaoEvento = nomeImp;
	}
	
	public void setComponente(String nomeCmp) {
		this.nomeComponente = nomeCmp;
	}
	
	public void setIdTipoEvento(String idTipoEvento) {
		this.idTipoEvento = idTipoEvento;
	}
	
	public void setEvtDescricao(String evtDescricao) {
		this.evtDescricao = evtDescricao;
	}
	
	public void setEvtAssinatura(String evtAssinatura) {
		this.evtAssinatura = evtAssinatura;
	}
	
	public Evento clone() {
		Evento novo = new Evento(this.nomeEvento, this.nomeImplementacaoEvento, this.nomeComponente, this.idTipoEvento, this.evtDescricao, this.evtAssinatura);
		return novo;
	}
}
