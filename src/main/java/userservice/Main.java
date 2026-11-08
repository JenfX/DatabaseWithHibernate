package userservice;

import userservice.dao.UserDaoHibernate;
import userservice.entity.HibernateUtil;
import userservice.presentation.ConsoleIO;
import userservice.presentation.MainMenu;
import userservice.presentation.Menu;
import userservice.service.UserService;
import userservice.service.UserServiceDao;

public class Main {
    public static void main(String[] args) {
        try {
            UserService service = new UserServiceDao(new UserDaoHibernate(HibernateUtil.getSessionFactory()));
            Menu current = new MainMenu(service, new ConsoleIO());

            while (current != null) {
                current = current.show();
            }
        } finally {
            HibernateUtil.shutdown();
        }
    }
}