package dev.agasen.ecom.util.mongo;

import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.ReactiveMongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import reactor.core.publisher.Mono;

@Component
public class SequenceGeneratorService {
  
  private final ReactiveMongoOperations mongoOperations;

  public SequenceGeneratorService(ReactiveMongoOperations mongoOperations) {
    this.mongoOperations = mongoOperations;
  }

  public Mono<Long> generateSequence(String seqName) {
    return mongoOperations
        .findAndModify(
          Query.query(Criteria.where("_id").is(seqName)), 
          new Update().inc("seq", 1), 
          FindAndModifyOptions.options().returnNew(true).upsert(true),
          DatabaseSequence.class
        )
        .doOnNext(e -> System.out.println(e))
        .map(DatabaseSequence::getSeq)
        .switchIfEmpty(Mono.just(1L));
  }
}
