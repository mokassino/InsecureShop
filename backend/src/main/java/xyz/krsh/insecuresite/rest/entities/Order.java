package xyz.krsh.insecuresite.rest.entities;

import java.util.Date;
import javax.persistence.*;

@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column
    private Date orderDate;

    @Column
    private float price = 0;

    public Order() {
    }

    public Order(User user, Date orderDate) {
        super();
        this.orderDate = orderDate;
        this.user = user;

    }

    public int getId() {
        return orderId;
    }

    public User getUser() {
        return user;

    }

    public Date getOrderDate() {
        return orderDate;
    }

    public String getDate() {
        return orderDate.toString().split(" ")[0];
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void setOrderDate(Date date) {
        orderDate = date;
    }
}
