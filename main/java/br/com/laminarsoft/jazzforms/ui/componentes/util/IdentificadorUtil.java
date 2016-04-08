package br.com.laminarsoft.jazzforms.ui.componentes.util;

public class IdentificadorUtil {

	private static int BUTTON = 1;
	private static int CAROUSEL = 1;
	private static int CHECKBOX = 1;
	private static int DATAVIEW = 1;
	private static int DATEPICKER = 1;
	private static int FIELDSET = 1;
	private static int FORMPANEL = 1;
	private static int PAGINA = 1;
	private static int SELECT = 1;
	private static int TABPANEL = 1;
	private static int TEXTFIELD = 1;
	private static int CAMERA = 1;
	private static int TITLEBAR = 1;
	private static int TOGGLE = 1;
	private static int TOOLBAR = 1;
	private static int PANEL = 1;
	private static int HIDDEN = 1;
	private static int GPS = 1;
	private static int CHART = 1;
	
	public static String getNextIdentificadorButton() {
		return "Botao_" + BUTTON++;
	}
	
	public static String getNextIdentificadorCarousel() {
		return "Carousel_" + CAROUSEL++;
	}
	
	public static String getNextIdentificadorCehckbox() {
		return "Checkbox_" + CHECKBOX++;
	}

	public static String getNextIdentificadorDataview() {
		return "Dataview_" + DATAVIEW++;
	}
	
	public static String getNextIdentificadorDatepicker() {
		return "Datepicker_" + DATEPICKER++;
	}
	
	public static String getNextIdentificadorFieldset() {
		return "Fieldset_" + FIELDSET++;
	}
	
	public static String getNextIdentificadorFormpanel() {
		return "Formpanel_" + FORMPANEL++;
	}
	
	public static String getNextIdentificadorPagina() {
		return "Pagina_" + PAGINA++;
	}
	
	public static String getNextIdentificadorSelect() {
		return "Select_" + SELECT++;
	}
	
	public static String getNextIdentificadorTabpanel() {
		return "Tabpanel_" + TABPANEL++;
	}
	
	public static String getNextIdentificadorTextfield() {
		return "Textfield_" + TEXTFIELD++;
	}
	
	public static String getNextIdentificadorTitlebar() {
		return "Titlebar_" + TITLEBAR++;
	}
	
	public static String getNextIdentificadorToggle() {
		return "Toggle_" + TOGGLE++;
	}
	
	public static String getNextIdentificadorToolbar() {
		return "Toolbar_" + TOOLBAR++;
	}
	
	public static String getNextIdentificadorPanel() {
		return "Panel_" + PANEL++;
	}
	
	public static String getNextIdentificadorCamera() {
		return "Camera_" + CAMERA++;
	}
	
	public static String getNextIdentificadorHidden() {
		return "Hidden_"  + HIDDEN++;
	}
	
	public static String getNextIdentificadorGps() {
		return "GPS_" + GPS++;
	}
	
	public static String getNextIdentificadorChart() {
		return "Chart_" + CHART++;
	}
}
