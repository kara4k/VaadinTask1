package com.example;

import com.example.entities.Payment;
import com.vaadin.data.ValidationResult;
import com.vaadin.data.Validator;
import com.vaadin.event.selection.SingleSelectionListener;
import com.vaadin.server.UserError;
import com.vaadin.ui.*;
import com.vaadin.ui.themes.ValoTheme;

import java.util.Locale;

public class PaymentField extends CustomField<Payment> {

    public static final String CARD = "Credit Card";
    public static final String CASH = "Cash";
    public static final String CAPTION = "PaymentMethod";
    public static final String CASH_MESSAGE = "Payment will be made directly in the hotel";

    private VerticalLayout mLayout = new VerticalLayout();
    private RadioButtonGroup<String> mRadioGroup = new RadioButtonGroup<>();
    private TextField mPrePaymentTF = new TextField();
    private Label mCashLabel = new Label(CASH_MESSAGE);

    private Payment mValue = new Payment();
    private Byte lastPrePayment;

    @Override
    protected Component initContent() {
        setupLayout();
        setupListeners();

        return mLayout;
    }

    private void setupLayout() {
        setCaption(CAPTION);

        mLayout.addComponents(mRadioGroup, mPrePaymentTF, mCashLabel);
        mLayout.setWidth(100, Unit.PERCENTAGE);

        mRadioGroup.setItems(CARD, CASH);
        mRadioGroup.setStyleName(ValoTheme.OPTIONGROUP_HORIZONTAL);

        mPrePaymentTF.setPlaceholder("Guaranty Deposit");

        mRadioGroup.setWidth(100, Unit.PERCENTAGE);
        mPrePaymentTF.setWidth(100, Unit.PERCENTAGE);
        mCashLabel.setWidth(100, Unit.PERCENTAGE);

        mPrePaymentTF.setVisible(false);
        mCashLabel.setVisible(false);
    }

    private void setupListeners() {
        mRadioGroup.addSelectionListener((SingleSelectionListener<String>) singleSelectionEvent -> {
            ValidationResult result = validateType();
            showValidation(result);

            if (!result.isError()) {
                String paymentType = singleSelectionEvent.getSelectedItem().get();

                switch (paymentType) {
                    case CARD:
                        mPrePaymentTF.setVisible(true);
                        mCashLabel.setVisible(false);
                        mValue.setType(Payment.TYPE_CARD);
                        break;
                    case CASH:
                        mPrePaymentTF.setVisible(false);
                        mCashLabel.setVisible(true);
                        mValue.setType(Payment.TYPE_CASH);
                        mValue.setPrePayment(null);
                        mPrePaymentTF.setValue("");
                        break;
                }
            }
        });

        mPrePaymentTF.addValueChangeListener((ValueChangeListener<String>) valueChangeEvent -> {
            ValidationResult result = validatePrePayment();
            showValidation(result);

            if (isValidPrePayment()) {
                Byte newPrePayment = Byte.parseByte(mPrePaymentTF.getValue());

                mValue.setPrePayment(newPrePayment);
                showPaymentChange(newPrePayment);
            }
        });
    }

    @Override
    protected boolean isDifferentValue(Payment newValue) {
        return true;
    }

    private void showValidation(ValidationResult result) {
        if (result.isError()) {
            setComponentError(new UserError(result.getErrorMessage()));
        } else {
            setComponentError(null);
        }
    }

    private ValidationResult validateType() {
        if (!mRadioGroup.getSelectedItem().isPresent()) {
            return ValidationResult.error("Please, select payment method");
        }
        return ValidationResult.ok();
    }

    private ValidationResult validatePrePayment() {
        if (mRadioGroup.getSelectedItem().isPresent() && mRadioGroup.getSelectedItem().get().equals(CARD)) {
            if (!isValidPrePayment()) {
                return ValidationResult.error("Prepayment must be in range 0 to 100");
            }
        }
        return ValidationResult.ok();
    }

    @Override
    public Validator<Payment> getDefaultValidator() {
        return (Validator<Payment>) (payment, valueContext) -> {
            ValidationResult typeValidation = validateType();
            if (typeValidation.isError()) {
                return ValidationResult.error(typeValidation.getErrorMessage());
            }

            ValidationResult priceValidation = validatePrePayment();
            if (priceValidation.isError()) {
                return ValidationResult.error(priceValidation.getErrorMessage());
            }

            return ValidationResult.ok();
        };
    }

    private void showPaymentChange(Byte newPrePayment) {
        if (lastPrePayment == null || lastPrePayment.equals(newPrePayment)) return;

        String message = String.format(Locale.ENGLISH, "Prepayment changed from %d%% to %d%%",
                lastPrePayment, newPrePayment);
        Notification.show(message, Notification.Type.HUMANIZED_MESSAGE);
    }

    private boolean isValidPrePayment() {
        try {
            byte val = Byte.parseByte(mPrePaymentTF.getValue());
            if (val < 0 || val > 100) return false;
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    protected void doSetValue(Payment payment) {
        mValue = new Payment(payment);
        updateValues();
    }

    private void updateValues() {
        Byte type = mValue.getType();

        if (type == null) {
            mRadioGroup.setSelectedItem(null);
            mPrePaymentTF.setVisible(false);
            mCashLabel.setVisible(false);
            lastPrePayment = null;
            mPrePaymentTF.setValue("");
        } else {
            String selected = (type == Payment.TYPE_CARD) ? CARD : CASH;
            mRadioGroup.setSelectedItem(selected);

            Byte prePayment = mValue.getPrePayment();
            if (prePayment != null) {
                lastPrePayment = prePayment;
                mPrePaymentTF.setValue(prePayment.toString());
            }
        }
    }

    @Override
    public Payment getValue() {
        return mValue;
    }
}
