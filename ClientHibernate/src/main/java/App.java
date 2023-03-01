import javax.persistence.*;
import java.util.List;
import java.util.Scanner;

public class App {
    static EntityManagerFactory entityManagerFactory;
    static EntityManager entityManager;

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        try {
            entityManagerFactory = Persistence.createEntityManagerFactory("Shop");
            entityManager = entityManagerFactory.createEntityManager();
            try {
                while (true) {
                    System.out.println("1 client mod");
                    System.out.println("2 manager mod");

                    System.out.println("->");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1"->{
                            System.out.println("1 Enter contact details");
                            System.out.println("2 Change information");
                            System.out.println("3 Make an order");
                            System.out.println("4 My order");
                            System.out.println("->");

                            String choice = sc.nextLine();
                            switch (choice){
                                case "1":
                                    addClient(sc);
                                    break;
                                case "2":
                                    changeClient(sc);
                                    break;
                                case "3":
                                    saveClientsWithGoods(sc);
                                    break;
                                case "4":
                                    findOrderByClientID(sc);
                                 default:
                                    return;
                            }
                        }

                        case "2"->{
                            System.out.println("1 Add product: ");
                            System.out.println("2 Change product: ");
                            System.out.println("3 Delete product: ");
                            System.out.println("4 View product: ");
                            System.out.println("5 Delete client: ");
                            System.out.println("6 View clients: ");
                            System.out.println("7 View orders");
                            System.out.println("->");

                            String choice = sc.nextLine();
                            switch (choice){
                                case "1"-> {
                                    addProduct(sc);
                                }
                                case "2"-> {
                                    changeProduct(sc);
                                }
                                case "3"-> {
                                    deleteProduct(sc);
                                }
                                case "4"-> {
                                    viewProducts(sc);
                                }
                                case "5"-> {
                                    deleteClient(sc);
                                }
                                case "6"-> {
                                    viewClients(sc);
                                }
                                case "7"-> {
                                    viewOrders(sc);
                                }
                                default -> {
                                    return;
                                }


                            }
                        }

                        default ->{return;}

                    }
                }
            } finally {
                sc.close();
                entityManager.close();
                entityManagerFactory.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    public static void addClient(Scanner sc) {
        System.out.println("Enter your name");
        String name = sc.nextLine();
        System.out.println("Enter your age");
        Integer age = Integer.valueOf(sc.nextLine());
        System.out.println("Enter your E-mail");
        String eMail = sc.nextLine();
        System.out.println("Enter your phone number");
        String sPhoneNumber = sc.nextLine();
        Integer phoneNumber = Integer.parseInt(sPhoneNumber);


        entityManager.getTransaction().begin();
        try {
            Clients client = new Clients(name, age, eMail, phoneNumber);
            entityManager.persist(client);
            entityManager.getTransaction().commit();
            Long id = client.getId();
            System.out.println(client.getId());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }

    }

    public static void addProduct(Scanner sc) {
        System.out.println("Enter product name");
        String name = sc.nextLine();
        System.out.println("Enter product price");
        String sPrice = sc.nextLine();
        Double price = Double.parseDouble(sPrice);


        entityManager.getTransaction().begin();
        try {
            Goods goods = new Goods(name, price);
            entityManager.persist(goods);
            entityManager.getTransaction().commit();

            System.out.println(goods.getId());
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }

    }
    private static void viewProducts(Scanner sc){
        System.out.println("1 Find product");
        System.out.println("2 View All products");
        System.out.println("->");

        String choice = sc.nextLine();
        switch (choice){
            case "1"-> {
                findProduct(sc);
            }
            case "2"-> {
                viewAllProducts();
            }

        }
    }

    private static void viewAllProducts() {
        Query query = entityManager.createQuery("SELECT c FROM Goods c", Goods.class);
        List<Goods> list = (List<Goods>) query.getResultList();
        for (Goods g : list) {
            System.out.println(g);
        }
    }
    private static void viewClients(Scanner sc){
        System.out.println("1 find Client");
        System.out.println("2 View All clients");
        System.out.println("->");

        String choice = sc.nextLine();
        switch (choice){
            case "1":
                findClient(sc);
                break;


            case "2":
                viewAllClients();
                break;

            default:
                return;

        }
    }

    private static void viewAllClients() {
        Query query = entityManager.createQuery("SELECT c FROM Clients c", Clients.class);
        List<Clients> list = (List<Clients>) query.getResultList();

        for (Clients c : list) {
            System.out.println(c);
        }
    }

    private static void deleteClient(Scanner sc) {
        System.out.print("Enter client id: ");
        String sId = sc.nextLine();
        Long id = Long.parseLong(sId);

        Clients c = entityManager.getReference(Clients.class, id);
        if (c == null) {
            System.out.println("Client not found!");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            entityManager.remove(c);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
        }
    }

    private static void deleteProduct(Scanner sc) {
        System.out.print("Enter product id: ");
        Long id = Long.valueOf(sc.nextLine());

        Goods g = entityManager.getReference(Goods.class, id);
        if (g == null) {
            System.out.println("Product not found!");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            entityManager.remove(g);
            entityManager.getTransaction().commit();
        } catch (Exception ex) {
            entityManager.getTransaction().rollback();
        }
    }

    private static void changeClient(Scanner sc) {
        System.out.print("Enter id:");
        Long id = Long.valueOf(sc.nextLine());


        Query quer = entityManager.createQuery("SELECT x FROM Clients x WHERE x.id = :id", Clients.class);

        System.out.println("Enter new age");
        Integer age = Integer.valueOf(sc.nextLine());
        System.out.println("Enter new E-Mail");
        String eMail = sc.nextLine();
        System.out.println("Enter new phone number");
        Integer phone = Integer.valueOf(sc.nextLine());


        Clients clients = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Clients x WHERE x.id = :id");
            query.setParameter("id", id);
            clients = (Clients) query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Client not found");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            clients.setAge(age);
            clients.seteMail(eMail);
            clients.setPhoneNumber(phone);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }

    private static void changeProduct(Scanner sc) {
        System.out.print("Enter id:");
        Long id = Long.valueOf(sc.nextLine());


        Query quer = entityManager.createQuery("SELECT x FROM Goods x WHERE x.id = :id", Goods.class);

        System.out.println("Enter new price");
        Double price = Double.valueOf(sc.nextLine());


        Goods goods = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Goods x WHERE x.id = :id");
            query.setParameter("id", id);
            goods = (Goods) query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Product not found");
            return;
        }

        entityManager.getTransaction().begin();
        try {
            goods.setPrice(price);
            entityManager.getTransaction().commit();
        } catch (Exception e) {
            entityManager.getTransaction().rollback();
        }
    }
    public static void findProduct(Scanner sc){
        System.out.println("Enter product ID:");
        Long id = Long.valueOf(sc.nextLine());

        Goods goods = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Goods x WHERE x.id = :id");
            query.setParameter("id", id);
            goods = (Goods) query.getSingleResult();
            System.out.println("Product");
            System.out.println("_____________________________");
            System.out.println(goods);
            System.out.println("_____________________________");
        } catch (NoResultException e) {
            System.out.println("Product not found");
        }
    }
    public static void findClient(Scanner sc){
        System.out.println("Enter client ID:");
        Long id = Long.valueOf(sc.nextLine());

        Clients clients = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Clients x WHERE x.id = :id");
            query.setParameter("id", id);
            clients = (Clients) query.getSingleResult();
            System.out.println("Client");
            System.out.println("_____________________________");
            System.out.println(clients);
            System.out.println("_____________________________");
        } catch (NoResultException e) {
            System.out.println("Client not found");
        }
    }

    public static void saveClientsWithGoods(Scanner sc) {

        System.out.println("Enter your id");
        Long id = Long.valueOf(sc.nextLine());

        Clients clients = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Clients x WHERE x.id = :id");
            query.setParameter("id", id);
            clients = (Clients) query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("Client not found");
            return;
        }
        System.out.println("1 - view all products");
        System.out.println("2 - buy goods");

        String choice = sc.nextLine();
        switch (choice) {
            case "1" :
                System.out.println("Product list");
                System.out.println("_______________________");
                viewAllProducts();
                System.out.println("_______________________");
            break;
            case "2" :
                System.out.println("Enter product ID:");
                Long pId = Long.valueOf(sc.nextLine());


                Goods goods = null;
                try {
                    Query query = entityManager.createQuery("SELECT x FROM Goods x WHERE x.id = :id");
                    query.setParameter("id", pId);
                    goods = (Goods) query.getSingleResult();
                } catch (NoResultException e) {
                    System.out.println("Product not found");
                }

                entityManager.getTransaction().begin();
                try {
                    Orders order = new Orders(clients.getId(), clients.getName(), clients.getAge(), clients.geteMail(), clients.getPhoneNumber());
                    order.setClients(clients);
                    order.addGoods(goods);
                    System.out.println("Your order:");
                    entityManager.persist(order);
                    entityManager.getTransaction().commit();

                    System.out.println("id order:" + order.getId());
                } catch (Exception e) {
                    entityManager.getTransaction().rollback();
                    System.out.println("Wrong order");
                }
        }
    }
    private static void viewOrders(Scanner sc){
        System.out.println("1 Find order");
        System.out.println("2 Find order by client ID");
        System.out.println("3 View All orders");
        System.out.println("->");

        String choice = sc.nextLine();
        switch (choice){
            case "1":
                findOrder(sc);
                break;
            case "2":
                findOrderByClientID(sc);
                break;
            case "3":
                viewAllOrders();
            default:
                return;

        }
    }
    private static void viewAllOrders() {
        Query query = entityManager.createQuery("SELECT c FROM Orders c", Orders.class);
        List<Orders> list = (List<Orders>) query.getResultList();

        for (Orders o : list) {
            System.out.println(o);
        }
    }

    public static void findOrder(Scanner sc){
        System.out.println("Enter order ID:");
        Long id = Long.valueOf(sc.nextLine());

        Orders orders = null;
        try {
            Query query = entityManager.createQuery("SELECT x FROM Orders x WHERE x.id = :id");
            query.setParameter("id", id);
            orders = (Orders) query.getSingleResult();
            System.out.println("Order");
            System.out.println("_____________________________");
            System.out.println(orders);
            System.out.println("_____________________________");
        } catch (NoResultException e) {
            System.out.println("Orders not found");
        }
    }
    public static void findOrderByClientID(Scanner sc){
        System.out.println("Enter client id: ");
        Long cId = Long.valueOf(sc.nextLine());

        Orders orders = null;
        try {
            Query query = entityManager.createQuery("SELECT o FROM Orders o WHERE o.clientID = :cId");
            query.setParameter("cId", cId);
            orders = (Orders) query.getSingleResult();
            System.out.println("Order");
            System.out.println("_____________________________");
            System.out.println(orders);
            System.out.println("_____________________________");
        } catch (NoResultException e) {
            System.out.println("Order not found");
        }
    }
}



