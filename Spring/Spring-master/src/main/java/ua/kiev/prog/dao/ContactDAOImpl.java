package ua.kiev.prog.dao;

import org.springframework.stereotype.Repository;
import ua.kiev.prog.model.Contact;
import ua.kiev.prog.model.Group;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import java.util.List;
//Реализация DAO
//@Repository она производная от Component
//указывает что это DAO, будет создан бин этого класса и добавят его в контекст
//и мы можем такой класс инжектить дальше без необходимости описывать его в конфиге
@Repository
public class ContactDAOImpl implements ContactDAO {
    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public void add(Contact contact) {
        entityManager.persist(contact);
    }

    @Override
    public void delete(long[] ids) {
        Contact c;
        for (long id : ids) {
            c = entityManager.getReference(Contact.class, id);
            entityManager.remove(c);
        }
    }

    @Override
    public List<Contact> list(Group group, int start, int count) {
        TypedQuery<Contact> query;

        if (group != null) {
            query = entityManager.createQuery("SELECT c FROM Contact c WHERE c.group = :group ORDER BY c.id DESC", Contact.class);
            query.setParameter("group", group);
        } else {
            query = entityManager.createQuery("SELECT c FROM Contact c ORDER BY c.id DESC", Contact.class);
        }

        if (start >= 0) {
            query.setFirstResult(start);// начало
            query.setMaxResults(count);//
        }

        return query.getResultList();
    }

    @Override
    public List<Contact> list(String pattern) {
        TypedQuery<Contact> query = entityManager.createQuery("SELECT c FROM Contact c WHERE c.name LIKE :pattern", Contact.class);
        query.setParameter("pattern", "%" + pattern + "%");
        return query.getResultList();
    }

    @Override
    public long count() {
        TypedQuery<Long> query = entityManager.createQuery("SELECT COUNT(c) FROM Contact c", Long.class);
        return query.getSingleResult();
    }
}