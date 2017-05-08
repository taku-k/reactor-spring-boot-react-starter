package server.services;

import static org.assertj.core.api.Assertions.*;

import io.reactivex.subscribers.TestSubscriber;
import java.util.List;
import org.junit.Test;
import server.domain.Todo;

public class TodoServiceTest {
  @Test
  public void testGetEmptyTodoList() {
    TodoService todoService = new TodoServiceImpl();
    TestSubscriber<List<Todo>> testSubscriber = new TestSubscriber<>();
    todoService.getTodoList().subscribe(testSubscriber);

    // No emit error
    testSubscriber.assertNoErrors();
    List<Todo> emptyTodo = testSubscriber.values().get(0);
    assertThat(emptyTodo).as("Get empty todo list").isEmpty();
  }

  @Test
  public void testOneTodoList() {
    TodoService todoService = new TodoServiceImpl();
    // Add one text to the list
    todoService.addTodo("todo1").blockingFirst();
    TestSubscriber<List<Todo>> testSubscriber = new TestSubscriber<>();
    todoService.getTodoList().subscribe(testSubscriber);

    testSubscriber.assertNoErrors();
    List<Todo> oneTodo = testSubscriber.values().get(0);
    assertThat(oneTodo)
        .as("List contains one message `todo1`")
        .extracting(Todo::getText)
        .containsExactly("todo1");
  }
}
