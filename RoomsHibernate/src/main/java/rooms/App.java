package rooms;

import javax.persistence.*;

import java.util.List;
import java.util.Scanner;

public class App {
    static EntityManagerFactory entityManagerFactory;
    static EntityManager entityManager;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("Rooms");
            entityManager = entityManagerFactory.createEntityManager();
            try {
                while (true) {
                    System.out.println("1 add apartment");
                    System.out.println("2 delete apartment");
                    System.out.println("3 change apartment");
                    System.out.println("4 view apartment");
                    System.out.println("->");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addApartment(sc);
                            break;
                        case "2":
                            deleteApartment(sc);
                            break;
                        case "3":
                            changeApartment(sc);
                            break;
                        case "4":
                            viewApartment(sc);
                            break;
                        default:
                            return;
                    }
                }
            } finally {
                sc.close();
                entityManager.close();
                entityManagerFactory.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    public static void addApartment(Scanner sc) {
        System.out.println("Enter District");
        String district = sc.nextLine();
        System.out.println("Enter Street name");
        String street = sc.nextLine();
        System.out.println("Enter House number");
        String sHouse = sc.nextLine();
        int house = Integer.parseInt(sHouse);
        System.out.println("Enter Apartment number");
        String sApartment = sc.nextLine();
        int apartment = Integer.parseInt(sApartment);
        System.out.println("Enter Number of rooms");
        String sRooms = sc.nextLine();
        int numberOfRooms = Integer.parseInt(sRooms);
        System.out.println("Enter Apartment area");
        String sArea = sc.nextLine();
        double apartmentArea = Double.parseDouble(sArea);
        System.out.println("Enter Price");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);

        entityManager.getTransaction().begin();
        try {
            Rooms rooms = new Rooms(district, street,  house, apartment, numberOfRooms, apartmentArea, price);
            entityManager.persist(rooms);
            entityManager.getTransaction().commit();

            System.out.println(rooms.getId());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }

    }

    private static void deleteApartment(Scanner sc) {
        System.out.print("Enter apartment id:");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);

        Rooms rooms = entityManager.getReference(Rooms.class, id);
        if (rooms == null) {
            System.out.println("Apartment no found");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            entityManager.remove(rooms);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    private static void changeApartment(Scanner sc) {
        System.out.print("Enter id:");
        String sId = sc.nextLine();
        long id = Long.parseLong(sId);


        Query quer = entityManager.createQuery("SELECT x FROM Rooms x WHERE x.id = :id", Rooms.class);
        //List<Rooms> list = (List<Rooms>) quer.getResultList();
        System.out.println("Selected apartments to change");

       // for (Rooms rooms : list)
        //1    System.out.println(rooms);
        System.out.println("------------------------------");

        System.out.println("Enter new District");
        String district = sc.nextLine();
        System.out.println("Enter new Street name");
        String street = sc.nextLine();
        System.out.println("Enter new House number");
        String sHouse = sc.nextLine();
        int house = Integer.parseInt(sHouse);
        System.out.println("Enter new Apartment number");
        String sApartment = sc.nextLine();
        int apartment = Integer.parseInt(sApartment);
        System.out.println("Enter new Number of rooms");
        String sRooms = sc.nextLine();
        int numberOfRooms = Integer.parseInt(sRooms);
        System.out.println("Enter new Apartment area");
        String sArea = sc.nextLine();
        double apartmentArea = Double.parseDouble(sArea);
        System.out.println("Enter new Price");
        String sPrice = sc.nextLine();
        double price = Double.parseDouble(sPrice);

        Rooms rooms = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Rooms x WHERE x.id = :id");
            query.setParameter("id", id);
            rooms = (Rooms) query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Apartment not found");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            rooms.setDistrict(district);
            rooms.setStreet(street);
            rooms.setHouse(house);
            rooms.setApartment(apartment);
            rooms.setNumberOfRooms(numberOfRooms);
            rooms.setApartmentArea(apartmentArea);
            rooms.setPrice(price);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }
    private static void viewApartment (Scanner sc) {
        System.out.println("Search by: ");
        System.out.println("1 District");
        System.out.println("2 Address");
        System.out.println("3 Number of Rooms");
        System.out.println("4 Area");
        System.out.println("5 Price");
        System.out.println("6 View all");
        System.out.println("->");

        String search = sc.nextLine();
        switch (search){
            case "1" ->{
                System.out.print("Enter District: ");
                String district =  sc.nextLine();
                searchApartment("district", district);
            }
            case "2" ->{
                System.out.println("Enter address:");
                System.out.print("Enter street name: ");
                String street = sc.nextLine();
                searchApartment("street",  street);
            }

            case  "3" ->{
                System.out.println("Enter minimum rooms: ");
                String sMinRooms = sc.nextLine();
                double minRooms = Double.parseDouble(sMinRooms);
                System.out.println("Enter maximum rooms: ");
                String sMaxRooms = sc.nextLine();
                double maxRooms = Double.parseDouble(sMaxRooms);
                searchApartment("numberOfRooms", minRooms, maxRooms);

            }
            case  "4" ->{
                System.out.println("Enter minimum apartment area: ");
                String sMinArea = sc.nextLine();
                double minArea = Double.parseDouble(sMinArea);
                System.out.println("Enter maximum apartment area: ");
                String sMaxArea = sc.nextLine();
                double maxArea = Double.parseDouble(sMaxArea);
                searchApartment("area", minArea, maxArea);

            }
            case  "5" ->{
                System.out.println("Enter minimum price: ");
                String sMinPrice = sc.nextLine();
                double minPrice = Double.parseDouble(sMinPrice);
                System.out.println("Enter maximum price: ");
                String sMaxPrice = sc.nextLine();
                double maxPrice = Double.parseDouble(sMaxPrice);
                searchApartment("area", minPrice, maxPrice);

            }
            case "6" -> {
                searchApartment();
            }
        }
    }
    private static void searchApartment(Object... args){
        Query query;
        if(args.length == 0){
            try {
                query = entityManager.createQuery("SELECT r FROM Rooms r", Rooms.class);
                List<Rooms> list = (List<Rooms>) query.getResultList();
                for (Rooms r : list) {
                    System.out.println(r);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        } else if (args.length == 2) {
            try {
                query = entityManager.createQuery("SELECT r FROM Rooms r WHERE r." + args[0].toString() + " = :value", Rooms.class);
                query.setParameter("value", args[1]);
                List<Rooms> list = (List<Rooms>) query.getResultList();
                for (Rooms r : list) {
                    System.out.println(r);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else if (args.length == 3) {
            try {
                query = entityManager.createQuery(
                        "SELECT r FROM Rooms r WHERE r." + args[0].toString() + " BETWEEN :start AND  :end", Rooms.class);
                query.setParameter("start", args[1]);
                query.setParameter("end", args[2]);
                List<Rooms> list = (List<Rooms>) query.getResultList();
                for (Rooms r : list) {
                    System.out.println(r);
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }else {
            System.out.println("False, Try again");
        }
    }

}
