package com.example.service;

import com.example.entities.Hotel;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HotelService {

    private static HotelService sHotelService;

    private EntityManager mEntityManager = Persistence.createEntityManagerFactory("hotelsdb")
            .createEntityManager();

    private HotelService() {
    }

    public static HotelService getInstance() {
        if (sHotelService == null) sHotelService = new HotelService();
        return sHotelService;
    }

    public Hotel get(long id) {
        return mEntityManager.find(Hotel.class, id);
    }

    public List<Hotel> getAll() {
        TypedQuery<Hotel> query = mEntityManager.createNamedQuery("Hotel.getAll", Hotel.class);
        return query.getResultList();
    }

    public List<Hotel> getAll(@NotNull String nameFilter, @NotNull String addressFilter) {
        TypedQuery<Hotel> namedQuery = mEntityManager.createNamedQuery("Hotel.filter", Hotel.class);
        namedQuery.setParameter("namefilter", getFilterString(nameFilter));
        namedQuery.setParameter("addressfilter", getFilterString(addressFilter));
        return namedQuery.getResultList();
    }

    private String getFilterString(String text) {
        return "%" + text.toLowerCase() + "%";
    }

    public void save(Hotel hotel) {
        mEntityManager.getTransaction().begin();
        if (hotel.getId() == null) {
            mEntityManager.persist(hotel);
        } else {
            mEntityManager.merge(hotel);
        }
        transCommit();
    }

    public void updateHotels(Iterable<Hotel> iterable) {
        mEntityManager.getTransaction().begin();
        iterable.forEach(hotel -> {
            mEntityManager.merge(hotel);
        });
        transCommit();
    }

    public void delete(Iterable<Hotel> hotels) {
        mEntityManager.getTransaction().begin();
        hotels.forEach(hotel -> {
            mEntityManager.remove(get(hotel.getId()));
        });
        transCommit();
    }

    private void transCommit() {
        try {
            mEntityManager.getTransaction().commit();
        } catch (Exception e) {
            Logger.getLogger(HotelService.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
