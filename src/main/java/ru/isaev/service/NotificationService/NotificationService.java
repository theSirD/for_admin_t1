package ru.isaev.service.NotificationService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.isaev.domain.Notifications.Notification;
import ru.isaev.domain.Products.Product;
import ru.isaev.domain.Users.Roles;
import ru.isaev.domain.Users.User;
import ru.isaev.repo.NotificationRepo;
import ru.isaev.repo.UserRepo;
import ru.isaev.service.Security.MyUserDetails;
import ru.isaev.service.Utilities.Exceptions.NotYourNotificationException;
import ru.isaev.service.Utilities.Exceptions.UserNotFoundException;
import ru.isaev.service.Utilities.NotificationsTimestampComparator;

import java.util.List;

@Service
public class NotificationService implements INotificationService {
    private final UserRepo userRepo;

    private final NotificationRepo notificationRepo;

    @Autowired
    public NotificationService(UserRepo userRepo, NotificationRepo notificationRepo) {
        this.userRepo = userRepo;
        this.notificationRepo = notificationRepo;
    }

    @Override
    public Notification getNotificationById(Long id) {
        MyUserDetails currentPrincipal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = currentPrincipal.getUser();

        Notification notification = notificationRepo.findById(id).orElseThrow(
                () -> new UserNotFoundException("Not found notification with id= " + id)
        );

        if (!currentUser.getId().equals(notification.getUserId()) && !currentUser.getRole().equals(Roles.ROLE_ADMIN))
            throw new NotYourNotificationException("You do not have access to notification with id= " + notification.getId());

        return notification;
    }
    @Override
    public List<Notification> getAllNotificationsOfUserById(Long userId) {
        MyUserDetails currentPrincipal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = currentPrincipal.getUser();

        if (!currentUser.getId().equals(userId) && !currentUser.getRole().equals(Roles.ROLE_ADMIN))
            throw new NotYourNotificationException("You do not have access to notifications of user with id = " + userId);

        List<Notification> notificationsList = notificationRepo.findAllByUserId(currentUser.getId());
        notificationsList.sort(new NotificationsTimestampComparator());
        return notificationsList;
    }

    @Override
    public void addNotification(Notification notification) {
        notificationRepo.save(notification);
    }

    @Override
    public void addNotificationToSubscribersOfProduct(Notification notification, Product product, List<User> productSubscribersList) {
        for (User user :
               productSubscribersList) {
            notification.setUserId(user.getId());
            notification.setProductId(product.getId());
            notification.setCategoryOfProduct(product.getCategory());
            this.addNotification(notification);
        }
    }

    @Override
    public void deleteNotificationById(Long notificationId) {
        MyUserDetails currentPrincipal = (MyUserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        User currentUser = currentPrincipal.getUser();

        Notification notification = notificationRepo.findById(notificationId).orElseThrow(
                () -> new UserNotFoundException("Not found notification with id= " + notificationId)
        );

        if (!currentUser.getId().equals(notification.getUserId()) && !currentUser.getRole().equals(Roles.ROLE_ADMIN))
            throw new NotYourNotificationException("You do not have access to notification with id= " + notification.getId());

        notificationRepo.deleteById(notificationId);
    }
}
