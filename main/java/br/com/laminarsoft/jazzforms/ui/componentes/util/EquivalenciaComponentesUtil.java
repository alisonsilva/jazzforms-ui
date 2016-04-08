package br.com.laminarsoft.jazzforms.ui.componentes.util;

public class EquivalenciaComponentesUtil {

	public static final String TOOLBAR_UI = "CustomToolBar";
	public static final String CAROUSEL_UI = "CustomCarousel";
	public static final String TITLEBAR_UI = "CustomTitleBar";
	public static final String FIELDSET_UI = "CustomFieldset";
	public static final String FORMPANEL_UI = "CustomFormPanel";
	public static final String TABPANEL_UI = "CustomTabpanel";
	public static final String DATAVIEW_UI = "CustomDataview";
	public static final String BUTTON_UI = "CustomButton";
	public static final String DATEPICKER_UI = "CustomDatepicker";
	public static final String DATEPICKER_FORM_UI = "CustomCarousel";
	public static final String TEXTFIELD_UI = "CustomTextfield";
	public static final String TEXTFIELD_FORM_UI = "CustomTextfieldForm";
	public static final String TOGGLE_UI = "CustomToggle";
	public static final String TOGGLE_FORM_UI = "CustomToggleForm";
	public static final String SELECT_UI = "CustomSelect";
	public static final String SELECT_FORM_UI = "CustomSelectForm";
	public static final String CHECKBOX_UI = "CustomCheckbox";
	public static final String CHECKBOX_FORM_UI = "CustomCheckboxForm";
	public static final String CAMERA_UI = "CustomCamera";
	public static final String CAMERA_FORM_UI = "CustomCameraForm";
	public static final String GPS_UI = "CustomGPS";
	public static final String GPS_FORM_UI = "CustomGPSForm";
	public static final String CHART_UI = "CustomChart";
	public static final String NUMBER_UI = "CustomNumberfield";
	public static final String NUMBER_FORM_UI = "CustomNumberfieldForm";
	
	public static String[] getNomeClassesComponent(String componentName) {
		String[] nomesCompUI = new String[2];
		switch (componentName) {
		case "ToolBar" :
			nomesCompUI[0] = TOOLBAR_UI;
			nomesCompUI[1] = "";
			break;
		case "Carousel" :
			nomesCompUI[0] = CAROUSEL_UI;
			nomesCompUI[1] = "";
			break;
		case "TitleBar" :
			nomesCompUI[0] = TITLEBAR_UI;
			nomesCompUI[1] = "";
			break;
		case "FieldSet" :
			nomesCompUI[0] = FIELDSET_UI;
			nomesCompUI[1] = "";
			break;
		case "FormPanel" :
			nomesCompUI[0] = FORMPANEL_UI;
			nomesCompUI[1] = "";
			break;
		case "TabPanel" :
			nomesCompUI[0] = TABPANEL_UI;
			nomesCompUI[1] = "";
			break;
		case "DataView" :
			nomesCompUI[0] = DATAVIEW_UI;
			nomesCompUI[1] = "";
			break;
		case "Button" :
			nomesCompUI[0] = BUTTON_UI;
			nomesCompUI[1] = "";
			break;
		case "DatePicker" :
			nomesCompUI[0] = DATEPICKER_UI;
			nomesCompUI[1] = DATEPICKER_FORM_UI;
			break;
		case "Text" :
			nomesCompUI[0] = TEXTFIELD_UI;
			nomesCompUI[1] = TEXTFIELD_FORM_UI;
			break;
		case "Toggle" :
			nomesCompUI[0] = TOGGLE_UI;
			nomesCompUI[1] = TOGGLE_FORM_UI;
			break;
		case "Select" :
			nomesCompUI[0] = SELECT_UI;
			nomesCompUI[1] = SELECT_FORM_UI;
			break;
		case "CheckBox" :
			nomesCompUI[0] = CHECKBOX_UI;
			nomesCompUI[1] = CHECKBOX_FORM_UI;
			break;
		case "Camera" :
			nomesCompUI[0] = CAMERA_UI;
			nomesCompUI[1] = CAMERA_FORM_UI;
			break;
		case "GPSField" :
			nomesCompUI[0] = GPS_UI;
			nomesCompUI[1] = GPS_FORM_UI;
			break;	
		case "Chart" :
			nomesCompUI[0] = CHART_UI;
			nomesCompUI[1] = "";
			break;
		case "Number" :
			nomesCompUI[0] = NUMBER_UI;
			nomesCompUI[1] = NUMBER_FORM_UI;
			break;
		}
		
		return nomesCompUI;
	}
}
