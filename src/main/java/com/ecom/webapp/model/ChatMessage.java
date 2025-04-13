package com.ecom.webapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "chat_message")
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private int id;

    @Size(max = 45)
    @NotNull
    @Column(name = "sender_username", nullable = false, length = 45)
    private String senderUsername;

    @Size(max = 45)
    @NotNull
    @Column(name = "recipient_username", nullable = false, length = 45)
    private String recipientUsername;

    @Column(name = "date_created")
    private Instant dateCreated;

    @NotNull
    @Lob
    @Column(name = "content", nullable = false)
    private String content;


    @PrePersist
    protected void onCreate() {
        this.dateCreated = Instant.now();
    }

}