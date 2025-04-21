package com.ecom.webapp.repository.impl;

import com.ecom.webapp.model.ChatMessage;
import com.ecom.webapp.model.User;
import com.ecom.webapp.repository.ChatMessageRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
@Transactional
public class ChatMessageRepositoryImpl implements ChatMessageRepository {

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Override
    public List<ChatMessage> getChatMessagesHistory(User sender, User recipient) {
        Session session = sessionFactory.getObject().getCurrentSession();
        CriteriaBuilder builder = session.getCriteriaBuilder();
        CriteriaQuery<ChatMessage> criteriaQuery = builder.createQuery(ChatMessage.class);
        Root<ChatMessage> chatMessageRoot = criteriaQuery.from(ChatMessage.class);
        criteriaQuery.select(chatMessageRoot);

        // In a private chatroom, both chat users are sender or recipient
        // sender -> recipient
        Predicate condition1 = builder.and(
                builder.equal(chatMessageRoot.get("senderUsername"), sender.getUsername()),
                builder.equal(chatMessageRoot.get("recipientUsername"), recipient.getUsername())
        );
        // sender -> recipient
        Predicate condition2 = builder.and(
                builder.equal(chatMessageRoot.get("senderUsername"), recipient.getUsername()),
                builder.equal(chatMessageRoot.get("recipientUsername"), sender.getUsername())
        );

        Predicate finalPredicate = builder.or(condition1, condition2);
        criteriaQuery.where(finalPredicate);
        criteriaQuery.orderBy(builder.asc(chatMessageRoot.get("dateCreated")));

        return session.createQuery(criteriaQuery).getResultList();
    }

    @Override
    public ChatMessage saveChatMessage(ChatMessage chatMessage) {
        Session session = sessionFactory.getObject().getCurrentSession();
        session.persist(chatMessage);
        return chatMessage;
    }
}
