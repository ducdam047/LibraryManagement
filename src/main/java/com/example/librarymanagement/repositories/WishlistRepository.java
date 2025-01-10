package com.example.librarymanagement.repositories;

import com.example.librarymanagement.entities.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WishlistRepository extends JpaRepository<Wishlist, Integer> {

    List<Wishlist> findByUser_UserId(int userId);
}
