package br.com.laminarsoft.jazzforms.ui.componentes.util;

public class ComponentesUtil {

	
	/**
	 * Método estático que retorna o nome da função principal. Ela será a primeira função encontrada, ou a função "tagueada" com @MainFunction
	 * @param codigo Todo o código a ser analisado para recuperação da função principal
	 * @return A função principal do código passado
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
