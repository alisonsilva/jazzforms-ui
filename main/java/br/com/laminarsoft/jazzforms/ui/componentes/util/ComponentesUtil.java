package br.com.laminarsoft.jazzforms.ui.componentes.util;

public class ComponentesUtil {

	
	/**
	 * M�todo est�tico que retorna o nome da fun��o principal. Ela ser� a primeira fun��o encontrada, ou a fun��o "tagueada" com @MainFunction
	 * @param codigo Todo o c�digo a ser analisado para recupera��o da fun��o principal
	 * @return A fun��o principal do c�digo passado
	 */
	public static String getMainFunctionName(String codigo) {
		String mfunction = "";
		int pos = codigo.indexOf("@MainFunction");
		if (pos >= 0 ) {
			String subs = codigo.substring(pos);
			int fpos = subs.indexOf("function");
			if (fpos >= 0) {
				String fname = subs.substring(fpos,
						subs.indexOf(")")+1);
				mfunction = fname.trim();
			}
		} else {
			int fpos = codigo.indexOf("function");
			if (fpos >= 0) {
				String fname = codigo.substring(fpos,
						codigo.indexOf(")") + 1);
				mfunction = fname.trim();
			}
		}
		return mfunction;
	}	
}
