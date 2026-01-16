package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.WishlistModel;
import com.example.librarymanagement.dtos.requests.wishlist.WishlistAddRequest;
import com.example.librarymanagement.entities.Book;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.entities.Wishlist;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.UserRepository;
import com.example.librarymanagement.repositories.WishlistRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishlistRepository wishlistRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;

    public WishlistModel toModel(Wishlist wishlist) {
        return new WishlistModel(
                wishlist.getWishlistId(),
                wishlist.getBook().getTitle(),
                wishlist.getCreatedAt(),
                wishlist.getUser().getFullName(),
                wishlist.getBook().getImageUrl(),
                wishlist.getBook().getPdfPath(),
                wishlist.getBook().getBookId()
        );
    }

    public List<Integer> getWishlistBookIds(int userId) {
        return wishlistRepository.findByUser_UserId(userId)
                .stream()
                .map(w -> w.getBook().getBookId())
                .toList();
    }

    @PreAuthorize("hasRole('USER')")
    public List<WishlistModel> getWishlist() {
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
    public WishlistModel getWishlistBook(int wishlistId) {
        Wishlist wishlist = wishlistRepository.findById(wishlistId).orElse(null);
        if(wishlist==null) return null;
        return toModel(wishlist);
    }

    @PreAuthorize("hasRole('USER')")
    public WishlistModel addToWishlist(WishlistAddRequest request) {
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
