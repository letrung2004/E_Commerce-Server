package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.Comment;
import com.ecom.webapp.repository.CommentRepository;
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
public class CommentRepositoryImpl implements CommentRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<Comment> getCommentsByCommentParentId(Comment commentParent) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<Comment> criteria = builder.createQuery(Comment.class);
        Root<Comment> comment = criteria.from(Comment.class);
        criteria.where(builder.equal(comment.get("commentParent"), commentParent));
        return session.createQuery(criteria).getResultList();
    }

    @Override
    public Comment getCommentById(int id) {
        Session session = sessionFactory.getObject().getCurrentSession();
        return session.get(Comment.class, id);
    }



    @Override
    public void createComment(Comment comment) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(comment);
    }

    @Override
    public void deleteComment(Comment comment) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.remove(comment);
    }
}
