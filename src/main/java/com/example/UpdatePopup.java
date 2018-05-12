package com.example;

import com.example.entities.Category;
import com.example.entities.Hotel;
import com.example.service.CategoryService;
import com.vaadin.data.ValidationResult;
import com.vaadin.event.ShortcutAction;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.Set;

public class UpdatePopup extends PopupView {

    private static final String TITLE = "<b>Bulk Update</b>";
    private static final String CHOOSER_EMPTY_ITEM = "Please select field";

    private VerticalLayout mLayout = new VerticalLayout();
    private Label mTitleLabel = new Label(TITLE, ContentMode.HTML);
    private NativeSelect<String> mFieldChooser = new NativeSelect<>();
    private TextField mTextField = new TextField();
    private NativeSelect<Category> mCategoryChooser = new NativeSelect<>();
    private DateField mDateField = new DateField();

    private HorizontalLayout mControlsLayout = new HorizontalLayout();
    private Button mUpdateBtn = new Button("Update");
    private Button mCancelBtn = new Button("Cancel");

    private HotelUI mHotelUI;
    private Set<Hotel> mHotels;

    public UpdatePopup(HotelUI hotelUI) {
        mHotelUI = hotelUI;

        setupLayout();
        setupListeners();
    }

    private void setupLayout() {
        mFieldChooser.setEmptySelectionAllowed(true);
        mFieldChooser.setEmptySelectionCaption(CHOOSER_EMPTY_ITEM);
        mFieldChooser.setItems(Fields.getChooserItems());

        mTextField.setPlaceholder("Field value");
        mCategoryChooser.setEmptySelectionAllowed(false);
        mCategoryChooser.setVisible(false);
        mDateField.setVisible(false);

        mFieldChooser.setWidth(100, Unit.PERCENTAGE);
        mCategoryChooser.setWidth(100, Unit.PERCENTAGE);
        mUpdateBtn.setWidth(100, Unit.PERCENTAGE);
        mCancelBtn.setWidth(100, Unit.PERCENTAGE);
        mUpdateBtn.setClickShortcut(ShortcutAction.KeyCode.ENTER);

        mControlsLayout.addComponents(mUpdateBtn, mCancelBtn);

        mLayout.addComponents(mTitleLabel, mFieldChooser, mTextField, mCategoryChooser, mDateField, mControlsLayout);
        setContent(createContent(null, mLayout));
        setHideOnMouseOut(false);
    }

    private void setupListeners() {
        mFieldChooser.addSelectionListener((SingleSelectionListener<String>) singleSelectionEvent -> {
            boolean isPresent = singleSelectionEvent.getSelectedItem().isPresent();

            if (isPresent) {
                String itemName = singleSelectionEvent.getSelectedItem().get();
                switch (itemName) {
                    case Fields.CATEGORY:
                        mTextField.setVisible(false);
                        mDateField.setVisible(false);
                        mCategoryChooser.setVisible(true);
                        break;
                    case Fields.OPERATES_FROM:
                        mTextField.setVisible(false);
                        mCategoryChooser.setVisible(false);
                        mDateField.setValue(null);
                        mDateField.setVisible(true);
                        break;
                    default:
                        mTextField.setValue("");
                        mTextField.setVisible(true);
                        mDateField.setVisible(false);
                        mCategoryChooser.setVisible(false);
                        break;
                }
            }
        });

        mCancelBtn.addClickListener(e -> {
            setPopupVisible(false);
        });

        mUpdateBtn.addClickListener(e -> update());
    }

    public void show(Set<Hotel> hotelList) {
        mHotels = hotelList;
        mFieldChooser.setSelectedItem(CHOOSER_EMPTY_ITEM);
        mTextField.setValue("");
        mDateField.setValue(null);
        mCategoryChooser.setItems(CategoryService.getInstance().getAll());
        setPopupVisible(true);
    }

    private void update() {
        boolean isPresent = mFieldChooser.getSelectedItem().isPresent();

        if (isPresent) {
            String fieldName = mFieldChooser.getSelectedItem().get();

            switch (fieldName) {
                case CHOOSER_EMPTY_ITEM:
                    showError("Please, select field first!");
                    break;
                case Fields.CATEGORY:
                    if (mCategoryChooser.getSelectedItem().isPresent()) {
                        Category category = mCategoryChooser.getSelectedItem().get();
                        applyChanges(fieldName.toLowerCase(), category);
                    } else {
                        showError("Please, choose a category");
                    }
                    break;
                case Fields.OPERATES_FROM:
                    LocalDate date = mDateField.getValue();
                    if (date == null) {
                        showError("Please, select date!");
                        return;
                    }

                    ValidationResult operatesValidation = HotelFormBinder.validateDate(date, null);
                    boolean operatesIsValid = isValid(operatesValidation);
                    if (operatesIsValid) {
                        long timeStamp = HotelFormBinder.getTimeStamp(mDateField.getValue());
                        applyChanges(Fields.OPERATES_FIELD_NAME, timeStamp);
                    }
                    break;
                case Fields.RATING:
                    ValidationResult ratingValidation = HotelFormBinder.validateRating(mTextField.getValue(), null);
                    boolean ratingIsValid = isValid(ratingValidation);

                    if (ratingIsValid) applyChanges(fieldName.toLowerCase(), Integer.parseInt(mTextField.getValue()));
                    break;
                default:
                    if (!fieldName.equals(Fields.DESCRIPTION)) {
                        if (mTextField.getValue() == null || mTextField.getValue().equals("")) {
                            showError("Value can't be emty!");
                            return;
                        }
                    }
                    applyChanges(fieldName.toLowerCase(), mTextField.getValue());
            }
        } else {
            showError("Please, select field first!");
        }
    }

    private boolean isValid(ValidationResult operatesValidation) {
        if (operatesValidation.isError()) {
            showError(operatesValidation.getErrorMessage());
            return false;
        }
        return true;
    }

    //reflection?)
    private void applyChanges(String fieldName, Object value) {
        Class hotelClass = Hotel.class;

        mHotels.forEach(hotel -> {
            try {
                Field field = hotelClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                field.set(hotel, value);
            } catch (NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
            setPopupVisible(false);
            mHotelUI.onUpdateHotels(mHotels);
        });
    }

    private void showError(String message) {
        Notification.show(message, Notification.Type.ERROR_MESSAGE);
    }

    private static class Fields {

        private static final String NAME = "Name";
        private static final String ADDRESS = "Address";
        private static final String RATING = "Rating";
        private static final String OPERATES_FROM = "Operates From";
        private static final String CATEGORY = "Category";
        private static final String DESCRIPTION = "Description";
        private static final String URL = "Url";
        private static final String[] CHOOSER_ITEMS = new String[]{NAME, ADDRESS, RATING,
                OPERATES_FROM, CATEGORY, DESCRIPTION, URL};

        private static final String OPERATES_FIELD_NAME = "operatesFrom";

        private static String[] getChooserItems() {
            return CHOOSER_ITEMS;
        }
    }


}
