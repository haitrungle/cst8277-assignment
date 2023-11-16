package org.ac.cst8277.kim.riyoun.twitterclone;


import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


interface MessageRepository extends JpaRepository<Message, UUID> {
    @Query("SELECT m FROM Message m WHERE m.producer.id = :id")
    List<Message> findAllByProducer(@Param("id") UUID id);

    @Query("SELECT m FROM Message m " +
            "JOIN Subscription s ON m.producer.id = s.producer.id " +
            "WHERE s.subscriber.id = :id")
    List<Message> findAllBySubscriber(@Param("id") UUID id);
}

interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {
    @Query("SELECT s FROM Subscription s WHERE s.subscriber.id = :id")
    List<Subscription> findAllBySubscriber(@Param("id") UUID id);
}
