package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.WishlistModel;
import com.example.librarymanagement.dtos.requests.wishlist.AddToWishlistRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.entities.Wishlist;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.UserRepository;
import com.example.librarymanagement.repositories.WishlistRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WishlistService {

    @Autowired
    private WishlistRepository wishlistRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    public WishlistModel toModel(Wishlist wishlist) {
        return new WishlistModel(
                wishlist.getWishlistId(),
                wishlist.getBook().getTitle(),
                wishlist.getDescription(),
                wishlist.getCreatedAt(),
                wishlist.getUser().getFullName(),
                wishlist.getBook().getImageUrl()
        );
    }

    @PreAuthorize("hasRole('USER')")
    public List<WishlistModel> getAllWishlist() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.UNAUTHORIZED));

            List<Wishlist> wishlists = wishlistRepository.findByUser_UserId(userCurrent.getUserId());
            List<WishlistModel> wishlistModels = new ArrayList<>();
            for(Wishlist wishlist : wishlists)
                wishlistModels.add(toModel(wishlist));

            return wishlistModels;
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public WishlistModel addToWishlist(AddToWishlistRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            Book bookWishlist = bookRepository.findFirstByTitle(request.getBookName())
                    .orElseThrow(() -> new AppException(ErrorCode.BOOK_NOT_FOUND));

            if(wishlistRepository.existsByUserAndBook(userCurrent, bookWishlist))
                throw new AppException(ErrorCode.WISHLIST_EXISTED);

            Wishlist wishlist = Wishlist.builder()
                    .book(bookWishlist)
                    .description(request.getDescription())
                    .createdAt(LocalDate.now())
                    .user(userCurrent)
                    .build();

            wishlistRepository.save(wishlist);
            return toModel(wishlist);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    @PreAuthorize("hasRole('USER')")
    public void deleteFromWishlist(int wishlistId) {
        if(!wishlistRepository.existsById(wishlistId))
            throw new AppException(ErrorCode.WISHLIST_NOT_FOUND);
        wishlistRepository.deleteById(wishlistId);
    }
}
