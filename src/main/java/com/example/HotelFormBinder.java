package com.example;

import com.example.entities.Hotel;
import com.vaadin.data.*;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Calendar;

public class HotelFormBinder {

    public static LocalDate getDate(Hotel hotel) {
        if (hotel.getOperatesFrom() == null) return null;

        return Instant.ofEpochMilli(hotel.getOperatesFrom())
                .atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public void bindForm(Binder<Hotel> binder, HotelEditForm form) {
        binder.forField(form.name).asRequired("Please, enter a name!")
                .bind(Hotel::getName, Hotel::setName);

        binder.forField(form.address).asRequired("Please, enter a address!")
                .bind(Hotel::getAddress, Hotel::setAddress);

        binder.forField(form.rating).asRequired("Please, enter a rating!")
                .withValidator(HotelFormBinder::validateRating)
                .bind(this::getRating, this::setRating);

        binder.forField(form.operatesFrom).asRequired("Please, choose a date!")
                .withValidator(HotelFormBinder::validateDate)
                .bind(HotelFormBinder::getDate, this::setDate);

        binder.forField(form.category).asRequired("Please, choose a category!")
                .withNullRepresentation(null)
                .bind(Hotel::getCategory, Hotel::setCategory);

        binder.forField(form.description).bind(Hotel::getDescription, Hotel::setDescription);
        binder.forField(form.url).asRequired("Please, enter a URL!").bind(Hotel::getUrl, Hotel::setUrl);

        binder.forField(form.paymentField).asRequired("Please, choose a payment method")
                .withValidator(form.paymentField.getDefaultValidator())
                .bind(Hotel::getPayment, Hotel::setPayment);

        form.name.setDescription("Hotel name");
        form.address.setDescription("Hotel address");
        form.rating.setDescription("Hotel rating");
        form.operatesFrom.setDescription("Hotel operates from");
        form.category.setDescription("Hotel category");
        form.description.setDescription("Hotel description");
        form.url.setDescription("Hotel URL");
    }

    public static ValidationResult validateRating(String s, ValueContext valueContext) {
        try {
            int rating = Integer.parseInt(s);

            if (rating > -1 && rating < 6) {
                return ValidationResult.ok();
            }
            return ValidationResult.error("Rating should be in range 0 to 5");
        } catch (Exception e) {
            return ValidationResult.error("Rating should be a integer number in range 0 to 5");
        }
    }

    public static ValidationResult validateDate(LocalDate localDate, ValueContext valueContext) {
        if (localDate.isBefore(LocalDate.now())) {
            return ValidationResult.ok();
        }
        return ValidationResult.error("Date should be in past time");
    }

    public String getRating(Hotel hotel) {
        if (hotel.getRating() == null) return "";

        return String.valueOf(hotel.getRating());
    }

    public void setRating(Hotel hotel, String s) {
        hotel.setRating(Integer.parseInt(s));
    }

    public void setDate(Hotel hotel, LocalDate localDate) {
        hotel.setOperatesFrom(getTimeStamp(localDate));
    }

    public static long getTimeStamp(LocalDate localDate) {
        Calendar calendar = Calendar.getInstance();
        int year = localDate.getYear();
        int month = localDate.getMonthValue() - 1;
        int day = localDate.getDayOfMonth();

        calendar.set(year, month, day, 0, 0);
        return calendar.getTimeInMillis();
    }
}
