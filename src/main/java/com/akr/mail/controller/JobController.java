package com.akr.mail.controller;

import com.akr.mail.datastore.Order;
import com.akr.mail.model.NewOrderObject;
import com.akr.mail.model.SimpleMailObject;
import com.akr.mail.model.StatusUpdate;
import com.akr.mail.service.OrderService;
import com.akr.mail.util.EmailUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping(value = "/v1")
public class JobController {

    @Autowired
    OrderService orderService;

    @Autowired
    EmailUtility emailUtility;

    //order received
    @RequestMapping(value = "/order/new", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> createNewOrder(@RequestBody NewOrderObject newOrderObject) {
        int id = orderService.add(newOrderObject);
        return new ResponseEntity<>("New order created with id: " + id
                + ". An email with update will be sent.", HttpStatus.CREATED);
    }

    @GetMapping(value = "/order/all")
    public ResponseEntity<List<Order>> displayAllOrders() {
        return new ResponseEntity<>(orderService.getAllOrders(), HttpStatus.OK);
    }

    @DeleteMapping(value = "/order/{id}")
    public ResponseEntity<String> deleteOrder(@PathVariable Integer id) {
        Optional<String> deleted = Optional.ofNullable(orderService.delete(id));
        if (deleted.isPresent())
            return new ResponseEntity<>(deleted.get(), HttpStatus.OK);
        else
            return new ResponseEntity<>("No such order id exists: " + id, HttpStatus.NOT_FOUND);
    }

    //order shipped or canceled
    @RequestMapping(value = "/order/{id}", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> orderStateChanged(@PathVariable Integer id, @RequestBody StatusUpdate statusUpdate) {
        Optional<Order> optionalOrder = orderService.findOrderById(id);
        if (optionalOrder.isPresent()) {
            String returnMessage = orderService.changeOrderState(id, statusUpdate);
            if ("BAD_REQUEST".equals(returnMessage))
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            else
                return new ResponseEntity<>(returnMessage, HttpStatus.OK);
        } else {
            return new ResponseEntity<>("No such order with id " + id, HttpStatus.NOT_FOUND);
        }
    }

    //send normal format email
    @RequestMapping(value = "/mail", method = RequestMethod.POST, consumes = "application/json")
    public ResponseEntity<String> sendNormalEmail(@RequestBody SimpleMailObject simpleMailObject) {
        if (emailUtility.sendSimpleEmail(simpleMailObject))
            return new ResponseEntity<>("Mail sent successfully to " + simpleMailObject.getToAddress(), HttpStatus.OK);
        else
            return new ResponseEntity<>("Error occurred while sending mail, please retry with same link.", HttpStatus.NOT_ACCEPTABLE);
    }

    //check status of email for a given order id
    @GetMapping(value = "/mail/check/{id}")
    public ResponseEntity<String> checkMailState(@PathVariable Integer id) {
        String emailState = emailUtility.getEmailStateForOrderId(id);
        if (Objects.isNull(emailState))
            return new ResponseEntity<>("No such order with id " + id, HttpStatus.NOT_FOUND);
        else
            return new ResponseEntity<>("Sent".equalsIgnoreCase(emailState) ?
                    "Email update sent successfully." : "Email in " + emailState + " state, please retry with /mail/retry/{id}"
                    , HttpStatus.OK);
    }

    //retry sending email for a given order id
    @GetMapping(value = "/mail/retry/{id}")
    public ResponseEntity<String> resendMail(@PathVariable Integer id) {
        String emailState = emailUtility.getEmailStateForOrderId(id);
        if (Objects.isNull(emailState))
            return new ResponseEntity<>("No such order with id " + id, HttpStatus.NOT_FOUND);
        else if ("Sent".equalsIgnoreCase(emailState))
            return new ResponseEntity<>("Email sent already", HttpStatus.OK);
        else {
            Optional<Order> order = orderService.findOrderById(id);
            if (order.isPresent()) {
                emailUtility.setEmailStateForOrderId(id, "Sending");
                emailUtility.sendTemplateEmail(id, order.get());
                return new ResponseEntity<>("Retrying to send email"
                        , HttpStatus.OK);
            } else {
                return new ResponseEntity<>("No such order with id " + id, HttpStatus.NOT_FOUND);
            }
        }
    }
}
