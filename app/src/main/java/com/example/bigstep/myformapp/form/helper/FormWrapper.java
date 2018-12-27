/*
 *   Copyright (c) 2016 BigStep Technologies Private Limited.
 *
 *   You may not use this file except in compliance with the
 *   SocialEngineAddOns License Agreement.
 *   You may obtain a copy of the License at:
 *   https://www.socialengineaddons.com/android-app-license
 *   The full copyright and license information is also mentioned
 *   in the LICENSE file that was distributed with this
 *   source code.
 *
 */

package com.example.bigstep.myformapp.form.helper;

import android.content.Context;
import android.os.Build;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;

import com.example.bigstep.myformapp.R;
import com.example.bigstep.myformapp.form.fields.CheckBox;
import com.example.bigstep.myformapp.form.fields.DateTime;
import com.example.bigstep.myformapp.form.fields.FileElement;
import com.example.bigstep.myformapp.form.fields.Heading;
import com.example.bigstep.myformapp.form.fields.MultiSelect;
import com.example.bigstep.myformapp.form.fields.Select;
import com.example.bigstep.myformapp.form.fields.SwitchElement;
import com.example.bigstep.myformapp.form.fields.TextField;
import com.example.bigstep.myformapp.ui.WidgetLayoutParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * @FormWrapper is the main/parent class of the form by which we can render any kind of form
 * based on applied schema. For more flexibility we can extends/sub-class & override
 * and do whatever we want.
 */

public class FormWrapper implements View.OnClickListener {

    private static final String SCHEMA_KEY_TYPE = "type";
    private static final String SCHEMA_KEY_CHECKBOX = "checkbox";
    public static final String SCHEMA_KEY_SELECT_UPPER = "Select";
    public static final String SCHEMA_KEY_SELECT = "select";
    private static final String SCHEMA_KEY_TEXT = "text";
    private static final String SCHEMA_KEY_PASSWORD = "password";
    private static final String SCHEMA_KEY_LOCATION = "location";
    private static final String SCHEMA_KEY_TEXT_AREA = "textarea";
    private static final String SCHEMA_KEY_OPTIONS = "multiOptions";
    public static final String SCHEMA_KEY_HINT = "hint";
    private static final String SCHEMA_KEY_FILE = "file";
    private static final String SCHEMA_KEY_RADIO = "Radio";
    private static final String SCHEMA_KEY_MULTI_CHECKBOX = "multicheckbox";
    private static final String SCHEMA_KEY_MULTI_SELECT = "multiselect";
    private static final String SCHEMA_KEY_DATE = "Date";
    private static final String SCHEMA_KEY_DATE_LOWER = "date";
    private static final String SCHEMA_KEY_HIDDEN = "Hidden";
    private static final String SCHEMA_KEY_DUMMY = "dummy";
    private static final String SCHEMA_KEY_SWITCH_ELEMENT = "switch";
    public static final LayoutParams defaultLayoutParams = WidgetLayoutParams.getFullWidthLayoutParams();
    private static final LayoutParams defaultLayoutParamsWithMargins = WidgetLayoutParams.getFullWidthLayoutParams();


    public static ArrayList<AbstractWidget> _widgets;
    public static Map<String, AbstractWidget> widgetMap;
    public static JSONObject formSchema;
    public static LinearLayout _layout;
    // Member variables.
    public Context mContext;
    protected Map<String, AbstractWidget> _map;
    protected LinearLayout _container;
    protected ScrollView _viewport;
    public static int mLayoutType = 1;

    public FormWrapper(Context context) {
        mContext = context;
    }

    /**
     * Method to extract fields attribute value by their name
     *
     * @param propertyName
     * @param attributeName
     * @param isFieldsKey
     * @return
     */
    public static String getAttribByProperty(String propertyName, String attributeName, String isFieldsKey) {
        if (isFieldsKey != null) {
            JSONArray formArray = formSchema.optJSONObject("fields").optJSONArray(isFieldsKey);
            for (int i = 0; i < formArray.length(); i++) {
                if (formArray.optJSONObject(i).opt("name").equals(propertyName)) {
                    return formArray.optJSONObject(i).optString(attributeName);
                }
            }
        } else {
            JSONArray formArray = formSchema.optJSONArray("form");
            for (int i = 0; i < formArray.length(); i++) {
                if (formArray.optJSONObject(i).opt("name").equals(propertyName)) {
                    return formArray.optJSONObject(i).optString(attributeName);
                }
            }
        }
        return null;
    }

    /**
     * Getter of form schema
     * @return
     */
    public static JSONObject getFormSchema() {
        return formSchema;
    }

    /**
     * Setter of form schema
     * @param form
     */
    public static void setFormSchema(JSONObject form) {
        formSchema = form;
    }


    /**
     * Updating given field's attribute to the field schema
     * @param propertyName
     * @param attributeName
     * @param attributeValue
     * @return
     */
    public static boolean setAttribByProperty(String propertyName, String attributeName, String attributeValue) {
        JSONArray formArray = formSchema.optJSONArray("form");
        for (int i = 0; i < formArray.length(); i++) {
            if (formArray.optJSONObject(i).opt("name").equals(propertyName)) {
                try {
                    formArray.optJSONObject(i).put(attributeName, attributeValue);
                    return true;
                } catch (JSONException e) {
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Setting registry by field name in the form schema
     *
     * @param propertyName
     * @param parent
     * @param child
     * @param elementType
     * @param order
     * @return
     */
    public static boolean setRegistryByProperty(String propertyName, JSONObject parent, String child, String elementType, int order) {
        try {
            JSONObject element = new JSONObject();
            String parentKey = (parent != null) ? parent.optString("parent") : null;
            if (child != null && !child.trim().equals("")) {
                element.put("parent", parentKey);
                if (elementType != null && !elementType.isEmpty() && elementType.equals("MultiCheckbox")) {
                    JSONObject temp = formSchema.optJSONObject(propertyName);
                    JSONObject multiChild = (temp != null && temp.optJSONObject("multiChild") != null) ? temp.optJSONObject("multiChild") : new JSONObject();
                    multiChild = FormWrapper.updateMultiChild(multiChild, 1, 0);
                    multiChild.put(child, order);
                    element.put("multiChild", multiChild);
                } else {
                    element.put("child", child);
                }
                formSchema.putOpt(propertyName, element);

                JSONArray subFormElements = formSchema.optJSONObject("fields").optJSONArray(child);
                if (subFormElements != null) {
                    for (int i = 0; i < subFormElements.length(); i++) {
                        if (subFormElements.optJSONObject(i).optBoolean("hasSubForm")) {
                            JSONObject childElement = new JSONObject();
                            childElement.put("parent", propertyName);
                            //setRegistryByProperty(subFormElements.optJSONObject(i).optString("name"),childElement,null);
                            formSchema.putOpt(subFormElements.optJSONObject(i).optString("name"), childElement);
                        }
                    }
                }

            } else if (parentKey != null) {
                element.put("parent", parentKey);
                element.put("child", child);
                formSchema.putOpt(propertyName, element);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Getting registry by field name
     *
     * @param propertyName
     * @param key
     * @return
     */
    public static String getRegistryByProperty(String propertyName, String key) {
        if (getFormSchema().optJSONObject(propertyName) != null) {
            return getFormSchema().optJSONObject(propertyName).optString(key);
        }
        return null;
    }


    public static JSONObject getRegistryByProperty(String propertyName, String key, String type) {
        if (getFormSchema().optJSONObject(propertyName) != null) {
            return getFormSchema().optJSONObject(propertyName).optJSONObject(key);
        }
        return null;
    }

    /**
     * Setter for multiChild schema for the given field name.
     * @param propertyName
     * @param key
     * @param multiChild
     */
    public static void setMultiChild(String propertyName, String key, JSONObject multiChild) {
        if (getFormSchema().optJSONObject(propertyName) != null) {
            try {
                getFormSchema().optJSONObject(propertyName).put("multiChild", multiChild);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * Method is used to update the schema of multiOption of @MultiSelect widget.
     * @param multiChild
     * @param value
     * @param elementOrder
     * @return
     */
    public static JSONObject updateMultiChild(JSONObject multiChild, int value, int elementOrder) {
        Iterator<String> keys = multiChild.keys();
        while (keys.hasNext()) {
            String key = keys.next();
            int keyOrder = multiChild.optInt(key);
            try {
                if (value < 0 && keyOrder > elementOrder || value > 0) {
                    multiChild.put(key, keyOrder + value);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return multiChild;
    }

    /**
     * parses a supplied schema of raw json data and creates widgets
     *
     * @param data - the raw json data as a String
     */
    public View getFormWrapper(Context context, JSONObject data) {

        int margin = (int) (mContext.getResources().getDimension(R.dimen.dimen_5dp) /
                mContext.getResources().getDisplayMetrics().density);
        defaultLayoutParamsWithMargins.setMargins(margin, margin, margin, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            defaultLayoutParamsWithMargins.setMarginEnd(margin);
            defaultLayoutParamsWithMargins.setMarginStart(margin);
        }
        _widgets = new ArrayList<>();
        widgetMap = new HashMap<>();
        _map = new LinkedHashMap<>();


        JSONArray formArray;
        formArray = data.optJSONArray("form");
        if (formArray != null) {
            setFormWidgets(context, formArray);
        }


        // -- create the layout

        _container = new LinearLayout(context);
        _container.setOrientation(LinearLayout.VERTICAL);
        _container.setLayoutParams(FormWrapper.defaultLayoutParams);

        _viewport = new ScrollView(context);
        _viewport.setLayoutParams(FormWrapper.defaultLayoutParams);

        _layout = new LinearLayout(context);
        _layout.setOrientation(LinearLayout.VERTICAL);
        _layout.setLayoutParams(FormWrapper.defaultLayoutParams);


        for (Map.Entry<String, AbstractWidget> entry : _map.entrySet()) {
            _layout.addView(entry.getValue().getView());
        }
        _viewport.addView(_layout);
        _container.addView(_viewport);
        _container.setId(R.id.form_layout);

        return _container;
    }

    @Override
    public void onClick(View view) {

    }

    /**
     * This method used to generate element's widget and add them into the form layout.
     *
     * @param context
     * @param jsonArray
     */
    public void setFormWidgets(Context context, JSONArray jsonArray) {
        AbstractWidget widget;
        for (int j = 0; j < jsonArray.length(); j++) {

            JSONObject jsonObject = jsonArray.optJSONObject(j);

            String fieldName = jsonObject.optString("name");
            String fieldLabel = jsonObject.optString("label");
            String fieldDescription = jsonObject.optString("description");

            boolean required = jsonObject.optBoolean("required");


            widget = getWidget(context, fieldName, jsonObject, fieldLabel, required,
                    fieldDescription, _widgets, _map);

            if (widget == null) continue;

            if (jsonObject.has(FormWrapper.SCHEMA_KEY_HINT))
                widget.setHint(jsonObject.optString(FormWrapper.SCHEMA_KEY_HINT));

            _widgets.add(widget);
            _map.put(fieldName, widget);

        }

    }

    /**
     * this method preps the data and saves it
     * if there is a problem w/ creating the json string, the method fails
     * loop through each mFormWidget and set a property on a json object to the value of the mFormWidget's getValue() method
     */
    public HashMap<String, String> save() {
        AbstractWidget widget;
        HashMap<String, String> params = new HashMap<>();

        boolean success = true;
        if (_widgets != null) {
            for (int i = 0; i < _widgets.size(); i++) {
                widget = _widgets.get(i);

                if (!widget.getPropertyName().equals("searchGuests")
                        && !widget.getPropertyName().equals("toValues")) {
                    if (!widget.getPropertyName().equals("text")) {
                        if (widget.isRequired() && widget.getValue().isEmpty() &&
                                widget.getView().getVisibility() == View.VISIBLE) {

                            success = false;
                            widget.setErrorMessage(mContext.getResources().getString(R.string.widget_error_msg));

                        } else {
                            params.put(widget.getPropertyName(), widget.getValue());
                        }
                    }

                }
            }
        }

        if (success) {
            return params;
        }

        return null;
    }

    /**
     * Method is used to validate the form at application level.
     *
     * @param validationMessages
     * @return
     */
    public boolean validateForm(JSONObject validationMessages) {

        AbstractWidget widget;
        boolean error = false;
        for (int i = 0; i < _widgets.size(); i++) {
            widget = _widgets.get(i);
            String fieldName = widget.getPropertyName();

            if (validationMessages.has(fieldName)) {

                try {
                    String message = validationMessages.getString(fieldName);
                    widget.setErrorMessage(message);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                error = true;
            }
        }
        return error;
    }


    /**
     * factory method for actually instantiating widgets
     */

    public AbstractWidget getWidget(Context context, String name, JSONObject property, String label, boolean hasValidator, String description, ArrayList<AbstractWidget> _widgets,
                                    Map<String, AbstractWidget> _map) {
        JSONArray jsonArray;
        JSONObject jsonObject;
        String value;
        if (property != null) {
            String type = property.optString(FormWrapper.SCHEMA_KEY_TYPE);
            type = type.toLowerCase();
            switch (type) {

                case FormWrapper.SCHEMA_KEY_PASSWORD:
                case FormWrapper.SCHEMA_KEY_TEXT:
                case FormWrapper.SCHEMA_KEY_LOCATION:
                    value = property.optString("value");
                    return new TextField(context, name, property, description, hasValidator, type,
                            property.optString("inputType"), _widgets, _map,
                            false, value);


                case FormWrapper.SCHEMA_KEY_CHECKBOX:
                    return new CheckBox(context, property, name, label, hasValidator, property.optInt("value"), _widgets, _map);
                case FormWrapper.SCHEMA_KEY_SWITCH_ELEMENT:
                    return new SwitchElement(context, property.optString("name"), hasValidator, property, _widgets, _map);

                case FormWrapper.SCHEMA_KEY_FILE:
                    return new FileElement(context, name, property);

                case FormWrapper.SCHEMA_KEY_RADIO:
                    jsonObject = property.optJSONObject(FormWrapper.SCHEMA_KEY_OPTIONS);
                    if (jsonObject == null) {
                        jsonArray = property.optJSONArray(FormWrapper.SCHEMA_KEY_OPTIONS);
                        jsonObject = convertToJsonObject(jsonArray);
                    }
                    if (jsonObject != null && jsonObject.length() != 0) {
                        return new Select(context, name, jsonObject, label, hasValidator,
                                description, property);
                    }

                case FormWrapper.SCHEMA_KEY_MULTI_CHECKBOX:
                case FormWrapper.SCHEMA_KEY_MULTI_SELECT:

                    jsonObject = property.optJSONObject(FormWrapper.SCHEMA_KEY_OPTIONS);
                    if (jsonObject == null) {
                        jsonArray = property.optJSONArray(FormWrapper.SCHEMA_KEY_OPTIONS);
                        jsonObject = convertToJsonObject(jsonArray);
                    }
                    if (jsonObject != null && jsonObject.length() != 0) {
                        MultiSelect formMultiCheckBox = new MultiSelect(context, name, jsonObject, label, hasValidator, description, _widgets,
                                _map);
                        formMultiCheckBox.setValue(property.optString("value"));
                        return formMultiCheckBox;
                    }

                case FormWrapper.SCHEMA_KEY_SELECT:
                    if (property.has(FormWrapper.SCHEMA_KEY_OPTIONS)) {
                        jsonObject = property.optJSONObject(FormWrapper.SCHEMA_KEY_OPTIONS);
                        if (jsonObject == null) {
                            jsonArray = property.optJSONArray(FormWrapper.SCHEMA_KEY_OPTIONS);
                            jsonObject = convertToJsonObject(jsonArray);
                        }
                        if (jsonObject != null && jsonObject.length() != 0) {
                            return new Select(context, name, jsonObject, label, hasValidator, description,
                                    property, _widgets, _map, false);
                        }
                    }
                    break;

                case FormWrapper.SCHEMA_KEY_DATE:
                case FormWrapper.SCHEMA_KEY_DATE_LOWER:
                    return new DateTime(context, name, property);

                case FormWrapper.SCHEMA_KEY_HIDDEN:
                    return new CheckBox(context, property, name, label, hasValidator, property.optInt("value"), _widgets, _map);

                case FormWrapper.SCHEMA_KEY_TEXT_AREA:

                    value = property.optString("value");
                    return new TextField(context, name, property, description, hasValidator, type,
                            property.optString("inputType"), _widgets, _map, false, value);
                case FormWrapper.SCHEMA_KEY_DUMMY:
                    return new Heading(context, name, label, property);

            }
        } else {
            return new Heading(context, name, label, null);
        }
        return null;
    }

    /**
     * Method to convert jsonArray in JsonObject
     *
     * @param jsonArray JsonArray to convert in JsonObject
     * @return Converted JsonObject
     */
    public JSONObject convertToJsonObject(JSONArray jsonArray) {
        JSONObject jsonObject = new JSONObject();
        if (jsonArray != null) {
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    String value = jsonArray.optString(i);
                    jsonObject.put("" + i, value);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return jsonObject;
        } else {
            return null;
        }
    }

    /**
     * Setter for layout type
     * @param type
     */
    public void setLayoutType(int type) {
        mLayoutType = type;
    }

    /**
     * Getter for layout type
     * @return
     */
    public static int getLayoutType() {
        return mLayoutType;
    }
}
