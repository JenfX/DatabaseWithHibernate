package userservice.entity;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class HibernateUtil {
    private static final Logger log = LoggerFactory.getLogger(HibernateUtil.class);
    private static final SessionFactory SESSION_FACTORY = build();

    private static  SessionFactory build() {
        try {
            log.info("Создание SessionFactory");
            return new Configuration()
                    .configure()
                    .addAnnotatedClass(User.class)
                    .buildSessionFactory();
        } catch (Exception e) {
            log.error("Не удалось создать SessionFactory", e);
            throw new ExceptionInInitializerError("Не удалось создать SessionFactory");
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        SESSION_FACTORY.close();
        log.info("SessionFactory закрыта");
    }
}
