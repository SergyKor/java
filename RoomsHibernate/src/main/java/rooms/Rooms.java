package rooms;

import javax.persistence.*;

import java.util.Objects;
@Entity
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;
    private int apartment;
    private int house;
    private String street;
    private String district;
    @Column(name = "number_of_rooms")
    private int numberOfRooms;
    @Column(name = "apartment_area") private double apartmentArea;
    private double price;

    public Rooms(String district, String street,  int house, int apartment,  int numberOfRooms, double apartmentArea, double price) {
        this.apartment = apartment;
        this.house = house;
        this.street = street;
        this.district = district;
        this.numberOfRooms = numberOfRooms;
        this.apartmentArea = apartmentArea;
        this.price = price;
    }

    public Rooms() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getApartment() {
        return apartment;
    }

    public void setApartment(int apartment) {
        this.apartment = apartment;
    }

    public int getHouse() {
        return house;
    }

    public void setHouse(int house) {
        this.house = house;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public int getNumberOfRooms() {
        return numberOfRooms;
    }

    public void setNumberOfRooms(int numberOfRooms) {
        this.numberOfRooms = numberOfRooms;
    }

    public double getApartmentArea() {
        return apartmentArea;
    }

    public void setApartmentArea(double apartmentArea) {
        this.apartmentArea = apartmentArea;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rooms rooms = (Rooms) o;
        return apartment == rooms.apartment && house == rooms.house && numberOfRooms == rooms.numberOfRooms && Double.compare(rooms.apartmentArea, apartmentArea) == 0 && Double.compare(rooms.price, price) == 0 && street.equals(rooms.street) && district.equals(rooms.district);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, apartment, house, street, district, numberOfRooms, apartmentArea, price);
    }

    @Override
    public String toString() {
        return "Rooms{" +
                "id=" + id +
                ", apartment=" + apartment +
                ", house=" + house +
                ", street='" + street + '\'' +
                ", district='" + district + '\'' +
                ", numberOfRooms=" + numberOfRooms +
                ", apartmentArea=" + apartmentArea +
                ", price=" + price +
                '}';
    }
}

