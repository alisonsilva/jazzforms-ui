package br.com.laminarsoft.jazzforms.ui.dialog;

import javafx.scene.layout.StackPane;
import javafx.scene.web.WebView;
import br.com.laminarsoft.jazzforms.ui.communicator.PropertiesServiceController;

public class CSSCodeEditor extends StackPane {
	/** a webview used to encapsulate the CodeMirror JavaScript. */
	final WebView webview = new WebView();

	/**
	 * a snapshot of the code to be edited kept for easy initilization and
	 * reversion of editable code.
	 */
	private String editingCode;

	/**
	 * a template for editing code - this can be changed to any template derived
	 * from the supported modes at http://codemirror.net to allow syntax
	 * highlighted editing of a wide variety of languages.
	 */
/*	private final String editingTemplate = "<!doctype html>"
			+ "<html>"
			+ "<head>"
			+ "  <link rel=\"stylesheet\" href=\"http://codemirror.net/lib/codemirror.css\">"
			+ "  <script src=\"http://codemirror.net/lib/codemirror.js\"></script>"
			+ "  <script src=\"http://codemirror.net/mode/clike/clike.js\"></script>"
			+ "</head>"
			+ "<body>"
			+ "<form><textarea id=\"code\" name=\"code\" >\n"
			+ "${code}"
			+ "</textarea></form>"
			+ "<script>"
			+ "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
			+ "    lineNumbers: true," + "    matchBrackets: true,"
			+ "    mode: \"text/x-java\"" + "  });" + "</script>" + "</body>"
			+ "</html>";
*/

	private final String editingTemplate = "<!doctype html>"
			+ "<html>"
			+ "<head>"
			+ "  <link rel=\"stylesheet\" href=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/codemirror.css\">"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/codemirror.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/clike.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/matchbrackets.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/continuecomment.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/comment.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/javascript.js\"></script>"
			+ "  <script src=\"" + PropertiesServiceController.getInstance().getProperty("url.base") + "/estatico/css.js\"></script>"
			+ "</head>"
			+ "<body>"
			+ "<form><textarea id=\"code\" name=\"code\">\n"
			+ "${code}"
			+ "</textarea></form>"
			+ "<script>"
			+ "  var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {"
			+ "    lineNumbers: false," 
			+ "    matchBrackets: true,"
			+ "    continueComments: \"Enter\","
			+ "    extraKeys: {\"Ctrl-Q\": \"toggleComment\"},"
			+ "    mode: \"text/css\""
			+ "  }); "
			+ "  editor.setSize(1500, 1000);"
			+ "</script>" + "</body>"
			+ "</html>";
	
	
	/**
	 * applies the editing template to the editing code to create the
	 * html+javascript source for a code editor.
	 */
	private String applyEditingTemplate() {
		return editingTemplate.replace("${code}", editingCode);
	}

	/**
	 * sets the current code in the editor and creates an editing snapshot of
	 * the code which can be reverted to.
	 */
	public void setCode(String newCode) {
		this.editingCode = newCode;
		webview.getEngine().loadContent(applyEditingTemplate());
	}

	/**
	 * returns the current code in the editor and updates an editing snapshot of
	 * the code which can be reverted to.
	 */
	public String getCodeAndSnapshot() {
		this.editingCode = (String) webview.getEngine().executeScript(
				"editor.getValue();");
		return editingCode;
	}

	/** revert edits of the code to the last edit snapshot taken. */
	public void revertEdits() {
		setCode(editingCode);
	}

	public WebView getWebView() {
		return webview;
	}	
	
	/**
	 * Create a new code editor.
	 * 
	 * @param editingCode
	 *            the initial code to be edited in the code editor.
	 */
	public CSSCodeEditor(String editingCode) {
		this.editingCode = editingCode;

		webview.getEngine().loadContent(applyEditingTemplate());

		this.getChildren().add(webview);
	}
}
