package com.game.repository;

import com.game.entity.Player;
import jakarta.annotation.PreDestroy;
import jakarta.persistence.NamedQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.query.NativeQuery;
import org.hibernate.query.Query;
import org.hibernate.service.ServiceRegistry;
import org.springframework.stereotype.Repository;

//import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;
import java.util.Properties;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private SessionFactory sessionFactory;


    public PlayerRepositoryDB() {

        try {
            Configuration configuration = new Configuration();
            Properties settings = new Properties();
            String connectString = "jdbc:mysql://localhost:3307/rpg";
            settings.put("hibernate.connection.url", connectString);
            settings.put("hibernate.connection.username", "root");
            settings.put("hibernate.connection.password", "root");
            settings.put("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
            settings.put("hibernate.dialect", "org.hibernate.dialect.MySQL8Dialect");
            settings.put("hibernate.show_sql", "true");
            settings.put("hibernate.format_sql", "true");
            settings.put(Environment.HBM2DDL_AUTO, "update");
            settings.put("hibernate.current_session_context_class", "thread");
            configuration.setProperties(settings);


            configuration.addAnnotatedClass(Player.class);

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .applySettings(configuration.getProperties()).build();

            sessionFactory = configuration.buildSessionFactory(serviceRegistry);


        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }

    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {
        int offsets = pageNumber * pageSize;
        String nativeSql = "SELECT * FROM rpg.player LIMIT :limit OFFSET :offset";


        try {
            Transaction trans = sessionFactory.getCurrentSession().beginTransaction();
            List<Player> list = sessionFactory.getCurrentSession()
                    .createNativeQuery(nativeSql, Player.class)
                    .setParameter("limit",pageSize)
                    .setParameter("offset",offsets).getResultList();

            System.out.println(list);

            trans.commit();
            return list;
        } catch (Exception e) {

        }

        return null;


    }

    @Override
    public int getAllCount() {
        Long count = 0L;
        Transaction transaction = null;
        try (Session session = sessionFactory.openSession()) {
            transaction = session.beginTransaction();
            count = session.createNamedQuery("findAllPlayers", Long.class)
                    .getSingleResult();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        return Math.toIntExact(count);
    }

    @Override
    public Player save(Player player) {
        return null;
    }

    @Override
    public Player update(Player player) {
        return null;
    }

    @Override
    public Optional<Player> findById(long id) {
        return Optional.empty();
    }

    @Override
    public void delete(Player player) {

    }

    @PreDestroy
    public void beforeStop() {
        sessionFactory.close();
    }
}