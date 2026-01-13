package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.Evaluate;
import com.example.librarymanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EvaluateRepository extends JpaRepository<Evaluate, Integer> {

    List<Evaluate> findByUser_UserId(int userId);
    List<Evaluate> findByBook_Title(String title);
    boolean existsByUserAndBook_Title(User user, String title);
    @Query("""
            select e.rating, count(e)
            from Evaluate e
            where e.book.title = :title
            group by e.rating
            order by e.rating desc
            """)
    List<Object[]> countRatingByBookTitle(@Param("title") String title);
    @Query("""
            select AVG(e.rating)
            from Evaluate e
            where e.book.title = :title
            """)
    Double averageRatingByBookId(@Param("title") String title);
//    @Query("""
//            select AVG(e.rating), count(e)
//            from Evaluate e
//            where e.title = :title
//            """)
//    Object[] averageAndTotalRatingByTitle(@Param("title") String title);
}
