package com.apress.todo.controller;

import com.apress.todo.domain.ToDoBuilder;
import com.apress.todo.domain.Todo;
import com.apress.todo.repository.TodoRepository;
import com.apress.todo.validation.ToDoValidationError;
import com.apress.todo.validation.ToDoValidationErrorBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.Optional;

@RestController
@RequestMapping("/api")
public class ToDoController {
    private final TodoRepository toDoRepository;

    @Autowired
    public ToDoController(TodoRepository toDoRepository) {
        this.toDoRepository = toDoRepository;
    }

    @GetMapping("/todo")
    public ResponseEntity<Iterable<Todo>> getToDos() {
        return ResponseEntity.ok(toDoRepository.findAll());
    }

    @GetMapping("todo/{id}")
    public ResponseEntity<Todo> getToDoById(@PathVariable String id) {
        Optional<Todo> todo = toDoRepository.findById(id);
        if (todo.isPresent())
            return ResponseEntity.ok(todo.get());
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/todo/{id}")
    public ResponseEntity<Todo> setCompleted(@PathVariable String id) {
        Optional<Todo> toDo = toDoRepository.findById(id);
        if (!toDo.isPresent())
            return ResponseEntity.notFound().build();
        Todo result = toDo.get();
        result.setCompleted(true);
        toDoRepository.save(result);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().
                buildAndExpand(result.getId()).toUri();
        return ResponseEntity.ok().header("Location", location.toString()).
                build();
    }

    @RequestMapping(value = "/todo", method = {RequestMethod.
            POST, RequestMethod.PUT})
    public ResponseEntity<?> createToDo(@Valid @RequestBody Todo toDo,
                                        Errors errors) {
        if (errors.hasErrors()) {
            return ResponseEntity.badRequest().
                    body(ToDoValidationErrorBuilder.fromBindingErrors(errors));
        }

        Todo result = toDoRepository.save(toDo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest().
                path("/{id}").buildAndExpand(result.getId()).toUri();
        return ResponseEntity.created(location).build();
    }

    @DeleteMapping("/todo/{id}")
    public ResponseEntity<Todo> deleteToDo(@PathVariable String id) {
        toDoRepository.delete(ToDoBuilder.create().withId(id).build());
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/todo")
    public ResponseEntity<Todo> deleteToDo(@RequestBody Todo todo) {
        toDoRepository.delete(todo);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    public ToDoValidationError handleException(Exception exception) {
        return new ToDoValidationError(exception.getMessage());
    }
}