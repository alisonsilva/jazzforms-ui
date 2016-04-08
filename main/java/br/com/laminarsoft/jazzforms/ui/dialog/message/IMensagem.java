package br.com.laminarsoft.jazzforms.ui.dialog.message;

import br.com.laminarsoft.jazzforms.persistencia.model.util.InfoRetornoMensagem;

public interface IMensagem {
	public static final int MENSAGEM_USUARIO = 1;
	public static final int MENSAGEM_GRUPO_USUARIOS = 2;
	public static final int MENSAGEM_EQUIPAMENTO = 3;
	public static final int MENSAGEM_GRUPO_EQUIPAMENTOS = 4;
	
	public void mensagemEnviadaSucesso(InfoRetornoMensagem info);
	public void mensagemEnviadaError(Exception excp);
}
