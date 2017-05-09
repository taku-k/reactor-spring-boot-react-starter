package server.services;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import server.domain.Todo;

public class TodoServiceImpl implements TodoService {

  private final Cache<Long, Todo> todoById =
      CacheBuilder.newBuilder().maximumSize(100).expireAfterWrite(10, TimeUnit.MINUTES).build();
  private final AtomicLong lastId = new AtomicLong(0);

  public Flowable<List<Todo>> getTodoList() {
    return Flowable.just(Arrays.asList(todoById.asMap().values().toArray(new Todo[] {})));
  }

  public Flowable<Todo> addTodo(String text) {
    return Flowable.create(
        emitter -> {
          long newId = lastId.incrementAndGet();
          Todo todo = new Todo(newId, text);
          this.todoById.put(todo.getId(), todo);
          emitter.onNext(todo);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }

  public Flowable<Todo> deleteTodoById(Long todoId) {
    return Flowable.create(
        emitter -> {
          Todo delTodo = todoById.getIfPresent(todoId);
          todoById.invalidate(todoId);
          emitter.onNext(delTodo);
          emitter.onComplete();
        },
        BackpressureStrategy.BUFFER);
  }
}
