package server.services;

import io.reactivex.Flowable;
import java.util.List;
import server.domain.Todo;

public interface TodoService {

  Flowable<List<Todo>> getTodoList();

  Flowable<Todo> addTodo(String text);

  Flowable<Todo> deleteTodoById(Long todoId);
}
