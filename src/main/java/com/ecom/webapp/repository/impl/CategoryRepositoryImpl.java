package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Category;
import com.ecom.webapp.repository.CategoryRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class CategoryRepositoryImpl implements CategoryRepository {
    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Category> getCategories() {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Category> q = builder.createQuery(Category.class);
        Root<Category> root = q.from(Category.class);

        q.select(root);

        return session.createQuery(q).getResultList();
    }

    @Override
    public void addOrUpdateCategory(Category category) {
        Session session = sessionFactory.getObject().getCurrentSession();
        if(category.getId()!=null){
            session.merge(category);
        }
        else {
            session.persist(category);
        }
    }


    @Override
    public Category getCategoryById(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Category.class, id);
    }


    @Override
    public void deleteCategory(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(getCategoryById(id));
    }
}
