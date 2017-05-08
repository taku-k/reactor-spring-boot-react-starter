package server.resources;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import server.domain.Todo;
import server.services.TodoService;

@RestController
@RequestMapping("/api/todo")
public class TodoResource {

  @Autowired TodoService todoService;

  @RequestMapping(method = RequestMethod.GET)
  public List<Todo> list() {
    return todoService.getTodoList().blockingFirst();
  }

  @RequestMapping(method = RequestMethod.POST)
  public Todo add(@RequestBody Todo input) {
    return todoService.addTodo(input.getText()).blockingFirst();
  }

  @RequestMapping(value = "/{todoId}", method = RequestMethod.DELETE)
  public Todo del(@PathVariable Long todoId) {
    return todoService.deleteTodoById(todoId).blockingFirst();
  }
}
