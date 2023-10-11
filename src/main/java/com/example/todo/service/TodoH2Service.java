/*
 * You can use the following import statements
 *
 * import org.springframework.beans.factory.annotation.Autowired;
 * import org.springframework.http.HttpStatus;
 * import org.springframework.jdbc.core.JdbcTemplate;
 * import org.springframework.stereotype.Service;
 * import org.springframework.web.server.ResponseStatusException;
 * import java.util.*;
 *
 */

// Write your code here
package com.example.todo.service;

import com.example.todo.model.*;
import com.example.todo.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@Service
public class TodoH2Service implements TodoRepository {
    @Autowired
    public JdbcTemplate db;

    @Override
    public ArrayList<Todo> getTodos() {
        List<Todo> todoC = db.query("select * from TODOLIST", new TodoRowMapper());
        ArrayList<Todo> todos = new ArrayList<>(todoC);

        return todos;
    }

    @Override
    public Todo getTodoById(int todoId) {
        try {
            Todo todo = db.queryForObject("select * from TODOLIST where id = ?", new TodoRowMapper(), todoId);
            return todo;
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public Todo addTodo(Todo todo) {
        db.update("insert into TODOLIST(todo, status, priority) values(?, ?, ?)", todo.getTodo(), todo.getStatus(),
                todo.getPriority());

        Todo savedTodo = db.queryForObject("select * from TODOLIST where todo = ? and status = ? and priority = ?",
                new TodoRowMapper(), todo.getTodo(), todo.getStatus(), todo.getPriority());

        return savedTodo;
    }

    @Override
    public Todo updateTodo(int todoId, Todo todo) {
        try {
            if (todo.getTodo() != null) {
                db.update("update TODOLIST set todo = ? where id = ?", todo.getTodo(), todoId);
            }
            if (todo.getStatus() != null) {
                db.update("update TODOLIST set status = ? where id = ?", todo.getStatus(), todoId);
            }
            if (todo.getPriority() != null) {
                db.update("update TODOLIST set priority = ? where id = ?", todo.getPriority(), todoId);
            }

            return getTodoById(todoId);
        } 
        catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @Override
    public void deleteTodo(int todoId) {

        int rowAffected =  db.update("delete from TODOLIST where id = ?", todoId);
        
        if(rowAffected == 0){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
      
    }

}
