package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Evaluate;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluateRepository extends JpaRepository<Evaluate, Integer> {

    List<Evaluate> findByUser_UserId(int userId);
    List<Evaluate> findByTitle(String title);
    boolean existsByUserAndTitle(User user, String title);
    @Query("""
            select e.rating, count(e)
            from Evaluate e
            where e.title = :title
            group by e.rating
            order by e.rating desc
            """)
    List<Object[]> countRatingByTitle(@Param("title") String title);
    @Query("""
            select AVG(cast(e.rating as double))
            from Evaluate e
            where e.title = :title
            """)
    Double averageRatingByTitle(@Param("title") String title);
    @Query("""
            select AVG(e.rating), count(e)
            from Evaluate e
            where e.title = :title
            """)
    Object[] averageAndTotalRatingByTitle(@Param("title") String title);
}
