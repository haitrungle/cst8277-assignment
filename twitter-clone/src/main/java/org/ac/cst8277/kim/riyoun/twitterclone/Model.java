package org.ac.cst8277.kim.riyoun.twitterclone;

import java.sql.Timestamp;
import java.util.UUID;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.SourceType;
import lombok.Data;

@Entity
@Data
class Producer {
  @Id
  private UUID id;
}

@Entity
@Data
class Subscriber {
  @Id
  private UUID id;
}

@Entity
@Data
class Subscription {
  @Id
  @GeneratedValue
  private UUID id;

  @ManyToOne
  @JoinColumn(name="subscriber_id", referencedColumnName="id")
  private Subscriber subscriber;

  @ManyToOne
  @JoinColumn(name="producer_id", referencedColumnName="id")
  private Producer producer;
}

@Entity
@Data
class Message {
  @Id
  @GeneratedValue
  private UUID id;

  private String content;

  @ManyToOne
  @JoinColumn(name="producer_id", referencedColumnName="id")
  private Producer producer;

  @CreationTimestamp(source=SourceType.DB)
  private Timestamp created;
}
