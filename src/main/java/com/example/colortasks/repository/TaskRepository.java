package com.example.colortasks.repository;

import com.example.colortasks.entity.Task;
import com.example.colortasks.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    Optional<Task> findByTaskNameAndUser(String taskName, User user);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tasks WHERE user_id = ?;", nativeQuery = true)
    void deleteAllTasksByUserId(int id);

    @Transactional
    @Modifying
    @Query(value = "DELETE FROM tasks WHERE color = ? AND user_id = ?", nativeQuery = true)
    void deleteTasksByColor(String color, int id);


    @Query(value = "SELECT * FROM tasks WHERE user_id = ? AND color = ?", nativeQuery = true)
    List<Task> findAllTasksByColor(int id, String color);
}
